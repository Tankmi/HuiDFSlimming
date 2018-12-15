package com.huidf.slimming.activity.home.weight.history;

import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import com.google.gson.Gson;
import com.huidf.slimming.R;
import com.huidf.slimming.base.BaseFragmentActivityForAnnotation;
import com.huidf.slimming.entity.home.weight.WeightHistoryTableEntity;
import com.huidf.slimming.entity.user.UserEntity;
import com.huidf.slimming.view.home.weight.WeightHistoryView;
import com.huidf.slimming.view.home.weight.YCoordForWeight;

import org.xutils.view.annotation.ViewInject;
import java.util.ArrayList;
import java.util.List;

import huitx.libztframework.utils.NetUtils;

public class WeightHistoryBaseActivity extends BaseFragmentActivityForAnnotation{

//	@ViewInject(R.id.views_weighthis_day) private LinearLayout views_weighthis_day;
	@ViewInject(R.id.view_ycoord_weight_his) private YCoordForWeight view_ycoord_weight_his;
	@ViewInject(R.id.horview_weight_his) private HorizontalScrollView horview_weight_his;
	@ViewInject(R.id.view_weight_hsi) private WeightHistoryView view_weight_hsi;

    protected UserEntity mUserEntity;

	public WeightHistoryBaseActivity( ) {
		super( );
	}

	@Override
	protected void initHead() {

	}

	@Override
	protected void initContent() {
//		views_weighthis_day.inflate();
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
//		if(mUserEntity.code == ContextConstant.RESPONSECODE_200){
//			if(type == GETUSERINFO){	//获取用户信息
//				Message message = Message.obtain();
//				message.what = type;
//				message.obj = mUserEntity.data;
//				mHandler.sendMessage(message);
//			}else if(type == UPUSERINFO){	//修改用户信息
//				PreferencesUtils.putString(mContext, PreferenceEntity.KEY_USER_NICK, userNick + "");
//				ToastUtils.showToast(mUserEntity.msg);
//				btn_user_info_name_clear.setVisibility(View.GONE);
//				tv_user_info_commit.setVisibility(View.GONE);
//				setData(null);
//			}else if(type == EDITHEADER){	//修改头像
////				imageLoader_base.displayImage(userHeader,iv_user_info_header,
////						ImageLoaderUtils.setImageOptionsLoadImg(mContext.getResources().getDrawable(R.drawable.iv_man_bef), 2),
////						animateFirstListener_base);
//				ToastUtils.showToast(mUserEntity.msg);
//				PreferencesUtils.putString(mContext, PreferenceEntity.KEY_USER_HEADER, userHeader);
//				setSexHeader();
//			}
//		} else if (mUserEntity.code == ContextConstant.RESPONSECODE_310) {    //登录信息过时跳转到登录页
//			reLoading();
//		} else {
//			ToastUtils.showToast(NewWidgetSetting.getInstance().filtrationStringbuffer(mUserEntity.msg, "接口信息异常！"));
//		}
	}

	@Override
	public void error(String msg, int type) {
		setLoading(false,"");
		if (!NetUtils.isAPNType(mContext)) {	//没网

		}else{
//			ToastUtils.showToast("操作失败，请稍候重试！");

		}
	}


//	/** 获取用户的信息  */
//	public void GetUserInfo() {
//		RequestParams params = PreferenceEntity.getLoginParams();
//		mgetNetData.GetData(this, UrlConstant.GET_PERSONAL_CENTER, GETUSERINFO, params);
//		setLoading(true, "");
//	}


	@Override
	protected void initLocation() {
//		mLayoutUtil.drawViewRBLinearLayout(view_ycoord_weight_his, 80, 397, 20, 0, 0, 0);
//		mLayoutUtil.drawViewRBLinearLayout(horview_weight_his, 0, 0, 0, 32, 0, 0);
//		mLayoutUtil.drawViewRBLinearLayout(view_weight_hsi, 0, 397, 0, 0, 0, 0);
	}

	@Override
	protected void initLogic() {
		List<WeightHistoryTableEntity> mDrawlist = new ArrayList<WeightHistoryTableEntity>();
		WeightHistoryTableEntity lineData;
//                    for (int i = 0; i<mData.list3.size(); i++) {
//		if(mData.list3 != null && mData.list3.size()>0)
//			for (SprotHistoryEntity.Data.SportWeights.SportInfo Data : mData.list3.get(0).list) {
//				lineData = new PerCenSportTableEntity();
//				lineData.date = tranTimes.convert(Data.createTime, "M/d");
//				lineData.date1 = tranTimes.convert(Data.createTime, "yyyy");
////                lineData.percent = Float.parseFloat("" + 10000 * Math.random());
//				lineData.score = Float.parseFloat("" + Data.count1);
////                lineData.type = "" + 1;
//				mDrawlist.add(lineData);
//			}
		lineData = new WeightHistoryTableEntity();
		lineData.date = tranTimes.convert(System.currentTimeMillis(), "M/d");
		lineData.date1 = tranTimes.convert(System.currentTimeMillis(), "yyyy");
//                lineData.percent = Float.parseFloat("" + 10000 * Math.random());
		lineData.score = Float.parseFloat("" + 60);
//                lineData.type = "" + 1;
		mDrawlist.add(lineData);
		mDrawlist.add(lineData);
		mDrawlist.add(lineData);
		mDrawlist.add(lineData);
		if(mDrawlist!=null && mDrawlist.size()>0) {
//			views_weighthis_day.setVisibility(View.VISIBLE);
			LOG("setData");
			view_weight_hsi.setData(mDrawlist);
		}
//		else views_weighthis_day.setVisibility(View.GONE);
	}


	@Override
	protected void destroyClose() { }

	@Override
	protected void pauseClose() { }

}
