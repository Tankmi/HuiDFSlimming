package com.huidf.slimming.view.home.today_movement;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * 作者：ZhuTao
 * 创建时间：2018/12/10 : 17:27
 * 描述：
 */
public class RunCircleAnimationView extends View {
    //设置状态
    public static final int STATE_NOT_STARTED = 0;
    public static final int STATE_FILL_STARTED = 1;
    public static final int STATE_FINSHED = 3;
    /** 标记是展示状态（true），还是缩放状态 */
    private boolean isStart;

    private static final Interpolator INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private static final int FILL_TIME = 1000;

    private int state = STATE_NOT_STARTED;

    private Paint fillPaint;
    private int currentRadius;
    private ObjectAnimator revealAnimator;

    private int startLocationX;
    private int startLocationY;

    private OnStateChangeListener onStateChangeListener;


    public RunCircleAnimationView(Context context) {
        super(context);
        init();
    }

    public RunCircleAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RunCircleAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化数据
     */
    private void init() {
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.WHITE);
    }

    public void setFillPaintColor(int color) {
        fillPaint.setColor(color);
    }

    public void startFromLocation(int[] tapLocationOnScreen) {
        isStart = true;
        //设置状态为开始
        changeState(STATE_FILL_STARTED);
        //获取到中心点即动画开始的位置
        startLocationX = tapLocationOnScreen[0];
        startLocationY = tapLocationOnScreen[1];
        //设置半径
        int finalRadius = (int) Math.hypot(getHeight(), getWidth());
        //设置动画 重点currentRadius设置动画的半径
        revealAnimator = ObjectAnimator.ofInt(this, "currentRadius", 0, finalRadius);
        revealAnimator.setInterpolator(INTERPOLATOR);
        revealAnimator.setDuration(FILL_TIME);
        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                changeState(STATE_FINSHED);
            }
        });
        revealAnimator.start();
    }

    public void endToLocation(int[] tapLocationOnScreen) {
        isStart = false;
        //设置状态为开始
        changeState(STATE_FILL_STARTED);
        //获取到中心点即动画开始的位置
        startLocationX = tapLocationOnScreen[0];
        startLocationY = tapLocationOnScreen[1];
        //设置半径
        int finalRadius = (int) Math.hypot(getHeight(), getWidth());
        //设置动画 重点currentRadius设置动画的半径
        revealAnimator = ObjectAnimator.ofInt(this, "currentRadius", finalRadius, 0);
        revealAnimator.setInterpolator(INTERPOLATOR);
        revealAnimator.setDuration(FILL_TIME);
        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                changeState(STATE_FINSHED);
            }
        });
        revealAnimator.start();
    }

    public void setToFinishedFrame() {
        changeState(STATE_FINSHED);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (state == STATE_FINSHED){
            //状态完成画一个矩形
            if(isStart)canvas.drawRect(0,0,getWidth(),getHeight(),fillPaint);
        } else {
            //画圆
            canvas.drawCircle(startLocationX,startLocationY,currentRadius,fillPaint);
        }
    }

    /**
     * 设置状态
     * @param state
     */
    private void changeState(int state) {
        if(this.state == state) {
            return;
        }
        this.state = state;
        if(onStateChangeListener != null) {
            onStateChangeListener.onStateChange(state,isStart);
        }
    }

    /**
     * 动画回调此方法
     * @param radius
     */
    public void setCurrentRadius(int radius) {
        this.currentRadius = radius;
        invalidate();
    }

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }
    public interface OnStateChangeListener{
        void onStateChange(int state, boolean playState);
    }
}
