#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include "httpUtils.h"
#include "../common_macro.h"
#include <curl.h>
#include "cJSON.h"


static const char *baseUrl = "http://m.so.com/suggest/mso?kw=";
static const char *http_test_class = "cck/com/chello/HttpTest";
static const char *java_call_class = "cck/com/chello/JavaCall";
static const char *final_java_call_class = "cck/com/chello/FinalJavaCall";

JNIEXPORT void init(JNIEnv *,jobject,jstring);
JNIEXPORT void testCurl(JNIEnv *,jobject, jstring);
JNIEXPORT void test_url_copy(const char *);
JNIEXPORT void test_array();
JNIEXPORT void test_memory();
JNIEXPORT void test_free(JNIEnv *);
JNIEXPORT void test_global_ref(JNIEnv *,jobject);
JNIEXPORT void test_free_global_ref(JNIEnv *);
JNIEXPORT void test_call_java(JNIEnv *);
JNIEXPORT void test_httpclient(JNIEnv *,jobject,jstring);

size_t writeFuc(const void *ptr,size_t size,size_t nmemb,FILE *stream);

static jobject global_java_call_ref;

const static JNINativeMethod sMethods[] = {
        {"testCurl","(Ljava/lang/String;)V",(void*)testCurl},
        {"testArray","()V",(void*)test_array},
        {"testNativeMemory","()V",(void*)test_memory},
        {"testFreeNativeMemory","()V",(void*)test_free},
        {"testGlobalRef","()V",(void*)test_global_ref},
        {"testFreeGlobalRef","()V",(void*)test_free_global_ref},
        {"testJavaCall","()V",(void*)test_call_java},
        {"testHttpClient","(Ljava/lang/String;)V",(void*)test_httpclient},
        {"init","(Ljava/lang/String;)V",(void*)init},
};


typedef struct {
    char *ptr;
    size_t len;
} string;

void init_string(string *s) {
    s->len = 0;
    s->ptr = malloc(s->len+1);
    if (s->ptr == NULL) {
        fprintf(stderr, "malloc() failed\n");
        exit(EXIT_FAILURE);
    }
    s->ptr[0] = '\0';
}

size_t writefunc(void *ptr, size_t size, size_t nmemb,  string *s)
{
    size_t new_len = s->len + size*nmemb;
    s->ptr = realloc(s->ptr, new_len+1);
    if (s->ptr == NULL) {
        fprintf(stderr, "realloc() failed\n");
        exit(EXIT_FAILURE);
    }
    memcpy(s->ptr+s->len, ptr, size*nmemb);
    s->ptr[new_len] = '\0';
    s->len = new_len;
    return size*nmemb;
}

size_t writeFuc(const void *ptr, size_t size, size_t nmemb, FILE *stream) {
    char buffer[128];
    FILE *tmp = fopen("/sdcard/temp.txt","w+");
    if(tmp == 0) {
        LOG("%s","open file failed");
        return 0;
    }
    return fwrite(ptr,size,nmemb,tmp);
}

void joinUrl(char **url, const char *baseUrl, const char *keyword) {
    *url= realloc(*url,strlen(baseUrl)+strlen(keyword)+1);
    strcpy(*url,baseUrl);
    strcat(*url,keyword);
}

char *test[] = {"aaaa","dccc11","11212"};

static int *big_mem = NULL;
void test_memory() {
    LOG_INFO("test native memory");
    if(big_mem != NULL)free(big_mem);
    big_mem = (int *) malloc(sizeof(int) * 2 * 1024 * 1024);
    LOG("size : %d",(int) sizeof(big_mem));
}

void test_free(JNIEnv *env) {
    if(big_mem) {
        free(big_mem);
        big_mem = NULL;
    }
    if(global_java_call_ref != NULL){
        jobject  clz = (*env)->FindClass(env,http_test_class);
        if(clz) {
            jmethodID  m_id = (*env)->GetMethodID(env,clz,"emptyMethod","()V");
            if(m_id) {
                (*env)->CallVoidMethod(env,global_java_call_ref,m_id);
            }
        }
    }
}



void test_array() {
    int count = NELEM(test);
    char **dest = malloc(sizeof(char*) *count);
    for(int i =0;i<count;i++) {
        char *source = test[i];
        dest[i] = malloc(strlen(source)+1);
        strcpy(dest[i],source);
    }
    //do something
    for(int i =0;i<count;i++) {
        LOG("%s",dest[i]);
    }
    //free
    for(int i=0;i<count;i++) free(dest[i]);
    free(dest);
}

