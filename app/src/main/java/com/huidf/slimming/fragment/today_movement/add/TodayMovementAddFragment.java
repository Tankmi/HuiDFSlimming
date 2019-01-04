package com.huidf.slimming.fragment.today_movement.add;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.huidf.slimming.R;
import com.huidf.slimming.activity.personal_center.UserInfoActivity;
import com.huidf.slimming.activity.personal_center.set.UserSetActivity;

/**
 * 今日运动，添加运动
 * @author ZhuTao
 * @date 2018/12/5 
 * @params 
*/

public class TodayMovementAddFragment extends TodayMovementAddBaseFragment {

    public TodayMovementAddFragment() {
        super(R.layout.fragment_todaym_add);
        TAG = getClass().getSimpleName();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        setUserVisibleHint(true);
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    protected void initHead() {

    }

    @Override
    protected void initLogic() {

    }

    @Override
    public void onResume() {
        super.onResume();
        setData();
//        GetUserInfo();
    }

    @Override
    protected void pauseClose() {
        super.pauseClose();
    }

}
