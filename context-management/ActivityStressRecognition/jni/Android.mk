LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

# ========================================================
# librxtx
# ========================================================

LOCAL_MODULE_TAGS := eng
LOCAL_MODULE:= librxtxSerial


LOCAL_SRC_FILES := \
	fuserImp.c \
	SerialImp.c

LOCAL_C_INCLUDES += \
	dalvik/libnativehelper/include/nativehelper \
	$(JNI_H_INCLUDE) \
	$(KERNEL_HEADERS) \
	$(LOCAL_PATH)

LOCAL_CFLAGS += \
	 -fPIC

LOCAL_PRELINK_MODULE := false
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_SHARED_LIBRARIES := libdl  liblog

lib018 := external/libusb-0.1.12/Android.mk \
	external/libftdi-0.18/Android.mk
	
lib1 := external/libusb-1.0.8/Android.mk \
	external/libftdi-1.0/Android.mk

include $(lib018)

include $(BUILD_SHARED_LIBRARY)
