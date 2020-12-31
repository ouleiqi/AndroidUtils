package org.tcshare.utils;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPort {
    private static final String TAG = SerialPort.class.getSimpleName();
    /**
     * 不要修改 mFd 变量名 : native close() 方法在用
     */
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    private String rs485Path;

    /**
     *
     * @param device
     * @param baudrate
     * @param flags
     * @param rs485Path null 则不使用
     * @throws SecurityException
     * @throws IOException
     */
    public SerialPort(File device, int baudrate, int flags, @Nullable String rs485Path) throws SecurityException, IOException {
        this.rs485Path = rs485Path;
        mFd = open(device.getAbsolutePath(), baudrate, flags);
        if (mFd == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException();
        } else{
            Log.e(TAG, "native open returns ok");
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    /**
     * 打开串口
     * @return
     */
    public native static FileDescriptor open(String path, int baudrate, int flags);
    public native void close();

    public int enSend(){
        return rs485Path == null ? -1 : SystemCMD("echo 1 > " + rs485Path);
    }
    public int enRec(){
        return rs485Path == null ? -1 : SystemCMD("echo 0 > " + rs485Path);
    }

    public static native int SystemCMD(String path);

}
