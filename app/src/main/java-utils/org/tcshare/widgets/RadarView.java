package org.tcshare.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.tcshare.app.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RadarView extends View {
    private Bitmap pointImg;
    // 参数调整>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    private final boolean usePointBitmap = true;
    private final float pointRadius = 10f;  // 扫描点的半径
    private final int MAX_POINT_SHOW = 100;// 最多显示100个点
    private final MaskFilter outLightFilter = new BlurMaskFilter(pointRadius, BlurMaskFilter.Blur.SOLID); // 外发光
    private final int showPointAngel = 300; // 最大显示范围, 最大360度
    private final int bgColor = -0x472304; // 背景颜色
    private final int bgRadar = -0xcd874c; // 雷达区域背景色
    private final int colorCircle = -0xce360e; // 内部园环及分割线的颜色
    private final int radarColor = Color.parseColor("#AA0000FF"); // 扫描扇形的颜色
    private final int mNumCicle = 4; // 多少个园环
    private final int mNumLines = 4; // 对角线的个数
    // 参数调整<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    private SweepGradient sweepGradient = null;
    private boolean isSearching = false;
    private final Paint mPaint = new Paint();
    private int mCurrentAngel = 0;
    private final List<MyPoint> mPointArray = new ArrayList<MyPoint>();
    private int mWidth = 0;
    private int mHeight = 0;

    private int mOutWidth = 0; // 外圆宽度(w/4/5*2=w/10)
    private int mCx = 0; // x、y轴中心点
    private int mCy = 0;
    private int mOutsideRadius = 0;// 外、内圆半径
    private int mInsideRadius = 0;

    public RadarView(Context context) {
        super(context);
        init(context);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        if (usePointBitmap) {
            pointImg = BitmapFactory.decodeResource(getResources(), R.mipmap.point); // xxhdpi
            // 默认硬件加速
            setLayerType(View.LAYER_TYPE_HARDWARE, (Paint) null);
        } else {
            setLayerType(View.LAYER_TYPE_SOFTWARE, (Paint) null);
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取控件区域宽高
        if (mWidth == 0 || mHeight == 0) {
            int minimumWidth = getSuggestedMinimumWidth();
            int minimumHeight = getSuggestedMinimumHeight();
            mWidth = resolveMeasured(widthMeasureSpec, minimumWidth);
            mHeight = resolveMeasured(heightMeasureSpec, minimumHeight);
            // 获取x/y轴中心点
            mCx = mWidth / 2;
            mCy = mHeight / 2;
            // 获取外圆宽度
            mOutWidth = mWidth / 10;
            // 计算内、外半径
            mOutsideRadius = mWidth / 2;// 外圆的半径
            mInsideRadius = (mWidth - mOutWidth) / mNumCicle / 2;// 内圆的半径,除最外层,其它圆的半径=层数*insideRadius
            sweepGradient = new SweepGradient((float) mCx, (float) mCy, 0, radarColor);
        }

    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        // 1. 绘制圆形背景
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(bgColor);
        mPaint.setShader(null);
        mPaint.setAlpha(255);
        canvas.drawCircle((float) mCx, (float) mCy, (float) mOutsideRadius, mPaint);
        // 2. 绘制雷达区域背景
        mPaint.setColor(bgRadar);
        canvas.drawCircle((float) mCx, (float) mCy, (float) mInsideRadius * (float) mNumCicle, mPaint);
        // 3. 绘制园环
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(colorCircle);

        for (int num = mNumCicle; num >= 1; --num) {
            canvas.drawCircle((float) mCx, (float) mCy, (float) mInsideRadius * (float) num, mPaint);
        }
        // 4. 绘制对角线
        double angle = (double) (180 / mNumLines);
        int lineRadius = mInsideRadius * mNumCicle;
        for (int num = 0; num < mNumLines; ++num) {
            double radianS = Math.toRadians(num * angle); // 将角度转换为弧度
            double radianE = Math.toRadians(num * angle + 180);
            canvas.drawLine(
                    (float) (mCx + lineRadius * Math.cos(radianS)),
                    (float) (mCy + lineRadius * Math.sin(radianS)),
                    (float) (mCx + lineRadius * Math.cos(radianE)),
                    (float) (mCy + lineRadius * Math.sin(radianE)),
                    mPaint
            );
        }
        // 5.绘制扫描扇形图
        if (isSearching) {// 判断是否处于扫描
            canvas.save();
            canvas.rotate(
                    mCurrentAngel,
                    mCx,
                    mCy
            );
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setShader(sweepGradient);
            mCurrentAngel += 3;
            canvas.drawCircle(
                    mCx,
                    mCy,
                    mInsideRadius * mNumCicle,
                    mPaint
            );
            canvas.restore();


            // 6.开始绘制动态点
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setShader(null);
            if (!usePointBitmap) mPaint.setMaskFilter(outLightFilter); // 绘制圆的外发光, 需要关闭硬件加速

            for (int i = mPointArray.size() - 1; i >= 0; i--) { // 倒序，先画最新的，然后更改不透明度
                MyPoint p = mPointArray.get(i);
                if (mCurrentAngel >= showPointAngel && mCurrentAngel - showPointAngel > p.angel) {
                    mPointArray.remove(i); //倒序移除
                } else {
                    int sAngel = (mCurrentAngel - p.angel - 1) % showPointAngel; // 这里减一，是因为浮点型导致最后一个又正常显示了
                    if (sAngel == 0) {
                        mPaint.setAlpha(255);
                    } else {
                        mPaint.setAlpha((int) (255 - (float) sAngel / showPointAngel * 255));
                    }

                    if (usePointBitmap) {
                        canvas.drawBitmap(
                                pointImg,
                                p.pos.x - pointImg.getWidth() / 2f,
                                p.pos.y - pointImg.getHeight() / 2f,
                                mPaint
                        );
                    } else {
                        canvas.drawCircle(
                                p.pos.x,
                                p.pos.y,
                                pointRadius,
                                mPaint
                        );
                    }
                }
            }
            this.invalidate();
        }

    }

    public final void setSearching(boolean status) {
        isSearching = status;
        if (!isSearching) {
            mPointArray.clear();
        }

        invalidate();
    }

    public final boolean isSearching() {
        return isSearching;
    }

    public final void addPoint(float percent) {
        if (!isSearching) return;
        double radian = Math.toRadians(mCurrentAngel);
        float lineRadius = mInsideRadius * mNumCicle * percent;
        float pX = (float) (mCx + lineRadius * Math.cos(radian));
        float pY = (float) (mCy + lineRadius * Math.sin(radian));
        MyPoint point = new MyPoint(percent, new PointF(pX, pY), mCurrentAngel);
        mPointArray.add(point);
        if (mPointArray.size() > MAX_POINT_SHOW) { // 超过允许的点个数，移除最开始的一个
            mPointArray.remove(0); //TODO 测试是否存在重新分配数组的问题
        }
        this.invalidate();
    }

    private int resolveMeasured(int measureSpec, int desired) {
        int specSize = MeasureSpec.getSize(measureSpec);
        int result;
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                result = desired;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.max(specSize, desired);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            default:
                result = specSize;
                break;
        }

        return result;
    }


    public static final class MyPoint {
        float percent;
        PointF pos;
        int angel;

        public String toString() {
            return "MyPoint(percent=" + percent + ", pos=" + pos + "， mOffsetArgs=" + angel + ",)";
        }

        public MyPoint(float percent, @NonNull PointF pos, int angel) {
            super();
            this.percent = percent;
            this.pos = pos;
            this.angel = angel;
        }
    }
}

