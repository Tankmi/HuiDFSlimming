package com.huidf.slimming.view.home.sport;


import android.content.Context;
import android.content.res.Resources;
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
 * 运动历史记录柱状图
 * @author ZhuTao
 * @date 2018/12/19
 * @params
*/

public class SportHistogramTable2 extends View {

	/** 表中横轴刻度 */
	private Paint paint_coordinate_line;
	private int colorCoordinateLine = 0xffdcdcdc;	//坐标轴色值
	/** y轴坐标值 */
	public List<Float> yCoors;

	/**
	 * 初始化Y坐标的范围
	 */
	public void initYcoorData(String ycoordinate){
		String[] yCorrdinateArray;
		yCoors = new ArrayList<>();
		yCorrdinateArray = ycoordinate.split(",");
		for (int i = 0; i < yCorrdinateArray.length; i++) {
			yCoors.add(Float.parseFloat(yCorrdinateArray[i]));
		}

		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		canvas.drawColor(0xffc4fc3e);
//		drawCoordinateLine(canvas);
		drawRectf(canvas);
	}

	/**
	 * 绘制坐标轴
	 */
	public void drawCoordinateLine(Canvas canvas) {
		paint_coordinate_line.setColor(colorCoordinateLine);
//		DashPathEffect pathEffect = new DashPathEffect(new float[] { 6,2 }, 1); //绘制虚线
//		paint_coordinate_line.setStyle(Style.STROKE);
//		paint_coordinate_line.setPathEffect(pathEffect);

		for(int i=0; i<yCoors.size(); i++){ //只画横坐标轴
//			Path path = new Path();
//			path.moveTo(0, getHeight() - (marginBottom + marginVertical * i));
//			path.lineTo(getWidth(),getHeight() - (marginBottom + marginVertical * i));
//			canvas.drawPath(path, paint_coordinate_line);	//绘制虚线
			canvas.drawLine(0, getHeight() - (marginBottom + marginVertical * i),
					getWidth(),getHeight() - (marginBottom + marginVertical * i), paint_coordinate_line);
		}

	}

	/**
	 * 绘制矩形
	 */
	public void drawRectf(Canvas canvas) {
		float lastCoordinate = 0;

//		linearGradient = new LinearGradient(0, 0, 0, (0.89f) * getHeight(), color, null, Shader.TileMode.MIRROR);
		linearGradient = new LinearGradient(0, getHeight() - marginBottom - columnarHeight,
				0, getHeight() - marginBottom, color, null, Shader.TileMode.MIRROR);
		paint_pillar.setShader(linearGradient);

		for (int i = 0; i < mScores.size(); i++) {
			float newCoordinate = 0;
			if(i == 0){
				newCoordinate = marginLeft;
				lastCoordinate = marginLeft +columnarWidth;
			}else{
				newCoordinate = lastCoordinate + columnarPadding;
				lastCoordinate = newCoordinate + columnarWidth;
			}

			//绘制背景
			mRectf.set(newCoordinate , getHeight() - columnarHeight - marginBottom, lastCoordinate,  getHeight()- marginBottom);
			canvas.drawRoundRect(mRectf, rectRadian, rectRadian, paint_back);

			canvas.save();
//			SportHistogramEntity mData = mScores.get(i);
//			if(mData.level.equals("A")){
//				paint_pillar.setColor(colorA);
//			}else if(mData.level.equals("B")){
//				paint_pillar.setColor(colorB);
//			}else{
//                paint_pillar.setColor(colorC);
//            }

//			float yDate = getHeight() -  columnarHeight + (1 - mScores.get(i).value/mScores.get(i).maxNum) * columnarHeight - marginBottom;

			float yDate = YCoord(mScores.get(i).value+"");
			LOG("yCoors.size():  " + yCoors.size());
			LOG("yDate:  " + yDate);
			mRectf.set(newCoordinate , yDate, lastCoordinate,  getHeight()- marginBottom);
			LinearGradient linearGradient = new LinearGradient(0, getHeight()- marginBottom, 0, yDate, color, null, Shader.TileMode.MIRROR);
			paint_pillar.setShader(linearGradient);	//设置渐变色
			canvas.drawRoundRect(mRectf, rectRadian, rectRadian, paint_pillar);
			canvas.restore();

//			canvas.drawText(mScores.get(i).date,newCoordinate + columnarWidth/2, getHeight(), paint_coordinate);	//绘制x轴
			canvas.drawText(mScores.get(i).date,newCoordinate + columnarWidth/2,
					(getHeight() -  marginBottom + (LayoutUtil.getInstance().getWidgetHeight(18) + textSize)),
					paint_coordinate);	//绘制x轴
		}
	}

