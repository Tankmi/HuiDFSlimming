package com.huidf.slimming.fragment.home;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.huidf.slimming.R;
import com.huidf.slimming.activity.home.foodsport_scheme.FoodSportSchemeActivity;
import com.huidf.slimming.activity.home.sleep.SleepHisActivity;
import com.huidf.slimming.activity.home.sport.SportHisActivity;
import com.huidf.slimming.activity.home.weight.WeightActivity;
import com.huidf.slimming.activity.home.weight.history.WeightHistoryActivity;
import com.huidf.slimming.activity.toady_movement.TodayMovementActivity;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.fragment.home.weight.HomeRefreshData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import huitx.libztframework.utils.LOGUtils;


/**
 * 精选
 *
 * @author ZhuTao
 * @date 2018/8/22
 * @params
 */

public class HomeFragment extends HomeBaseFragment {

    public HomeFragment() {
        super(R.layout.fragment_home);
        TAG = getClass().getSimpleName();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setUserVisibleHint(true);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void initHead() {
        if (mHandler == null) mHandler = new MyHandler(getActivity());
    }

    @Override
    protected void initLogic() {
        super.initLogic();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LOG("PreferenceEntity.isRefreshHomeData:  " + PreferenceEntity.isRefreshHomeData);
        if (!isInit || PreferenceEntity.isRefreshHomeData) {
            refreshHomeData();
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBusRefresh(HomeRefreshData state){
        LOG("EventBus 回调" + state.isRefreshData);
        if(state.isRefreshData){
            PreferenceEntity.isRefreshHomeData = true;
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        Intent intent = null;

        switch (view.getId()) {
            case R.id.tv_ht_today_movement:    //今日运动
                intent = new Intent(getActivity(), TodayMovementActivity.class);
                break;
            case R.id.rel_home_title:    //录入体重
                intent = new Intent(getActivity(), WeightActivity.class);
                break;
            case R.id.rel_home_food:    //饮食运动方案
                intent = new Intent(getActivity(), FoodSportSchemeActivity.class);
                break;
            case R.id.rel_home_sport:    //运动记录
                intent = new Intent(getActivity(), SportHisActivity.class);
                break;
            case R.id.rel_home_weight:    //体重记录
                intent = new Intent(getActivity(), WeightHistoryActivity.class);
                break;
            case R.id.rel_home_sleep:    //睡眠记录
                intent = new Intent(getActivity(), SleepHisActivity.class);
                break;

        }
        if (intent != null) startActivity(intent);
    }

    @Override
    protected void pauseClose() {
        super.pauseClose();
    }

    @Override
    protected void destroyClose() {
        super.destroyClose();
        if (mHandler != null) mHandler.removeCallbacksAndMessages(null);

        if (EventBus.getDefault().isRegistered(this)) {
            LOGUtils.LOG("解除EventBus 注册");
            EventBus.getDefault().unregister(this);
        }
    }
}
