/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tcshare.app.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.zxing.ResultPoint;

import org.tcshare.app.R;
import org.tcshare.app.android.camera.CameraManager;

import java.util.ArrayList;
import java.util.List;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

    private static final long  ANIMATION_DELAY       = 20L; // 1600ms
    private static final int   CURRENT_POINT_OPACITY = 0xA0;
    private final int lineWidth, linLength, lineWidthHalf;
    private final Bitmap scanLine;
    private final int density;


    private       CameraManager     cameraManager;
    private final Paint             paint;
    private       Bitmap            resultBitmap;
    private final int               maskColor;
    private final int               resultColor;
    private float y = 0f;
    private float moveDistance = -1f;
    private Rect scanLineRect = new Rect();

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        density = (int) getResources().getDisplayMetrics().density;
        lineWidth = density * 3;
        linLength = density * 20;
        lineWidthHalf = (int) (lineWidth / 2f);
        scanLine = BitmapFactory.decodeResource(getResources(),R.mipmap.scan_line);

    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }


    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        if (cameraManager == null) {
            return; // not ready yet, early draw before done configuring
        }
        Rect frame = cameraManager.getFramingRect();
        Rect previewFrame = cameraManager.getFramingRectInPreview();
        if (frame == null || previewFrame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        // 边角线
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(lineWidth);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawLine(frame.left, frame.top + lineWidthHalf, frame.left + linLength, frame.top + lineWidthHalf, paint);
        canvas.drawLine(frame.right - linLength, frame.top + lineWidthHalf, frame.right, frame.top + lineWidthHalf, paint);
        canvas.drawLine(frame.left, frame.bottom - lineWidthHalf, frame.left + linLength, frame.bottom - lineWidthHalf, paint);
        canvas.drawLine(frame.right - linLength, frame.bottom - lineWidthHalf, frame.right, frame.bottom - lineWidthHalf, paint);

        canvas.drawLine(frame.left+lineWidthHalf, frame.top, frame.left+lineWidthHalf, frame.top+linLength, paint);
        canvas.drawLine(frame.right-lineWidthHalf, frame.top, frame.right-lineWidthHalf, frame.top+linLength, paint);
        canvas.drawLine(frame.left+lineWidthHalf, frame.bottom - linLength, frame.left +lineWidthHalf, frame.bottom, paint);
        canvas.drawLine(frame.right-lineWidthHalf, frame.bottom - linLength, frame.right-lineWidthHalf, frame.bottom, paint);


        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {
            // Draw a red "laser scanner" line through the middle to show decoding is active
            if(y < frame.top || y > frame.bottom - density * 8){
                y = frame.top + density * 2;
            }else{
                if(moveDistance < 0){
                    moveDistance = (frame.bottom - frame.top) / 80;
                }
                y += moveDistance;
            }
            if(scanLine.getWidth() > frame.right - frame.left){
                scanLineRect.set(frame.left + density * 8,(int)y, frame.right - density * 8, (int) (y + scanLine.getHeight()));
                canvas.drawBitmap(scanLine,null, scanLineRect, null);
            }else {
                canvas.drawBitmap(scanLine, (frame.left + frame.right - scanLine.getWidth()) / 2f, y, null);
            }

            postInvalidateDelayed(ANIMATION_DELAY);
        }
    }

    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

}
