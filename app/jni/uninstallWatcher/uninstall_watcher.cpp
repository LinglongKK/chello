#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/file.h>
#include <sys/stat.h>
#include <sys/inotify.h>//inotify_init inotify_add_watch....
#include <android/log.h>
#include "log_for_android.h"

#define TAG "UNINSTALL_WATCHER"
#define WATCHER "app_MyLibs/watcher"

//#define DEVICE_DEBUG

#define EVENT_SIZE  ( sizeof (struct inotify_event) )  
#define BUF_LEN     ( 1024 * ( EVENT_SIZE + 16 ) )  
#define ERR_EXIT(msg,flag)  {perror(msg);goto flag;}  

#define MAX_PATH 256
#define MAX_DEPTH 2
#define ROOT_LIST_LENGTH 2
#define PATH_LIST_LENGTH 4
#define LOCK_NAME "watcher.lock"
#define PID_NAME "watcher.pid"
static const char* mUser = NULL;

typedef struct 
{
	int len;
	char pdata[1024*1024];
}HTTP_DATA_BUF;

inline void init_http_data_buf(HTTP_DATA_BUF *buf){
	buf->len = 0;
//	buf->off = 0;
}

size_t http_data_writer(void *data, size_t size, size_t nmemb, void* buf)
{
    long totalSize = size * nmemb;
   // printf("Get %d bytes from server,0x%08X,0x%08X\n", totalSize,data,buf);
	HTTP_DATA_BUF *pbuf = (HTTP_DATA_BUF*)buf;
	memcpy(pbuf->pdata+pbuf->len,data,totalSize);
	pbuf->len += totalSize;
	pbuf->pdata[pbuf->len+1] = '0';

    return totalSize;
}

/*
int http_get(const char* url,HTTP_DATA_BUF *buf){

	CURLcode res;  

    CURL* curl = curl_easy_init();  

    if(NULL == curl)  
    {  
        return CURLE_FAILED_INIT;  
    }  
	  if(m_bDebug)  
    {  
     //   curl_easy_setopt(curl, CURLOPT_VERBOSE, 1);  
     //   curl_easy_setopt(curl, CURLOPT_DEBUGFUNCTION, OnDebug);  
    }  
    curl_easy_setopt(curl, CURLOPT_URL, url);  
    curl_easy_setopt(curl, CURLOPT_READFUNCTION, NULL);  
    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, http_data_writer);  
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, (void *)buf);  
     
    //当多个线程都使用超时处理的时候，同时主线程中有sleep或是wait等操作。
    //如果不设置这个选项，libcurl将会发信号打断这个wait从而导致程序退出。
    //
    curl_easy_setopt(curl, CURLOPT_NOSIGNAL, 1);  
    curl_easy_setopt(curl, CURLOPT_CONNECTTIMEOUT, 3);  
    curl_easy_setopt(curl, CURLOPT_TIMEOUT, 3);  
    res = curl_easy_perform(curl);  
	curl_easy_cleanup(curl);  
}*/


void cutdir(const char *path,char* buf){
	int len = strlen(path);
	int index = len;
	for(int i=len;i>=0;i--){
		if(*(path+i)=='/' || *(path+i)=='\\'){
			index = i;
			break;
		}
	}
	memcpy(buf,path,index);
}
 
int mkdirs(const   char   *sPathName)  
{  
  char   DirName[256];  
  strcpy(DirName,   sPathName);  
  int   i,len   =   strlen(DirName);  
  if(DirName[len-1]!='/')  
  strcat(DirName,   "/");  
   
  len   =   strlen(DirName);  
   
  for(i=1;   i<len;   i++)  
  {  
  if(DirName[i]=='/')  
  {  
  DirName[i]   =   0;  
  if(   access(DirName,  0)!=0   )  
  {  
      if(mkdir(DirName,   0777)==-1)  
      {   
                      perror("mkdir   error");   
                //      return   -1;   
      }  
  }  
  DirName[i]   =   '/';  
  }  
  }
  return   0;  
}   

int countSubString(const char*str,const char*sub){
	int count = 0;
	int len1 = strlen(str);
	int len2 = strlen(sub);
	for(int i=0;i<len1;i++){
		bool isFind = true;
		for(int j=0;j<len2;j++){
			if(str[i]!=sub[j]){
				isFind = false;
				break;
			}
		}
		if(isFind){
			count++;
		}
	}
	LOGE(TAG,"process count,%d", count);
	return count;
}

