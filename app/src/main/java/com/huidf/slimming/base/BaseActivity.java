package com.huidf.slimming.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.ViewUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huidf.slimming.R;
import com.huidf.slimming.context.ApplicationData;
import com.huidf.slimming.context.PreferenceEntity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import huitx.libztframework.interf.ConsultNet;
import huitx.libztframework.net.GetNetData;
import huitx.libztframework.utils.LayoutUtil;
import huitx.libztframework.utils.ToastUtils;
import huitx.libztframework.utils.TransitionTime;
import huitx.libztframework.utils.image_loader.AnimateFirstDisplayListener;

public abstract class BaseActivity extends Activity implements ConsultNet {
	
	protected View Mview; // 当前界面的根
	private int MlayoutId; // 当前界面对应的布局
	public Context mContext;	//上下文
	/** 获取当前类名 */
	public String TAG;
	
	/********************* 头部分控件 *************************/
	public RelativeLayout title;
	public Button mBtnLeft; // 标题左边按钮
	public TextView tv_title_view_left_text; // 返回按钮提示文字（聊天也用于圈子人数展示）
	public TextView mTvTitle; // 标题
	public Button mBtnRight; // 右边按钮
	/*****************************************************/
	
	//***************图片操作
	public ImageLoader imageLoader_base;
	public ImageLoadingListener animateFirstListener_base;
	//***************图片操作
	
	/** 时间展示格式转换工具 */
	public TransitionTime tranTimes;
	
	/** 网络请求 回调 */
	public GetNetData mgetNetData;
	
	/**
	 *	软键盘的处理
	 * 	imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);	//显示软键盘
	 *	imm.hideSoftInputFromWindow(et_sendmessage.getWindowToken(), 0); //强制隐藏键盘  
	 */
	public InputMethodManager imm;	
	
	//**************登录弹窗
	public AlertDialog.Builder login_dialog;
	
	// 布局
	public int screenWidth;
	public int screenHeight;
	protected LayoutUtil mLayoutUtil;
	
	public BaseActivity(int layoutId,Context context) {
		super();
		this.MlayoutId = layoutId;
		this.mContext = context;
	}
	
	public BaseActivity(int layoutId) {
		super();
		this.mContext = ApplicationData.context;
		this.MlayoutId = layoutId;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Mview = View.inflate(this,MlayoutId, null);
//		view = LayoutInflater.from(this).inflate(layoutId, null);
		setContentView(Mview);
		ApplicationData.getInstance().addActivity(this);
		/** 设置竖屏加载 */
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		init(); // 初始化头中的各个控件,以及公共控件ImageLoader
		initHead(); // 初始化设置当前界面要显示的头状态，以及视图内容
		initContent(); // 初始化当前界面的主要内容
		initLocation(); // 初始化空间位置
		initLogic(); // 初始化逻辑
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		pauseClose();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		setContentView(R.layout.view_null);
		destroyClose();
	}
	
	/**
	 * destroy关闭方法
	 */
	protected abstract void destroyClose();
	
	/**
	 * pause关闭方法
	 */
	protected abstract void pauseClose();
	
	/**
	 * 初始化头中的各个控件,以及公共控件ImageLoader
	 * 
	 */
	protected void init() {
		//初始化软键盘操作
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		//初始化布局参数
		screenWidth = PreferenceEntity.screenWidth;
		screenHeight = PreferenceEntity.screenHeight;

		mLayoutUtil = LayoutUtil.getInstance();
		mgetNetData = GetNetData.getInstance();
		tranTimes = TransitionTime.getInstance();
		imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);//初始化软键盘处理的方法
		