void test_url_copy(const char *key) {
    char *url = malloc(0);
    joinUrl(&url, "hello","world");
    LOG("%s",url);
    free(url);
}


void testCurl(JNIEnv *env, jobject thiz,jstring keywords) {
    CURL *curl;
    CURLcode res;
    const char* key = (*env)->GetStringUTFChars(env,keywords,0);
    FILE *file = fopen("/sdcard/temp.txt","w+");
    if(file == NULL) {
        LOG("%s","open file fail");
        return;
    }

    curl = curl_easy_init();
    if(curl) {
        string s;
        init_string(&s);
        char *url = malloc(0);

        joinUrl(&url,baseUrl,key);

        LOG("%s",url);
        curl_easy_setopt(curl, CURLOPT_URL, url);
        /* example.com is redirected, so we tell libcurl to follow redirection */
        curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 1L);
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION,writefunc);
        curl_easy_setopt(curl,CURLOPT_WRITEDATA,&s);
//        curl_easy_setopt(curl,CURLOPT_WRITEDATA,file);
        /* Perform the request, res will get the return code */
        res = curl_easy_perform(curl);
        /* Check for errors */
        if(res == CURLE_OK) {
            LOG("%s\n",s.ptr);
            int err_num = -1;
            char *query = NULL;
            char *test_q_ref = "";
            char **sug_list = NULL;
            int sug_size = 0;

            cJSON *root = cJSON_Parse(s.ptr);
            cJSON *err_item = cJSON_GetObjectItemCaseSensitive(root,"errno");
            if(cJSON_IsNumber(err_item)) {
                err_num = err_item->valueint;
            }


            if(err_num == 0) {
                cJSON *data = cJSON_GetObjectItem(root,"data");
                if(data != NULL && cJSON_HasObjectItem(data,"query")) {
                    cJSON *query_itemt = cJSON_GetObjectItem(data,"query");
                    test_q_ref = query_itemt->valuestring;
                    query = malloc(sizeof(char) * strlen(query_itemt->valuestring)+1);
                    strcpy(query,query_itemt->valuestring);
                }
                if(data != NULL && cJSON_HasObjectItem(data,"sug")) {
                    cJSON *sug_array = cJSON_GetObjectItem(data,"sug");
                    int  size = cJSON_GetArraySize(sug_array);
                    sug_size = size;
                    if(size > 0) {
                        sug_list = malloc(sizeof(char*) * size);
                        for(int i = 0;i<size;i++) {
                            cJSON *sug_item = cJSON_GetArrayItem(sug_array,i);
                            if(sug_item != NULL) {
                                cJSON *word_item = cJSON_GetObjectItem(sug_item,"word");
                                //copy string;
                                sug_list[i] = malloc(strlen(word_item->valuestring)+1);
                                strcpy(sug_list[i],word_item->valuestring);
                            } else {
                                sug_list[i] = NULL;
                            }
                        }
                    }
                }

            }else{
                LOG("errno:%d",err_num);
            }
            cJSON_Delete(root);

            LOG("ref a invalid char : %s",test_q_ref);

            LOG("errno:%d",err_num);
            if(query != NULL) LOG("query:%s",query);
            for(int i=0;i<sug_size;i++) {
                LOG("word:%s\n",sug_list[i]);
            }
            if(query != NULL) free(query);
            for(int i = 0;i<sug_size;i++) {
                if(sug_list != NULL) {
                    if(sug_list[i]) free(sug_list[i]);
                }
            }

            if(sug_list != NULL) free(sug_list);
        }else{
            LOG("%s\n", curl_easy_strerror(res));
        }

        free(s.ptr);
        free(url);
        /* always cleanup */
        curl_easy_cleanup(curl);
    }
    (*env)->ReleaseStringUTFChars(env,keywords,key);
    fclose(file);
}



void test_global_ref(JNIEnv *env, jobject thiz) {
    jobject clz = (*env)->FindClass(env,http_test_class);
    if(clz) {
        jmethodID get_java_call_method = (*env)->GetMethodID(env,clz,"getCallObj","()Lcck/com/chello/FinalJavaCall;");
        if(get_java_call_method){
            jobject java_call_obj = (*env)->CallObjectMethod(env,thiz,get_java_call_method);
            if(java_call_obj) {
                test_free_global_ref(env);//delete exist ref;
                global_java_call_ref = (*env)->NewGlobalRef(env,java_call_obj);
                LOG_INFO("create new global java ref");
            }
        }
    }
}