int read_pid(const char* pid_file){
	int fd = open(pid_file,O_RDONLY,0666);
	if(fd < 0) {
		LOGE(TAG,"read_pid -- open file [%s] fail,%d,%s", pid_file,errno, strerror(errno));
		return -1;
	}
	
	char buf[10];
	memset(buf,0,sizeof(buf));
	int size = read(fd,buf,sizeof(buf));

	close(fd);
	if(size>0){
		return atoi(buf);
	}
	return -1;
}

int getPid(const char*proc){
	int pid = 0;
	const char* scan = proc;
	int section = 1;//取第几段数据,默认从0开始
	int cursec = 0;
	while((*scan) != '\0' && cursec <= section){
		if(cursec > section)
			break;
		if(cursec == section && (*scan) >= '0' && (*scan) <= '9'){
			pid = pid*10 + (*scan - '0');
		}
		if((*scan) == ' ' || (*scan) == '\t'){
			cursec++;
			while(((*scan) == ' ' || (*scan) == '\t') && (*scan) != '\0')
				scan++;
			if((*scan) == '\0')
				break;
		}else{
			scan++;
		}
	}
	LOGE(TAG,"Get Other Pid:%d",pid);
	return pid;
}

int killProcess(const char*dir,int pid){
	FILE *fp;
	int count = 0;
	char path[4096];
	char file_path[128];
	/* Open the command for reading. */
	sprintf(file_path,"%s/%s",dir,WATCHER);
	fp = popen("ps", "r");
	if (fp == NULL) {
		LOGE(TAG,"Failed to run command\n" );
		return count;
	}

	/* Read the output a line at a time - output it. */
	while (fgets(path, sizeof(path)-1, fp) != NULL) {
		if(strstr(path,file_path)){
			LOGI(TAG,"ps read line:%s", path);
			int opid = getPid(path);
			int res = 0;
			if(pid != opid){
				res = kill(opid,SIGUSR2);
				res = kill(opid,SIGUSR1);//确保万一，发两次信号
				if(res == 0){
					count++;
					LOGE(TAG,"Kill Process Success!----- Pid=%d",opid);
				}else{
					LOGE(TAG,"Kill Process Error!----- Pid=%d",opid);
				}
			}
		}
	}

	/* close */
	pclose(fp);
	LOGE(TAG,"kill process num,%d\n%s", count,path);
	return count;
}

bool singleton(const char *lock_file)
{
    int rv; 
    int fd; 
 
    fd = open(lock_file, O_WRONLY|O_CREAT);

	if(fd < 0){
		LOGE(TAG,"singleton::open %s error,%d,%s",lock_file,errno,strerror(errno));
		return false;
	}

    rv = flock(fd, LOCK_EX|LOCK_NB);
    if (rv == -1) {
		LOGE(TAG,"singleton::flock error,%d,%s",errno,strerror(errno));
        close(fd);
		return false;
    }   
 
    return true; 
}

bool is_running(const char* pid_file){
	int pid = read_pid(pid_file);
	if(pid>0){

		char filename[255];
		memset(filename,0,sizeof(filename));
		
		sprintf(filename,"/proc/%d",pid);
		
		return !access(filename,F_OK);
	}
	return false;
}


bool set_running(const char* pid_file,int pid){
	
	char dir[255]={0};

	cutdir(pid_file,dir);
	mkdirs(dir);

	int fd = open(pid_file,O_WRONLY|O_CREAT|O_TRUNC,0777);
	if(fd<0) {
		LOGE(TAG,"set_running -- open file [%s] fail,%d,%s", pid_file,errno, strerror(errno));
		return -1;
	}

	char buf[10] = { 0 };

	sprintf(buf,"%d",pid);
	write(fd,buf,strlen(buf));
	close(fd);
	return true;
}

#include <pthread.h>
void *thread2(void *p)
{
	LOGI(TAG,"thread2 : I'm thread 2");
	while(true)
	{
		if(access("/sdcard/1.exit",F_OK)!=-1){
			 LOGI(TAG,"thread2 : exit process");
			 remove("/sdcard/1.exit");
			exit(0);
		}
		sleep(1);
	}
}

void *threadPushServiceTimer(void *p){
	
	const char* user = (const char*)p;
	
	char tmp[1024] = { 0 };
	if(!strcmp(user,"null")){
		sprintf(tmp,"am startservice -n com.qihoo.haosou/com.qihoo.haosou.service.GuardService");
	}else{
		sprintf(tmp,"am startservice --user %s -n com.qihoo.haosou/com.qihoo.haosou.service.GuardService",user);
	}
	
	while(true){
		LOGI(TAG,"threadPushServiceTimer is running...");
		sleep(60 * 60);
		system(tmp);
	}
	
}

