package com.huidf.slimming.activity.personal_center.change_phone_number;

import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huidf.slimming.R;
import com.huidf.slimming.base.BaseFragmentActivity;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.entity.user.UserEntity;
import com.huidf.slimming.view.edittext_number.EditTextNumberView;

import org.xutils.http.RequestParams;

import java.util.HashMap;

import huitx.libztframework.context.ContextConstant;
import huitx.libztframework.interf.ConsultNet;
import huitx.libztframework.utils.NetUtils;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.PreferencesUtils;
import huitx.libztframework.utils.StringUtils;
import huitx.libztframework.utils.ToastUtils;

public class ChangePhoneNumberBaseActivity extends BaseFragmentActivity implements OnClickListener,ConsultNet{

	public UserEntity mUserEntity;
	public String phoneNumber;
	protected final int GETVERIFY = 100;
	protected final int CHANGEPHONENUMBER = 101;

	public ChangePhoneNumberBaseActivity(int layoutId) {
		super(layoutId);
	}

	@Override
	protected void initHead() {

	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	protected void initContent() {
		mBtnRight.setOnClickListener(this);
		setRightButtonText("完成",R.color.main_color);
		mBtnRight.setVisibility(View.GONE);
		findView();
	}


	@Override
	public void paddingDatas(String mData, int type) {
		setLoading(false,"");
		Gson mGson = new Gson();
		try{
			mUserEntity = mGson.fromJson(mData, UserEntity.class);
		}catch(Exception e){
			return;
		}
		if(mUserEntity.code == ContextConstant.RESPONSECODE_200){
			if(type == GETVERIFY){	//获取验证码
				mTimeCount.start();
			}else if(type == CHANGEPHONENUMBER){	//修改手机号
				PreferencesUtils.putString(mContext, PreferenceEntity.KEY_USER_ACCOUNT, phoneNumber);
				ToastUtils.showToast(mUserEntity.msg);
				setResult(200);
				finish();
			}
		} else if (mUserEntity.code == ContextConstant.RESPONSECODE_310) {    //登录信息过时跳转到登录页
			reLoading();
		} else {
			ToastUtils.showToast(NewWidgetSetting.getInstance().filtrationStringbuffer(mUserEntity.msg, "接口信息异常！"));
		}
	}

	@Override
	public void error(String msg, int type) {
		setLoading(false,"");
		if (!NetUtils.isAPNType(mContext)) {	//没网

		}else{
//			ToastUtils.showToast("操作失败，请稍候重试！");

		}
	}

	/** 获取验证码  */
	public void GetVerifyCode() {
		String mPhoneNumber = PreferencesUtils.getString(mContext, PreferenceEntity.KEY_USER_ACCOUNT);
		phoneNumber = et_cpna_account.getRealNumber();
		if(mPhoneNumber.equals(phoneNumber)){
			ToastUtils.showToast("手机号不能与原手机号重复！");
			return;
		}

		final StringBuilder url = new StringBuilder();
		url.append(UrlConstant.API_VERIFICATION);
		RequestParams params = new RequestParams();
		HashMap map = PreferenceEntity.getLoginData();
		params.addBodyParameter("imei",map.get("imei") + "");
		params.addBodyParameter("phone", phoneNumber);
		mgetNetData.GetData(this, url.toString(), GETVERIFY, params);
		setLoading(true,"");
	}


	/** 修改绑定手机号 */
	public void commitChangePhoneNumber(){

		RequestParams params = PreferenceEntity.getLoginParams();
        HashMap map = PreferenceEntity.getLoginData();
        params.addBodyParameter("imei",map.get("imei") + "");
		params.addBodyParameter("phone", phoneNumber);
		params.addBodyParameter("vd", et_cpnv_verify.getText().toString());
		mgetNetData.GetData(this, UrlConstant.API_CHANGE_PHONE, CHANGEPHONENUMBER, params);
		setLoading(true, "");
	}


	@Override
	protected void initLocation() {
//		mLayoutUtil.drawViewRBLinearLayout(iv_cpna_account, 100, 100, 0, -1, -1, -1);// 用户的头像！

	}
	
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	public void findView(){
		lin_change_pn_account =  findViewByIds(R.id.lin_change_pn_account);
		iv_cpna_account = findViewByIds(R.id.iv_cpna_account); 
		et_cpna_account = findViewByIds(R.id.et_cpna_account);
		lin_change_pn_verify = findViewByIds(R.id.lin_change_pn_verify);
		iv_cpnv_verify = findViewByIds(R.id.iv_cpnv_verify); 
		et_cpnv_verify = findViewByIds(R.id.et_cpnv_verify);
		tv_cpnv_verify = findViewByIds(R.id.tv_cpnv_verify);

		tv_cpnv_verify.setOnClickListener(this);
		tv_cpnv_verify.setEnabled(false);

		addTextchange();
	}


	private void addTextchange() {
		et_cpna_account.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start,
									  int before, int count) {
				if (s.length() > 0) {    //有内容
					LOG("有内容：" + (et_cpna_account.getRealNumber().toString()));
					if (StringUtils.isMobileNO(et_cpna_account.getRealNumber().toString()))
						tv_cpnv_verify.setEnabled(true);
					else tv_cpnv_verify.setEnabled(false);
				} else {    //无内容
					tv_cpnv_verify.setEnabled(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		et_cpnv_verify.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start,
									  int before, int count) {
				if (s.length() > 0) {    //有内容
					LOG("有内容：" + (et_cpnv_verify.getText().toString()));
					if (!phoneNumber.equals("") && et_cpnv_verify.getText().length() == 6)
						mBtnRight.setVisibility(View.VISIBLE);
					else mBtnRight.setVisibility(View.GONE);
				} else {    //无内容
					mBtnRight.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	protected TimeCount mTimeCount;

	/* 定义一个倒计时的内部类 */
	protected class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);

		}

		@Override
		public void onFinish() {
			tv_cpnv_verify.setText("获取验证码");
//			btn_register_veri.setClickable(true);
			tv_cpnv_verify.setEnabled(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			tv_cpnv_verify.setEnabled(false);
			tv_cpnv_verify.setText("" + millisUntilFinished / 1000 + "s");
		}

	}
	
	private LinearLayout lin_change_pn_account;
	private ImageView iv_cpna_account;
	protected EditTextNumberView et_cpna_account;
	private LinearLayout lin_change_pn_verify;
	private ImageView iv_cpnv_verify;
	private EditText et_cpnv_verify;
	private TextView tv_cpnv_verify;

	@Override
	protected void initLogic() { }
	
	@Override
	protected void destroyClose() { }

	@Override
	protected void pauseClose() { }

	@Override
	public void onClick(View arg0) { }

}
