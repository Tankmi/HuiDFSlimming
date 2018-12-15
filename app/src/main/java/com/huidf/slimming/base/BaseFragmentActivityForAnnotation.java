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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dou361.dialogui.DialogUIUtils;
import com.huidf.slimming.R;
import com.huidf.slimming.activity.user.SelLoginActivity;
import com.huidf.slimming.context.ApplicationData;
import com.huidf.slimming.context.PreferenceEntity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import huitx.libztframework.interf.ConsultNet;
import huitx.libztframework.net.GetNetData;
import huitx.libztframework.utils.LOGUtils;
import huitx.libztframework.utils.LayoutUtil;
import huitx.libztframework.utils.StatusBarCompat;
import huitx.libztframework.utils.ToastUtils;
import huitx.libztframework.utils.TransitionTime;
import huitx.libztframework.utils.image_loader.AnimateFirstDisplayListener;

public abstract class BaseFragmentActivityForAnnotation extends FragmentActivity implements ConsultNet {

	public Context mContext;	//上下文
	/** 获取当前类名 */
	public String TAG;

	/********************* 头部分控件 *************************/
	@ViewInject(R.id.iv_title_status_bar_fill)public ImageView iv_title_status_bar_fill;
	@ViewInject(R.id.title)public RelativeLayout mTitleView;
	@ViewInject(R.id.rel_title_view_main)public RelativeLayout rel_title_view_main;
	@ViewInject(R.id.btn_title_view_left)public Button mBtnLeft; // 标题左边按钮
	@ViewInject(R.id.tv_title_view_title)public TextView mTvTitle; // 标题
	@ViewInject(R.id.btn_title_view_right)public Button mBtnRight; // 右边按钮
	@ViewInject(R.id.iv_title_line) public ImageView mTitleLine; // 底部分割线


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

	/** 是否全屏 */
	private boolean isFit;
	protected LayoutUtil mLayoutUtil;

	public BaseFragmentActivityForAnnotation() {
		super();
		mContext = ApplicationData.context;
	}
	public BaseFragmentActivityForAnnotation(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		  if (savedInstanceState != null) {
			  LOG("非正常退出：" + savedInstanceState.getString("home_datas"));
        }

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
		initLocation(); // 初始化空间位置
		initLogic(); // 初始化逻辑
	}

	/**
     * 初始化标题栏的控件
     */
    public void initTitle(){
		if (mBtnLeft!=null) {
			mLayoutUtil.drawViewDefaultLayout(mBtnLeft, 120, 56, -1, 0, 0, 0);
		}
      }

      @Event(value={R.id.btn_title_view_left})
	  private void getEvent(View view)
	  {
		  switch(view.getId()) {
			  case R.id.btn_title_view_left:  //返回键
				  imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),  0);
				  BaseFragmentActivityForAnnotation.this.onBackPressed();
				  break;
		  }
	  }
	/**
	 * 初始化头中的各个控件,以及公共控件ImageLoader
	 *
	 */
	protected void init() {
		mLayoutUtil = LayoutUtil.getInstance();
		mgetNetData = GetNetData.getInstance();
		tranTimes = TransitionTime.getInstance();
		imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);//初始化软键盘处理的方法
	}

	/** 重新登录 */
	protected void reLoading(){
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
				mBuildDialog = DialogUIUtils.showLoading(mContext, data, true, true, false, true).show();
			else mBuildDialog.show();
		} else if (mBuildDialog != null) mBuildDialog.dismiss();
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
