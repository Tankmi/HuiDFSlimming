package com.huidf.slimming.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.dou361.dialogui.DialogUIUtils;
import com.huidf.slimming.R;
import com.huidf.slimming.activity.user.SelLoginActivity;
import com.huidf.slimming.context.ApplicationData;
import com.huidf.slimming.context.PreferenceEntity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import huitx.libztframework.interf.ConsultNet;
import huitx.libztframework.net.GetNetData;
import huitx.libztframework.utils.LOGUtils;
import huitx.libztframework.utils.LayoutUtil;
import huitx.libztframework.utils.ToastUtils;
import huitx.libztframework.utils.TransitionTime;
import huitx.libztframework.utils.image_loader.AnimateFirstDisplayListener;

public abstract class BaseFragment extends Fragment implements ConsultNet {
	protected View Mview; // 当前界面的根
	private int MlayoutId; // 当前界面对应的布局
	public Context mContext;	//上下文
	/** 获取当前类名 */
	public String TAG;
	
	/********************* 头部分控件 *************************/
//	public Button mBtnLeft; // 标题左边按钮
//	public TextView mTvTitle; // 标题
//	public Button mBtnRight; // 右边按钮
	/*****************************************************/
	/**
	 *	软键盘的处理
	 * 	imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);	//显示软键盘
	 *	imm.hideSoftInputFromWindow(et_sendmessage.getWindowToken(), 0); //强制隐藏键盘  
	 */
	public InputMethodManager imm;	//软键盘的处理
	/** 时间展示格式转换工具 */
	public TransitionTime tranTimes;
	
	//***************图片操作
	public ImageLoader imageLoader_base;
	public ImageLoadingListener animateFirstListener_base;
	//***************图片操作
	
	/** 网络请求 回调 */
	public GetNetData mgetNetData;
	
	//**************登录弹窗
	public AlertDialog.Builder login_dialog;
	// 布局
	public int screenWidth;
	public static int screenHeight;
	protected LayoutUtil mLayoutUtil;

	public BaseFragment(int layoutId,Context context) {
		super();
		this.MlayoutId = layoutId;	
		this.mContext = context;
	}
	
	public BaseFragment(int layoutId) {
		super();
		this.MlayoutId = layoutId;	
		mContext = ApplicationData.context;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Mview = View.inflate(getActivity(),MlayoutId, null);
//		view = inflater.inflate(layoutId, container, false);

		init(); // 初始化头中的各个控件,以及公共控件ImageLoader
		initHead(); // 初始化设置当前界面要显示的头状态
		initContent(); // 初始化当前界面的主要内容
		initLocation(); // 初始化空间位置
		initLogic(); // 初始化逻辑
		return Mview;
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
		imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);//初始化软键盘处理的方法
		
//		mBtnLeft = findViewByIds(R.id.btn_title_view_left);
//		mTvTitle = findViewByIds(R.id.tv_title_view_title);
//		mBtnRight = findViewByIds(R.id.btn_title_view_right);
//		if (mBtnLeft!=null) {
//		mBtnLeft.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					getActivity().onBackPressed();
//				}
//			});
//		}
		//初始化ImageLoader
		imageLoader_base = ImageLoader.getInstance();
		animateFirstListener_base = new AnimateFirstDisplayListener();
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


	protected Dialog mBuildDialog;

	protected  void setLoading(boolean isShowLoading) {
		setLoading(isShowLoading,"");
	}

	/** 显示或者隐藏，正在加载弹窗 */
	protected  void setLoading(boolean isShowLoading,String data) {
		if (isShowLoading) {
			if (mBuildDialog == null)
				mBuildDialog = DialogUIUtils.showLoading(mContext, data, true, true, false, true).show();
			else mBuildDialog.show();
		} else if (mBuildDialog != null) mBuildDialog.dismiss();
	}


	/** 重新登录 */
	protected void reLoading(){
		Intent intent = new Intent(mContext,SelLoginActivity.class);
		ToastUtils.showToast("登录信息异常，请重新登录");
		startActivity(intent);
		getActivity().finish();
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

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		pauseClose();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		destroyClose();
	}
	
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
//	protected void setTittle(String title) {
//		mTvTitle.setText(title);
//	}
//
//	protected void setTittle(String title,int color) {
//		mTvTitle.setText(title);
//		mTvTitle.setTextColor(color);
//	}
//
//	/**
//	 * 设置右边的字体内容
//	 */
//	public void setRightText(String text,int color) {
//		mBtnRight.setText(text);
//		mBtnRight.setTextColor(color);
//	}

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
	public void LOG(int data){
		LOG(TAG,data+"");
	}

	public void LOG(String data){
		LOG(TAG,data);
	}

	public void LOG(String tag, String data){
		LOGUtils.LOG(tag + data);
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if(getUserVisibleHint()){
			onVisibile();
		}else{
			onInVisibile();
		}
	}
	
	/** fragment可见时的操作 */
	protected void onVisibile(){}
	
	/** fragment不可见时的操作 */
	protected void onInVisibile(){}

	
}
