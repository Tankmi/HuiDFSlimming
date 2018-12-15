package com.huidf.slimming.fragment.personal_center;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.huidf.slimming.R;
import com.huidf.slimming.activity.personal_center.UserInfoActivity;
import com.huidf.slimming.activity.personal_center.set.UserSetActivity;

/**
 * 个人中心
 * @author ZhuTao
 * @date 2018/11/28 
 * @params 
*/

public class PersonalCenterFragment extends PersonalCenterBaseFragment {

    public PersonalCenterFragment() {
        super(R.layout.fragment_personal_center);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        setUserVisibleHint(true);
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    protected void initHead() {
        TAG = getClass().getName();
        initDialog(getActivity());
    }

    @Override
    protected void initLogic() {

    }

    @Override
    public void onResume() {
        super.onResume();
        setData(null);
        GetUserInfo();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_sett_setting: // 设置
                intent = new Intent(mContext, UserSetActivity.class);
                break;
            case R.id.rel_sett_info: // 个人设置
                intent = new Intent(mContext, UserInfoActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void pauseClose() {
        super.pauseClose();
    }

}
