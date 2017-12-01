#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include "duktape.h"

#ifndef LOG
#define LOG(msg) __android_log_print(ANDROID_LOG_INFO,"chenlong","[c]:%s",msg)
#endif

#ifndef NELEM
#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif


jint init(JNIEnv*,jobject,jobject);
void uninit(JNIEnv*,jobject);
jstring hello(JNIEnv*, jobject);
jint callJs(JNIEnv*, jobject, jstring);
void func1(int);

typedef void (*Func1Type)(int);
static jobject instance;
static const char* str = "aaa";
static const char* className = "cck/com/chello/MainActivity";
static Func1Type gFunc1;
static duk_context *gJsContext;
static const JNINativeMethod methods[] = {
        {"hello","()Ljava/lang/String;",(jstring*)hello},
        {"init","(Ljava/lang/Object;)V",(jint*)init},
        {"callJs","(Ljava/lang/String;)I",(jint*)callJs},
        {"uninit","()V",(void*)uninit},
};

JNIEXPORT void func1(int a ) {
    char temp[128];
    sprintf(temp,"%d",a);
    LOG(temp);
}

JNIEXPORT jstring hello(JNIEnv* env, jobject jobj) {
    LOG("call hello");
    if(instance == jobj) LOG("same instance");
    if(gFunc1 != NULL) gFunc1(10);

    return (*env)->NewStringUTF(env,str);
}

JNIEXPORT jint callJs(JNIEnv *env, jobject thiz, jstring javascript){
    if(gJsContext == NULL) {
        LOG("js context is NULL");
        return -1;
    }
    jboolean jsCopy;
    const char *js = (*env)->GetStringUTFChars(env,javascript, &jsCopy);
    duk_eval_string(gJsContext,js);
    int result = (int) duk_get_int(gJsContext,-1);
    return result;
}

JNIEXPORT jint JNICALL init(JNIEnv* env,jobject thiz, jobject ins) {
    LOG("call init");
    instance = ins;
    gFunc1 = &func1;
    gJsContext = duk_create_heap_default();
    if(gJsContext == NULL) {
        LOG("init fail!");
    }

    return 0;
}

JNIEXPORT void uninit(JNIEnv* env, jobject thiz) {
    LOG("uninit");
    if(gJsContext != NULL) duk_destroy_heap(gJsContext);
}

jint JNI_OnLoad(JavaVM *vm, void* reserved) {
    LOG("jni_onload");
    JNIEnv *env = NULL;
    if((*vm)->GetEnv(vm, (void **)&env,JNI_VERSION_1_4) != JNI_OK) {
        return JNI_ERR;
    }
    jclass clazz = (*env)->FindClass(env,className);
    if(clazz == NULL) {
        return JNI_ERR;
    }
    if((*env)->RegisterNatives(env, clazz,methods, NELEM(methods)) < 0) {
        return JNI_ERR;
    }
    return  JNI_VERSION_1_4;
}