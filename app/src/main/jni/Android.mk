LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := printVersion
LOCAL_SRC_FILES := jniMain.cpp
LOCAL_LDLIbS := -llog

include $(BUILD_SHARED_LIBRARY)