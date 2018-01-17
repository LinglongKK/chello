//
// Created by chenlong on 17-12-1.
//

#ifndef CHELLO_COMMON_MACRO_H
#define CHELLO_COMMON_MACRO_H

#ifndef NELEM
#define NELEM(x) ((int)(sizeof(x)/sizeof((x)[0])))
#endif

#ifndef LOG
#include <android/log.h>
#define LOG(FORMAT, MSG) __android_log_print(ANDROID_LOG_INFO,"chenlong",(FORMAT),(MSG))
#define LOG_INFO(MSG) __android_log_print(ANDROID_LOG_INFO,"chenlong","%s",(MSG))
#endif

#endif //CHELLO_COMMON_MACRO_H
