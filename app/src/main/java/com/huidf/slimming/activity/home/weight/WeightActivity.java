package com.huidf.slimming.activity.home.weight;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.huidf.slimming.R;
import com.huidf.slimming.activity.user.perfect_info.PerfectInfoActivity;
import com.huidf.slimming.context.ApplicationData;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.fragment.home.weight.HomeRefreshData;
import com.huidf.slimming.fragment.home.weight.WeightSelDialogFragment;
import com.mengii.scale.api.MengiiSDK;
import com.mengii.scale.model.MUser;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

import huitx.libztframework.utils.MathUtils;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.PreferencesUtils;

/**
 * @author : Zhutao
 * @version 创建时间：@2016年12月8日
 * @Description: 体重
 * @params：
 */
@ContentView(R.layout.activity_weight)
public class WeightActivity extends WeightBaseActivity {
    public WeightActivity() {
        super( );
        TAG = getClass().getSimpleName();
    }

    @Override
    protected void initHead() {
        setStatusBarColor(false, true, mContext.getResources().getColor(R.color.weight_main_color));
        mBtnLeft.setBackgroundResource(R.drawable.btn_back_white);
        setTitleBackgroudColor(R.color.weight_main_color);
        setTittle("减重记录", R.color.white);
        setRightButtonText("手动录入", R.color.white);
        setHideTitleLine();

        if (mHandler == null) mHandler = new MyHandler(this);
    }

    @Override
    protected void initLogic() {
        super.initLogic();
//		GetWeight(1);
        String strSex = PreferencesUtils.getString(mContext, PreferenceEntity.KEY_USER_SEX, "1");
        int height = (int) MathUtils.stringToFloatForPreference(PreferenceEntity.KEY_USER_HEIGHT, 170f);
        int age = Integer.parseInt(PreferencesUtils.getString(mContext, PreferenceEntity.KEY_USER_AGE, "25").equals("") ? "18" : PreferencesUtils.getString(mContext, PreferenceEntity.KEY_USER_AGE, "25"));
        int sex = Integer.parseInt(strSex.equals("1") ? "0" : "1");
        mUserData = new MUser(height, age, sex, MUser.UNIT_KG);    //身高，年龄，性别（SDK是0男，1女），单位
    }

    @Override
    protected void onResume() {
        super.onResume();
        LOG("onResume onResume");
        mHandler.sendEmptyMessage(mHandler.BLE_START);
        mHandler.sendEmptyMessage(mHandler.GETWEIGHTDATA);
    }

    private final int REINSTALLUSERINFO = 1001;

    @Event(value = {R.id.btn_title_view_right,R.id.tv_weight_new_target})
    private void getEvent(View view)
    {//必须用private进行修饰,否则无效

        switch (view.getId()) {
            case R.id.btn_title_view_right:  //手动录入体重
                ShowMovementDialog();
                break;
            case R.id.tv_weight_new_target:  //设定新目标
                Intent intent = new Intent(this, PerfectInfoActivity.class);
                intent.putExtra("isReinstall",true);
                startActivityForResult(intent, REINSTALLUSERINFO);
                break;
        }
    }

    WeightSelDialogFragment playQueueFragment;
    private FragmentManager fragmentManager;
    private String MOVEMENT_TIME_TAG = "movementdialog";
    /**
     * 显示条目选择框
     */
    protected void ShowMovementDialog()
    {
        if (playQueueFragment == null) playQueueFragment = new WeightSelDialogFragment();
        if (fragmentManager == null) fragmentManager = this.getSupportFragmentManager();
        playQueueFragment.show(fragmentManager,MOVEMENT_TIME_TAG);
//        playQueueFragment.setDataInfo(null);
        playQueueFragment.setOnSelDataListener(new WeightSelDialogFragment.onSelDataListener() {
            @Override
            public int onSelData(float value)
            {
//                commitRunningInfo(mEntity,value,equalCalorie);
                submitWeightInfo(value,2);

                tv_weight_current.setText("" + value);
                NewWidgetSetting.setIdenticalLineTvColor(tv_weight_current, mContext.getResources().getColor(R.color.text_color_hint), 0.5f , "  KG", false);
                return 0;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(REINSTALLUSERINFO == requestCode){
            if(resultCode == 200){
                LOG("更新数据");
                EventBus.getDefault().post(new HomeRefreshData(true));
                mHandler.sendEmptyMessage(mHandler.GETWEIGHTDATA);
            }
        }
    }

    @Override
    protected void pauseClose() {
        super.pauseClose();
        MengiiSDK.the().stop();
        LOG("回收体脂称资源");
    }


    @Override
    protected void destroyClose() {

    }

    public int getRandom(int min, int max) {
        return (int) Math.round(Math.random() * (max - min) + min);
    }
}
