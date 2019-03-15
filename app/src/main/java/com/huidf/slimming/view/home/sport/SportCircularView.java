package com.huidf.slimming.view.home.sport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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

import huitx.libztframework.utils.LayoutUtil;
import huitx.libztframework.utils.NumberConversion;


/**
 * 运动圆环进度条
 *
 * @author ZhuTao
 * @date 2017/6/12
 * @params
 */

public class SportCircularView extends View {

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

    /** 进度条背景色 默认透明 */
    private int colorLineBack = 0x50ffffff;
    /** 进度条颜色 */
    private int colorLinePb = 0xffffffff;
    /**
     * 画笔宽度
     */
    private float lineWidth = 0;
    /**
     * 字体大小
     */
    private float textSize = 0;

    public SportCircularView(Context context)
    {
        super(context);
        init();
    }

    public SportCircularView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public SportCircularView(Context context, AttributeSet attrs, int defStyle)
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

    Bitmap pointBitmap;
    int height_point;
    int width_point;
    /** 围绕中心旋转的圆点，外部显示半径 */
    private float pointWidth;

    public void init()
    {
        Resources res = getResources();
        tb = res.getDimension(R.dimen.detection_10);

        lineWidth = LayoutUtil.getInstance().getWidgetWidth(15, true);
        pointWidth = LayoutUtil.getInstance().getWidgetWidth(11.5f);
//        circlestart = lineWidth / 2 + pointWidth/2;
        circlestart = lineWidth / 2 + pointWidth/2;
    }

    @SuppressLint("DrawAllocation")
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


        pointBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.iv_circular_point);
        width_point = pointBitmap.getWidth();
        height_point = pointBitmap.getHeight();
        // 设置想要的大小
        int newWidth_point = (int) LayoutUtil.getInstance().getWidgetWidth(23);
        int newHeight_point = (int) LayoutUtil.getInstance().getWidgetWidth(23);
        // 计算缩放比例
        float scaleWidth_point = ((float) newWidth_point) / width_point;
        float scaleHeight_point = ((float) newHeight_point) / height_point;
        // 取得想要缩放的matrix参数
        Matrix matrix_point = new Matrix();
        matrix_point.postScale(scaleWidth_point, scaleHeight_point);
        // 得到新的图片
        pointBitmap = Bitmap.createBitmap(pointBitmap, 0, 0, width_point,
                height_point, matrix_point, true);
        width_point = pointBitmap.getWidth();
        height_point = pointBitmap.getHeight();
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

        canvas.translate(circlestart / 2 + circleWidth / 2, circlestart / 2 + circleWidth / 2); // 移动圆心到画布的中央
//		canvas.drawText( (int)arc_y +"", 0, 0, paint_text);
        canvas.save();
        canvas.rotate(arc_y); // 旋转画布

//        canvas.drawText( (int)arc_y +"", 0, 0, paint_text);
//        canvas.drawText( (int)arc_y +"", -(pointWidth), 0, paint_text);
//        canvas.drawText( (int)arc_y +"", 0,  -((circleWidth - circlestart )/ 2 +pointWidth), paint_text);
        try {
            canvas.drawBitmap(pointBitmap,
                    -(pointWidth),
                    -((circleWidth - circlestart )/ 2 +pointWidth), null); // 上面
        } catch (Exception e) {
            // TODO: handle exception
        }
        canvas.restore(); // 恢复画布之前的状态
    }

    /** 绘制文字描述 */
    public void drawText(Canvas canvas)
    {
        float mTextSize = tb * 3.6f;
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Style.STROKE);
        textPaint.setTextSize(mTextSize);
        textPaint.setColor(Color.parseColor("#ffffff"));
        textPaint.setStyle(Style.FILL);

        String text = mLevel + "级";
        float textWidth,textHeight;
        textHeight = Layout.getDesiredWidth("级", textPaint);
        float drawHeight = getHeight()/2 - textHeight/2;
        textWidth = Layout.getDesiredWidth(text, textPaint);
        canvas.drawText(text, getWidth() / 2 - textWidth / 2,
//                LayoutUtil.getInstance().getWidgetHeight(80) +
                drawHeight,
                textPaint);

        mTextSize = tb * 1.8f;
        textPaint.setTextSize(mTextSize);
        text = "已完成" + NumberConversion.reducedPoint(mValue);
        float unitWidth = Layout.getDesiredWidth("%", textPaint);
        textWidth = Layout.getDesiredWidth(text, textPaint);
        drawHeight = getHeight()/2 + textHeight -
                Layout.getDesiredWidth("已", textPaint)/2;
        canvas.drawText(text + "%", getWidth() / 2 - (textWidth + unitWidth) / 2,
                drawHeight, textPaint);
    }


    /** 进度值 例：60% 传60 */
    private float mValue;
    private float maxArc_y;
    private String mLevel = "C";   //等级 ABC


    /**
     * 根据当前值和目标值，计算出 进度百分比（0-100）
     * @param mLevel      等级 ABC
     * @param mValue      进度值 ：60% 传60
     */
    public void setData(String mLevel,float mValue)
    {
        this.mLevel = mLevel;
        this.mValue = mValue;
        maxArc_y = mValue * maxNum * 0.01f;

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

    @SuppressLint("HandlerLeak")
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
