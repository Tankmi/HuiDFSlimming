package com.huidf.slimming.base;

import android.app.Activity;
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

public abstract class BaseActivity extends Activity implements ConsultNet {

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
	protected LayoutUtil mLayoutUtil;

	public BaseActivity(int layoutId) {
		super();
		this.MlayoutId = layoutId;
		mContext = ApplicationData.context;
	}
	public BaseActivity(int layoutId, Context mContext) {
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
		}
		initContent(); // 初始化当前界面的主要内容
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
		if (mBtnLeft!=null) {
			mLayoutUtil.drawViewDefaultLayout(mBtnLeft, 120, 56, -1, 0, 0, 0);
			mBtnLeft.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {//实现返回键！
					imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),  0);
					BaseActivity.this.onBackPressed();
				}
			});
		}

      }

	/**
	 * 初始化各个控件*/
	protected void init() {
		mLayoutUtil = LayoutUtil.getInstance();
		mgetNetData = GetNetData.getInstance();
		tranTimes = TransitionTime.getInstance();
		imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);//初始化软键盘处理的方法
	}


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

	protected abstract void initHead();

	/**
	 * 初始化当前界面的主要内容,即除了头部以外的其它部分
	 */
	protected abstract void initContent();

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
				mBuildDialog = DialogUIUtils.showLoading(BaseActivity.this, data, true, true, false, true).show();
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
