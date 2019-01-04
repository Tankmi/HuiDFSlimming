package com.huidf.slimming.fragment.personal_center;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.huidf.slimming.R;
import com.huidf.slimming.activity.personal_center.UserInfoActivity;
import com.huidf.slimming.activity.personal_center.joingroup.InputActivity;
import com.huidf.slimming.activity.personal_center.set.UserSetActivity;
import com.huidf.slimming.context.HtmlUrlConstant;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.web.MyWebViewUtil;
import com.huidf.slimming.web.activity.WebViewActivity;

import huitx.libztframework.utils.MathUtils;
import huitx.libztframework.utils.PreferencesUtils;

/**
 * 个人中心
 * @author ZhuTao
 * @date 2018/11/28 
 * @params 
*/

public class PersonalCenterFragment extends PersonalCenterBaseFragment {

    public PersonalCenterFragment() {
        super(R.layout.fragment_personal_center);
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
        setData(null);
        GetUserInfo();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_sett_setting: // 设置
                intent = new Intent(getActivity(), UserSetActivity.class);
                break;
            case R.id.rel_sett_info: // 个人设置
                intent = new Intent(getActivity(), UserInfoActivity.class);
                break;
            case R.id.lin_sett_dynamic: // 个人动态
                String pId = PreferencesUtils.getString(mContext, PreferenceEntity.KEY_USER_ID,"");
                if(pId==null || pId.equals("")){
                    reLoading();
                    break;
                }
                intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url", HtmlUrlConstant.HTML_USER_INFO + pId);
                intent.putExtra("is_refresh", false);
                break;
            case R.id.lin_sett_joingroup: // 拉人进群
                intent = new Intent(getActivity(), InputActivity.class);
                intent.putExtra("type",1);
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
