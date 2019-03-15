package com.huidf.slimming.activity.home.sport;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dou361.dialogui.DialogUIUtils;
import com.google.gson.Gson;
import com.huidf.slimming.R;
import com.huidf.slimming.adapter.home.SportHistoryAdapter;
import com.huidf.slimming.base.BaseFragmentActivityForAnnotation;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.entity.home.sport.SportHisEntity;
import com.huidf.slimming.entity.home.sport.SportHistogramEntity;
import com.huidf.slimming.entity.home.weight.UploadingDataEntity;
import com.huidf.slimming.entity.today_movement.MovementEntity;
import com.huidf.slimming.view.home.CircularView;
import com.huidf.slimming.view.home.HorizonalScheduleView;
import com.huidf.slimming.view.home.sport.SportCircularView;
import com.huidf.slimming.view.home.sport.SportHistogramTable;
import com.huidf.slimming.view.home.weight.YCoordForWeight;
import com.huidf.slimming.view.swiperecyclerview.SpacesItemDecoration;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import huitx.libztframework.context.ContextConstant;
import huitx.libztframework.utils.BitmapUtils;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.ToastUtils;

public class SportHisBaseActivity extends BaseFragmentActivityForAnnotation implements OnClickListener, OnCheckedChangeListener, OnItemClickListener {

    @ViewInject (R.id.rel_sport_history_title) protected RelativeLayout rel_sport_history_title;
    @ViewInject (R.id.rel_sht_data) protected RelativeLayout rel_sht_data;
    @ViewInject (R.id.tv_sht_duration) protected TextView tv_sht_duration;
    @ViewInject (R.id.circular_sht_level) protected SportCircularView circular_sht_level;
    @ViewInject (R.id.tv_sht_calorie) protected TextView tv_sht_calorie;
    @ViewInject (R.id.rec_sport_history) protected RecyclerView mRecyclerView;
    @ViewInject (R.id.iv_sport_his_level_days) protected ImageView iv_sport_his_level_days;
    @ViewInject (R.id.view_ycoord_sport_his_days) protected YCoordForWeight view_ycoord_sport_his_days;
    @ViewInject (R.id.hs_sport_his_days) protected HorizontalScrollView hs_sport_his_days;
    @ViewInject (R.id.view_sport_his_days) protected SportHistogramTable view_sport_his_days;
    @ViewInject (R.id.lin_sport_his_day_main) protected LinearLayout lin_sport_his_day_main;
    @ViewInject (R.id.tv_sport_his_day_01) protected TextView tv_sport_his_day_01;
    @ViewInject (R.id.tv_sport_his_day_02) protected TextView tv_sport_his_day_02;
    @ViewInject (R.id.tv_sport_his_day_03) protected TextView tv_sport_his_day_03;
    @ViewInject (R.id.view_ycoord_sport_his_30days) protected YCoordForWeight view_ycoord_sport_his_30days;
    @ViewInject (R.id.hs_sport_his_30days) protected HorizontalScrollView hs_sport_his_30days;
    @ViewInject (R.id.view_sport_his_30days) protected SportHistogramTable view_sport_his_30days;
    @ViewInject (R.id.lin_sport_his_30day_main) protected LinearLayout lin_sport_his_30day_main;
    @ViewInject (R.id.tv_sport_his_30day_01) protected TextView tv_sport_his_30day_01;
    @ViewInject (R.id.tv_sport_his_30day_02) protected TextView tv_sport_his_30day_02;
    @ViewInject (R.id.tv_sport_his_30day_03) protected TextView tv_sport_his_30day_03;

