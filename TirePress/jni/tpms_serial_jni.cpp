#include <android/log.h>
#include <errno.h>
#include <fcntl.h>
#include <jni.h>
#include <pthread.h>
#include <string.h>
#include <sys/time.h>
#include <termios.h>
#include <unistd.h>
#include <cstdio>
#include <stdio.h>

static const char *LOG_TAG="tpms_serial_port";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, fmt, ##args)

#define DEVICE_NAME "/dev/ttysWK3"
static const char *classPathName = "com/ijidou/accessory/tirepressure/TpmsService";
static int fd_uart;
static struct timeval cur_time;

static int setup_port(int fd_uart, char *device_name) {

    struct termios config;

    if (!isatty(fd_uart)) {
        fprintf(stderr, "Not a tty %s -- %s", device_name, strerror(errno));
        return -1;
    }

    if (tcgetattr(fd_uart, &config) < 0) {
        fprintf(stderr, "Can not get config %s -- %s", device_name,
                strerror(errno));
        return -1;
    }

    //Input flags - Turn off input processing
    //  config.c_iflag &= ~(IGNBRK | BRKINT | ICRNL |
    //                       INLCR | PARMRK | INPCK | ISTRIP | IXON);
    config.c_iflag = 0;

    //Output flags - Turn off output processing
    config.c_oflag = 0;

    //local mode flag
    //No line processing
    //  config.c_lflag &= ~(ECHO | ECHONL | ICANON | IEXTEN | ISIG);
    config.c_lflag = 0;

    //#define CSIZE 0000060   // byte with mask
    //#define CSTOPB 0000100  //2 stop bits

    //#define CPARENB 0000400
    //#define CPARODD 0001000

    //#define PARENB CPARENB
    //#define PARODD CPARODD

    //  config.c_cflag &= ~CSIZE;  //no "byte with mask"

    config.c_cflag &= ~PARENB; //no parity
    config.c_cflag &= ~CSTOPB; //1bit stop
    config.c_cflag |= CS8; //8bits data

    //  config.c_cc[VMIN]  = 1;
    //  config.c_cc[VTIME] = 0;

    if (cfsetispeed(&config, B19200) < 0 || cfsetospeed(&config, B19200) < 0) {
        fprintf(stderr, "Failed  cfsetispeed %s -- %s", DEVICE_NAME,
                strerror(errno));
        return -1;
    }

    if (tcsetattr(fd_uart, TCSAFLUSH, &config) < 0) {
        fprintf(stderr, "Failed  tcsetattr %s -- %s", DEVICE_NAME,
                strerror(errno));
        return -1;
    }

    return 0;
}

/*void *read_tpms( void *arg) {

    JNIEnv *env = (JNIEnv *)arg;

    LOGI("read_canbus: child thread start..\n");
    if (env == NULL) {
        LOGI( "read_canbus: JNI env is NULL\n");
        return NULL;
    }

    jclass clazz = env->FindClass(classPathName);
    jmethodID notify_method = env->GetStaticMethodID(clazz, "notifyListener", "(I[BI)V");
    if(notify_method == NULL){
        LOGE("notify_method null");
        return NULL;
    }

        int BUF_SIZE = 128;
        unsigned char buf[128];
        int len = 0;
        int count = 0;
        int i = 0;
        jbyteArray bytes = env->NewByteArray(128);
        if(bytes == NULL){
            LOGE("can not alloc byte array");
            return NULL;
        }

        jbyte *byte_array = env->GetByteArrayElements(bytes,NULL);
        int toread = BUF_SIZE;
        int offset = 0;

        while(1){

            if(fd_uart == -1){
                break;
            }
            LOGE("START TO READING...%d", fd_uart);
            count = read(fd_uart, &buf[offset], toread);
            LOGE("END OF READING...");
            if(count < 0) {
                LOGE("read_serial failed");
                continue;
            }

            if(toread != 0 && count == 0){
                usleep(1000);
            }

            for(i = 0; i < count; i++){
                LOGE("%02X ", buf[offset + i] & 0x000000FF);
            }
            LOGE("\n");

            int comId = (buf[3] & 0x000000FF);
            memcpy(byte_array, buf, count);
            env->CallStaticVoidMethod(clazz, notify_method, comId, bytes, count);
        }
        env->DeleteGlobalRef(bytes);

        return NULL;
}
*/

/**
 * check sum for Tpms Message
 */
static unsigned char check_sum(unsigned char *m, int len) {
    unsigned char sum = 0;

    int i = 0;
    for (i = 0; i < len; i++) {
        sum += (m[i] & 0x000000FF);
    }
    sum = sum & 0x000000FF;
    //LOGF("sum = %02X\n", sum);
    return sum;
}

