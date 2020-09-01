package org.tcshare.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 如果报错，先检查 /dev/ttyXXX 的权限 <br/>
 * 注意： 仅支持写入N字节， 读M个字节的设定 <br/>
 * 该代码使用java打开系统文件描述符，由于无法精确计时，下位机应该设置50~100ms左右的延时，应用内设置读写的延时！！！
 */
public class RS485SerialPortUtil {
    private static final String TAG = RS485SerialPortUtil.class.getSimpleName();
    static {
        System.loadLibrary("serialport");
    }

    private SerialPort mSerialPort;
    private ReadThread mReadThread;
    private WriteThread mWriteThread;
    private ScheduledExecutorService timerPool = Executors.newScheduledThreadPool(10);
    private final BlockingQueue<WaitClass> blockingQueue = new LinkedBlockingQueue<>();
    private final ConcurrentLinkedQueue<CallBack> cbs = new ConcurrentLinkedQueue<CallBack>();
    private final ReadThread.OnDataReceive receiveCallBack = new ReadThread.OnDataReceive() {
        @Override
        public void onReceived(final byte[] buffer, final int size) {
                for (int i = 0; i < size; i++) {
                    CallBack cb = cbs.peek();
                    if (cb == null) return;
                    if (cb.putByte(buffer[i])) {
                        cbs.remove(cb);
                        //timerPool.submit(notifyLock);
                        synchronized (lock) {
                            lock.notify();
                        }
                    }
                }
        }
    };
    private Runnable notifyLock = new Runnable() {
        @Override
        public void run() {
            synchronized (lock) {
                lock.notify();
            }
        }
    };
    private final Object lock = new Object();
    private static boolean isOpenSerialSuccess = false;
    private String rs485Path;


    private static class ReadThread extends Thread {

        private final InputStream mInputStream;
        private final OnDataReceive callBack;

        public interface OnDataReceive {
            void onReceived(byte[] buffer, int size);
        }

        public ReadThread(InputStream inputStream, OnDataReceive callBack) {
            this.mInputStream = inputStream;
            this.callBack = callBack;
            android.os.Process.setThreadPriority (-19);//priority：【-20, 19】，高优先级 -> 低优先级
            //setPriority (10); //priority：【1, 10】，低优先级 -> 高优先级。
        }

        @Override
        public void run() {
            super.run();
            while (true) {
                int size;
                try {
                    byte[] buffer = new byte[512];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    Log.e(TAG, "on read :" + size);
                    if (size > 0 && callBack != null) {
                        callBack.onReceived(buffer, size);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "read 0");
                    e.printStackTrace();
                    return;
                }
                Log.e(TAG, "read 1");
            }
        }
    }

    private class WriteThread extends Thread {

        private final OutputStream mOutputStream;

        public WriteThread(OutputStream outputStream) {
            mOutputStream = outputStream;
            android.os.Process.setThreadPriority (-19);//priority：【-20, 19】，高优先级 -> 低优先级
            //setPriority (10); //priority：【1, 10】，低优先级 -> 高优先级。
        }


        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    WaitClass obj = blockingQueue.take();
                    synchronized (lock) {
                        if (cbs.size() > 0) {
                            Log.e(TAG, "wait size:" + cbs.size());
                            lock.wait(2000); // 最长等待超时
                        }
                        cbs.clear();
                        byte[] sendBytes = obj.sendBytes;
                        final CallBack cb = obj.cb;
                        if(rs485Path != null) {
                            int ret = mSerialPort.enSend(); // 使能发送
                           // Log.e(TAG, "en snd ret: " + ret);
                            if(cb.sendDelay > 0) {
                                try {
                                    Thread.sleep(cb.sendDelay);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        //Log.e(TAG, "send data:" + HexDump.toHexString(sendBytes) + " send delay " + cb.sendDelay + " rev delay: " + cb.recDelay);

                        mOutputStream.write(sendBytes);
                        mOutputStream.flush();
                        if(rs485Path != null) {
                            cbs.add(cb);
                            if(cb.recDelay > 0){
                                try {
                                    Thread.sleep(cb.recDelay);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            int ret = mSerialPort.enRec(); // 使能接收
                           // Log.e(TAG, "en rec ret: "+ ret);

                            timerPool.schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (cbs.remove(cb)) {
                                        cb.timeOut();
                                        timerPool.submit(notifyLock);
                                    }
                                }
                            }, cb.timeOut, TimeUnit.MILLISECONDS);
                        }
                        Log.e(TAG, "send data:" + HexDump.toHexString(sendBytes) + " timeOut:" + cb.timeOut + " send delay " + cb.sendDelay + " rev delay: " + cb.recDelay);
                        //lock.wait(2000); // 接收等待
                        //Log.e(TAG, "wait finish");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    isOpenSerialSuccess = false;
                    return;
                }
            }
        }
    }



