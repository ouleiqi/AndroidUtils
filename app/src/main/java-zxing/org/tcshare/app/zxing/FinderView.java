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

package org.tcshare.app.zxing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import org.tcshare.app.R;
import org.tcshare.app.zxing.camera.CameraManager;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class FinderView extends View {
    private static final long  ANIMATION_DELAY       = 40L;
    private static final int   CURRENT_POINT_OPACITY = 0xA0;
    private final int lineWidth, lineLength, lineWidthHalf,lineColor;
    private final Bitmap scanLine;
    private final int density;
    private final int mTextSize,mTextColor;
    private final String mTextContent;
    private final String mLightText;


    private       CameraManager     cameraManager;
    private final Paint             paint;
    private       Bitmap            resultBitmap;
    private final int               maskColor;
    private final int               resultColor;
    private float y            = 0f;
    private float moveDistance = -1f;
    private Rect  scanLineRect = new Rect();
    private Rect  textBounds   = new Rect();
    private float ambientLightLux;
    private Rect lightTextBounds = new Rect();

    // This constructor is used when the class is built from an XML resource.
    public FinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        density = (int) getResources().getDisplayMetrics().density;

        scanLine = BitmapFactory.decodeResource(getResources(),R.mipmap.scan_line);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ViewFinder);
        mTextContent = ta.getString(R.styleable.ViewFinder_text);
        mLightText = ta.getString(R.styleable.ViewFinder_lightText);
        mTextSize = ta.getDimensionPixelSize(R.styleable.ViewFinder_textSize, 12*density);
        mTextColor = ta.getColor(R.styleable.ViewFinder_textColor, Color.WHITE);
        lineWidth = (int) ta.getDimension(R.styleable.ViewFinder_cornerBorderWidth, 3*density);
        lineLength = (int) ta.getDimension(R.styleable.ViewFinder_cornerLength, 20*density);
        lineColor = ta.getColor(R.styleable.ViewFinder_cornerColor,Color.GREEN);
        lineWidthHalf = (int) (lineWidth / 2f);

        ta.recycle();
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
        paint.reset();
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        // 边角线
        paint.reset();
        paint.setColor(lineColor);
        paint.setStrokeWidth(lineWidth);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawLine(frame.left, frame.top + lineWidthHalf, frame.left + lineLength, frame.top + lineWidthHalf, paint);
        canvas.drawLine(frame.right - lineLength, frame.top + lineWidthHalf, frame.right, frame.top + lineWidthHalf, paint);
        canvas.drawLine(frame.left, frame.bottom - lineWidthHalf, frame.left + lineLength, frame.bottom - lineWidthHalf, paint);
        canvas.drawLine(frame.right - lineLength, frame.bottom - lineWidthHalf, frame.right, frame.bottom - lineWidthHalf, paint);

        canvas.drawLine(frame.left+lineWidthHalf, frame.top, frame.left+lineWidthHalf, frame.top+ lineLength, paint);
        canvas.drawLine(frame.right-lineWidthHalf, frame.top, frame.right-lineWidthHalf, frame.top+ lineLength, paint);
        canvas.drawLine(frame.left+lineWidthHalf, frame.bottom - lineLength, frame.left +lineWidthHalf, frame.bottom, paint);
        canvas.drawLine(frame.right-lineWidthHalf, frame.bottom - lineLength, frame.right-lineWidthHalf, frame.bottom, paint);

        if(!TextUtils.isEmpty(mTextContent)) {
            paint.reset();
            paint.setColor(mTextColor);
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
            paint.setTypeface(Typeface.DEFAULT);
            paint.setTextSize(mTextSize);
            if(textBounds.isEmpty()) paint.getTextBounds(mTextContent,0, mTextContent.length(), textBounds);
            canvas.drawText(mTextContent, (frame.right + frame.left - textBounds.width()) / 2f, frame.bottom + textBounds.height() + density * 20, paint);
        }

        if (!TextUtils.isEmpty(mLightText) && ambientLightLux <= CaptureActivity.TOO_DARK_LUX) {
            paint.reset();
            paint.setColor(mTextColor);
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
            paint.setTypeface(Typeface.DEFAULT);
            paint.setTextSize(mTextSize);
            if(lightTextBounds.isEmpty()) paint.getTextBounds(mLightText,0, mLightText.length(), lightTextBounds);
            canvas.drawText(mLightText, (frame.right + frame.left - lightTextBounds.width()) / 2f,frame.bottom - textBounds.height() - density * 20, paint );
        }
        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.reset();
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {
            // Draw a red "laser scanner" line through the middle to show decoding is active
            if(y < frame.top || y > frame.bottom - density * 8){
                y = frame.top + density * 2;
            }else{
                if(moveDistance < 0){
                    moveDistance = (frame.bottom - frame.top) / 90f;
                }
                y += moveDistance;
            }
            if(scanLine.getWidth() > frame.right - frame.left){
                scanLineRect.set(frame.left + density * 8,(int)y, frame.right - density * 8, (int) (y + scanLine.getHeight()));
                canvas.drawBitmap(scanLine,null, scanLineRect, null);
            }else {
                canvas.drawBitmap(scanLine, (frame.left + frame.right - scanLine.getWidth()) / 2f , y, null);
            }
            postInvalidateDelayed(ANIMATION_DELAY, frame.left+lineWidth, frame.top+lineWidth, frame.right-lineWidth, frame.bottom-lineWidth);
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

    public void setAmbientLightLux(float ambientLightLux) {
        this.ambientLightLux = ambientLightLux;
    }
}
