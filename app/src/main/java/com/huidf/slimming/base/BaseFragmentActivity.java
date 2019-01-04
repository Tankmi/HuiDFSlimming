package com.huidf.slimming.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dou361.dialogui.DialogUIUtils;
import com.huidf.slimming.R;
import com.huidf.slimming.activity.personal_center.set.UserSetActivity;
import com.huidf.slimming.activity.user.SelLoginActivity;
import com.huidf.slimming.context.ApplicationData;
import com.huidf.slimming.context.PreferenceEntity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import huitx.libztframework.context.ContextConstant;
import huitx.libztframework.interf.ConsultNet;
import huitx.libztframework.net.GetNetData;
import huitx.libztframework.utils.LOGUtils;
import huitx.libztframework.utils.LayoutUtil;
import huitx.libztframework.utils.NetUtils;
import huitx.libztframework.utils.StatusBarCompat;
import huitx.libztframework.utils.ToastUtils;
import huitx.libztframework.utils.TransitionTime;
import huitx.libztframework.utils.image_loader.AnimateFirstDisplayListener;

public abstract class BaseFragmentActivity extends FragmentActivity implements ConsultNet {

	protected View Mview; // 当前界面的根
	private int MlayoutId; // 当前界面对应的布局
	public Context mContext;	//上下文
	/** 获取当前类名 */
	public String TAG;

	/********************* 头部分控件 *************************/
	public ImageView iv_title_status_bar_fill;
	public RelativeLayout mTitleView;
	public RelativeLayout rel_title_view_main;
	public Button mBtnLeft; // 标题左边按钮
	public TextView mTvTitle; // 标题
	public Button mBtnRight; // 右边按钮
	public ImageView mTitleLine; // 底部分割线

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

	/** 是否全屏 */
	private boolean isFit;
	// 布局
	public int screenWidth;
	public int screenHeight;
	protected LayoutUtil mLayoutUtil;

	public BaseFragmentActivity(int layoutId) {
		super();
		this.MlayoutId = layoutId;
		mContext = ApplicationData.context;
	}
	public BaseFragmentActivity(int layoutId,Context mContext) {
		super();
		this.MlayoutId = layoutId;
		this.mContext = mContext;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Mview = View.inflate(this,MlayoutId, null);
		  if (savedInstanceState != null) {
			  LOG("非正常退出：" + savedInstanceState.getString("home_datas"));
        }

//		view = LayoutInflater.from(this).inflate(layoutId, null);
//		Mview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);  //隐藏虚拟键盘，自适应
		setContentView(Mview);
		ApplicationData.getInstance().addActivity(this);
		Log.i("打开的页面", this.getClass().getName());
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);	// 设置竖屏加载
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 设置横屏加载
		init(); // 初始化头中的各个控件,以及公共控件ImageLoader
		initTitle();//初始化标题栏的控件
		initHead(); // 初始化设置当前界面要显示的头状态
		if(isFit){	//全屏时添加一个占位的textview
			iv_title_status_bar_fill.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_title_status_bar_fill.getLayoutParams();
			params.height = (int) PreferenceEntity.ScreenTop;
			iv_title_status_bar_fill.setLayoutParams(params);
//			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) title.getLayoutParams();
//			params.topMargin = (int)PreferenceEntity.ScreenTop;
//			title.setLayoutParams(params);
		}
		initContent(); // 初始化当前界面的主要内容
		initLocation(); // 初始化空间位置
		initLogic(); // 初始化逻辑
	}

	/**
     * 初始化标题栏的控件
     */
    public void initTitle(){
//		setStatusBarColor(getResources().getColor(R.color.title_color_bg));
		iv_title_status_bar_fill = (ImageView) Mview.findViewById(R.id.iv_title_status_bar_fill);
		mTitleView = (RelativeLayout) Mview.findViewById(R.id.title);
		rel_title_view_main = (RelativeLayout) Mview.findViewById(R.id.rel_title_view_main);
    	mBtnLeft = (Button) Mview.findViewById(R.id.btn_title_view_left);
		mTvTitle = (TextView) Mview.findViewById(R.id.tv_title_view_title);
		mBtnRight = (Button) Mview.findViewById(R.id.btn_title_view_right);
		mTitleLine = Mview.findViewById(R.id.iv_title_line);
//		mLayoutUtil.drawViewHomeRBLayout(rel_title_view_main, 0, 94, 0, 0, 0, 0);	//涉及到全屏时需要填充状态栏，所以不能设置title这个view为标题栏的高度
		if (mBtnLeft!=null) {
			mLayoutUtil.drawViewDefaultLayout(mBtnLeft, 120, 56, -1, 0, 0, 0);
			mBtnLeft.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {//实现返回键！
					imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),  0);
					BaseFragmentActivity.this.onBackPressed();
				}
			});
		}

      }
	/**
	 * 初始化头中的各个控件,以及公共控件ImageLoader
	 *
	 */
	protected void init() {
		//初始化布局参数
		screenWidth = PreferenceEntity.screenWidth;
		screenHeight = PreferenceEntity.screenHeight;

		mLayoutUtil = LayoutUtil.getInstance();
		mgetNetData = GetNetData.getInstance();
		tranTimes = TransitionTime.getInstance();
		imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);//初始化软键盘处理的方法

		//初始化ImageLoader
		imageLoader_base = ImageLoader.getInstance();
		animateFirstListener_base = new AnimateFirstDisplayListener();
	}

	/**
	 * 初始化登录弹窗
	 */
