#libhello.so
LOCAL_PATH := $(call my-dir)
include $(CLER_VARS)
LOCAL_MODULE          := hello
LOCAL_SRC_FILES       := nativeLib.c  \
                         duktape.c
LOCAL_MODULE_FILENAME := libhello
LOCAL_CFLAGS          += -std=c99
LOCAL_LDLIBS          += -llog
include $(BUILD_SHARED_LIBRARY)

#libtypeTest.so
include $(CLER_VARS)
LOCAL_MODULE := typeTest
LOCAL_MODULE_FILENAME :=libtypeTest
LOCAL_SRC_FILES := type_test.c
LOCAL_LDLIBS += -llog
LOCAL_CFLAGS += -std=c99
include $(BUILD_SHARED_LIBRARY)
