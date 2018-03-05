
#include <jni.h>
#include <android/log.h>
#include <string.h>
#include <stdbool.h>
#include "common_macro.h"


JNIEXPORT jboolean replace_method(JNIEnv*,jobject,jobject,jobject);
JNIEXPORT jint get_art_method_size(JNIEnv*,jobject);

static const char* HOTFIX_JAVA_CLASS_NAME = "cck/com/chello/hotfix/Hotfix";
static const char* METHOD_SIZE_JAVA_CLASS_NAME = "cck/com/chello/hotfix/MethodSize";
static const JNINativeMethod gRegisterMethod[] = {
        {"replaceMethod","(Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)Z",(void*)replace_method},
        {"getArtMethodSize","()I",(void*)get_art_method_size},
};
static size_t global_art_method_size = 0;

jboolean replace_method(JNIEnv *env, jobject thiz,jobject origin,jobject replace) {
    if(global_art_method_size == 0) {
        LOG_INFO("ArtMethod is Zero,Abort replace");
        return false;
    }
    void* origin_id = (*env)->FromReflectedMethod(env,origin);
    if(origin_id == NULL) {
        LOG_INFO("origin method id is NULL");
        return false;
    }
    void* replace_id = (*env)->FromReflectedMethod(env,replace);
    if(replace_id == NULL) {
        LOG_INFO("replace method id is NULL");
        return false;
    }
    memcpy(origin_id,replace_id,global_art_method_size);
    return true;
}

jint get_art_method_size(JNIEnv *env, jobject thiz){
    return global_art_method_size;
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOG("%s", "jni_onload");
    JNIEnv *env;
    //获取env
    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return JNI_ERR;
    }

    jclass hotClazz;
    if((hotClazz = (*env)->FindClass(env,HOTFIX_JAVA_CLASS_NAME)) == NULL) {
        LOG("%s is NULL",HOTFIX_JAVA_CLASS_NAME);
        return JNI_ERR;
    }
    jint rs = (*env)->RegisterNatives(env, hotClazz, gRegisterMethod, NELEM(gRegisterMethod));
    if (rs < 0) return JNI_ERR;


    jclass  method_size_class;
    if((method_size_class = (*env)->FindClass(env,METHOD_SIZE_JAVA_CLASS_NAME)) == NULL) {
        LOG("%s is NULL",METHOD_SIZE_JAVA_CLASS_NAME);
        return JNI_ERR;
    }

    void* first = (*env)->GetStaticMethodID(env,method_size_class,"firstMethod","()V");
    void* second = (*env)->GetStaticMethodID(env,method_size_class,"secondMethod","()V");
    if(first == NULL) {
        LOG_INFO("first method is NULL");
        return JNI_ERR;
    }
    if(second == NULL){
        LOG_INFO("second method is NULL");
        return JNI_ERR;
    }
    global_art_method_size = second - first;
    LOG("ArtMethod Size:%d",global_art_method_size);
    return JNI_VERSION_1_4;
}