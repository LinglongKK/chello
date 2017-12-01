//
// Created by chenlong on 17-12-1.
//

#ifndef CHELLO_TYPE_TEST_H
#define CHELLO_TYPE_TEST_H
#include <jni.h>
#define JAVA_CLASS_NAME  "cck/com/chello/typetest/TypeTest"

JNIEXPORT jint get_int_value(JNIEnv*,jobject,jint);
JNIEXPORT jboolean get_boolean_value(JNIEnv*,jobject,jboolean);
JNIEXPORT jbyte  get_byte_value(JNIEnv*,jobject ,jbyte);
JNIEXPORT jshort get_short_value(JNIEnv*,jobject ,jshort);
JNIEXPORT jchar get_char_value(JNIEnv*,jobject ,jchar);
JNIEXPORT jdouble get_double_value(JNIEnv*,jobject ,jdouble);
JNIEXPORT jfloat get_float_value(JNIEnv*,jobject ,jfloat);
JNIEXPORT jlong get_long_value(JNIEnv*,jobject ,jlong);
JNIEXPORT void set_instance(JNIEnv*,jobject);
JNIEXPORT void log_native(JNIEnv*,jclass,jstring,jstring);
#endif //CHELLO_TYPE_TEST_H
