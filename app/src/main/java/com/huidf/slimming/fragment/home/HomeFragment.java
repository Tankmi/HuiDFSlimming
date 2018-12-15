package com.huidf.slimming.fragment.home;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.huidf.slimming.R;
import com.huidf.slimming.activity.toady_movement.TodayMovementActivity;
import com.huidf.slimming.context.PreferenceEntity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import huitx.libztframework.utils.MathUtils;
import huitx.libztframework.utils.PreferencesUtils;


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
        TAG = getClass().getName();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {

        setUserVisibleHint(true);
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    protected void initHead()
    {

    }

    @Override
    protected void initLogic()
    {
        super.initLogic();
    }

    @Override
    public void onResume()
    {
        super.onResume();
//        getChoiceness();

        float weight = MathUtils.stringToFloatForPreference(PreferenceEntity.KEY_USER_INITIAL_WEIGHT, 70);
        float current_weight = MathUtils.stringToFloatForPreference(PreferenceEntity.KEY_USER_CURRENT_WEIGHT, weight);
        float target_weight = MathUtils.stringToFloatForPreference(PreferenceEntity.KEY_USER_TARGET_WEIGHT, current_weight);
        if (weight == 0.0f) {
            weight = 72;
            current_weight = 70;
            target_weight = 66;
        }
        circular_ht_weight.setData(weight, current_weight, target_weight);
        DecimalFormat df = new DecimalFormat("#");
        DecimalFormat df1 = new DecimalFormat("#.00");
        tv_htd_target_weight.setText("目标体重\n" + df.format(target_weight) + "KG");
        tv_htd_progress.setText("进度\n" + df.format((Float.parseFloat(df1.format((weight - current_weight) / (weight - target_weight))) * 100)) + "%");


        horschedule_hf_schedule.setData(50);
        horschedule_hs_schedule.setData(80);
        List<Float> mLists = new ArrayList<>();
        mLists.add(70f);
        mLists.add(66f);
        mLists.add(66f);
        mLists.add(66f);
        mLists.add(75f);
        weighttable_hw_schedule.setData(mLists);
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
            case R.id.rel_home_food:    //饮食运动方案
                LOG("饮食运动方案");
                break;
            case R.id.rel_home_sport:    //运动记录
                LOG("运动记录");
                break;
            case R.id.rel_home_weight:    //体重记录
                LOG("体重记录");
                break;
            case R.id.rel_home_sleep:    //睡眠记录
                LOG("睡眠记录");
                break;

        }
        if (intent != null) startActivity(intent);
    }

    @Override
    protected void pauseClose()
    {
        super.pauseClose();
    }

}
