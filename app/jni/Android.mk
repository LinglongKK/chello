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
LOCAL_SRC_FILES := $(LOCAL_PATH)/curl/http_client.c
LOCAL_LDLIBS += -llog -lz
LOCAL_CFLAGS += -std=c99
LOCAL_STATIC_LIBRARIES := curl
include $(BUILD_SHARED_LIBRARY)
