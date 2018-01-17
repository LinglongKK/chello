#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <sys/stat.h>
#include <sys/resource.h>
#include <syslog.h>
#include <fcntl.h>
#include "../common_macro.h"
pthread_t ntid;
void* thread_func(void *args) {
    LOG_INFO("get environment");
//    while(*environ != NULL) {
//        LOG_INFO(*environ);
//        environ++;
//    }
    LOG_INFO("##############");
    char *path = getenv("PATH");
    if(path) {
        LOG("%s",path);
    }else{
        LOG_INFO("path is NULL");
    }
    return (void *)0;
}

void when_exit() {
    LOG_INFO("process exit!!!!!!!!");
}

void fork_child() {
    pid_t  pid = fork();
    if(pid < 0) {
        LOG_INFO("fork error");
    } else if(pid == 0) {
       LOG_INFO("fork child");
        pid_t  child_pid = getpid();
        pid_t ppid = getppid();
        LOG("forever process pid:%d",child_pid);
        LOG("ppid:%d",ppid);
        while (1);
    }else {
        exit(0);
    }
}


void daemonize(const char *cmd) {
    int i,fd0,fd1,fd2;
    pid_t pid;
    struct rlimit rl;
    struct sigaction sa;
    umask(0);
    if(getrlimit(RLIMIT_NOFILE, &rl)<0) {
        exit(1);
    }

    if((pid = fork()) < 0) {
        LOG_INFO("fork error");
        exit(1);
    } else if(pid != 0) {
        exit(0);
    }

    setsid();
    sa.sa_handler = SIG_IGN;
    sigemptyset(&sa.sa_mask);
    if(sigaction(SIGHUP,&sa,NULL) < 0) {
        exit(1);
    }
    if((pid = fork()) < 0) {
        exit(1);
    }else if(pid != 0) {
        exit(0);
    }

    if(chdir("/data/data/cck.com.chello") < 0) {
        LOG_INFO("can't change directory to xxx");
        exit(1);
    }

    if(rl.rlim_max == RLIM_INFINITY) {
        rl.rlim_max = 1024;
    }
    for(i = 0;i<rl.rlim_max;i++) {
        close(i);
    }

    fd0 = open("/dev/null",O_RDWR);
    fd1 = dup(0);
    fd2 = dup(0);

    openlog(cmd,LOG_CONS,LOG_DAEMON);
    if(fd0 != 0 || fd1 != 1 || fd2 != 2) {
        syslog(LOG_ERR,"fffffff");
        exit(1);
    }
    LOG("pid:%d",getpid());
    LOG("ppid:%d",getppid());
    setuid(10183);
}

int glob = 6;
char buf[] = "a write to stdout\n";

static void fork_exec() {
    pid_t  pid = fork();
    if(pid == 0) {
        execv("/data/data/cck.com.chello/files/process/process_ex",NULL);
    }
}

static void check_process_num() {


}

int main (int argc, char *argv[]) {

//    pid_t pid = getpid();
//    pid_t ppid = getppid();
//    LOG("execute native process ,pid:%d!\n",pid);
//    LOG("ppid: %d",ppid);
//    int err =  pthread_create(&ntid,NULL,thread_func,NULL);
//    if(err != 0) {
//        LOG_INFO("create thread fail!");
//        return -1;
//    }
//    pthread_join(ntid,NULL);
//    printf("native process exit");

//    int var = 88;
//    pid_t pid;
//    if(write(STDOUT_FILENO,buf,sizeof(buf)-1) != sizeof(buf)-1) {
//        LOG_INFO("write error");
//    }
//    LOG_INFO("before fork");
//
//    if((pid = fork()) < 0) {
//        LOG_INFO("fork error");
//    } else if(pid == 0) {
//        fork_child();
//    } else {
//        sleep(2);
//    }
//    LOG("#######pid:%d#######",getpid());
    daemonize("test");
    LOG("running uid:%d",getuid());
    LOG("running gid:%d",getgid());
//    atexit(when_exit);
    sleep(2);

    int count = 0;
    FILE *num = fopen("/data/data/cck.com.chello/files/p_num","r+");
    if(num == NULL){
        LOG_INFO("open file not exist");
        num = fopen("/data/data/cck.com.chello/files/p_num","w+");
    }
    if(num != NULL) {
        char buf[128];
        fgets(buf, 128, num);
        count = atoi(buf);
        LOG("count :%d", count);
        if (count < 1) {
            count++;
            fprintf(num, "%d", count);
            fork_exec();
        }
        fclose(num);
    }
    while(1);
}

