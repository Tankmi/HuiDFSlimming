package com.huidf.slimming.view.home.sleep;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.huidf.slimming.R;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.entity.home.sleep.SleepCTEntity;

import java.util.ArrayList;
import java.util.List;

/** 
* @author : Zhutao 
* @version 创建时间：@2016年12月6日
* @Description: 检测结果 柱状图
* @params：
*/ 
public class SleepTable extends View {

	public void init() {
		if(mScores == null) mScores = new ArrayList<>();

		mRectf = new RectF();
		screenWidth = PreferenceEntity.screenWidth;
		screenHeight = PreferenceEntity.screenHeight;
		Resources res = getResources();
		tb = res.getDimension(R.dimen.detection_10);

		textSize = tb * 1.0f;
		lineWidth = tb * 0.1f;
		marginBottom = screenHeight * 0.06f;
		marginHorizonal = screenHeight * 0.039f;
//		textPadding = 0.0045f * screenHeight;
		textPadding = 0.01f * screenHeight;
		rectPadding = tb * 0.4f;
		marginLeft = screenWidth *0.024f;
//		marginLeft = screenWidth * marginLeft;
//		lineRadius = 0.028f * screenWidth;

		paint_pillar = initPaint(paint_pillar);



//		this.getViewTreeObserver().addOnPreDrawListener(
//				new OnPreDrawListener() {
//					public boolean onPreDraw() {
//						new MyThread();
//						getViewTreeObserver().removeOnPreDrawListener(this);
//						return false;
//					}
//				});
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		canvas.drawColor(0xffc4bebf);
//		drawCoordinateLine(canvas);
		drawRectf(canvas);
	}

	/**
	 * 获取数据的状态
	 * @return	0,deep;1,light;2,sober
	 */
	public void setPaintState(String strDatas){
			if(strDatas!=null && strDatas.equals("0")){
				paint_pillar.setColor(colorDeepSleep);
			}else if(strDatas!=null && strDatas.equals("1")){
				paint_pillar.setColor(colorLightSleep);
			}else{
				paint_pillar.setColor(colorSober);
			}
	}


	/**
	 * 绘制矩形
	 */
	public void drawRectf(Canvas canvas) {
		float lastCoordinate = 0;
		float drawWidth = screenWidth - (2 * marginLeft);

		for (int i = 0; i < mScores.size(); i++) {
			setPaintState(mScores.get(i).type);
			float newCoordinate = 0;

			if(i == 0){
				newCoordinate = marginLeft;
				lastCoordinate = marginLeft + mScores.get(i).percent * drawWidth;
			}else{
				newCoordinate = lastCoordinate;
				lastCoordinate = newCoordinate + mScores.get(i).percent * drawWidth;
			}
//			Log.i("spoort_list",newCoordinate + ", " +  YPoint + ", " + lastCoordinate + ", " +   getHeight());

			float top;
			if(mScores.get(i).type.equals("0")){	//深睡
				top = (1-0.67f) * getHeight();
			}else if(mScores.get(i).type.equals("1")){	//浅睡
				top = (1-0.89f) * getHeight();
			}else{	//清醒
				top = (1-1) * getHeight();
			}
			mRectf.set(newCoordinate , top,
					lastCoordinate,  getHeight());
			canvas.drawRoundRect(mRectf, rectRadian, rectRadian, paint_pillar);
			canvas.save();
		}
	}

	/**
	 * 绘制坐标轴
	 */
	public void drawCoordinateLine(Canvas canvas) {
		canvas.drawLine(0, 0, getWidth(), 0, paint_pillar);
		canvas.drawLine(0, getHeight(), getWidth(), getHeight(), paint_pillar);
	}


	class MyThread implements Runnable {
		private Thread thread;
		private int statek;
		int count;

		public MyThread() {
			thread = new Thread(this);
			thread.start();
		}

		public void run() {
			while (true) {
				switch (statek) {
				case 0:	//动画启动等待时间200毫秒
					try {
						Thread.sleep(200);
						statek = 1;
					} catch (InterruptedException e) {
					}
					break;
				case 1:
					try {
						Thread.sleep(50);
						arc_y += 1;
						count++;
						postInvalidate();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;
				}
				if (count >= score_num)
					break;
			}
		}
	}

	public SleepTable(Context context) {
		super(context);
	}
	public SleepTable(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDetectionData(context,attrs);
	}
	public SleepTable(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setDetectionData(context,attrs);
	}
	/**
	 * 初始化数据
	 * @param context
	 * @param attrs
	 */
	public void setDetectionData(Context context, AttributeSet attrs) {
//		TypedArray attribute = context.obtainStyledAttributes(attrs, R.styleable.columnar_detection);
//		String ycoordinate = attribute.getString(R.styleable.columnar_detection_ycoordinate_data);
		String ycoordinate = "";
		yCoors = new ArrayList<Float>();
		String[] yCorrdinateArray = new String[7];
		if(ycoordinate.equals("")){ ycoordinate = "0,2.5,5.0,7.5,10,20,30"; }
		yCorrdinateArray = ycoordinate.split(",");
		for (int i = 0; i < yCorrdinateArray.length; i++) {
			yCoors.add(Float.parseFloat(yCorrdinateArray[i]));
		}
		
		init();
	}
	
	public Paint initPaint(Paint mPaint){
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(colorDeepSleep);
		mPaint.setStrokeWidth(lineWidth);	//画笔宽度1sp
		mPaint.setStyle(Style.FILL);	//实心
		mPaint.setTextSize(textSize);	//字号10sp
		mPaint.setTextAlign(Align.CENTER);
		return mPaint;
	}
	

	public void setData(List<SleepCTEntity> score){
		this.mScores = score;
		score_num = score.size() + 10;
		invalidate();
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
    				newValue = YPoint - 5;	//保证能绘制出来
    				return newValue;
    			}
    		}else if (y >= yCoors.get(i-1) && y <= yCoors.get(i)){
    			newValue = (float) (YPoint-((y-yCoors.get(i-1))/(yCoors.get(i)-yCoors.get(i-1))* marginHorizonal) - (i-1) * marginHorizonal);
    			return newValue;
    		}else if(y>yCoors.get(yCoors.size()-1)){	//比最大值大
    			newValue = YPoint - (yCoors.size()-1)*marginHorizonal;
    			return newValue;
    		}else{
//    			System.out.println("不满足，重新循环");
    		}

    	}
    	newValue = YPoint - 5;	//保证能绘制出来
    	return newValue;
    }
	
	//设置视图的大小
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);//获得控件的高度
		YPoint = height - marginBottom;
		setMeasuredDimension(screenWidth, height);
	}
	
	/** 屏幕的宽 */
	private int screenWidth;
	/** 屏幕的高 */
	private int screenHeight;
	
	List<SleepCTEntity> mScores;
	
	//**********************************动画
	private int arc_y = 0;
	private int score_num = 0;
	//**********************************动画 
	
	/** 绘制柱状图，柱状图上的数值文字 */
	private Paint paint_pillar;
	private int colorDeepSleep = 0xff1d9e8a;	//深睡
	private int colorLightSleep = 0xff1fbaa2;	//浅睡
	private int colorSober = 0xff22d7bb;	//清醒

	private float tb;
	RectF mRectf;
	/** 矩形边角弧度 */
	public float rectRadian = 0;

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
	
	/** y轴坐标值 */
	public List<Float> yCoors;
}
