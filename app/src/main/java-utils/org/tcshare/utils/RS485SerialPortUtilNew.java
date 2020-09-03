package org.tcshare.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 新版实现 <br/>
 * 程序读发送寄存器，保证发送完成后，再使能接收<br/>
 * 1. 如果内核有gpio的驱动则使用ioctl <br/>
 * 2. 如果内核没有驱动则使用system(echo > 1 /sys/class/gpio/value)来设置
 */
public class RS485SerialPortUtilNew {
    private static final String TAG = RS485SerialPortUtilNew.class.getSimpleName();

    static {
        System.loadLibrary("serialport");
    }

    private RS485SerialPort rs485SerialPort = new RS485SerialPort();
    private WriteReadThread mWriteReadThread;
    private boolean isOpenSerialSuccess;
    private  BlockingQueue<RSCallback> rsBlockingQueue;

    public RS485SerialPortUtilNew() {
        this(Integer.MAX_VALUE);
    }

    public RS485SerialPortUtilNew(int queueSize) {
        rsBlockingQueue = new LinkedBlockingQueue<>(queueSize);
    }

    /**
     * @param band      波特率
     * @param port      串口路径
     * @param rs485Path 使能收发节点
     * @param hasDriver 如果有内核驱动， 请先注销掉对应接口！， 否则会导致打不开； 注销示例：  echo 68 > /sys/class/gpio/unexport
     */
    public void open(int band, @NonNull String port, @NonNull String rs485Path, boolean hasDriver) {
        try {
            // 注意要让APP拥有ROOT 权限
            String cmdPort = "chmod 777 " + port;
            ShellUtils.CommandResult cmdPortResult = ShellUtils.execCommand(cmdPort, true);
            Log.d(TAG, String.format("exe command: %s result %s", cmdPort, cmdPortResult.toString()));

            String cmdEnable = "chmod 777 " + rs485Path;
            ShellUtils.CommandResult cmdEnableResult = ShellUtils.execCommand(cmdEnable, true);
            Log.d(TAG, String.format("exe command: %s result %s", cmdEnable, cmdEnableResult.toString()));

            rs485SerialPort.open(port, rs485Path, band, 0, hasDriver);
            mWriteReadThread = new WriteReadThread();
            mWriteReadThread.start();
            isOpenSerialSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "打开串口时，异常");
            isOpenSerialSuccess = false;
        } finally {
        }
    }

    public boolean isOpenSerialSuccess() {
        return isOpenSerialSuccess;
    }


    public void destroy() {
        try {
            if (mWriteReadThread != null) {
                mWriteReadThread.interrupt();
            }
            if (rs485SerialPort != null) {
                rs485SerialPort.close();
                rs485SerialPort = null;
            }
            rsBlockingQueue.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param cb
     */
    public void sendData(@NonNull final RSCallback cb) {
        sendData(false, cb);
    }

    /**
     * @param cb
     */
    public void sendDataInsert(@NonNull final RSCallback cb) {
        sendData(true, cb);
    }

    /**
     * @param cb
     */
    private void sendData(boolean insert, @NonNull final RSCallback cb) {
        try {
            if (insert) {// 插队
                List<RSCallback> contains = new ArrayList<>();
                rsBlockingQueue.drainTo(contains);
                rsBlockingQueue.put(cb);
                rsBlockingQueue.addAll(contains);
            } else {
                rsBlockingQueue.put(cb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isQueueFull() {
        return rsBlockingQueue.remainingCapacity()  == 0;
    }


    private class WriteReadThread extends Thread {

        public WriteReadThread() {
            android.os.Process.setThreadPriority(-19);//priority：【-20, 19】，高优先级 -> 低优先级
            //setPriority (10); //priority：【1, 10】，低优先级 -> 高优先级。
        }


        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    RSCallback obj = rsBlockingQueue.take();
                    byte[] recBytes = rs485SerialPort.send(obj.sendBytes, obj.recBufLen, obj.waitTime);
                    obj.onReceiveFinish(recBytes);
                } catch (Exception e) {
                    e.printStackTrace();
                    isOpenSerialSuccess = false;
                    return;
                }
            }
        }
    }

    public static abstract class RSCallback {
        /**
         * 需要发送的数据
         */
        public byte[] sendBytes;
        /**
         * 接收缓冲区的长度
         */
        public int recBufLen;
        /**
         * 读取超时时间 ms
         */
        public int waitTime;

        public RSCallback(@NonNull byte[] sendData, int recBufLen, int waitTime) {
            this.sendBytes = sendData;
            this.recBufLen = recBufLen;
            this.waitTime = waitTime;
        }

        /**
         * @param recBytes may be null
         */
        public abstract void onReceiveFinish(byte[] recBytes);
    }
}
