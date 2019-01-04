package com.huidf.slimming.fragment.home;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.huidf.slimming.R;
import com.huidf.slimming.activity.home.foodsport_scheme.FoodSportSchemeActivity;
import com.huidf.slimming.activity.home.sleep.SleepHisActivity;
import com.huidf.slimming.activity.home.sleep.SleepHisBaseActivity;
import com.huidf.slimming.activity.home.sport.SportHisActivity;
import com.huidf.slimming.activity.home.weight.WeightActivity;
import com.huidf.slimming.activity.home.weight.history.WeightHistoryActivity;
import com.huidf.slimming.activity.toady_movement.TodayMovementActivity;
import com.huidf.slimming.context.PreferenceEntity;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import huitx.libztframework.utils.MathUtils;


/**
 * 精选
 *
 * @author ZhuTao
 * @date 2018/8/22
 * @params
 */

public class HomeFragment extends HomeBaseFragment {

    public HomeFragment()
    {
        super(R.layout.fragment_home);
        TAG = getClass().getSimpleName();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setUserVisibleHint(true);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void initHead()
    {
        if (mHandler == null) mHandler = new MyHandler(getActivity());
    }

    @Override
    protected void initLogic()
    {
        super.initLogic();
    }

    @Override
    public void onResume() {
        super.onResume();
        LOG("PreferenceEntity.isRefreshHomeData:  " + PreferenceEntity.isRefreshHomeData);
        if(!isInit || PreferenceEntity.isRefreshHomeData){
            getChoiceness();
        }
    }

    @Override
    public void onClick(View view)
    {
        super.onClick(view);
        Intent intent = null;

        switch (view.getId()) {
//            case R.id.iv_choiceness_article:    //健康指南
//            case R.id.lin_cc_1:    //病友案例1
//            case R.id.lin_cc_2:    //病友案例2
//                String url = (String) view.getTag();
//                intent = new Intent(mContext, WebViewActivity.class);
//                intent.putExtra("url", UrlConstant.API_BASEH5 + url);
////			intent.putExtra("title_name", "" + "");
//                intent.putExtra("is_refresh", false);
//                break;
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
    protected void pauseClose()
    {
        super.pauseClose();
    }

    @Override
    protected void destroyClose()
    {
        super.destroyClose();
        if (mHandler != null) mHandler.removeCallbacksAndMessages(null);
    }
}
