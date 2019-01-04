package com.huidf.slimming.view.home.sleep;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.huidf.slimming.R;
import com.huidf.slimming.context.ApplicationData;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.entity.home.sport.SportHistogramEntity;

import java.util.ArrayList;
import java.util.List;

import huitx.libztframework.utils.LayoutUtil;

/** 
* @author : Zhutao 
* @version 创建时间：@2016年12月6日
* @Description: 检测结果 柱状图,带背景
* @params：
*/ 
public class SleepHistogramTable extends View {

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		canvas.drawColor(0xffc4bebf);
//		drawCoordinateLine(canvas);
		drawRectf(canvas);
	}



	/**
	 * 绘制矩形
	 */
	public void drawRectf(Canvas canvas) {
		float lastCoordinate = 0;
		float drawWidth = screenWidth - (2 * marginLeft);

		linearGradient = new LinearGradient(0, 0, 0, (0.89f) * getHeight(), color, null, Shader.TileMode.MIRROR);
		paint_pillar.setShader(linearGradient);

		for (int i = 0; i < mScores.size(); i++) {
//		for (int i = 0; i < 1; i++) {
			float newCoordinate = 0;

			if(i == 0){
				newCoordinate = marginLeft;
				lastCoordinate = marginLeft +columnarWidth;
			}else{
				newCoordinate = lastCoordinate + columnarPadding;
				lastCoordinate = newCoordinate + columnarWidth;
			}

			mRectf.set(newCoordinate , getHeight() - columnarHeight - marginBottom, lastCoordinate,  getHeight()- marginBottom);
			canvas.drawRoundRect(mRectf, rectRadian, rectRadian, paint_back);
			canvas.save();

			float yDate = getHeight() - marginBottom - columnarHeight + (1 - mScores.get(i).value/mScores.get(i).maxNum) * columnarHeight;
			mRectf.set(newCoordinate , yDate, lastCoordinate,  getHeight()- marginBottom);
			LinearGradient linearGradient = new LinearGradient(0, getHeight()- marginBottom, 0, yDate, color, null, Shader.TileMode.MIRROR);
			paint_pillar.setShader(linearGradient);
			canvas.drawRoundRect(mRectf, rectRadian, rectRadian, paint_pillar);
			canvas.restore();

			canvas.drawText(mScores.get(i).date,newCoordinate + columnarWidth/2,getHeight(),paint_coordinate);
		}
	}

	/**
	 * 绘制坐标轴
	 */
	public void drawCoordinateLine(Canvas canvas) {
//		canvas.drawLine(0, 0, getWidth(), 0, paint_pillar);
//		canvas.drawLine(0, getHeight(), getWidth(), getHeight(), paint_pillar);

	}

	public SleepHistogramTable(Context context) {
		super(context);
	}
	public SleepHistogramTable(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDetectionData(context,attrs);
	}
	public SleepHistogramTable(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setDetectionData(context,attrs);
	}
	/**
	 * 初始化数据
	 * @param context
	 * @param attrs
	 */
	public void setDetectionData(Context context, AttributeSet attrs) {
		TypedArray attribute = context.obtainStyledAttributes(attrs, R.styleable.chart_columnar_table);
//		String ycoordinate = attribute.getString(R.styleable.chart_ycoords_ycoordinate);
		columnarHeight = attribute.getInteger(R.styleable.chart_columnar_table_ccolumnart_height,2);
		columnarWidth = attribute.getInteger(R.styleable.chart_columnar_table_ccolumnart_width,0);
		columnarPadding = attribute.getInteger(R.styleable.chart_columnar_table_ccolumnart_padding,0);
		marginLeft = attribute.getInteger(R.styleable.chart_columnar_table_ccolumnart_marginleft,0);
		init();
	}

	public void init() {
		if(mScores == null) mScores = new ArrayList<>();

		mRectf = new RectF();
		screenWidth = PreferenceEntity.screenWidth;
		screenHeight = PreferenceEntity.screenHeight;
		Resources res = getResources();
		tb = res.getDimension(R.dimen.detection_10);

		columnarWidth = LayoutUtil.getInstance().getWidgetWidth(columnarWidth);
		columnarPadding = LayoutUtil.getInstance().getWidgetWidth(columnarPadding);
		columnarHeight = LayoutUtil.getInstance().getWidgetHeight(columnarHeight);
		marginLeft = LayoutUtil.getInstance().getWidgetWidth(marginLeft);

		textSize = tb * 1.0f;
		lineWidth = tb * 0.1f;
		marginBottom = LayoutUtil.getInstance().getWidgetHeight(47);
		marginHorizonal = columnarPadding + columnarWidth;
		textPadding = 0.01f * screenHeight;
		rectPadding = tb * 0.4f;


		paint_pillar = initPaint(paint_pillar);
		paint_back = initPaint(paint_back);
		paint_coordinate = initPaint(paint_coordinate);
		paint_coordinate.setColor(ApplicationData.context.getResources().getColor(R.color.text_color_hint));
//		float yDate = getHeight() -  0.1f * columnarHeight;
//		LinearGradient linearGradient = new LinearGradient(0, 0, 0, yDate, color, null, Shader.TileMode.REPEAT);
//		paint_pillar.setShader(linearGradient);
	}
	
	public Paint initPaint(Paint mPaint){
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(colorColumnarBack);
		mPaint.setStrokeWidth(lineWidth);	//画笔宽度1sp
		mPaint.setStyle(Style.FILL);	//实心
		mPaint.setTextSize(textSize);	//字号10sp
		mPaint.setTextAlign(Align.CENTER);
		return mPaint;
	}
	

	public void setData(List<SportHistogramEntity> score){
		this.mScores = score;
		invalidate();
	}

	//设置视图的大小
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);//获得控件的高度
		int width = 0;
		if(mScores !=null){
			width = (int)marginHorizonal * (mScores.size()-1) + (int)marginLeft;
		}
		setMeasuredDimension(width>screenWidth?width:screenWidth, height);
		YPoint = getHeight() - marginBottom;
	}

	private void LOG (String data){
		Log.i("spoort_list", "Columnar  " + data);}

	/** 屏幕的宽 */
	private int screenWidth;
	/** 屏幕的高 */
	private int screenHeight;

	LinearGradient linearGradient = null;
	private int[] color = new int[]{0xff3dccd6,0xff9af25e};

	List<SportHistogramEntity> mScores;
	

	/** 绘制柱状图，柱状图上的数值文字 */
	private Paint paint_pillar,paint_back,paint_coordinate;
	private int colorDeepSleep = 0xff1d89e4;
	private int colorColumnarBack = 0xffededed;

	private float tb;
	RectF mRectf;
	/** 矩形边角弧度 */
	public float rectRadian = 4;

	/** 左右间距 两个圆柱的间距，加上圆柱的直径 */
//	private float interval_left_right;
	/** 控件间距 */
//	private float weight_margin;
	/**  X轴的长度 */
//	public int XLength = 0;
	/** 第一个圆柱中心距离画布左边的距离 */
	private float marginLeft;
	/** 圆柱圆点距离画布底部的距离 */
	private float marginBottom;
	/** 纵坐标轴的纵向间距 以6个区间为标准制定的 */
	private float marginHorizonal;
    /** Y坐标轴原点的位置 */
    public float YPoint = 0;
    /** X坐标轴文字以及柱状图文字绘制间距 */
    public float textPadding = 0;
    
    /** 字体大小 */
    public float textSize = 0;
    /** 画笔宽度 */
    public float lineWidth = 0;
    /** X轴文本选中框超出文字偏离边距 */
    public float rectPadding = 0;
	
	/** 柱状图宽度 */
	private float columnarWidth;
	/** 柱状图间距 */
	private float columnarPadding;
	/** 柱状图高度 */
	private float columnarHeight;
}
