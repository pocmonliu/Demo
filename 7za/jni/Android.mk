LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := 7za_bin
LOCAL_SRC_FILES := 7za_bin.cpp
LOCAL_MODULE_TAGS := optional

include $(BUILD_EXECUTABLE)
