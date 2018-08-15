package org.tcshare.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.tcshare.app.BuildConfig;
import org.tcshare.app.R;
import org.tcshare.utils.DensityUtil;
import org.tcshare.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 */
public class FavorLayout extends RelativeLayout {
    private static final String TAG = FavorLayout.class.getSimpleName();

    private int iHeight = 120;
    private int iWidth  = 120;
    private int mHeight;
    private int mWidth;

    private LayoutParams lp;
    private List<Drawable> loves;

    private List<Interpolator> interpolators =  new ArrayList<Interpolator>(){
        {
            add(new LinearInterpolator());
            //add(new AccelerateInterpolator());
            add(new DecelerateInterpolator());
            add(new AccelerateDecelerateInterpolator());
        }
    };
    private PointF startPoint = new PointF();
    private PointF ancherPoint;
    private View ancherView;
    private int favorWidth = -1, favorHeight = -1;
    private boolean stop = false;


    public FavorLayout(Context context) {
        super(context);
        init();
    }

    public FavorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FavorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        startPoint.set((mWidth - iWidth) / 2, mHeight - iHeight);
    }

    private void init() {
        //底部 并且 水平居中
        lp = new LayoutParams(iWidth, iHeight);
        lp.addRule(CENTER_HORIZONTAL, TRUE); //这里的TRUE 要注意 不是true
        lp.addRule(ALIGN_PARENT_BOTTOM, TRUE);

        //初始化显示的图片
        loves = new ArrayList<Drawable>(){
            {
                add(getResources().getDrawable(R.mipmap.love_a));
                add(getResources().getDrawable(R.mipmap.love_b));
                add(getResources().getDrawable(R.mipmap.love_c));
                add(getResources().getDrawable(R.mipmap.love_d));
                add(getResources().getDrawable(R.mipmap.love_e));
            }

        };

    }

    public void setAncher(final View view){
        ancherView = view;
        resetAncherPoint();
    }
    private void resetAncherPoint(){
        if(ancherView != null){
            ancherView.post(new Runnable() {
                @Override
                public void run() {
                    int[] outLocation = new int[2];
                    ancherView.getLocationOnScreen(outLocation);
                    float x = outLocation[0] + (ancherView.getWidth() - iWidth)/2;
                    float y = outLocation[1] +(ancherView.getHeight() - iHeight)/2;
                    ancherPoint = new PointF( x, y);
                }
            });
        }
    }

    /**
     * 点赞
     * 对外暴露的方法
     */
    public void addFavor() {
        if(stop){
            return;
        }
        ImageView imageView = new ImageView(getContext());
        // 随机选一个
        imageView.setImageDrawable(RandomUtils.getRandomElement(loves));

        if(ancherPoint == null) {
            imageView.setLayoutParams(lp);
        }else{
            imageView.setX(ancherPoint.x - getX());
            imageView.setY(ancherPoint.y - getY());
        }

        addView(imageView);
        if(BuildConfig.IS_DEBUG) Log.d(TAG, "addFavor: " + "add后子view数:" + getChildCount());

        Animator set = getAnimator(imageView);
        set.addListener(new AnimEndListener(imageView));
        set.start();

    }

    /**
     * 设置动画
     */
    private Animator getAnimator(View target) {
        AnimatorSet set = getEnterAnimtor(target);

        ValueAnimator bezierValueAnimator = getBezierValueAnimator(target);

        AnimatorSet finalSet = new AnimatorSet();
        finalSet.playSequentially(set);
        finalSet.playSequentially(set, bezierValueAnimator);
        finalSet.setInterpolator(RandomUtils.getRandomElement(interpolators));//实现随机变速
        finalSet.setTarget(target);
        return finalSet;
    }


    /**
     * 设置初始动画
     * 渐变 并且横纵向放大
     */
    private AnimatorSet getEnterAnimtor(final View target) {

        ObjectAnimator alpha = ObjectAnimator.ofFloat(target, View.ALPHA, 0.2f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(target, View.SCALE_X, 0.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(target, View.SCALE_Y, 0.2f, 1f);
        AnimatorSet enter = new AnimatorSet();
        enter.setDuration(500);
        enter.setInterpolator(new LinearInterpolator());
        enter.playTogether(alpha, scaleX, scaleY);
        enter.setTarget(target);
        return enter;
    }

    public void setFavorWidthHeight(int width, int height){
        this.favorWidth = DensityUtil.dp2px(getContext(), width);
        this.favorHeight = DensityUtil.dp2px(getContext(), height);
    }

    private PointF getPointLow() {
        PointF pointF = new PointF();
        if(ancherView !=null && favorWidth != -1 && favorHeight != -1 && ancherPoint != null){
            // 中心点
            float x = ancherPoint.x - getX();
            float y = ancherPoint.y - getY();
            pointF.x = x - favorWidth / 2 + RandomUtils.getRandomInt(favorWidth);
            pointF.y = y - favorHeight / 4 - RandomUtils.getRandomInt(favorHeight / 4);

        }else {
            //减去100 是为了控制 x轴活动范围
            pointF.x = RandomUtils.getRandomInt(mWidth - 100);
            //再Y轴上 为了确保第二个控制点 在第一个点之上,我把Y分成了上下两半
            pointF.y = RandomUtils.getRandomInt(mHeight - 100) / 2;
        }

        return pointF;
    }
    private PointF getPointHight() {
        PointF pointF = new PointF();
        if(ancherView !=null && favorWidth != -1 && favorHeight != -1 && ancherPoint != null){
            // 中心点
            float x = ancherPoint.x - getX();
            float y = ancherPoint.y - getY();
            pointF.x = x - favorWidth / 2 + RandomUtils.getRandomInt(favorWidth);
            pointF.y = y - favorHeight / 2 - RandomUtils.getRandomInt(favorHeight / 4);

        }else {
            pointF.x = RandomUtils.getRandomInt(mWidth - 100);
            pointF.y = RandomUtils.getRandomInt(mHeight - 100);
        }

        return pointF;
    }

    private PointF getEndPoint() {
        PointF pointF = new PointF();
        if(ancherView !=null && favorWidth != -1 && favorHeight != -1 && ancherPoint != null){
            // 中心点
            float x = ancherPoint.x - getX();
            float y = ancherPoint.y - getY();
            pointF.x = x - favorWidth / 2 + RandomUtils.getRandomInt(favorWidth);
            pointF.y = y - favorHeight;

        }else {
            pointF.set(RandomUtils.getRandomInt(getWidth()), 0);
        }

        return pointF;
    }

    /**
     * 获取贝塞尔曲线动画
     */
    private ValueAnimator getBezierValueAnimator(View target) {

        //初始化一个BezierEvaluator
        BezierEvaluator evaluator = new BezierEvaluator(getPointLow(), getPointHight());

        // 起点固定，终点随机
        ValueAnimator animator = ValueAnimator.ofObject(evaluator,
                ancherPoint == null ? startPoint : new PointF(ancherPoint.x - getX(), ancherPoint.y - getY()),
                getEndPoint());
        animator.addUpdateListener(new BezierListener(target));
        animator.setTarget(target);
        animator.setDuration(3000);
        return animator;
    }


    /**
     * 设置点赞效果集合
     * @param items
     */
    public void setFavors(List<Drawable> items) {
        loves.clear();
        loves.addAll(items);
        if(items.size() == 0){
            throw  new UnsupportedOperationException("点赞效果图片不能为空");
        }

        this.iWidth = items.get(0).getIntrinsicWidth();
        this.iHeight =  items.get(0).getIntrinsicHeight();
        startPoint = new PointF((mWidth - iWidth) / 2, mHeight - iHeight);
        resetAncherPoint();
    }

    public void setStat(boolean stop) {
        this.stop = stop;
    }


    private class AnimEndListener extends AnimatorListenerAdapter {
        private View target;

        public AnimEndListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            //因为不停的add 导致子view数量只增不减,所以在view动画结束后remove掉
            removeView((target));
            if(BuildConfig.IS_DEBUG) Log.v(TAG, "removeView后子view数:" + getChildCount());
        }
    }


}
