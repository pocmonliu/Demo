LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

TARGET_PLATFORM := android-19
LOCAL_MODULE    := tpms_serial_jni
LOCAL_SRC_FILES := tpms_serial_jni.cpp
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)