//监控push service
void startPushServiceMonitor(const char* user){
	int temp;
	pthread_t pthr;

			LOGI(TAG,"startPushServiceMonitor...");
	if((temp = pthread_create(&pthr, NULL, threadPushServiceTimer, (void*)user)) != 0) {
		LOGI(TAG,"pthread_create error,%d,%s\n",errno,strerror(errno));
	
	}
}


void execCmd(const char *cmd, char *result,int buf_size)    
{    
	char r_buf[1024];    
	char cmd_buf[1024]={0};    
	FILE *ptr;    
	strcpy(cmd_buf, cmd);    
	
	
	if((ptr=popen(cmd_buf, "r"))!=NULL)    
	{    
		LOGD(TAG,"popen %s \n", cmd);
		int bytesOfRead = 0;
		int size = buf_size;
		int tmp = 0;
		while(size > 0 && (tmp = fread(result+bytesOfRead, size,1,ptr)) > 0)    
		{    
			size -= tmp;
			bytesOfRead += tmp;
			LOGD(TAG,"size=%d,bytesOfRead=%d,tmp=%d", size,bytesOfRead,tmp);
		}     
		
		LOGD(TAG,"pclose ...");
		pclose(ptr);    
		LOGD(TAG,"pclose2 ...");
		ptr = NULL;    
				
    }else{
		printf("popen %s error\n", cmd);
		LOGE(TAG,"popen %s error\n", cmd);
	}    
}  


void sigAlarm(int sig){
	char tmp[1024] = { 0 };
	if(!strcmp(mUser,"null")){
		sprintf(tmp,"am startservice -n com.qihoo.haosou/com.qihoo.haosou.service.GuardService");
	}else{
		sprintf(tmp,"am startservice --user %s -n com.qihoo.haosou/com.qihoo.haosou.service.GuardService",mUser);
	}
	LOGI(TAG,"Alarm resume Guard Service...");
	system(tmp);
	alarm(60 * 60);
}

#define QQ_BROWSER			"com.tencent.mtt"
#define UC_BROWSER			"com.UCMobile"
#define CHROME_BROWSER		"com.android.chrome"
#define SAMSUNG_BROWSER		"com.sec.android.app.sbrowser"
#define GOOGLE_BROWSER		"com.google.android.browser"
#define ANDROID_BROWSER		"com.android.browser"
#define QIHOO_BROWSER		"com.qihoo.browser"

bool isPkgInstalled(const char* pkgName){
	char app_dir[255] = { 0 };
	sprintf(app_dir,"/data/data/%s",pkgName);
	return access(app_dir,F_OK)!=-1;
}

void startBrowser(const char *user,const char* feedback_url){

	char target[255] = { 0 };
	if (isPkgInstalled(QIHOO_BROWSER)) {
		sprintf(target,"%s/com.qihoo.browser.BrowserActivity",QIHOO_BROWSER);
	} else if (isPkgInstalled(QQ_BROWSER)) {
		sprintf(target,"%s/com.tencent.mtt.MainActivity",QQ_BROWSER);
	} else if (isPkgInstalled(UC_BROWSER)) {
		sprintf(target,"%s/com.UCMobile.main.UCMobile",UC_BROWSER);
	} else if (isPkgInstalled(SAMSUNG_BROWSER)) {
		sprintf(target,"%s/com.sec.android.app.sbrowser.SBrowserMainActivity",SAMSUNG_BROWSER);
	} else if (isPkgInstalled(GOOGLE_BROWSER)) {
		sprintf(target,"%s/com.android.browser.BrowserActivity",GOOGLE_BROWSER);
	} else if (isPkgInstalled(ANDROID_BROWSER)) {
		sprintf(target,"%s/.BrowserActivity",ANDROID_BROWSER);
	} else if (isPkgInstalled(CHROME_BROWSER)) {
		sprintf(target,"%s/com.google.android.apps.chrome.Main",CHROME_BROWSER);
	}

	char tmp[2048] = { 0 };
	char result[1024] = { 0 };

	if(target[0]){
		if(!strcmp(user,"null")){
			sprintf(tmp,"am start -n %s -a android.intent.action.VIEW -d \"%s\"",target,feedback_url);
		}else{
			sprintf(tmp,"am start --user %s -n %s -a android.intent.action.VIEW -d \"%s\"",user,target,feedback_url);
		}
	}else{
		if(!strcmp(user,"null")){
			sprintf(tmp,"am start -a android.intent.action.VIEW -d \"%s\"",feedback_url);
		}else{
			sprintf(tmp,"am start --user %s -a android.intent.action.VIEW -d \"%s\"",user,feedback_url);
		}
	}

	LOGI(TAG,"tmp=%s",tmp);
	system(tmp);

}


