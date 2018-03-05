LOCAL_PATH := $(call my-dir)

#libhello.so
include $(CLEAR_VARS)
LOCAL_MODULE          := hello
LOCAL_SRC_FILES       := nativeLib.c  \
                         duktape.c
LOCAL_CFLAGS          += -std=c99
LOCAL_LDLIBS          += -llog
include $(BUILD_SHARED_LIBRARY)


#libtypeTest.so
include $(CLEAR_VARS)
LOCAL_MODULE := typeTest
LOCAL_SRC_FILES := type_test.c
LOCAL_LDLIBS += -llog
LOCAL_CFLAGS += -std=c99
include $(BUILD_SHARED_LIBRARY)

#prebuilt curl
include $(CLEAR_VARS)
LOCAL_MODULE := curl
LOCAL_SRC_FILES := $(LOCAL_PATH)/curl/libcurl.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/curl/include
include $(PREBUILT_STATIC_LIBRARY)

#libhttpUtil.so
include $(CLEAR_VARS)
LOCAL_MODULE := httpUtil
LOCAL_SRC_FILES := curl/http_client.c \
                   curl/cJSON.c       \
                   curl/cJSON_Utils.c \
                   curl/httpUtils.c   \
                   curl/thpool.c
LOCAL_LDLIBS += -llog -lz
LOCAL_CFLAGS += -std=c99
LOCAL_STATIC_LIBRARIES := curl
include $(BUILD_SHARED_LIBRARY)

#process_ex
include $(CLEAR_VARS)
LOCAL_MODULE := process_ex
LOCAL_MODULE_FILENAME := process_ex
LOCAL_SRC_FILES := process/main.c
LOCAL_LDLIBS += -llog
LOCAL_CFLAGS += -fPIE -fPIC
LOCAL_LDFLAGS += -pie
include $(BUILD_EXECUTABLE)

#andfix
include $(CLEAR_VARS)
LOCAL_MODULE := hotfix
LOCAL_SRC_FILES := hotfix/andfix.c
LOCAL_LDLIBS += -llog
include $(BUILD_SHARED_LIBRARY)
