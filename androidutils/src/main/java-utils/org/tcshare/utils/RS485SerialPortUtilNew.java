package org.tcshare.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * 新版实现 <br/>
 * 程序读发送寄存器，保证发送完成后，再使能接收<br/>
 * 1. 如果内核有gpio的驱动则使用ioctl <br/>
 * 2. 如果内核没有驱动则使用system(echo > 1 /sys/class/gpio/value)来设置
 * 3. 如果带有自动转换的芯片，则使能路径传null
 */
public class RS485SerialPortUtilNew {
    private static final String TAG = RS485SerialPortUtilNew.class.getSimpleName();
    private static final boolean DEBUG = false;

    static {
        System.loadLibrary("serialport");
    }

    private RS485SerialPort rs485SerialPort = new RS485SerialPort();
    private WriteReadThread mWriteReadThread;
    private boolean isOpenSerialSuccess;
    private LinkedBlockingQueue<RSCallback> rsBlockingQueue;
    private long waitSlaverTime = 100; // 使用等待从机模式， 从机释放总线可能比较慢, ms
    private boolean sending = false;

    public RS485SerialPortUtilNew() {
        this(Integer.MAX_VALUE, 100);
    }

    public RS485SerialPortUtilNew(int queueSize, long waitSlaverTime) {
        rsBlockingQueue = new LinkedBlockingQueue<>(queueSize);
        this.waitSlaverTime = waitSlaverTime;
    }

    public RS485SerialPortUtilNew(long waitSlaverTime) {
        this(Integer.MAX_VALUE,waitSlaverTime);
    }


    /**
     * @param band      波特率
     * @param port      串口路径
     * @param rs485Path 使能收发节点
     * @param hasDriver 如果有内核驱动， 请先注销掉对应接口！， 否则会导致打不开； 注销示例：  echo 68 > /sys/class/gpio/unexport
     */
    public void open(int band, @NonNull String port, String rs485Path, boolean hasDriver) {
        try {
            // 注意要让APP拥有ROOT 权限
            String cmdPort = "chmod 777 " + port;
            ShellUtils.CommandResult cmdPortResult = ShellUtils.execCommand(cmdPort, true);
            Log.d(TAG, String.format("exe command: %s result %s", cmdPort, cmdPortResult.toString()));

            if(rs485Path != null) {
                String cmdEnable = "chmod 777 " + rs485Path;
                ShellUtils.CommandResult cmdEnableResult = ShellUtils.execCommand(cmdEnable, true);
                Log.d(TAG, String.format("exe command: %s result %s", cmdEnable, cmdEnableResult.toString()));
            }

            int result = rs485SerialPort.open(port, rs485Path, band, 0, hasDriver);
            mWriteReadThread = new WriteReadThread();
            mWriteReadThread.start();
            if(result != -1) {
                isOpenSerialSuccess = true;
            }else{
                isOpenSerialSuccess = false;
            }
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
        try {
            rsBlockingQueue.put(cb);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步发送
     * @param sendArray
     * @param revBufSize
     * @param readWaitTime
     */
    public synchronized byte[] sendDataSync(byte[] sendArray, int revBufSize, int readWaitTime) {
        sending = true;
        byte[] recBytes = rs485SerialPort.send(sendArray, revBufSize, readWaitTime);
        sending = false;
        if(waitSlaverTime > 0){
            try {
                Thread.sleep(waitSlaverTime);
            }catch (Exception e){ }
        }
        return recBytes;
    }

    /**
     * @param cb
     */
    public void sendDataInsertHead(@NonNull final RSCallback cb) {
        cb.level = Long.MAX_VALUE; // 使用优先级队列时有效
        try {
            rsBlockingQueue.put(cb);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isQueueFull() {
        return rsBlockingQueue.remainingCapacity()  == 0;
    }
    public int queueSize() {
        return rsBlockingQueue.size();
    }

    public boolean contains(RSCallback rs){
        return rsBlockingQueue.contains(rs);
    }

    public BlockingQueue<RSCallback> queue() {
        return rsBlockingQueue;
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
                    RSCallback obj ;
                    while ((obj = rsBlockingQueue.poll()) == null){
                        if(!sending) { // 同步发送，则不drain, 休眠1ms
                            rs485SerialPort.drain(1);
                        }else{
                            try {
                                Thread.sleep(1);
                            }catch (Exception e){ }
                        }
                    }
                    if(obj.sendBytes.length == 0){
                        obj.onReceiveFinish(null);
                    }else {
                        if(DEBUG) Log.d(TAG, "------send Bytes: " + HexDump.toHexString(obj.sendBytes));
                        byte[] recBytes = rs485SerialPort.send(obj.sendBytes, obj.recBufLen, obj.waitTime);
                        if(DEBUG) Log.d(TAG, "------recv Bytes: " + (recBytes == null ? "null " : HexDump.toHexString(recBytes)));
                        obj.onReceiveFinish(recBytes);

                        if(waitSlaverTime > 0){
                            try {
                                Thread.sleep(waitSlaverTime);
                            }catch (Exception e){ }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    isOpenSerialSuccess = false;
                    return;
                }
            }
        }
    }

    public static abstract class RSCallback  implements Comparable<RSCallback>{
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
        /**
         * 用于排序的优先级，越大越靠前
         */
        public long level = 0;
        /**
         * 用于判断两个对象是否是相等，如果ID相等，则对象相等
         */
        public String id;

        public RSCallback setId(String id) {
            this.id = id;
            return this;
        }

        public RSCallback setLevel(long level) {
            this.level = level;
            return this;
        }

        public RSCallback(@NonNull byte[] sendData, int recBufLen, int waitTime) {
            this.sendBytes = sendData;
            this.recBufLen = recBufLen;
            this.waitTime = waitTime;
        }

        public RSCallback(@NonNull byte[] sendData, int recBufLen, int waitTime, long level) {
            this.sendBytes = sendData;
            this.recBufLen = recBufLen;
            this.waitTime = waitTime;
            this.level = level;
        }

        /**
         * @param recBytes may be null
         */
        public abstract void onReceiveFinish(byte[] recBytes);


        @Override
        public int compareTo(RSCallback o) {
            return Long.compare( o.level,level);
        }

        @Override
        public boolean equals(Object o) {
            RSCallback that = (RSCallback) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    public static class EmptyRSCallback extends RSCallback{

        public EmptyRSCallback(String id) {
            super(new byte[0], 0, 0);
            super.id = id;
        }

        @Override
        public void onReceiveFinish(byte[] recBytes) {

        }
        @Override
        public boolean equals(Object o) {
            RSCallback that = (RSCallback) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