//	protected void initDialog(final Context mContext){
//		login_dialog = new AlertDialog.Builder(mContext);
//		login_dialog.setIcon(R.drawable.app_icon);
//		login_dialog.setTitle(R.string.login_hint_title);
//		login_dialog.setMessage(R.string.login_hint_body);
//		login_dialog.setPositiveButton("登录", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog,int which) {
//				ToastUtils.showToast("执行登录操作！需要完善");
//			}
//		});
//		login_dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface arg0, int arg1) {
//
//			}
//		});
//	}

	/** 重新登录 */
	protected void reLoading(){
		PreferenceEntity.clearData();
		ApplicationData.getInstance().exit();
		PreferenceEntity.isLogin = false;
		Intent intent = new Intent(mContext,SelLoginActivity.class);
		ToastUtils.showToast("登录信息异常，请重新登录");
		startActivity(intent);
		finish();
	}

	/**
	 * 判断是否登录
	 * @return 登录返回true，否则返回false
	 */
	public boolean isLogin(){
		return PreferenceEntity.isLogin;
	}




	/**
	 * 初始化当前界面的主要内容,即除了头部以外的其它部分
	 */
	protected abstract void initContent();

	/**
	 * 初始化控件位置
	 */
	protected abstract void initLocation();


	protected abstract void initHead();

	/**
	 * 初始化逻辑
	 */
	protected abstract void initLogic();

	/**
	 * pause关闭方法
	 */
	protected abstract void pauseClose();

	/**
	 * destroy关闭方法
	 */
	protected abstract void destroyClose();


	/**
	 * 设置当前界面所对应头的标题
	 *
	 * @param title
	 */
	protected void setTittle(String title,int color) {
		mTvTitle.setText(title);
		mTvTitle.setTextColor(mContext.getResources().getColor(color));
	}

	/** 隐藏底部分割线 */
	protected void setHideTitleLine(){
		mTitleLine.setVisibility(View.GONE);
	}

	/**
	 * 设置当前界面所对应头的标题
	 *
	 * @param title
	 */
	protected void setTittle(String title) {
		mTvTitle.setText(title);
	}

	protected void setRightButtonText(String text,int color){
		mBtnRight.setText(text);
		mBtnRight.setTextColor(mContext.getResources().getColor(color));
		mBtnRight.setVisibility(View.VISIBLE);
	}

	protected void setTitleBackgroudColor(int color){
		rel_title_view_main.setBackgroundColor(mContext.getResources().getColor(color));
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

	public <T> T findViewByIds(View view,int viewId) {
		return (T) view.findViewById(viewId);
	}

	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  FragmentManager fm = getSupportFragmentManager();
	  int index = requestCode >> 16;
	  if (index != 0) {
	   index--;
	   if (fm.getFragments() == null || index < 0
	     || index >= fm.getFragments().size()) {
	    Log.w(TAG, "Activity result fragment index out of range: 0x"
	      + Integer.toHexString(requestCode));
	    return;
	   }
	   Fragment frag = fm.getFragments().get(index);
	   if (frag == null) {
	    Log.w(TAG, "Activity result no fragment exists for index: 0x"
	      + Integer.toHexString(requestCode));
	   } else {
	    handleResult(frag, requestCode, resultCode, data);
	   }
	   return;
	  }

	 }

	 /**
	  * 递归调用，对所有子Fragement生效
	  *
	  * @param frag
	  * @param requestCode
	  * @param resultCode
	  * @param data
	  */
	 private void handleResult(Fragment frag, int requestCode, int resultCode,
							   Intent data) {
	  frag.onActivityResult(requestCode & 0xffff, resultCode, data);
	  List<Fragment> frags = frag.getChildFragmentManager().getFragments();
	  if (frags != null) {
	   for (Fragment f : frags) {
	    if (f != null)
	     handleResult(f, requestCode, resultCode, data);
	   }
	  }
	 }


	/**
	 * 打印日志
	 * @param data	需要打印的内容
	 */
	public void LOG(String data){
		LOG(TAG,data);
	}

	public void LOG(String tag, String data){
		LOGUtils.LOG(tag + " " + data);
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
	 * 设置状态栏颜色，默认透明
	 * @param  isFit: 是否是全屏显示	5.0以上的系统需要此属性判断状态栏的属性设置
	 * @param  isSet: 是否设置特殊颜色，否的话，第三个参数可以设置为0
	 */
	public void setStatusBarColor(boolean isFit,boolean isSet,int color){
		this.isFit = isFit;
		if(isSet) StatusBarCompat.compat(this,color,isFit);
		else StatusBarCompat.compat(this,mContext.getResources().getColor(R.color.status_bar_default_bg),isFit);
	}

	/**
	 * 设置状态栏颜色，默认透明
	 * @param  isSet: 是否设置特殊颜色，是的话设置color字段的颜色
	 */
	public void setStatusBarColor(boolean isSet,int color){
		setStatusBarColor(false,isSet,color);
	}


	protected Dialog mBuildDialog;
	protected Boolean isShowLoading = false;
	protected  void setLoading(boolean isShowLoading) {
		setLoading(isShowLoading,"");
	}
	/** 显示或者隐藏，正在加载弹窗 */
	protected  void setLoading(boolean isShowLoading,String data) {
		this.isShowLoading = isShowLoading;
		if (isShowLoading) {
			if (mBuildDialog == null)
				mBuildDialog = DialogUIUtils.showLoading(BaseFragmentActivity.this, data, true, true, false, true).show();
			else mBuildDialog.show();
		} else if (mBuildDialog != null) mBuildDialog.dismiss();
	}

	@Override
	public void error(String msg, int type)
	{   NetUtils.isAPNType(mContext);
		setLoading(false);
		if(msg.equals(ContextConstant.HTTPOVERTIME)){
			LOG("请求超时");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		pauseClose();
	}

	@Override
	protected void onDestroy() {
		LOG("onDestroy");
		super.onDestroy();
		setContentView(R.layout.view_null);
		destroyClose();
	}
}