	// 计算绘制时的Y坐标，无数据时返回-999
	private float YCoord(String y0) {
		float y;
		float newValue = 0;
		try {
			y = Float.parseFloat(y0);
		} catch (Exception e) {
			return -999; // 出错则返回-999
		}
		for (int i = 0; i < yCoors.size(); i++) {
			if(i == 0){
				if(y <= yCoors.get(i)){	//比最小值小
					newValue = getHeight() - (marginBottom);
					return newValue;
				}
			}else if (y >= yCoors.get(i-1) && y <= yCoors.get(i)){
				newValue = (float) (getHeight()-((y-yCoors.get(i-1))/(yCoors.get(i)-yCoors.get(i-1))* marginVertical) - (marginBottom + marginVertical * (i-1)));
				return newValue;
			}else if(y>yCoors.get(yCoors.size()-1)){	//比最大值大
				newValue = getHeight() - (marginBottom + marginVertical * (yCoors.size()-1));
				return newValue;
			}
		}
		newValue = getHeight() - (marginBottom);	//保证能绘制出来
		return newValue;
	}

	public SportHistogramTable2(Context context) {
		super(context);
	}
	public SportHistogramTable2(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDetectionData(context,attrs);
	}
	public SportHistogramTable2(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setDetectionData(context,attrs);
	}
	/**
	 * 初始化数据
	 * @param context
	 * @param attrs
	 */
	public void setDetectionData(Context context, AttributeSet attrs) {
		initYcoorData("10,100,300");

		columnarWidth = LayoutUtil.getInstance().getWidgetWidth(26);
		columnarPadding = LayoutUtil.getInstance().getWidgetWidth(66);
		columnarHeight = LayoutUtil.getInstance().getWidgetHeight(270);
		marginBottom = LayoutUtil.getInstance().getWidgetHeight(50);
		marginLeft = LayoutUtil.getInstance().getWidgetWidth(30);


		if(mScores == null) mScores = new ArrayList<>();
		mRectf = new RectF();
		screenWidth = PreferenceEntity.screenWidth;
		screenHeight = PreferenceEntity.screenHeight;
		Resources res = getResources();
		tb = res.getDimension(R.dimen.detection_10);
		textSize = tb * 1.0f;
		lineWidth = tb * 0.1f;
		textPadding = 0.01f * screenHeight;
		rectPadding = tb * 0.4f;

		paint_coordinate_line = initPaint(paint_coordinate_line);
		paint_pillar = initPaint(paint_pillar);
		paint_back = initPaint(paint_back);
		paint_coordinate = initPaint(paint_coordinate);
		paint_coordinate.setColor(ApplicationData.context.getResources().getColor(R.color.text_color_hint));
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
		init();
	}

	public void init() {
		YPoint = getHeight() - marginBottom;
		marginVertical = columnarHeight/(yCoors.size()-1);
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
	

	public int setData(List<SportHistogramEntity> score){
		this.mScores = score;
		LOG("setData" + mScores.size());
		if(score != null){
			XLength = (int) (marginLeft + (columnarPadding + columnarWidth) * score.size());
//			measure(XLength,getHeight());
		}

		invalidate();
		if (XLength > screenWidth) {
			return XLength;
		} else {
			return screenWidth;
		}
	}

	//设置视图的大小
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec,heightMeasureSpec);
//		int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);//获得控件的高度
//		int width;
//		if (XLength > screenWidth) {
//			width = XLength;
//		} else {
//			width = screenWidth;
//		}
//		setMeasuredDimension(width, height);
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
	private int colorColumnarBack = 0xffededed;
//	private int colorA,colorB,colorC;
	private float tb;
	RectF mRectf;
	/** 矩形边角弧度 */
	private float rectRadian = 1;
	/** 第一个圆柱中心距离画布左边的距离 */
	private float marginLeft;
	/** 圆柱圆点距离画布底部的距离 */
	private float marginBottom;
	/** 纵坐标轴的纵向间距 */
	private float marginVertical;
    /** Y坐标轴原点的位置 */
	private float YPoint = 0;
    /** X坐标轴文字以及柱状图文字绘制间距 */
	private float textPadding = 0;
	/**  X轴的长度 */
	private int XLength = 0;
    /** 字体大小 */
	private float textSize = 0;
    /** 画笔宽度 */
	private float lineWidth = 0;
    /** X轴文本选中框超出文字偏离边距 */
	private float rectPadding = 0;
	/** 柱状图宽度 */
	private float columnarWidth;
	/** 柱状图间距 */
	private float columnarPadding;
	/** 柱状图高度，也是绘制区间高度 */
	private float columnarHeight;
}
