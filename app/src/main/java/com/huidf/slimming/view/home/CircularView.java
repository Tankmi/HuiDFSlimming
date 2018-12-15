package com.huidf.slimming.view.home;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.huidf.slimming.R;
import com.huidf.slimming.context.ApplicationData;
import com.huidf.slimming.context.PreferenceEntity;

import huitx.libztframework.utils.LayoutUtil;
import huitx.libztframework.utils.PreferencesUtils;


/**
 * 圆环进度条
 *
 * @author ZhuTao
 * @date 2017/6/12
 * @params
 */

public class CircularView extends View {

    private float tb;
    private Paint paint_scales, paint_text;
    /**
     * 最外面的细环
     */
    private RectF rectf_c1;
    private int maxNum = 360;
    /**
     * 角度1-maxNum
     */
    private float arc_y = 0;
    /**
     * 标记是否加载过1，第一次加载；2，已加载；
     */
    private int type = 0;

    /**
     * 最外层圆的直径
     */
    private float circleWidth;
    /**
     * 最外层圆的坐标起始点
     */
    private float circlestart;
    /**
     * 进度条颜色
     */
    private int colorLineBack = 0xffffffff;
    /**
     * 进度条背景色
     */
    private int colorLinePb = 0xff22d7bb;
    /**
     * 画笔宽度
     */
    private float lineWidth = 0;
    /**
     * 字体大小
     */
    private float textSize = 0;

    public CircularView(Context context)
    {
        super(context);
        init();
    }

    public CircularView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public CircularView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private Paint initPaint(Paint mPaint)
    {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(colorLineBack);
        mPaint.setStrokeWidth(lineWidth);    //画笔宽度
        mPaint.setStyle(Style.FILL);    //实心
        mPaint.setTextSize(textSize);    //字号
        mPaint.setTextAlign(Align.CENTER);
        return mPaint;
    }

    public void init()
    {
        Resources res = getResources();
        tb = res.getDimension(R.dimen.detection_10);

        lineWidth = LayoutUtil.getInstance().getWidgetWidth(15, true);
        circlestart = lineWidth / 2;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);

        circleWidth = getWidth() - circlestart;
        textSize = tb * 2.6f;    //52像素

        paint_scales = initPaint(paint_scales);
//        paint_scales.setStrokeJoin(Paint.Join.ROUND);   //设置结合处为圆弧
        paint_scales.setStrokeCap(Paint.Cap.ROUND); //设置线帽为圆弧
        paint_scales.setStyle(Style.STROKE);

        paint_text = initPaint(paint_text);

        Log.i("spoort_list", "getWidth():" + getWidth() + " circleWidth:" + circleWidth);
        rectf_c1 = new RectF();
        rectf_c1.set(circlestart, circlestart, circleWidth, circleWidth);
    }

    /**
     * 刻度的长度
     */
//    private int pb;
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
//        canvas.drawColor(0xff72fe01);

        drawText(canvas);
        drawCircle(canvas);
    }

    /** 绘制进度条背景，以及进度条 */
    public void drawCircle(Canvas canvas){

        paint_scales.setColor(colorLineBack);
        canvas.drawArc(rectf_c1, 0, maxNum, false, paint_scales);   //绘制背景

//		// 绘制圆弧
        if (arc_y > 0) {
            paint_scales.setColor(colorLinePb);
            canvas.drawArc(rectf_c1, -90, arc_y, false, paint_scales);  //绘制进度
            canvas.save();
        }
    }

    /** 绘制文字描述 */
    public void drawText(Canvas canvas)
    {
        float mTextSize = tb * 1.2f;
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(mTextSize);
        textPaint.setColor(Color.parseColor("#ffffff"));
        textPaint.setStyle(Style.FILL);

        String text = "比原来轻";
        int numSize = text.length();  //获取要显示的内容的长度
        float textWidth = Layout.getDesiredWidth(text, textPaint), unitWidth = Layout.getDesiredWidth("（KG）", textPaint);
        canvas.drawText("比原来轻（KG）", getWidth() / 2 - (textWidth + unitWidth) / 2,
                LayoutUtil.getInstance().getWidgetHeight(80) + Layout.getDesiredWidth("0", textPaint), textPaint);

        mTextSize = tb * 2.75f;
        textPaint.setTextSize(mTextSize);

        text = differenceValue + "";
        textWidth = Layout.getDesiredWidth(text, textPaint);
        canvas.drawText(text, getWidth() / 2 - textWidth / 2, LayoutUtil.getInstance().getWidgetHeight(136) + Layout.getDesiredWidth("0", textPaint), textPaint);
    }


    /** 最大值 */
    private float maxValue;
    /** 当前值 */
    private float currentValue;
    /** 目标值 */
    private float targetValue;
    /** 差值 */
    private float differenceValue = 0.0f;
    private float maxArc_y;


    /**
     * 根据当前值和目标值，计算出 进度百分比（0-100）
     * @param maxValue      初始体重
     * @param currentValue  当前体重
     * @param targetValue   目标体重
     */
    public void setData(final float maxValue, final float currentValue, float targetValue)
    {

        if (currentValue <= 0 || maxValue <= 0) return;
        this.maxValue = maxValue;
        this.currentValue = currentValue;
        this.targetValue = targetValue;

        this.differenceValue = (maxValue>currentValue?maxValue - currentValue:0.0f);
        maxArc_y = differenceValue/(maxValue - targetValue) * maxNum;

        this.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw()
                    {
//                        arc_y = 3.6f*pb;
//                        float pb = 0;
//                        arc_y = maxNum * pb;    //最大值是 maxNum 度
//                        postInvalidate();
                        mHandler.postDelayed(runnable2,16);
                        getViewTreeObserver().removeOnPreDrawListener(this);
                        return false;
                    }
                });
    }


    Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            if (maxArc_y /arc_y <= 1) {
                return;
            }
            try {
                arc_y += 1f;
                postInvalidate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mHandler.postDelayed(this, 16);	//延时加载
        }
    };

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what) {

                default:
                    break;
            }
        }

        ;
    };


    public void close()
    {
        if (runnable2 != null) {
            mHandler.removeCallbacks(runnable2);
            runnable2 = null;
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
////        setMeasuredDimension(LayoutUtil.getInstance().getWidgetWidth(220), LayoutUtil.getInstance().getWidgetHeight(220));
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//        Log.i("spoort_list","onMeasure: " + widthSize + "; heightSize: " + heightSize);
//        initView();
    }

}
