#include <jni.h>
#include "../common_macro.h"
#include <curl.h>



JNIEXPORT void testCurl();

const static JNINativeMethod sMethods[] = {
        {"testCurl","()V",(void*)testCurl},
};

void testCurl() {
    CURL *curl;
    CURLcode res;

    curl = curl_easy_init();
    if(curl) {
        curl_easy_setopt(curl, CURLOPT_URL, "http://www.baidu.com");
        /* example.com is redirected, so we tell libcurl to follow redirection */
        curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 1L);

        /* Perform the request, res will get the return code */
        res = curl_easy_perform(curl);
        /* Check for errors */
        if(res != CURLE_OK) {
            LOG("%s\n", curl_easy_strerror(res));
        }else{
            LOG("%d",res);
        }

        /* always cleanup */
        curl_easy_cleanup(curl);
    }
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

    jint r = (*env)->RegisterNatives(env,clazz,sMethods,1);
    if(r != JNI_OK) {
        return JNI_ERR;
    }
    return JNI_VERSION_1_4;
}