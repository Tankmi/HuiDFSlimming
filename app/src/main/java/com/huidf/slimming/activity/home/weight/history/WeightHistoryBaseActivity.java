package com.huidf.slimming.activity.home.weight.history;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.HorizontalScrollView;
import com.google.gson.Gson;
import com.huidf.slimming.R;
import com.huidf.slimming.base.BaseFragmentActivityForAnnotation;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.entity.home.weight.WeightHistoryTableEntity;
import com.huidf.slimming.view.home.weight.WeightHistoryView;
import com.huidf.slimming.view.home.weight.YCoordForWeight;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import huitx.libztframework.context.ContextConstant;
import huitx.libztframework.utils.NetUtils;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.ToastUtils;

public class WeightHistoryBaseActivity extends BaseFragmentActivityForAnnotation{

//	@ViewInject(R.id.views_weighthis_day) private LinearLayout views_weighthis_day;
	@ViewInject(R.id.view_ycoord_weight_his) private YCoordForWeight view_ycoord_weight_his;
	@ViewInject(R.id.horview_weight_his) private HorizontalScrollView horview_weight_his;
	@ViewInject(R.id.view_weight_hsi) private WeightHistoryView view_weight_hsi;
	@ViewInject(R.id.view_ycoord_weight_his_month) private YCoordForWeight view_ycoord_weight_his_month;
	@ViewInject(R.id.horview_weight_his_month) private HorizontalScrollView horview_weight_his_month;
	@ViewInject(R.id.view_weight_hsi_month) private WeightHistoryView view_weight_hsi_month;
	@ViewInject(R.id.view_ycoord_weight_his_3month) private YCoordForWeight view_ycoord_weight_his_3month;
	@ViewInject(R.id.horview_weight_his_3month) private HorizontalScrollView horview_weight_his_3month;
	@ViewInject(R.id.view_weight_hsi_3month) private WeightHistoryView view_weight_hsi_3month;

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

	/**
	 * 获取体重历史记录信息
	 */
	public void GetWeightHistory() {
		RequestParams params = PreferenceEntity.getLoginParams();
		mgetNetData.GetData(this, UrlConstant.API_GETALLWEIGHJT, mHandler.GETWEIGHTHISDATA, params);
		setLoading(true, "");
	}


	@Override
	public void paddingDatas(String mData, int type) {
		setLoading(false,"");
		Gson mGson = new Gson();
        WeightHistoryTableEntity mUserEntity;
		try{
			mUserEntity = mGson.fromJson(mData, WeightHistoryTableEntity.class);
		}catch(Exception e){
			return;
		}
		if(mUserEntity.code == ContextConstant.RESPONSECODE_200){
			if(type == mHandler.GETWEIGHTHISDATA){	//获取用户信息
                setData(mUserEntity.data);
			}
		} else if (mUserEntity.code == ContextConstant.RESPONSECODE_310) {    //登录信息过时跳转到登录页
			reLoading();
		} else {
			ToastUtils.showToast(NewWidgetSetting.getInstance().filtrationStringbuffer(mUserEntity.msg, "接口信息异常！"));
		}
	}

	@Override
	public void error(String msg, int type) {
		super.error(msg,type);
		setLoading(false,"");
	}

	public void setData(WeightHistoryTableEntity.Data mUserEntity){
		if(mUserEntity == null){
			view_weight_hsi.setData(null);
			view_weight_hsi_month.setData(null);
			view_weight_hsi_3month.setData(null);
			return;
		}
		if(mUserEntity.list1 != null && mUserEntity.list1.size()>0){
			mLayoutUtil.drawViewDefaultLinearLayout(
			        view_weight_hsi, view_weight_hsi.setData(compileData(mUserEntity.list1)));
		}else{
			view_weight_hsi.setData(null);
		}

		if(mUserEntity.list2 != null && mUserEntity.list2.size()>0){
			mLayoutUtil.drawViewDefaultLinearLayout(
			        view_weight_hsi_month, view_weight_hsi_month.setData(compileData(mUserEntity.list2)));
		}else{
			view_weight_hsi_month.setData(null);
		}

		if(mUserEntity.list3 != null && mUserEntity.list3.size()>0){
			mLayoutUtil.drawViewDefaultLinearLayout(
			        view_weight_hsi_3month, view_weight_hsi_3month.setData(compileData(mUserEntity.list3, "y.M")));
		}else{
			view_weight_hsi_3month.setData(null);
		}
	}

	private List<WeightHistoryTableEntity> compileData(List<WeightHistoryTableEntity.Data.WeightInfo> mList,String timeSmaple){
		List<WeightHistoryTableEntity> mDrawlist = new ArrayList<>();
		for(WeightHistoryTableEntity.Data.WeightInfo mData: mList){
			WeightHistoryTableEntity lineData = new WeightHistoryTableEntity();
			lineData.date = tranTimes.convert(mData.createTime, timeSmaple);
			lineData.date1 = "";
			lineData.score = Float.parseFloat("" + mData.weight);
			mDrawlist.add(lineData);
		}

		return mDrawlist;
	}

	private List<WeightHistoryTableEntity> compileData(List<WeightHistoryTableEntity.Data.WeightInfo> mList){
		return compileData(mList,"M.d");
	}

	@Override
	protected void initLocation() {
		mLayoutUtil.drawViewRBLinearLayout(view_ycoord_weight_his, 80, 330, -1, 0, 0, 0);
		mLayoutUtil.drawViewRBLinearLayout(horview_weight_his, 0, 0, 0, -1, 0, 0);
		mLayoutUtil.drawViewRBLinearLayout(view_weight_hsi, 0, 330, 0, 0, 0, 0);
		mLayoutUtil.drawViewRBLinearLayout(view_ycoord_weight_his_month, 80, 330, -1, 0, 0, 0);
		mLayoutUtil.drawViewRBLinearLayout(horview_weight_his_month, 0, 0, 0, -1, 0, 0);
		mLayoutUtil.drawViewRBLinearLayout(view_weight_hsi_month, 0, 330, 0, 0, 0, 0);
		mLayoutUtil.drawViewRBLinearLayout(view_ycoord_weight_his_3month, 80, 330, -1, 0, 0, 0);
		mLayoutUtil.drawViewRBLinearLayout(horview_weight_his_3month, 0, 0, 0, -1, 0, 0);
		mLayoutUtil.drawViewRBLinearLayout(view_weight_hsi_3month, 0, 330, 0, 0, 0, 0);
	}

	@Override
	protected void initLogic() {

	}


	protected MyHandler mHandler;

	protected class MyHandler extends Handler {
		protected final int GETWEIGHTHISDATA = 10002;

		// SoftReference<Activity> 也可以使用软应用 只有在内存不足的时候才会被回收
		private final WeakReference<Activity> mActivity;

		protected MyHandler(Activity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			Activity activity = mActivity.get();
			if (activity != null){
				//做操作
				switch (msg.what) {
					case GETWEIGHTHISDATA: // 获取体重历史记录信息
						GetWeightHistory();
						break;
				}
			}
			super.handleMessage(msg);
		}
	}

	@Override
	protected void destroyClose() { }

	@Override
	protected void pauseClose() { }

}