    public SportHisBaseActivity( ) {
        super();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    /**
     *
     * @param type 分为两部分，1当前接口，和2历史记录接口
     */
    public void getSportHistory(int type) {
        RequestParams params = PreferenceEntity.getLoginParams();
        if(type == 1)  mgetNetData.GetData(this, UrlConstant.API_GETSPORT, mHandler.GETSPORTNOW, params);
        else  mgetNetData.GetData(this, UrlConstant.API_GETHISTORYSPORT, mHandler.GETSPORTHISTORY, params);
        setLoading(true, "");
    }

    @Override
    public void paddingDatas(String mData, int type) {
        setLoading(false, "");
        Gson gson = new Gson();
        SportHisEntity  mTopicentity;
        try {
            mTopicentity = gson.fromJson(mData, SportHisEntity.class);
        } catch (Exception e) {
            return;
        }
        if (mTopicentity.code == ContextConstant.RESPONSECODE_200) {
            if (type == mHandler.GETSPORTNOW) {
                mHandler.sendEmptyMessage(mHandler.GETSPORTHISTORY);
                setNowData(mTopicentity.data);
            } else if (type == mHandler.GETSPORTHISTORY) {
                setHisData(mTopicentity.data);
            }
        } else if (mTopicentity.code == ContextConstant.RESPONSECODE_310) {    //登录信息过时跳转到登录页
            reLoading();
        } else {
            ToastUtils.showToast(NewWidgetSetting.filtrationStringbuffer(mTopicentity.msg, "接口信息异常！"));
        }
    }

    @Override
    public void error(String msg, int type) {
        super.error(msg,type);
        setLoading(false, "");
    }

    @SuppressLint("SetTextI18n")
    protected void setNowData(SportHisEntity.Data dataEntity){
        String level,time,calorie;
        float schedules;
        if(dataEntity == null ){
            level = "C";
            time = 0 + "";
            calorie = 0 + "";
            schedules = 0;
        }else{
            level = NewWidgetSetting.filtrationStringbuffer(dataEntity.level,"C");
            time = NewWidgetSetting.filtrationStringbuffer(dataEntity.time,"0");
            calorie = NewWidgetSetting.filtrationStringbuffer(dataEntity.kcal,"0");
//            schedules = Float.parseFloat(NewWidgetSetting.filtrationStringbuffer(dataEntity.schedules,"0"));
            schedules = dataEntity.schedules;
        }

        setLevel(level);
        circular_sht_level.setData(level, schedules);
        tv_sht_duration.setText("时长");
        NewWidgetSetting.setIdenticalLineTvColor(tv_sht_duration, -999, 1.11f,time + "分钟",true);
        tv_sht_calorie.setText("卡路里");
        NewWidgetSetting.setIdenticalLineTvColor(tv_sht_calorie, -999, 1.11f,calorie + "千卡",true);
        if(dataEntity != null && dataEntity.list2 != null){
            LinkedList<SportHisEntity.Data.SportGenre> mLsits = new LinkedList<>();
            for(SportHisEntity.Data.SportGenre data: dataEntity.list2){
                MovementEntity mMovementEntity = mIncoInfoList.get(Integer.parseInt(data.sporttype));
                data.sportName = mMovementEntity.name;
                data.sportIcon = mMovementEntity.icon;
                mLsits.add(data);
            }
            mAdapter.setListData(mLsits);
        }
    }

    protected void setHisData(SportHisEntity.Data dataEntity){
        if(dataEntity == null)  return;
        setHistogramData(view_sport_his_days, dataEntity.list3);
        setHistogramData(view_sport_his_30days, dataEntity.list4);
        setInfoText(tv_sport_his_day_01, dataEntity.weeklevel, "0", "天" ,"A级运动量天数");
        setInfoText(tv_sport_his_day_02, dataEntity.weeklongest, "0", "分钟" ,"1天最长运动时间");
        setInfoText(tv_sport_his_day_03, dataEntity.weeksumtime, "0", "分钟" ,"总时长");
        setInfoText(tv_sport_his_30day_01, dataEntity.monthlevel, "0", "天" ,"A级运动量天数");
        setInfoText(tv_sport_his_30day_02, dataEntity.monthlongest, "0", "分钟" ,"1天最长运动时间");
        setInfoText(tv_sport_his_30day_03, dataEntity.monthsumtime, "0", "分钟" ,"总时长");
    }

    private void setHistogramData(SportHistogramTable tableView,List<SportHisEntity.Data.SportLevel> lists){
        List<SportHistogramEntity> datas = new ArrayList<SportHistogramEntity>();
        //            for (int i = 1; i <= 7; i++) {
//                SportHistogramEntity data = new SportHistogramEntity();
//                data.id = i;
//                data.date = "" + tranTimes.convert(System.currentTimeMillis(),"M.d");
//                data.maxNum = 500;
//                data.value = i*40;
//                data.level = "A";
//                datas.add(data);
//            }
//            if(view_sport_his_days!=null) {
//                view_sport_his_days.setData(datas);
//                view_sport_his_30days.setData(datas);
//            }
        for(SportHisEntity.Data.SportLevel data: lists){
            SportHistogramEntity mEntity = new SportHistogramEntity();
            mEntity.date = "" + tranTimes.convert(data.createtime,"M.d");
            mEntity.value = Float.parseFloat(data.daysumtime);
            mEntity.level = data.level + "";
            datas.add(mEntity);
        }
        mLayoutUtil.drawViewDefaultLayout(tableView, tableView.setData(datas), -1, -1, -1, -1, -1);
    }

    private void setInfoText(TextView text,String num, String defaultData, String hint1, String hint2){
        NewWidgetSetting.setViewText(text,num,defaultData);
        NewWidgetSetting.setIdenticalLineTvColor(text, mContext.getResources()
                .getColor(R.color.text_color_hint), 0.6f , hint1, false);
        NewWidgetSetting.setIdenticalLineTvColor(text, mContext.getResources()
                .getColor(R.color.text_color_hint), 0.6f , hint2, true);
    }

    protected void setLevel(String level){
        int drawable,color;
        setTittle("运动记录", R.color.white);
        if(level.equals("A")){
            color = R.color.back_sport_his_a;
            drawable = R.drawable.back_sporthis_a;
        }else if(level.equals("B")){
            drawable = R.drawable.back_sporthis_b;
            color = R.color.back_sport_his_b;
        }else{
            drawable = R.drawable.back_sporthis_c;
            color = R.color.back_sport_his_c;
        }
        rel_sport_history_title.setBackgroundResource(drawable);
        setStatusBarColor(true, true, mContext.getResources().getColor(color));
        setTitleBackgroudColor(color);

    }

    protected MyHandler mHandler;

    protected class MyHandler extends Handler{
        protected final int GETSPORTNOW = 10001;
        protected final int GETSPORTHISTORY = 10002;

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
                    case GETSPORTNOW: // 获取今日运动信息
                        getSportHistory(1);
                        break;
                    case GETSPORTHISTORY: // 获取运动历史记录信息
                        getSportHistory(2);
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void initContent() {
        rel_sport_history_title.setBackground(BitmapUtils.getDrawableResources(R.drawable.back_home));
        initIconInfoData();
    }

    @Override
    protected void initLocation() {
        mLayoutUtil.drawViewRBLayout(mBtnRight, 50, 50, 0, 0, 0, 0);
        mLayoutUtil.drawViewRBLinearLayout(rel_sport_history_title, 0, 460, 0, 0, 0, 0);
        mLayoutUtil.drawViewRBLayout(tv_sht_duration, 0, 0, 0, 0, 61, 0);
        tv_sht_duration.setMinWidth(mLayoutUtil.getWidgetWidth(170));
        mLayoutUtil.drawViewRBLayout(tv_sht_calorie, 239, 0, 0, 0, 0, 0);
//        mLayoutUtil.drawViewRBLayout(circular_sht_level, 272, 272, 0, 0, 0, 0);
        mLayoutUtil.drawViewRBLayout(circular_sht_level, 337, 337, 0, 0, 0, 0);

        mLayoutUtil.drawViewRBLinearLayout(iv_sport_his_level_days, 239, 20, -1, -1, -1, 0);

        mLayoutUtil.drawViewRBLinearLayout(view_ycoord_sport_his_days, 80, 345, -1, 0, 0, 0);
        mLayoutUtil.drawViewRBLayout(view_sport_his_days, 0, 345, 0, 0, 0, 0);

        mLayoutUtil.drawViewRBLinearLayout(view_ycoord_sport_his_30days, 80, 345, -1, 0, 0, 0);
        mLayoutUtil.drawViewRBLayout(view_sport_his_30days, 0, 345, 0, 0, 0, 0);
    }

    protected SpacesItemDecoration decor;
    private SportHistoryAdapter mAdapter;
    private LinkedList<SportHisEntity.Data.SportGenre> movementInfoList = new LinkedList<>();
    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks;

    private void initRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        decor = new SpacesItemDecoration(0, 1,mContext.getResources().getColor(R.color.line_bg));
        mRecyclerView.addItemDecoration(decor);

        mAdapter = new SportHistoryAdapter(mContext, movementInfoList);
        mRecyclerView.setAdapter(mAdapter);

//        mAdapter.setOnMovementClickListener(new SportHistoryAdapter.onMovementAddClick() {
//            @Override
//            public void onMovementClick(final MovementEntity mEntity)
//            {
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        LOG("name: " + mEntity.name);
////                        ShowMovementDialog(true,mEntity);
//                    }
//                }, 60);
//            }
//        });
    }

    @Override
    protected void initLogic() {
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    protected void initHead() {
    }

    @Override
    protected void pauseClose() {
    }

    @Override
    protected void destroyClose() {
        mIncoInfoList.clear();
        mIncoInfoList=null;
    }

    @Override
    public void onCheckedChanged(RadioGroup arg0, int arg1) {
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    }

    private LinkedList<MovementEntity> mIncoInfoList = new LinkedList<>();
    protected void initIconInfoData() {
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_kuaizou, "快走" , 5.36f, 1));
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_kuaizou, "快走" , 5.36f, 1));
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_manpao, "慢跑" , 7.37f, 2));
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_youyong, "游泳" , 7.37f, 3));
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_qiche, "骑车" , 7.77f, 4));
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_tiaosheng, "跳绳" , 7.77f, 5));
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_tiaowu, "跳舞" , 6.7f, 6));
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_pashan, "爬山" , 11.38f, 7));
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_panyan, "攀岩" , 11.38f, 8));
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_palouti, "爬楼梯" , 7.24f, 9));
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_tabancao, "踏板操" , 8.71f,10));
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_lanqiu, "篮球" , 7.37f, 11));
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_zuqiu, "足球" , 8.04f,12));
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_paiqiu, "排球" , 6.3f,13));
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_pingpang, "乒乓球" , 4.01f,14));
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_yumaoqiu, "羽毛球" , 6.03f,15));
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_wangqiu, "网球" , 5.36f,16));
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_biqiu, "壁球" , 8.04f,17));
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_huaban, "滑板" , 5.36f,18));
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_huabing, "滑冰" , 8.04f,19));
        mIncoInfoList.add(new MovementEntity(R.drawable.icon_huaxue, "滑雪" , 8.04f,20));
        initRecycler();
    }

    private View mDialogView;
    protected Dialog dialog;
    private RelativeLayout rel_dialog_sporthis_hint;
    private Button btn_dia_sporthis_hint;

    protected void showHintDialogView() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (mDialogView == null) {
            mDialogView = View.inflate(this, R.layout.dialog_sport_his_hint, null);
            rel_dialog_sporthis_hint = findViewByIds(mDialogView, R.id.rel_dialog_sporthis_hint);
            btn_dia_sporthis_hint = findViewByIds(mDialogView, R.id.btn_dia_sporthis_hint);

            mLayoutUtil.drawViewRBLinearLayout(rel_dialog_sporthis_hint, 523, 536, 0, 0, 0, 0);
            mLayoutUtil.drawViewRBLinearLayout(btn_dia_sporthis_hint, 46, 46, 0, 0, -1, 0);

            btn_dia_sporthis_hint.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        if (dialog != null) DialogUIUtils.dismiss(dialog);
                    }
                }
            });
        }
        if (dialog == null)
            dialog = DialogUIUtils.showCustomAlert(this, mDialogView, Gravity.CENTER, true, false).show();
        else dialog.show();

    }

}
