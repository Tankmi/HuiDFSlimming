package com.huidf.slimming.activity.personal_center;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.huidf.slimming.R;
import com.huidf.slimming.base.BaseFragmentActivity;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.entity.user.UserEntity;

import org.xutils.http.RequestParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import huitx.libztframework.context.ContextConstant;
import huitx.libztframework.interf.ConsultNet;
import huitx.libztframework.utils.NetUtils;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.PreferencesUtils;
import huitx.libztframework.utils.ToastUtils;
import huitx.libztframework.utils.WidgetSetting;
import huitx.libztframework.utils.image_loader.ImageLoaderUtils;

public class UserInfoBaseActivity extends BaseFragmentActivity implements OnClickListener,OnCheckedChangeListener,ConsultNet,RadioGroup.OnCheckedChangeListener{

	public UserEntity mUserEntity;
	/** 修改头像 */
	public static final int Intent_Photo_100 = 100;
	/** 1男 2女 */
	public String userSex = "-1";

	/** 用户修改头像的地址 */
	public String userHeader;
	public String userNick;
	protected final int GETUSERINFO = 101;
	protected final int UPUSERINFO = 102;
	protected final int EDITHEADER = 103;

	public UserInfoBaseActivity(int layoutId) {
		super(layoutId);
	}

	@Override
	protected void initHead() {

	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	protected void initContent() {
		findView();
		setData(null);
	}

	public void setData(UserEntity.Data userInfo){

		if (userInfo == null) userInfo = UserEntity.getUserInfo();
		name = NewWidgetSetting.filtrationStringbuffer(userInfo.name,"") ;
		NewWidgetSetting.setViewText(et_user_info_name, name, "");
		NewWidgetSetting.setViewText(tv_user_info_phone_value, userInfo.account, "点击绑定手机号");
		NewWidgetSetting.setViewText(tv_user_info_bir_value, userInfo.birthday, "");
		NewWidgetSetting.setViewText(tv_user_info_bir_value, userInfo.birthday == null?"未设置":tranTimes.convert(userInfo.birthday, "yyyy年M月"), "");
		NewWidgetSetting.setViewText(tv_user_info_height_value," cm", userInfo.height, "00" ,false,false);
		NewWidgetSetting.setViewText(tv_user_info_start_weight_value, " kg", userInfo.weight, "00", false,false);
		setSexHeader();

		if(NewWidgetSetting.getInstance().notNull(et_user_info_name,"")){
			et_user_info_name.setSelection(et_user_info_name.getText().toString().length());}
	}
	private String user_header;
	/**
	 * 设置用户头像
	 */
	public void setSexHeader()
	{
		String sex = PreferencesUtils.getString(mContext, PreferenceEntity.KEY_USER_SEX);
        String header = PreferencesUtils.getString(mContext, PreferenceEntity.KEY_USER_HEADER);
		if (user_header != null && header != null && user_header.equals(header)) {    //头像没做修改
			return;
		}
		user_header = header;
		RequestOptions options;
		if (sex != null && sex.equals("1")) {    //男
			options = new RequestOptions()
					.placeholder(R.drawable.iv_man_bef)
					.circleCrop();
		} else {
			options = new RequestOptions()
					.placeholder(R.drawable.iv_woman_bef)
					.circleCrop();
		}

		Glide.with(mContext).load(user_header).apply(options).into(iv_user_info_header);

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
			if(type == GETUSERINFO){	//获取用户信息
				Message message = Message.obtain();
				message.what = type;
				message.obj = mUserEntity.data;
				mHandler.sendMessage(message);
			}else if(type == UPUSERINFO){	//修改用户信息
				PreferencesUtils.putString(mContext, PreferenceEntity.KEY_USER_NICK, userNick + "");
				ToastUtils.showToast(mUserEntity.msg);
				btn_user_info_name_clear.setVisibility(View.GONE);
				tv_user_info_commit.setVisibility(View.GONE);
				setData(null);
			}else if(type == EDITHEADER){	//修改头像
//				imageLoader_base.displayImage(userHeader,iv_user_info_header,
//						ImageLoaderUtils.setImageOptionsLoadImg(mContext.getResources().getDrawable(R.drawable.iv_man_bef), 2),
//						animateFirstListener_base);
				ToastUtils.showToast(mUserEntity.msg);
				PreferencesUtils.putString(mContext, PreferenceEntity.KEY_USER_HEADER, userHeader);
				setSexHeader();
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

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case GETUSERINFO:	//获取用户信息
				
				UserEntity.Data user_Entity = (UserEntity.Data) msg.obj;
				setData(user_Entity);

				break;
			case 1:
				
				break;
			}
		}
		
	};
	

	/** 获取用户的信息  */
	public void GetUserInfo() {
		RequestParams params = PreferenceEntity.getLoginParams();
		mgetNetData.GetData(this, UrlConstant.GET_PERSONAL_CENTER, GETUSERINFO, params);
		setLoading(true, "");
	}
	
	/** 上传用户信息 */
	public void updateUserInfo(String name,String value){
		RequestParams params = PreferenceEntity.getLoginParams();
		params.addBodyParameter(name, value);
		mgetNetData.GetData(this, UrlConstant.API_UPDATEPERSONAL, UPUSERINFO, params);
		setLoading(true, "");
	}


