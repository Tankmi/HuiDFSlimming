package com.huidf.slimming.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.dou361.dialogui.DialogUIUtils;
import com.huidf.slimming.R;
import com.huidf.slimming.activity.user.SelLoginActivity;
import com.huidf.slimming.context.ApplicationData;
import com.huidf.slimming.context.PreferenceEntity;

import org.xutils.x;

import huitx.libztframework.context.ContextConstant;
import huitx.libztframework.interf.ConsultNet;
import huitx.libztframework.net.GetNetData;
import huitx.libztframework.utils.LOGUtils;
import huitx.libztframework.utils.LayoutUtil;
import huitx.libztframework.utils.NetUtils;
import huitx.libztframework.utils.ToastUtils;
import huitx.libztframework.utils.TransitionTime;

public abstract class BaseFragmentForAnnotation extends Fragment implements ConsultNet {
	public Context mContext;	//上下文
	/** 获取当前类名 */
	public String TAG;

	/**
	 *	软键盘的处理
	 * 	imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);	//显示软键盘
	 *	imm.hideSoftInputFromWindow(et_sendmessage.getWindowToken(), 0); //强制隐藏键盘
	 */
	public InputMethodManager imm;	//软键盘的处理
	/** 时间展示格式转换工具 */
	public TransitionTime tranTimes;


	/** 网络请求 回调 */
	public GetNetData mgetNetData;

	//**************登录弹窗
	protected LayoutUtil mLayoutUtil;

	public BaseFragmentForAnnotation( ) {
		super();
		mContext = ApplicationData.context;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return  x.view().inject(this, inflater, container);//绑定fragment注解
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init(); // 初始化头中的各个控件,以及公共控件ImageLoader
		initHead(); // 初始化设置当前界面要显示的头状态
		initContent(); // 初始化当前界面的主要内容
		initLocation(); // 初始化空间位置
		initLogic(); // 初始化逻辑
	}


	/**
	 * 初始化头中的各个控件,以及公共控件ImageLoader
	 * 
	 */
	protected void init() {

		mLayoutUtil = LayoutUtil.getInstance();
//		mgetNetData = GetNetData.getInstance();
		mgetNetData = new GetNetData();
		tranTimes = TransitionTime.getInstance();
		imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);//初始化软键盘处理的方法
	}
	
	protected Dialog mBuildDialog;

	protected  void setLoading(boolean isShowLoading) {
		setLoading(isShowLoading,"");
	}

	/** 显示或者隐藏，正在加载弹窗 */
	protected  void setLoading(boolean isShowLoading,String data) {
		if (isShowLoading) {
			if (mBuildDialog == null)
				mBuildDialog = DialogUIUtils.showLoading(getActivity(), data, true, true, false, true).show();
			else mBuildDialog.show();
		} else if (mBuildDialog != null) mBuildDialog.dismiss();
	}


	/** 重新登录 */
	protected void reLoading(){
		PreferenceEntity.clearData();
		ApplicationData.getInstance().exit();
		PreferenceEntity.isLogin = false;
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

	@Override
	public void error(String msg, int type)
	{   NetUtils.isAPNType(mContext);
		setLoading(false);
		if(msg.equals(ContextConstant.HTTPOVERTIME)){
			LOG("请求超时");
		}
	}

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
	 * 打印日志
	 * @param data	需要打印的内容
	 */
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
