package org.tcshare.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.tcshare.utils.CameraUtils;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = CameraSurfaceView.class.getSimpleName();

    private SurfaceHolder mSurfaceHolder;

    public CameraSurfaceView(Context context) {
        super(context);
        init();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        CameraUtils.openFrontalCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        CameraUtils.getmCamera().startFaceDetection();
        CameraUtils.setPreviewSize(CameraUtils.getmCamera(), width, height);
        CameraUtils.setPictureSize(CameraUtils.getmCamera(), width, height);
        CameraUtils.startPreviewDisplay(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CameraUtils.getmCamera().stopFaceDetection();
        CameraUtils.releaseCamera();
    }
}