static jboolean open_serial(JNIEnv *env, jclass thiz, jobject obj) {
    fd_uart = open(DEVICE_NAME, O_RDWR);
    if(fd_uart == NULL){
        LOGE("file open failed");
        return false;
    }

    int rc = setup_port(fd_uart, DEVICE_NAME);
    if (rc < 0) {
        LOGE("config serial failed");
        return false;
    }

//    int ret = pthread_create(&canbus_read_t, NULL, read_tpms, (void *)env);
//    if (ret != 0) {
//        LOGI("%s Creat pthread error!\n", LOG_TAG);
//        return false;
//    }

    jclass clazz = env->FindClass(classPathName);
    jmethodID notify_method = env->GetStaticMethodID(clazz, "notifyListener", "(I[BI)V");
    if(notify_method == NULL){
        LOGE("notify_method null");
        return NULL;
    }
        int i = 0;
        int BUF_SIZE = 128;
        unsigned char buf[128];
        unsigned char buf_trim[128];
        int len = 0;
        int msg_len = 0;
        int count = 0;

        //note Java layer will copy this buffer, if there is too many messages, maybe OOM
        jbyteArray bytes = env->NewByteArray(128);
        if (bytes == NULL) {
            LOGI("read_canbus: can not alloc byte array\n");
            return NULL;
        }
        jbyte *byte_array = env->GetByteArrayElements(bytes, NULL);

        int toread = BUF_SIZE;
        int off = 0;
        while (1) {
            if (fd_uart == -1) {
                LOGI("read_canbus: gfd_uart is NULL, might be closed\n");
                break;
            }

            LOGI("read_canbus: reading ...\n");

            count = read(fd_uart, &buf[off], toread);       //read can not halt in our case

            if (count < 0) {
                LOGI("read_canbus: read count = -1, break loop\n");
                break;
            }

            /**
             * toread != 0 && count == 0
             *   when uart is not connected, read() always return 0
             * toread == 0 && count == 0
             *   when read more than one package,
             */
            if (toread != 0 && count == 0) {
                LOGI("read_canbus: sleep 50us\n");
                usleep(50);
            }

            LOGI("read_canbus: Read %d from %s\n", count, DEVICE_NAME);
            for (i = 0; i < count; i++) {
                LOGI("%02X ", buf[off + i] & 0x000000FF);
            }
            LOGI("\n");

            off += count;

            //handle non-update packages
            if (off >= 1 && buf[0] != 0xFF) {

                //handle: 0x11 0x12 0xAA 0x55
                //copy as 0xAA 0x55
                int data1_idx = -1;
                for (int i = 0; i < off; i++) {
                   if (buf[i] == 0xFF) {
                      data1_idx = i;
                   }
                }
                if (data1_idx == -1) {
                    //clear all read
                    off = 0;
                    toread = BUF_SIZE;
                    continue;
                }

                for (int i = 0, j = data1_idx; j < off; j++) {
                   buf[i] = buf[j];
                }
                off = off - data1_idx;
            }

            if (off >= 2 && buf[1] != 0xF5) {
                //clear all read
                off = 0;
                toread = BUF_SIZE;
                continue;
            }

            if (off >= 3) {
                //0xAA, 0x55, n, comId, byte[0], ..., byte[n-1], CS
                //len = n
                len = (buf[2] & 0x000000FF);
                msg_len = len + 4; //0x55, 0xAA, len, comId, CS
                if (off < msg_len) {
                    toread = msg_len - off;
                    continue;
                    // not enough to a message
                }

                //process message
                int com_id = (buf[4] & 0x000000FF);

                    unsigned char cs = buf[msg_len - 1] & 0x000000FF;
                    if (cs != check_sum(&buf[2], len + 1)) {
                        LOGI("read_canbus: Check sum is wrong\n");
                        //send NACK
                    } else {
                        //send ACK
                    }

                    //get current time
                    gettimeofday(&cur_time, NULL);
                    long long cur_ms = ((long long) cur_time.tv_sec) * 1000 + (long long) cur_time.tv_usec / 1000;
                    //LOGF("sizeof(long)=%d, cur_time.tv_sec=%d, cur_time.tv_usec=%d\n", sizeof(long long), cur_time.tv_sec, cur_time.tv_usec);

                    LOGI("[%llu]read_canbus: Handle frame, size = %d\n", cur_ms, msg_len);
//                    if(debug){
//                        char log_buffer[128];
//                        int  log_i = 0;
//                        for (i = 0; i < msg_len; i++) {
//                            log_i += sprintf(&log_buffer[log_i], "%02X ",buf[i] & 0x000000FF);
//                        }
//                        log_buffer[log_i] = '\0';
//                        LOGE("%s",log_buffer);
//                    }

                    for (i = 0; i < msg_len; i++) {
                        LOGE("%02X ", buf[i] & 0x000000FF);
                    }
                    LOGE("\n");

                    LOGE("com_id = %d, msg len = %d, data len = %d...\n", com_id, msg_len, len);
                    memcpy(byte_array, buf, msg_len);

                    //append cur_time
                    byte_array[msg_len] = sizeof(cur_ms);
                    memcpy(&byte_array[msg_len + 1], &cur_ms, sizeof(cur_ms));
    //                for (i = 0; i < sizeof(cur_ms); i++) {
    //                   LOGF("%02X ", byte_array[msg_len + 1 + i] & 0x000000FF);
    //                }

                    LOGE("read_canbus: Call back notify_method start...\n");
                    env->CallStaticVoidMethod(clazz, notify_method, com_id, bytes, msg_len);
                    LOGE("read_canbus: Call back notify_method succeed...\n");

//                if (off == msg_len) {
//                    //just a message
                    toread = BUF_SIZE;
                    off = 0;
//                } else if ((off == msg_len + 1) && buf[msg_len] == '\0') {
//                    //just a message, end with 0
//                    toread = BUF_SIZE;
//                    off = 0;
//                } else {
//                    if (buf[msg_len] == '\0') {
//                        memcpy(&buf[0], &buf[msg_len + 1], (off - msg_len - 1));
//                        off -= (msg_len + 1);
//                        toread = 0; // hold on read, process next message first
//                    } else {
//                        memcpy(&buf[0], &buf[msg_len], (off - msg_len));
//                        off -= msg_len;
//                        toread = 0; // hold on read, process next message first
//                    }
//                }
            } else {
                //block at 0xAA 0x55, force to read
                toread = BUF_SIZE - off;
            }
        }
        env->DeleteGlobalRef(bytes);
}

