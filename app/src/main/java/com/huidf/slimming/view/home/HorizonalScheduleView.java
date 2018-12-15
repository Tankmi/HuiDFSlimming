package com.huidf.slimming.view.home;

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

import java.text.DecimalFormat;

import huitx.libztframework.utils.LayoutUtil;


/**
 * 水平进度条
 * @author ZhuTao
 * @date 2018/11/26 
 * @params 
*/


public class HorizonalScheduleView extends View {

    private float tb;
    private Paint paint_scales, paint_text;
    /** 进度条颜色  */
    private int colorLineBack = 0xffeeeeee;
    /** 进度条背景色 */
    private int colorLinePb = 0xff1bdcbe;
    /**  画笔宽度 */
    private float lineWidth = 0;
    /** 字体大小 */
    private float textSize = 0;

    public HorizonalScheduleView(Context context)
    {
        super(context);
        init();
    }

    public HorizonalScheduleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public HorizonalScheduleView(Context context, AttributeSet attrs, int defStyle)
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

        lineWidth = LayoutUtil.getInstance().getWidgetWidth(10, true);
        marginLeft = LayoutUtil.getInstance().getWidgetWidth(10, true);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);

        textSize = tb * 2.6f;    //52像素

        paint_scales = initPaint(paint_scales);
//        paint_scales.setStrokeJoin(Paint.Join.ROUND);   //设置结合处为圆弧
        paint_scales.setStrokeCap(Paint.Cap.ROUND); //设置线帽为圆弧
        paint_scales.setStyle(Style.STROKE);

        paint_text = initPaint(paint_text);
    }

    /**
     * 刻度的长度
     */
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
//        canvas.drawColor(0xff72fe01);

        drawText(canvas);
        drawSchedule(canvas);
    }


    /** 绘制进度条背景，以及进度条 */
    public void drawSchedule(Canvas canvas){

        paint_scales.setColor(colorLineBack);
        canvas.drawLine(marginLeft, lineWidth , getWidth() - marginLeft, lineWidth , paint_scales);   //绘制背景

        if (schedule > 0) {
            float mSchedule = 0.01f * schedule;
            int width = getWidth() - marginLeft;
            mSchedule = mSchedule * width;
            paint_scales.setColor(colorLinePb);
            canvas.drawLine(marginLeft, lineWidth, mSchedule, lineWidth, paint_scales);   //绘制背景
            canvas.save();
        }
    }

    /** 绘制文字描述 */
    public void drawText(Canvas canvas)
    {
        float mTextSize = tb * 0.8f;
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Style.STROKE);
        textPaint.setTextSize(mTextSize);
        textPaint.setColor(ApplicationData.context.getResources().getColor(R.color.text_color_hint));
        textPaint.setStyle(Style.FILL);

        String text = "0%";
        float textWidth = Layout.getDesiredWidth(text, textPaint);
        canvas.drawText(text, 0,getHeight(), textPaint);

        text = "50%";
        textWidth = Layout.getDesiredWidth(text, textPaint);
        canvas.drawText(text, getWidth()/2 - textWidth/2,getHeight(), textPaint);

        text = "100%";
        textWidth = Layout.getDesiredWidth(text, textPaint);
        canvas.drawText(text, getWidth() - textWidth,getHeight(), textPaint);
    }


    /** 进度值 0-100 */
    private float schedule;
    private int marginLeft;

    /**
     * 根据当前值和目标值，计算出 进度百分比（0-100）
     * @param schedule      初始体重
     */
    public void setData(final float schedule)
    {

        if (schedule <= 0) return;
        this.schedule = schedule;
        postInvalidate();

//        this.getViewTreeObserver().addOnPreDrawListener(
//                new ViewTreeObserver.OnPreDrawListener() {
//                    public boolean onPreDraw()
//                    {
//                        mHandler.postDelayed(runnable2,200);
//                        getViewTreeObserver().removeOnPreDrawListener(this);
//                        return false;
//                    }
//                });
    }


//    Runnable runnable2 = new Runnable() {
//        @Override
//        public void run() {
//            if (maxArc_y /arc_y <= 1) {
//                return;
//            }
//            try {
//                arc_y += 3f;
//                postInvalidate();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            mHandler.postDelayed(this, 10);	//延时加载
//        }
//    };

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
