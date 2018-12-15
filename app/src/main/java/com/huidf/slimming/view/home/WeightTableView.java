package com.huidf.slimming.view.home;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

import com.huidf.slimming.R;
import com.huidf.slimming.context.ApplicationData;
import com.huidf.slimming.context.PreferenceEntity;

import java.util.ArrayList;
import java.util.List;

import huitx.libztframework.utils.LayoutUtil;
import huitx.libztframework.utils.MathUtils;
import huitx.libztframework.utils.PreferencesUtils;


/**
 * 体重进度条
 *
 * @author ZhuTao
 * @date 2018/11/26
 * @params
 */


public class WeightTableView extends View {

    private float tb;
    private Paint paint_line_effect;
    /** 圆点 */
    private Paint paint_circle;

    /** 画笔宽度 */
    private float lineWidth = 0;
    /** 字体大小  */
    private float textSize = 0;
    private float marginLeft;
    private float circleWidth;
    private float circleWidth_min;
    /**  左右间距 两个圆柱的间距，加上圆柱的直径 */
    private float interval_lr;
    private float interval_lr_min;

    private int colorSel = 0xff52c2e7;
    private int colorUnsel = 0xffdcdcdc;
    /** y轴坐标值 */
    private List<Float> yCoors;
    /** Y坐标轴原点的位置  */
    private float YPoint = 0;
    /** 纵坐标轴的纵向间距 以纵坐标区间数为标准制定的 */
    private float marginHorizonal;
    private List<Float> score;

    public WeightTableView(Context context) {
        super(context);
        init();
    }

    public WeightTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeightTableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private Paint initPaint(Paint mPaint)
    {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(colorSel);
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

        lineWidth = tb*0.1f;
        initYcoorData();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        textSize = tb * 2.6f;    //52像素
        YPoint = getHeight() - circleWidth;

//        paint_scales = initPaint(paint_scales);
////        paint_scales.setStrokeJoin(Paint.Join.ROUND);   //设置结合处为圆弧
//        paint_scales.setStrokeCap(Paint.Cap.ROUND); //设置线帽为圆弧
//        paint_scales.setStyle(Style.STROKE);

        marginHorizonal = (YPoint)/(yCoors.size()-1);

        circleWidth = LayoutUtil.getInstance().getWidgetWidth(6);
        circleWidth_min = LayoutUtil.getInstance().getWidgetWidth(3);
        interval_lr = (getWidth() - circleWidth*2) / 4;  //
        interval_lr_min = interval_lr/6;
        marginLeft = circleWidth;

        paint_circle = initPaint(paint_circle);

        paint_line_effect = initPaint(paint_line_effect);
        paint_line_effect.setStyle(Style.FILL_AND_STROKE);
        paint_line_effect.setColor(colorSel);
        PathEffect pathEffect3 = new DashPathEffect(new float[]{tb * 0.8f, tb * 0.3f}, 1);
        Path  p=new Path();
        p.addCircle(0,0, circleWidth_min, Path.Direction.CCW);  //逆时针，CW，顺时针
        PathDashPathEffect pathEffect4 = new PathDashPathEffect(p, interval_lr_min, 1, PathDashPathEffect.Style.ROTATE);
        paint_line_effect.setPathEffect(pathEffect4);


    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
//        canvas.drawColor(0xff72fe01);
        drawDate(canvas);
    }

    public void drawDate(Canvas canvas)
    {
        for (int i = 0; i < score.size(); i++) {
            if (i > 0) {
                if (MathUtils.compareFloat(score.get(i - 1), score.get(i)))
                    paint_line_effect.setColor(colorUnsel);
                else paint_line_effect.setColor(colorSel);
                drawImaginaryLine(marginLeft + interval_lr * (i - 1), YCoord(score.get(i - 1) + ""),
                        marginLeft + interval_lr * (i), YCoord(score.get(i) + ""), canvas);
            }
        }
        // 绘制节点
        for (int i = 0; i < score.size(); i++) {
            if (i > 0) {
                if (MathUtils.compareFloat(score.get(i - 1), score.get(i)))
                    paint_circle.setColor(colorUnsel);
                else paint_circle.setColor(colorSel);
            }
            canvas.drawCircle(marginLeft + interval_lr * (i), YCoord(score.get(i) + ""), circleWidth, paint_circle);
        }
    }

    /**
     * 绘制虚线
     */
    public void drawImaginaryLine(float startX, float startY, float stopX, float stopY, Canvas canvas)
    {
        canvas.save();
        Path path = new Path();
        path.moveTo(startX, startY);
        path.lineTo(stopX, stopY);
        canvas.drawPath(path, paint_line_effect);
        canvas.restore();
    }

    /**
     * @param score
     */
    public void setData(List<Float> score)
    {
        this.score = score;
        postInvalidate();
    }

    /**
     * 初始化Y坐标的范围
     */
    private void initYcoorData()
    {
        float Weight = Float.parseFloat(PreferencesUtils.getString(ApplicationData.context, PreferenceEntity.KEY_USER_INITIAL_WEIGHT, "70"));
        float TargetWeight = Float.parseFloat(PreferencesUtils.getString(ApplicationData.context, PreferenceEntity.KEY_USER_TARGET_WEIGHT, (Weight - 10) + ""));

//        Weight = 72;
//        TargetWeight = 66;
        yCoors = new ArrayList<>();
        yCoors.add(TargetWeight);
        yCoors.add(TargetWeight + ((Weight - TargetWeight) / 2));
        yCoors.add(Weight);
    }

    // 计算绘制时的Y坐标，无数据时返回-999
    private float YCoord(String y0)
    {
        float y;
        float newValue = 0;
        try {
            y = Float.parseFloat(y0);
        } catch (Exception e) {
            return -999; // 出错则返回-999
        }
        for (int i = 0; i < yCoors.size(); i++) {
            if (i == 0) {
                if (y <= yCoors.get(i)) {    //比最小值小
                    newValue = YPoint - circleWidth;    //保证能绘制出来
                    return newValue;
                }
            } else if (y >= yCoors.get(i - 1) && y < yCoors.get(i)) {
                newValue = (float) (YPoint - ((y - yCoors.get(i - 1)) / (yCoors.get(i) - yCoors.get(i - 1)) * marginHorizonal) - (i - 1) * marginHorizonal);
                return newValue;
            } else if (y >= yCoors.get(yCoors.size() - 1)) {    //比最大值大

                newValue = YPoint - (yCoors.size() - 1) * marginHorizonal + circleWidth;    // + circleWidth,保证最顶部的点能全部展示出来
                return newValue;
            }

        }
        newValue = YPoint - circleWidth;    //保证能绘制出来
        return newValue;
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
