package com.huidf.slimming.fragment.home;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huidf.slimming.R;
import com.huidf.slimming.base.BaseFragment;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.view.home.CircularView;
import com.huidf.slimming.view.home.HorizonalScheduleView;
import com.huidf.slimming.view.home.WeightTableView;

import org.xutils.http.RequestParams;

import java.util.HashMap;

import huitx.libztframework.utils.BitmapUtils;
import huitx.libztframework.utils.NewWidgetSetting;


public class HomeBaseFragment extends BaseFragment implements OnClickListener {


	public HomeBaseFragment(int layoutId) {
		super(layoutId);
	}

	@Override
	protected void initContent() {
		findView();
	}



	protected final int GETChoiceness = 101;

	/**  获取精选信息 */
	public void getChoiceness(){
		RequestParams params = PreferenceEntity.getLoginParams();
		mgetNetData.GetData(this, UrlConstant.POST_POST_MAINPAGE, GETChoiceness,params);
		setLoading(true,"");
	}

	@Override
	public void paddingDatas(String mData, int type) {
		setLoading(false,"");
		Gson gson = new Gson();
//		ChoicenessItemBean mEntity;
//		try{
//			mEntity = gson.fromJson(mData, ChoicenessItemBean.class);
//		}catch(Exception e){
//			return;
//		}
//		if (mEntity.code == ContextConstant.RESPONSECODE_200) {
//			Message message = Message.obtain();
//			if(type == GETChoiceness){
//				message.what = GETChoiceness;
//				message.obj = mEntity.data;
//			}
//			mHandler.sendMessage(message);
//		} else if (mEntity.code == ContextConstant.RESPONSECODE_310) {    //登录信息过时跳转到登录页
//			reLoading();
//		} else {
//			ToastUtils.showToast(NewWidgetSetting.getInstance().filtrationStringbuffer(mEntity.msg, "接口信息异常！"));
//		}
	}

	@Override
	public void error(String msg, int type) {
		setLoading(false,"");
	}

	HashMap<String,Integer> guide = new HashMap<>();
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case GETChoiceness:
//					ChoicenessItemBean.Data data = (ChoicenessItemBean.Data) msg.obj;
//					adapter.setList(data.list1);
//					for(int i=0; i<data.list.size(); i++){
//						ImageView iv = (ImageView) view[i][0];
//						TextView tv_title = (TextView) view[i][1];
//						TextView tv = (TextView) view[i][3];
//						LinearLayout lin = (LinearLayout) view[i][2];
//						imageLoader_base.displayImage(NewWidgetSetting.getInstance().filtrationStringbuffer(data.list.get(i).img,""), iv,
//								ImageLoaderUtils.setImageOptionsLoadImg(mContext.getResources().getDrawable(R.drawable.iv_loading_choiceness_01),0),
//								animateFirstListener_base);
//						tv_title.setText("" +  NewWidgetSetting.getInstance().filtrationStringbuffer(data.list.get(i).title,"暂无标题"));
//						tv.setText("" + NewWidgetSetting.getInstance().filtrationStringbuffer(data.list.get(i).content,"暂无内容"));
////						tv.setText("" + NewWidgetSetting.getInstance().appendViewTextString("",NewWidgetSetting.getInstance().filtrationStringbuffer(data.list.get(i).content,"暂无内容"),"",false));
//
//						lin.setTag(NewWidgetSetting.getInstance().filtrationStringbuffer(data.list.get(i).url,""));
//					}
//					iv_choiceness_article.setBackground(BitmapUtils.getDrawableResources(guide.get(NewWidgetSetting.getInstance().filtrationStringbuffer(data.guideImg,"guide1.png"))));
////					imageLoader_base.displayImage(guide.get(NewWidgetSetting.getInstance().filtrationStringbuffer(data.guideImg,"guide1.png")), iv_choiceness_article,
////							ImageLoaderUtils.setImageOptionsLoadImg(mContext.getResources().getDrawable(R.drawable.iv_img_loading),0),
////							animateFirstListener_base);
//					iv_choiceness_article.setTag(data.guideUrl);
					break;