    public boolean isIsOpenSerialSuccess() {
        return isOpenSerialSuccess;
    }

    public RS485SerialPortUtil() { }

    /**
     * 注意串口只需打开一次，不要重复打开
     * @param band 波特率
     * @param port 串口节点
     * @param rs485Path 485使能节点
     */
    public void openSerialPort(int band, String port, String rs485Path){
        this.rs485Path = rs485Path;
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

            mSerialPort = new SerialPort(new File(port), band, 0, rs485Path);
            mReadThread = new ReadThread(mSerialPort.getInputStream(), receiveCallBack);
            mReadThread.start();
            mWriteThread = new WriteThread(mSerialPort.getOutputStream());
            mWriteThread.start();
            isOpenSerialSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "打开串口时，异常");
            isOpenSerialSuccess = false;
        } finally {
        }
    }


    /**
     * @param sendBytes
     * @param cb
     */
    public void sendData(byte[] sendBytes, @NonNull final CallBack cb) {
        try {
            blockingQueue.put(new WaitClass(sendBytes, cb));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * @param sendBytes
     * @param cb
     */
    public void sendDataInsert(byte[] sendBytes, @NonNull  final CallBack cb) {
        sendData(true, sendBytes, cb);
    }
    /**
     * @param sendBytes
     * @param cb
     */
    public void sendData(boolean insert, byte[] sendBytes, @NonNull  final CallBack cb) {
        if(insert) {
            try {
                List<WaitClass> contains = new ArrayList<>();
                blockingQueue.drainTo(contains);
                blockingQueue.put(new WaitClass(sendBytes, cb));
                blockingQueue.addAll(contains);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            sendData(sendBytes, cb);
        }
    }


    public void destroy() {
        try {
            if (mReadThread != null) {
                mReadThread.interrupt();
            }
            if (mWriteThread != null) {
                mWriteThread.interrupt();
            }
            if (mSerialPort != null) {
                mSerialPort.close();
                mSerialPort = null;
            }
            blockingQueue.clear();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static class WaitClass{
        public byte[] sendBytes;
        public CallBack cb;

        public WaitClass(byte[] sendBytes, CallBack cb) {
            this.sendBytes = sendBytes;
            this.cb = cb;
        }
    }

    public static abstract class CallBack {
        private final long timeOut;
        private final int revLen;
        private final long sendDelay;
        private final long recDelay;
        private ByteBuffer byteBuffer;

        public CallBack(int revLen, long timeOut) {
            this(revLen, timeOut, 0, 0);
        }
        public CallBack(int revLen, long timeOut, long sendDelay, long recDelay) {
            this.sendDelay = sendDelay;
            this.recDelay = recDelay;
            this.revLen = revLen;
            this.timeOut = timeOut ;
            byteBuffer = ByteBuffer.allocate(revLen);
        }
        public void timeOut(){
            onDataReceive(byteBuffer.array(), true, byteBuffer.position());
        }

        public boolean putByte(byte b) {
            if (byteBuffer.position() < byteBuffer.capacity()) {
                byteBuffer.put(b);
                if(byteBuffer.position() >= byteBuffer.capacity()){
                    onDataReceive(byteBuffer.array(),false, byteBuffer.position());
                    return true;
                }else{
                    return false;
                }
            } else {
                onDataReceive(byteBuffer.array(),false, byteBuffer.position());
                return true;
            }
        }

        public abstract void onDataReceive(byte[] bytes, boolean timeOut, int receiveLen);
    }
}