	/**  修改头像 */
	public void uploadingCredentials(String path) throws Exception
	{
		RequestParams params = PreferenceEntity.getLoginParams();
		File file = new File(path);
		if (file.exists() && file.length() > 0) {
			params.addBodyParameter("header", file);
		}else {
			ToastUtils.showToast("头像选择失败，请重试！");
			return;
		}
		mgetNetData.GetData(this, UrlConstant.API_USER_CHANGEHEADER, EDITHEADER, params);
		setLoading(true, "");
	}

	@Override
	protected void initLocation() {
		mLayoutUtil.drawViewRBLinearLayout(iv_user_info_header, 100, 100, 0, -1, -1, -1);// 用户的头像！
		mLayoutUtil.drawViewRBLinearLayout(btn_user_info_name_clear, 20, 20, 0, -1, -1, -1);// 用户的头像！

		tv_user_info_commit.setMinWidth(mLayoutUtil.getWidgetWidth(332));
	}
	
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	public void findView(){
		lin_user_info_header =  findViewByIds(R.id.lin_user_info_header);
		iv_user_info_header = findViewByIds(R.id.iv_user_info_header); 
		lin_user_info_name = findViewByIds(R.id.lin_user_info_name); 
		et_user_info_name = findViewByIds(R.id.et_user_info_name); 
		btn_user_info_name_clear = findViewByIds(R.id.btn_user_info_name_clear); 
		lin_user_info_phone = findViewByIds(R.id.lin_user_info_phone);
		tv_user_info_phone_value = findViewByIds(R.id.tv_user_info_phone_value); 
		lin_user_info_sex =  findViewByIds(R.id.lin_user_info_sex); 
		tv_user_info_sex_value = findViewByIds(R.id.tv_user_info_sex_value); 
		tv_user_info_bir_value = findViewByIds(R.id.tv_user_info_bir_value);
		tv_user_info_height_value = findViewByIds(R.id.tv_user_info_height_value);
		tv_user_info_start_weight_value = findViewByIds(R.id.tv_user_info_start_weight_value);
		tv_user_info_commit = findViewByIds(R.id.tv_user_info_commit);

		lin_user_info_header.setOnClickListener(this);
		lin_user_info_phone.setOnClickListener(this);
		btn_user_info_name_clear.setOnClickListener(this);
		tv_user_info_commit.setOnClickListener(this);

		tv_user_info_commit.setVisibility(View.GONE);
		btn_user_info_name_clear.setVisibility(View.GONE);
		addTextchange();
	}


	private String name;
	private void addTextchange() {
		et_user_info_name.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int before, int count)
			{
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
			{

			}

			@Override
			public void afterTextChanged(Editable s)
			{
				if (s.length() > 0 && !et_user_info_name.getText().toString().equals(name)) {    //有内容,且内容已有改变
					btn_user_info_name_clear.setVisibility(View.VISIBLE);
					tv_user_info_commit.setVisibility(View.VISIBLE);
				} else {    //无内容
					btn_user_info_name_clear.setVisibility(View.GONE);
					tv_user_info_commit.setVisibility(View.GONE);
				}
			}
		});
	}
	
	private LinearLayout lin_user_info_header;
	protected ImageView iv_user_info_header;
	private LinearLayout lin_user_info_name;
	protected EditText et_user_info_name;
	private Button btn_user_info_name_clear;
	private LinearLayout lin_user_info_phone;
	private TextView tv_user_info_phone_value;
	private LinearLayout lin_user_info_sex;
	private TextView tv_user_info_sex_value;
	private TextView tv_user_info_bir_value;
	private TextView tv_user_info_height_value;
	private TextView tv_user_info_start_weight_value;
	private TextView  tv_user_info_commit;

	@Override
	protected void initLogic() { }
	
	@Override
	protected void destroyClose() { }

	@Override
	protected void pauseClose() { }

	@Override
	public void onClick(View arg0) { }
	
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) { }

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) { }


//	private EditHealthHistoryFragment prefectInfoFragment;
	private FragmentManager fragmentManager;
	private String MAIN_CONTENT_TAG = "main_content";
	/**
	 * 显示或者隐藏绑定手机号页
	 */
	protected void ShowOrHidePrefectInfoView(boolean isShow) {
//		if (prefectInfoFragment == null){
//			prefectInfoFragment = new EditHealthHistoryFragment(Integer.parseInt((String) rel_user_setting_health.getTag(R.id.is_diabetesState)),
//					Integer.parseInt((String) rel_user_setting_health.getTag(R.id.is_bpressureState)),Integer.parseInt((String) rel_user_setting_health.getTag(R.id.is_nephrosisState)));
//			prefectInfoFragment.setOnEditHealthHistoryListener(this);
//		}
//		if (fragmentManager == null) fragmentManager = getSupportFragmentManager();
//		FragmentTransaction fragmentTran = fragmentManager.beginTransaction();
//		if (isShow) {
//			if (prefectInfoFragment.isAdded()) fragmentTran.show(prefectInfoFragment);
//			else fragmentTran.replace(R.id.fram_user_setting, prefectInfoFragment, MAIN_CONTENT_TAG);
//			fragmentTran.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//			fragmentTran.addToBackStack(null);
//		} else if (prefectInfoFragment.isVisible()) fragmentTran.hide(prefectInfoFragment);
//		else finish();  //没有显示的话，直接finish
//		fragmentTran.commitAllowingStateLoss();
	}

}