		setStatusBarColor(getResources().getColor(R.color.title_color_bg));
		title = (RelativeLayout) Mview.findViewById(R.id.title);
		mBtnLeft = findViewByIds(R.id.btn_title_view_left);
		tv_title_view_left_text = findViewByIds(R.id.tv_title_view_left_text);
		mTvTitle = findViewByIds(R.id.tv_title_view_title);
		mBtnRight = findViewByIds(R.id.btn_title_view_right);
		mLayoutUtil.drawViewRBLayout(title, 0, 94, 0.0f, 0.0f, 0, 0);
		if (mBtnLeft!=null) {
			mLayoutUtil.drawViewLayouts(mBtnLeft, 0.061f, 0.034f, 0.021f, 0.0f);
		}
		if (mBtnRight!=null) {
			mLayoutUtil.drawViewlLayouts(mBtnRight, 0.0f, 0.0f,0.025f, 0.0f);
		}
		if (tv_title_view_left_text!=null) {
			mLayoutUtil.drawViewLayouts(tv_title_view_left_text, 0.0f, 0.0f, 0.023f, 0.0f);
		}
		mBtnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				BaseActivity.this.onBackPressed();
			}
		});
		//初始化ImageLoader
		imageLoader_base = ImageLoader.getInstance();
		animateFirstListener_base = new AnimateFirstDisplayListener();
	}
	
	/**
	 * 设置状态栏背景色
	 * @param color
	 */
	public void setStatusBarColor(int color){
		View view_title = findViewByIds(R.id.title);
		view_title.setBackgroundColor(color);
		//设置状态栏透明，这个方法只有在安卓4.4以上才能起作用！
        if (android.os.Build.VERSION.SDK_INT > 18) {
//        	LOG("首页设置状态栏透明");
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // 创建TextView
            TextView textView = new TextView(this);
            LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, (int) PreferenceEntity.ScreenTop);
            textView.setBackgroundColor(color);
//            textView.setBackgroundColor(Color.parseColor("#f03069"));
            textView.setLayoutParams(lParams);
            // 获得根视图并把TextView加进去。
            ViewGroup view = (ViewGroup) getWindow().getDecorView();
            view.addView(textView);
        }
	}
	
	/**
	 * 初始化登录弹窗
	 */
	protected void initDialog(final Context mContext){
		login_dialog = new AlertDialog.Builder(mContext);
		login_dialog.setIcon(R.mipmap.ic_launcher);
		login_dialog.setTitle(R.string.login_hint_title);
		login_dialog.setMessage(R.string.login_hint_body);
		login_dialog.setPositiveButton("登录", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog,int which) {
				ToastUtils.showToast("执行登录操作！需要完善");
			}
		});
		login_dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				
			}
		});
		
	}

	/**
	 * 判断是否登录
	 * @return 登录返回true，否则返回false
	 */
	public boolean isLogin(){
		return PreferenceEntity.isLogin;
	}
	
	protected abstract void initHead();

	/**
	 * 初始化当前界面的主要内容,即除了头部以外的其它部分
	 */
	protected abstract void initContent();

	/**
	 * 初始化控件位置
	 */
	protected abstract void initLocation();

	/**
	 * 初始化逻辑
	 */
	protected abstract void initLogic();

	/**
	 * 设置当前界面所对应头的标题
	 * 
	 * @param title
	 */
	protected void setTittle(String title) {
		mTvTitle.setText(title);
	}

	/**
	 * 隐藏左边返回键
	 */
	public void setLeftButtonVisibleGone() {
		mBtnLeft.setVisibility(View.GONE);
	}
	
	/**
	 * 显示右边按钮
	 */
	public void setRightButtonVisible() {
		mBtnRight.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 设置右边的字体内容
	 */
	public void setRightText(String text) {
		mBtnRight.setText(text);
	}

	/**
	 * 设置右边的字体颜色
	 */
	public void setRightTextColor(int color) {
		mBtnRight.setTextColor(color);
	}

	/**
	 * 避免每次都进行强转
	 * 
	 * @param viewId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T findViewByIds(int viewId) {
		return (T) Mview.findViewById(viewId);
	}
	
	/**
	 * 打印日志
	 * @param data	需要打印的内容
	 */
	public void LOG(String data){
		Log.i("spoort_list",TAG + ": " + data + "");
	}
	
}
