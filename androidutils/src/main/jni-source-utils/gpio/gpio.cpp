#include <jni.h>
#include <string>

#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <stdlib.h>
#include "android/log.h"

static const char *TAG="gpio";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)
#define RS485_SEND "echo 1 > /sys/class/misc/io_ctl/gpio_state"
#define RS485_RECEIVE "echo 0 > /sys/class/misc/io_ctl/gpio_state"