				default:
					break;
			}
		};
	};

	@Override
	protected void initHead() {

	}
	
	@Override
	protected void onVisibile() {
//		setData();
	}

	private View view[][] = new View[2][4];
	protected ScrollView sc_home;
	protected RelativeLayout rel_home_title;
	protected TextView tv_ht_today_movement;
	protected RelativeLayout rel_ht_data;
	protected TextView tv_htd_target_weight;
	protected CircularView circular_ht_weight;
	protected TextView tv_htd_progress;

	protected RelativeLayout rel_home_food;
	protected TextView tv_hf_title;
	protected TextView tv_hf_data;
	protected HorizonalScheduleView horschedule_hf_schedule;

	protected RelativeLayout rel_home_sport;
	protected TextView tv_hs_title;
	protected TextView tv_hs_data;
	protected HorizonalScheduleView horschedule_hs_schedule;

	protected RelativeLayout rel_home_weight;
	protected TextView tv_hw_title;
	protected TextView tv_hw_data;
	protected WeightTableView weighttable_hw_schedule;

	protected RelativeLayout rel_home_sleep;
	protected TextView tv_hsl_title;
	protected TextView tv_hsl_data;
	protected ImageView iv_hsl_schedule;

	/**
	 * 初始化视图控件 添加监听事件
	 */
	public void findView() {
		sc_home = findViewByIds(R.id.sc_home);
		rel_home_title = findViewByIds(R.id.rel_home_title);
		tv_ht_today_movement = findViewByIds(R.id.tv_ht_today_movement);
		rel_ht_data = findViewByIds(R.id.rel_ht_data);
		tv_htd_target_weight = findViewByIds(R.id.tv_htd_target_weight);
		circular_ht_weight = findViewByIds(R.id.circular_ht_weight);
		tv_htd_progress = findViewByIds(R.id.tv_htd_progress);
		rel_home_food = findViewByIds(R.id.rel_home_food);
		tv_hf_title = findViewByIds(R.id.tv_hf_title);
		tv_hf_data = findViewByIds(R.id.tv_hf_data);
		horschedule_hf_schedule = findViewByIds(R.id.horschedule_hf_schedule);
		rel_home_sport = findViewByIds(R.id.rel_home_sport);
		tv_hs_title = findViewByIds(R.id.tv_hs_title);
		tv_hs_data = findViewByIds(R.id.tv_hs_data);
		horschedule_hs_schedule = findViewByIds(R.id.horschedule_hs_schedule);
		rel_home_weight = findViewByIds(R.id.rel_home_weight);
		tv_hw_title = findViewByIds(R.id.tv_hw_title);
		tv_hw_data = findViewByIds(R.id.tv_hw_data);
		weighttable_hw_schedule = findViewByIds(R.id.weighttable_hw_schedule);
		rel_home_sleep = findViewByIds(R.id.rel_home_sleep);
		tv_hsl_title = findViewByIds(R.id.tv_hsl_title);
		tv_hsl_data = findViewByIds(R.id.tv_hsl_data);
		iv_hsl_schedule = findViewByIds(R.id.iv_hsl_schedule);

		rel_home_title.setBackground(BitmapUtils.getDrawableResources(R.drawable.back_home));
		iv_hsl_schedule.setBackground(BitmapUtils.getDrawableResources(R.drawable.iv_home_sleep));
		tv_hf_title.setCompoundDrawables(NewWidgetSetting.getInstance().getWeightDrawable(R.drawable.icon_food_scheme, 70, 70, true), null, null, null);
		tv_hs_title.setCompoundDrawables(NewWidgetSetting.getInstance().getWeightDrawable(R.drawable.icon_sport_history, 70, 70, true), null, null, null);
		tv_hw_title.setCompoundDrawables(NewWidgetSetting.getInstance().getWeightDrawable(R.drawable.icon_weight_history, 70, 70, true), null, null, null);
		tv_hsl_title.setCompoundDrawables(NewWidgetSetting.getInstance().getWeightDrawable(R.drawable.icon_sleep_history, 70, 70, true), null, null, null);


		tv_ht_today_movement.setOnClickListener(this);
		rel_home_food.setOnClickListener(this);
		rel_home_sport.setOnClickListener(this);
		rel_home_weight.setOnClickListener(this);
		rel_home_sleep.setOnClickListener(this);
	}

	@Override
	protected void initLocation() {
//		mLayoutUtil.drawViewDefaultLayout(sc_home, -1, -1, -1, -1, (int) PreferenceEntity.ScreenTop, -1);
		mLayoutUtil.drawViewRBLinearLayout(rel_home_title, 0, 460, 0, 0, 0, 0);
		mLayoutUtil.drawViewRBLayout(tv_ht_today_movement, 0, 0, 0, 0, 61, 0);
		tv_ht_today_movement.setMinWidth(mLayoutUtil.getWidgetWidth(170));
		mLayoutUtil.drawViewRBLayout(tv_htd_target_weight, 239, 0, 0, 0, 0, 0);
		mLayoutUtil.drawViewRBLayout(circular_ht_weight, 272, 272, 0, 0, 0, 0);
		mLayoutUtil.drawViewRBLayout(tv_htd_progress, 239, 0, 0, 0, 0, 0);

		tv_hf_title.setCompoundDrawablePadding(30);
		mLayoutUtil.drawViewRBLayout(tv_hf_data, 0, 0, 103, 0, -1, 0);
		mLayoutUtil.drawViewRBLayout(horschedule_hf_schedule, 285, 52, 0, -1, -1, 0);

		tv_hs_title.setCompoundDrawablePadding(30);
		mLayoutUtil.drawViewRBLayout(tv_hs_data, 0, 0, 103, 0, -1, 0);
		mLayoutUtil.drawViewRBLayout(horschedule_hs_schedule, 285, 52, 0, -1, -1, 0);

		tv_hw_title.setCompoundDrawablePadding(30);
		mLayoutUtil.drawViewRBLayout(tv_hw_data, 0, 0, 103, 0, -1, 0);
		mLayoutUtil.drawViewRBLayout(weighttable_hw_schedule, 285, 120, 0, -1, -1, 0);

		tv_hsl_title.setCompoundDrawablePadding(30);
		mLayoutUtil.drawViewRBLayout(tv_hsl_data, 0, 0, 103, 0, -1, 0);
		mLayoutUtil.drawViewRBLayout(iv_hsl_schedule, 285, 90, 0, -1, -1, 0);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	protected void initLogic() {
		tv_hf_title.setText("饮食&运动方案");
		tv_hf_data.setText("平均每周应减重");
		NewWidgetSetting.getInstance().setIdenticalLineTvColor(tv_hf_data, mContext.getResources().getColor(R.color.black), 2.0f , "12", true);
		NewWidgetSetting.getInstance().setIdenticalLineTvColor(tv_hf_data, -999, 1.0f , "      KG", false);

		tv_hs_title.setText("运动记录");
		tv_hs_data.setText("今日完成了    ");
		NewWidgetSetting.getInstance().setIdenticalLineTvColor(tv_hs_data, mContext.getResources().getColor(R.color.black), 2.0f , "80", true);
		NewWidgetSetting.getInstance().setIdenticalLineTvColor(tv_hs_data, -999, 1.0f , "      %", false);

		tv_hw_title.setText("体重记录");
		tv_hw_data.setText("最新体重      ");
		NewWidgetSetting.getInstance().setIdenticalLineTvColor(tv_hw_data, mContext.getResources().getColor(R.color.black), 2.0f , "70", true);
		NewWidgetSetting.getInstance().setIdenticalLineTvColor(tv_hw_data, -999, 1.0f , "      KG", false);

		tv_hsl_title.setText("睡眠记录");
		tv_hsl_data.setText("昨日睡眠时间  ");
		NewWidgetSetting.getInstance().setIdenticalLineTvColor(tv_hsl_data, mContext.getResources().getColor(R.color.black), 2.0f , "14", true);
		NewWidgetSetting.getInstance().setIdenticalLineTvColor(tv_hsl_data, -999, 1.0f , "      h", false);
	}

	@Override
	public void onClick(View view) {
	}

	@Override
	protected void pauseClose() {
	}

	@Override
	protected void destroyClose() {
	}

}
