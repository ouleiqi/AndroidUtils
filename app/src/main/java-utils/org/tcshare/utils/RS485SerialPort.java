package org.tcshare.utils;

/**
 * 485串口通信
 */
public class RS485SerialPort {


    /**
     * 注意使用system命令会有延时，需要设置从设备的延时时间， （CPU越快计时越精确）
     *
     * @param devPath 串口设备树
     * @param enableIO 使能节点，示例：无内核驱动， echo (1|0) > /sys/class/gpioXXX/value 有内核驱动 /dev/gpioXXX
     * @param baudRate 波特率
     * @param flags 打开标识
     * @param hasDriver 是否同时修改了内核驱动，无 false，
     * @return
     */
    public native int open(String devPath, String enableIO, int baudRate, int flags, boolean hasDriver);

    /**
     * 向串口发送数据
     * @param sendArray 发送内容
     * @param revBufSize 接收缓冲区
     * @param readWaitTime 读等待超时时间 ms
     * @return 接收到的字节 或 null
     */
    public native byte[] send(byte[] sendArray, int revBufSize, int readWaitTime);

    /**
     * 关闭
     */
    public native void close();

}