void test_free_global_ref(JNIEnv *env) {
    if(global_java_call_ref) {
        (*env)->DeleteGlobalRef(env,global_java_call_ref);
        global_java_call_ref = NULL;
    }
}

void test_call_java(JNIEnv *env) {
    LOG_INFO("call java method from native");
    if(!global_java_call_ref) {
        LOG_INFO("didn't have JavaCall ref!");
        return ;
    }
    jclass clz = (*env)->FindClass(env,java_call_class);
    jclass real_clz = (*env)->FindClass(env,final_java_call_class);
    if(clz && real_clz) {
        /*
        jmethodID  m_id = (*env)->GetMethodID(env,clz,"setCurrentCount","(I)V");
        if(m_id){
            (*env)->CallVoidMethod(env,global_java_call_ref,m_id,10);
        }
        */

        /*
        jfieldID  f_id = (*env)->GetFieldID(env,clz,"count","I");
        if(f_id) {
            (*env)->SetIntField(env,global_java_call_ref,f_id,11);
        }
        */

        /* different between Call<Type>Method and CallNonvirtual<Type>Method;
        jmethodID  m_id = (*env)->GetMethodID(env,clz,"setCount","(I)V");
        jmethodID  m_id_from_real = (*env)->GetMethodID(env,real_clz,"setCount","(I)V");
        if(m_id) {
            (*env)->CallVoidMethod(env,global_java_call_ref,m_id_from_real,12);
            //class and methodId MUST from real object;
            (*env)->CallNonvirtualVoidMethod(env,global_java_call_ref,real_clz,m_id_from_real,15);
        }
        */
        jmethodID  m_id = (*env)->GetMethodID(env,clz,"setCC","(I)V");
        jmethodID  m_id_from_real = (*env)->GetMethodID(env,real_clz,"setCC","(I)V");
        if(m_id) {
            //this will always call sub class method,whatever methodId is from sub or super;
//            (*env)->CallVoidMethod(env,global_java_call_ref,m_id,12);
            //class and methodId MUST from real object;
            (*env)->CallNonvirtualVoidMethod(env,global_java_call_ref,clz,m_id,15);
//            va_list va_lista = {10};
//            jvalue inta;
//            inta.i = 12;
//            jvalue params[] = {inta};
////            (*env)->CallVoidMethodV(env,global_java_call_ref,m_id,va_lista);
//            (*env)->CallVoidMethod(env,global_java_call_ref,m_id,11);
//            (*env)->CallVoidMethodA(env,global_java_call_ref,m_id,params);
        }

    }

}

void simple_callback(int http_status, Status status, cJSON *data) {
    LOG("status:%d",http_status);
    if(status == SUCCESS) {
        cJSON *errnum =  cJSON_GetObjectItem(data,"errno");
        if(cJSON_IsNumber(errnum)){
            LOG("errno:%d",errnum->valueint);
        }
    }
}

void init(JNIEnv *env,jobject clazz,jstring base_url) {
    const char * base = (*env)->GetStringUTFChars(env,base_url,NULL);
    init_httpclient(base);
    (*env)->ReleaseStringUTFChars(env,base_url,base);
}

void test_httpclient(JNIEnv *env,jobject thiz,jstring url) {
    const char *r_url = (*env)->GetStringUTFChars(env,url,NULL);
    get(r_url,simple_callback);
    (*env)->ReleaseStringUTFChars(env,url,r_url);
}


jint JNI_OnLoad(JavaVM *vm, void* reserve) {
    JNIEnv *env;
    jint result = (*vm)->GetEnv(vm, (void**)&env,JNI_VERSION_1_6);
    if(result != JNI_OK) {
        return JNI_ERR;
    }

    jclass clazz;
    if((clazz = (*env)->FindClass(env,"cck/com/chello/HttpTest")) == NULL) {
        return JNI_ERR;
    }

    jint r = (*env)->RegisterNatives(env,clazz,sMethods,NELEM(sMethods));
    if(r != JNI_OK) {
        return JNI_ERR;
    }
    return JNI_VERSION_1_4;
}