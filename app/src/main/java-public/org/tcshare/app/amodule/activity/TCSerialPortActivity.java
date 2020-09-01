package org.tcshare.app.amodule.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.tcshare.app.R;
import org.tcshare.utils.Hex;
import org.tcshare.utils.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

public class TCSerialPortActivity extends Activity {

    private static final String TAG = TCSerialPortActivity.class.getSimpleName();
    protected SerialPort mSerialPort;
    private ReadThread mReadThread;
    private WriteThread mWriteThread;

    private int mSendTimeInterval;
    private EditText mTimeInterval;
    private Button sendBtn;
    private TextView sendCountTV;
    private TextView recCountTV;
    private long sendCount;
    private long receiveCount;
    private EditText sendText;
    private TextView mReception;
    private CheckBox repeatCheckBox;
    private CheckBox hexCheckBox;
    private ReadThread.OnDataReceive receiveCallBack = new ReadThread.OnDataReceive() {
        @Override
        public void onReceived(final byte[] buffer, final int size) {
            runOnUiThread(new Runnable() {
                public void run() {
                    String data = hexCheckBox.isChecked() ? data =Hex.getHexString(buffer,size) : new String(buffer, 0, size);
                    mReception.append(data);
                    receiveCount += size;
                    recCountTV.setText(String.valueOf(receiveCount));
                }
            });
        }
    };


    private static class ReadThread extends Thread {

        private final InputStream mInputStream;
        private final OnDataReceive callBack;

        public interface OnDataReceive{
            void onReceived(byte[] buffer, int size);
        }
        public ReadThread(InputStream inputStream, OnDataReceive callBack) {
            this.mInputStream = inputStream;
            this.callBack = callBack;
        }

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[512];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0 && callBack != null) {
                        callBack.onReceived(buffer, size);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private static class WriteThread extends Thread {

        private final OutputStream mOutputStream;
        private final LinkedBlockingQueue<byte[]> sendQueue = new LinkedBlockingQueue<>(); //发送队列,线程安全！

        public WriteThread(OutputStream outputStream) {
            mOutputStream = outputStream;
        }
        public void sendData(byte[] bytes){
            try {
                sendQueue.put(bytes);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    byte[] bytes = sendQueue.take();
                    mOutputStream.write(bytes);
                    mOutputStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private void DisplayError(String msg) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Error");
        b.setMessage(msg);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TCSerialPortActivity.this.finish();
            }
        });
        b.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tc_serialport);
        sendText = (EditText) findViewById(R.id.sendContent);
        mReception = (TextView) findViewById(R.id.recevieContent);
        sendCountTV = (TextView) findViewById(R.id.send_num);
        recCountTV = (TextView) findViewById(R.id.rec_num);

        mSendTimeInterval = 1000;
        mTimeInterval = (EditText) findViewById(R.id.timeinterval);

        hexCheckBox = (CheckBox) findViewById(R.id.cb_hex);
        hexCheckBox.setChecked(false);

        repeatCheckBox = (CheckBox) findViewById(R.id.repeat_check);
        repeatCheckBox.setChecked(false);
        repeatCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            private Timer timer;

            @Override
            public void onCheckedChanged(CompoundButton arg0,
                                         boolean isChecked) {
                if (isChecked) {
                    if(!TextUtils.isEmpty(mTimeInterval.getText())){
                        mSendTimeInterval = Integer.parseInt(mTimeInterval.getText().toString());
                    }

                    final byte[] sendbytes = getSendData();
                    if (sendbytes != null) {
                        timer = new Timer(true);
                        TimerTask task = new TimerTask() {
                            public void run() {
                                sendData(sendbytes);
                            }
                        };
                        timer.schedule(task,mSendTimeInterval, mSendTimeInterval);
                        sendText.setEnabled(false);
                        sendBtn.setEnabled(false);
                    } else{
                        Toast.makeText(TCSerialPortActivity.this, "请输入 0~9,a~f,A~F,内的值，不含空格.", Toast.LENGTH_SHORT).show();
                        repeatCheckBox.setChecked(false);
                    }

                    if (mSendTimeInterval <= 0) {
                        Toast.makeText(getBaseContext(), "时间间隔必须大于 0 ms!", Toast.LENGTH_SHORT).show();
                        repeatCheckBox.setChecked(false);
                    } else {
                        mTimeInterval.setEnabled(false);
                    }
                } else {
                    sendBtn.setEnabled(true);
                    sendText.setEnabled(true);
                    mTimeInterval.setEnabled(true);
                    if(timer != null){
                        timer.cancel();
                        timer.purge();
                    }
                }
            }
        });

        sendBtn = (Button) findViewById(R.id.sendbtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!sendData(getSendData())) {
                    Toast.makeText(TCSerialPortActivity.this, "请输入 0~9,a~f,A~F,内的值，不含空格.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        String port = getIntent().getStringExtra("port");
        String band = getIntent().getStringExtra("band");
        try {
            mSerialPort = new SerialPort(new File(port), Integer.parseInt(band), 0,  null);

            mReadThread = new ReadThread(mSerialPort.getInputStream(), receiveCallBack);
            mReadThread.start();
            mWriteThread = new WriteThread(mSerialPort.getOutputStream());
            mWriteThread.start();


        } catch (SecurityException e) {
            DisplayError("打开串口时，安全异常");
        } catch (IOException e) {
            DisplayError("打开串口时，IO异常");
        } catch (InvalidParameterException e) {
            DisplayError("打开串口时，配置错误");
        }
    }

    private byte[] getSendData(){
        String sendStr = sendText.getText().toString().trim();
        if(hexCheckBox.isChecked()){
            return "".equals(sendStr) ? null : Hex.strToHexBytes(sendStr);
        }
        return "".equals(sendStr) ? null : sendStr.getBytes();
    }

    private boolean sendData(byte[] sendBytes ) {
        if(sendBytes != null) {
            mWriteThread.sendData(sendBytes);
            sendCount += sendBytes.length;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sendCountTV.setText(String.valueOf(sendCount));
                }
            });
            return true;
        }else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
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
        super.onDestroy();
    }
}
