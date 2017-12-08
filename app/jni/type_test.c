//
// Created by chenlong on 17-12-1.
//

#include <jni.h>
#include <android/log.h>
#include <stdbool.h>
#include "type_test.h"
#include "common_macro.h"

static const JNINativeMethod gRegisterMethod[] = {
        //基本数据类型
        {"getIntValue",     "(I)I", (void *) get_int_value},
        {"getBooleanValue", "(Z)Z", (void *) get_boolean_value},
        {"getByteValue",    "(B)B", (void *) get_byte_value},
        {"getShortValue",   "(S)S", (void *) get_short_value},
        {"getCharValue",    "(C)C", (void *) get_char_value},
        {"getDoubleValue",  "(D)D", (void *) get_double_value},
        {"getFloatValue",   "(F)F", (void *) get_float_value},
        {"getLongValue",    "(J)J", (void *) get_long_value},
        {"setInstance",     "()V",  (void *) set_instance},
        {"logNative",       "(Ljava/lang/String;Ljava/lang/String;)V",  (void *) log_native},

};


JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOG("%s", "jni_onload");
    JNIEnv *env;
    //获取env
    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return JNI_ERR;
    }

    jclass clazz;
    if((clazz = (*env)->FindClass(env,JAVA_CLASS_NAME)) == NULL) {
        LOG("%s is NULL",JAVA_CLASS_NAME);
        return JNI_ERR;
    }

    jint rs = (*env)->RegisterNatives(env, clazz, gRegisterMethod, NELEM(gRegisterMethod));
    if (rs < 0) return JNI_ERR;
    return JNI_VERSION_1_4;
}


void log_native(JNIEnv* env,jclass class,jstring format,jstring msg) {
    jboolean isCopy;
    LOG("%d",isCopy);
    LOG("%p",(void*)&isCopy);
    const char* formatchar = (*env)->GetStringUTFChars(env,format, &isCopy);
    LOG("%d",isCopy);
    const char* msgChar = (*env)->GetStringUTFChars(env,msg, &isCopy);
    LOG(formatchar,msgChar);
    (*env)->ReleaseStringUTFChars(env,format,formatchar);
    (*env)->ReleaseStringUTFChars(env,msg,msgChar);
}

void set_instance(JNIEnv* env,jobject obj) {
    LOG("%p",obj);
    jclass clz = (*env)->FindClass(env,"com/not/found/class");
    if((*env)->ExceptionCheck(env)) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
        (*env)->ThrowNew(env,(*env)->FindClass(env,"cck/com/chello/JNIException"),"JNI error!!!!");
        return ;
    }

}

jint get_int_value(JNIEnv *env, jobject thiz, jint a) {
    LOG("%d", a);
    jint version = (*env)->GetVersion(env);
    LOG("%X",version);
    return version;
}

jboolean get_boolean_value(JNIEnv *env, jobject thiz, jboolean b) {
    LOG("%d", b);
    return b ? false : true;
}

jbyte get_byte_value(JNIEnv *env, jobject thiz, jbyte b) {
    LOG("%d", b);
    return 1;
}

jshort get_short_value(JNIEnv *env, jobject thiz, jshort j) {
    LOG("%d", j);
    return 10;
}

jchar get_char_value(JNIEnv *env, jobject thiz, jchar c) {
    LOG("%c", c);
    return 'A';
}

jdouble get_double_value(JNIEnv *env, jobject thiz, jdouble d) {
    LOG("%f", d);
    LOG("%p",thiz);
    return 3.4;
}

jfloat get_float_value(JNIEnv *env, jobject thiz, jfloat f) {
    LOG("%f", f);
    return 3.5;
}

jlong get_long_value(JNIEnv *env, jobject thiz, jlong i) {
    LOG("%lld", i);
    return 1000L;
}
