//
// Created by oulei on 2020/8/26.
//

#ifndef ESTOOLCABIN_RS485SERIAL_H
#define ESTOOLCABIN_RS485SERIAL_H

#include <sys/types.h>
#include <asm/termbits.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL
Java_org_tcshare_utils_RS485SerialPort_open(JNIEnv *env, jobject type, jstring devPath,
                                            jstring enableIO, jint baudRate, jint flags, jboolean hasDriver);
JNIEXPORT void JNICALL
Java_org_tcshare_utils_RS485SerialPort_close(JNIEnv *env, jobject type);

JNIEXPORT jbyteArray JNICALL
Java_org_tcshare_utils_RS485SerialPort_send(JNIEnv *env, jobject type, jbyteArray sendArray,
                                            jint revBufSize, jint readWaitTime);

int setSpeed(int fd, speed_t speed);
int setParity(int fd, int databits, int stopbits, int parity);

int rsWrite(int fd, char *buf, int dataLen);
int rsRead(int fd, char *buf, int MaxLen, int waitTime);
int sendWaitRecv(int fd, char *sendBuf, int sendLen, char *revBuf, int readMaxLen, int readWaitTime);


char *jByteArrayToChar(JNIEnv *env, jbyteArray buf);
jbyteArray charToJByteArray(JNIEnv *env, char *buf, int len);

#ifdef __cplusplus
}
#endif

#endif //ESTOOLCABIN_RS485SERIAL_H
