package com.huidf.slimming.activity.home.sport;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.huidf.slimming.R;
import com.huidf.slimming.activity.user.perfect_info.PerfectInfoActivity;
import com.huidf.slimming.context.ApplicationData;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.fragment.home.weight.WeightSelDialogFragment;
import com.mengii.scale.api.MengiiSDK;
import com.mengii.scale.model.MUser;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

import huitx.libztframework.utils.MathUtils;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.PreferencesUtils;

/**
 * 运动历史记录
 * @author ZhuTao
 * @date 2018/12/18 
 * @params 
*/

@ContentView(R.layout.activity_sport_his)
public class SportHisActivity extends SportHisBaseActivity {
    public SportHisActivity() {
        super( );
        TAG = getClass().getName();
        mContext = ApplicationData.context;
    }

    @Override
    protected void initHead() {
//        setStatusBarColor(true, true, 0);
        mBtnLeft.setBackgroundResource(R.drawable.btn_back_white);
        setTittle("运动记录");
//        setRightButtonText("手动录入", R.color.white);
        mBtnRight.setBackgroundResource(R.drawable.icon_sporthis_hint);
        setHideTitleLine();

        if (mHandler == null) mHandler = new MyHandler(this);
    }

    @Override
    protected void initLogic() {
        super.initLogic();
        setNowData(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(mHandler.GETSPORTNOW);
    }

    @Event(value = {R.id.btn_title_view_right,R.id.tv_weight_new_target})
    private void getEvent(View view)
    {//必须用private进行修饰,否则无效

        switch (view.getId()) {
            case R.id.btn_title_view_right:  //提示
                showHintDialogView();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
//        if(REINSTALLUSERINFO == requestCode){
//            if(requestCode == 200){
//                LOG("更新数据");
//                mHandler.sendEmptyMessage(mHandler.GETSPORTHISTORY);
//            }
//        }
    }

    @Override
    protected void pauseClose() {
        super.pauseClose();
    }


    @Override
    protected void destroyClose() {
        if (mHandler != null) mHandler.removeCallbacksAndMessages(null);
    }
}