void sigExit(int signal)
{
	LOGI(TAG,"Exit By Signal ----- pid:%d\n",getpid());
	exit(0);
}

int main( int argc, char **argv ){
	__android_log_print(ANDROID_LOG_INFO,"chenlong","----start watcher-----");
	//sleep(1);
	LOGE(TAG,"-----------------------------------------------");
	LOGE(TAG,"-----------------------------------------------");
	LOGI(TAG,"-----------------------------------------------");
	LOGI(TAG,"-----------------------------------------------");
	LOGI(TAG,"-----------------------------------------------");
	LOGI(TAG,"WATCHER ---- pid=%d",getpid());
	LOGI(TAG,"WATCHER ---- pid=%d",getpid());
	
#ifdef DEBUG
	for(int i=0;i<argc;i++){
		LOGD(TAG,"argv[%d]=%s",i,argv[i]);
	}
#endif

	bool isImgCreate = false;
	char *app_dir = argv[1];
	//char *pkg_name = argv[2];
	char *feedback_url = argv[2];
	char *notify_url = argv[3];
	char *user = argv[4];
	char *work_dir = argv[5];
	char pid_file[MAX_PATH] = { 0 };
	char lock_file[MAX_PATH] = { 0 };
	char file_path[MAX_PATH] = { 0 };
	char lastfile_path[MAX_PATH] = { 0 };
	
	strcpy(pid_file,work_dir);
	strcat(pid_file,"/");
	strcat(pid_file,PID_NAME);
	
	strcpy(lock_file,work_dir);
	strcat(lock_file,"/");
	strcat(lock_file,LOCK_NAME);
/*
	if(!is_running(pid_file)){
		set_running(pid_file,getpid());
	}else{
		LOGI("Already have an instance running,exit");
		exit(0);
	}
*/


#ifdef DEBUG
	int temp;
	pthread_t pthr;

	if((temp = pthread_create(&pthr, NULL, thread2, NULL)) != 0) {
		LOGI(TAG,"pthread_create error,%d,%s\n",errno,strerror(errno));
	
	}
#endif

#ifdef DEVICE_DEBUG
	//fork子进程，以执行轮询任务
    pid_t pid = fork();
    if (pid < 0)
    {
		LOGI(TAG,"fork error,%d,%s\n",errno,strerror(errno));
    }
    else if (pid == 0)
    {
        int i=0;
		LOGI(TAG,"child WATCHER ---- pid=%d,%d",getpid(),i++);
		while(true){

			LOGI(TAG,"child WATCHER ---- pid=%d,%d",getpid(),i++);
			sleep(1);
		}
    }
    else
    {
        //父进程直接退出，使子进程被init进程领养，以避免子进程僵死

        int i=0;
		LOGI(TAG,"parent WATCHER ---- pid=%d,%d",getpid(),i++);
	//	while(true){

	//		LOGI("parent WATCHER ---- pid=%d,%d",getpid(),i++);
	//		sleep(1);
	//	}

		return 0;
    }
#endif
	
/*
	if(!singleton(lock_file)){
		LOGI("Already have an instance running,exit");
		exit(0);
	}
	*/
	if(!singleton(lock_file)){

		LOGI(TAG,"Already have an instance running");
		int pid = read_pid(pid_file);
		LOGI(TAG,"kill proc %d",pid);
		kill(pid,9);

		bool bSingleton = false;
		usleep(1);
		remove(lock_file);
		for(int i=0;i<10;i++){
			if(!singleton(lock_file)){
				usleep(10);
				continue;
			}else{
				bSingleton = true;
				LOGI(TAG,"singleton successed..............");
			}
		}
		if(!bSingleton){
			LOGE(TAG,"singleton error..............");
			return 0;
			//exit(0);
		}
		
	}else{
		LOGI(TAG,"singleton successed..............");
	}
	
	set_running(pid_file,getpid());
	
	LOGI(TAG,"WATCHER ---- pid=%d",getpid());
	startPushServiceMonitor(user);

	mUser = user;
	signal(SIGALRM,sigAlarm);
	signal(SIGUSR2,sigExit);
	signal(SIGUSR1,sigExit);
	alarm(60 * 60);
	
	killProcess(app_dir,getpid());

	int length, i = 0;
	int fd;
	int wd;
	char buffer[BUF_LEN];

	if((fd = inotify_init()) < 0){
		LOGI(TAG,"inotify_init error,%d,%s\n",errno,strerror(errno));
	}

	if( (wd = inotify_add_watch( fd, app_dir,	IN_DELETE_SELF|IN_DELETE) ) < 0)
		LOGI(TAG,"inotify_add_watch error,%d,%s\n",errno,strerror(errno));
	
	fd_set rfd;
	struct timeval tv;
	tv.tv_sec = 0;
	tv.tv_usec = 10000;//10millsecond
	while(true)
	{
		LOGI(TAG,"----------------------------------------------------------");
		int retval;
		FD_ZERO(&rfd);
		FD_SET(fd, &rfd);
		retval = select(fd + 1, &rfd, NULL, NULL, NULL);
		LOGI(TAG,"select-------------------------retval=%d",retval);
		if(retval == 0){
			continue;
		
		}
		else if(retval == -1){
			LOGI(TAG,"select error,%d,%s\n",errno,strerror(errno));
			//为保险起见，错误直接退出等待下次启动，避免死循环
//			exit(0);
		}

		// retval > 0
		length = read( fd, buffer, BUF_LEN );  
		if(length < 0)
			LOGI(TAG,"read error,%d,%s\n",errno,strerror(errno));

		//length >= 0
		int i = 0;
		while ( i < length ) 
		{
			struct inotify_event *event = ( struct inotify_event * ) &buffer[ i ];
			if ( event->len ) 
			{
				LOGI(TAG,"has event...%s,idx:%d,length:%d;event length=%d,event struct size=%d\n",event->name,i,length,event->len,EVENT_SIZE);
				usleep(10);
				int eidx = -1;
				if(eidx < 0){
					
						LOGI(TAG,"uninstalled***************---%s",event->name);

					//如果发现lib目录删除事件发生，则等待2秒检测安装目录是否存在，不存在则表示被卸载
					if(!strcmp(event->name,"lib")){
					
						sleep(1);
						if(access(app_dir,F_OK)!=-1){
							continue;
						}

						LOGI(TAG,"uninstalled***************---%s",event->name);
					//if(!strcmp(event->name,"lib") || !strcmp(event->name,"files")){
						LOGI(TAG,"uninstalled***************");
/*						char tmp[2048] = { 0 };
						char result[1024] = { 0 };
						
						if(!strcmp(user,"null")){
							sprintf(tmp,"am start -a android.intent.action.VIEW -d \"%s\"",feedback_url);
						}else{
					//		sprintf(tmp,"am start --user %s -a android.intent.action.VIEW -d \"%s\"",user,feedback_url);
					//	LOGI("%s\n",tmp);
					//		execlp("am","am","start","--user",user,"-a","android.intent.action.VIEW","-d","http://www.baidu.com",(char *)NULL);
							sprintf(tmp,"am start --user %s -a android.intent.action.VIEW -d \"%s\"",user,feedback_url);
						}
						LOGI("uninstall cmd:%s\n",tmp);
					//	execlp("am","am","-al","/zhmc",(char *)0);
					
						//	String  str = "am start --user 0 -a android.intent.action.VIEW -d http://info.so.com/?product=Msearchuninstall&src=soapp&userid=577c897500ae775ab489dee8e7e63b81&version_name=2.0.2.1001&code_version=207&configuration=-1&channel=MSO_APP&phone_type=Nexus6&network_type=WIFI";
								//LOGI("result=%s",str);
							//	execCmd("am start --user 0 -a android.intent.action.VIEW -d http://info.so.com/?product=Msearchuninstall&src=soapp&userid=577c897500ae775ab489dee8e7e63b81&version_name=2.0.2.1001&code_version=207&configuration=-1&channel=MSO_APP&phone_type=Nexus6&network_type=WIFI",result,sizeof(result));
							//	LOGI("result=%s",result);


						system(tmp);*/

						startBrowser(user,feedback_url);

						sleep(2);

					//	HTTP_DATA_BUF buf;
					//	init_http_data_buf(&buf);

						//char *url = "http://www.baidu.com";
					//	http_get(notify_url,&buf);
						inotify_rm_watch( fd, wd );
						close( fd );
						LOGI(TAG,"exit watcher process!\n");
						exit(0);
					}
				}
			}else
			{
				//TODO
				//when only a file(not directory) is specified by add watch function, event->len's value may be zero, we can handle it here
			}
			i += EVENT_SIZE + event->len;
		}
	}

	inotify_rm_watch( fd, wd );
	close( fd );

	return 0;

}