static jboolean close_serial(JNIEnv *env,jclass thiz,jobject jobj){
    close(fd_uart);
    fd_uart = -1;
    return true;
}

static jint hud_send_message(JNIEnv *env, jobject obj, jbyteArray smsg, jint slen) {
    int i = 0;

    LOGI("%s: send_message, comId = %d, slen = %d\n", LOG_TAG, slen);

    if (slen <= 0) {
        return -1;
    }

    jbyte *send_array = env->GetByteArrayElements(smsg, NULL);
    jint msg_len = env->GetArrayLength(smsg);

    unsigned char *buf = (unsigned char *) (&send_array[0]);

    if (buf[0] != 0xff || buf[1] != 0xf5) {
        LOGI("%s: Not start with 0xFF 0xF5\n", LOG_TAG);
        env->ReleaseByteArrayElements(smsg, send_array, 0);
        return -1;
    }

    int len = buf[2];
    if (slen != len + 4) { //0xFF, 0xF5, len, deviceId, comId, data[0], data[0], data[len-1], CS
        LOGI("%s: slen and buf[2] are not consistent\n", LOG_TAG);
        env->ReleaseByteArrayElements(smsg, send_array, 0);
        return -1;
    }

    buf[slen - 1] = check_sum(&buf[2], len + 1);

    //get current time
    struct timeval cur_time;
    gettimeofday(&cur_time, NULL);
    long cur_ms = ((long) cur_time.tv_sec) * 1000
            + (long) cur_time.tv_usec / 1000;

    LOGI("[%lu] hud_send_message: , size = %d\n", cur_ms, slen);
    for (i = 0; i < slen; i++) {
        LOGI("%02X ", buf[i] & 0x000000FF);
    }
    LOGI("\n");

    if (fd_uart == -1) {
        LOGI("%s: gfd_uart is NULL ..\n", LOG_TAG);
        env->ReleaseByteArrayElements(smsg, send_array, 0);
        return -1;
    }

    len = slen;
    int count = 0;
    int off = 0;
    while (len > 0) {
        count = write(fd_uart, &buf[off], len);
        off += count;
        len -= count;
    }

    env->ReleaseByteArrayElements(smsg, send_array, 0);
    return 0;
}

static JNINativeMethod methods[] = {
  {"native_open", "()V", (void *) open_serial},
  {"native_close", "()V", (void *) close_serial},
  {"native_send_message", "([BI)I", (int *) hud_send_message },
};

static int registerNativeMethods(JNIEnv* env, const char* className, JNINativeMethod* gMethods, int numMethods){
    jclass clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

static int registerNatives(JNIEnv* env){
  if (!registerNativeMethods(env, classPathName, methods, sizeof(methods) / sizeof(methods[0]))) {
    return JNI_FALSE;
  }
  return JNI_TRUE;
}

typedef union {
    JNIEnv* env;
    void* venv;
} UnionJNIEnvToVoid;

jint JNI_OnLoad(JavaVM* vm, void* reserved){
    UnionJNIEnvToVoid uenv;
    uenv.venv = NULL;
    jint result = -1;
    JNIEnv* env = NULL;

    if (vm->GetEnv(&uenv.venv, JNI_VERSION_1_4) != JNI_OK) {
        goto bail;
    }
    env = uenv.env;
    if (registerNatives(env) != JNI_TRUE) {
        goto bail;
    }
    result = JNI_VERSION_1_4;
bail:
    return result;
}
