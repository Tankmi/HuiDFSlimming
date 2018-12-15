package com.huidf.slimming.activity.toady_movement.run;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;

import com.huidf.slimming.R;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.util.run.RunningUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

import huitx.libztframework.utils.MathUtils;


/**
 * 跑步
 *
 * @author ZhuTao
 * @date 2018/12/11
 * @params
 */
@ContentView(R.layout.activity_run)
public class RunActivity extends RunBaseActivity {

    public RunActivity()
    {
        TAG = getClass().getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mMapView.onCreate(savedInstanceState);
    }

    @Override
    protected void initHead()
    {
        setStatusBarColor(false, true, mContext.getResources().getColor(R.color.back_run_color));
        mBtnLeft.setBackgroundResource(R.drawable.btn_back_white);
        setTitleBackgroudColor(R.color.back_run_color);
        setTittle("正在跑步", R.color.white);
        setRightButtonText("完成", R.color.main_color);
        setHideTitleLine();

        if (mHandler == null) mHandler = new MyHandler(this);

        mRunningUtil = RunningUtil.getInstance();
        mRunningUtil.setRunnintTimeObject(this);
        mRunningUtil.setUserInfo(MathUtils.stringToFloatForPreference(PreferenceEntity.KEY_USER_CURRENT_WEIGHT, 66.0f),
                MathUtils.stringToFloatForPreference(PreferenceEntity.KEY_USER_HEIGHT, 170f));
    }

    @Override
    protected void initLogic()
    {
        initMapLocation();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mMapView.onResume();

    }


    @Event(value = {R.id.btn_title_view_left, R.id.btn_title_view_right, R.id.btn_runc_pause, R.id.btn_runc_continue, R.id.btn_runc_finish, R.id.btn_rund_map})
    private void getEvent(View view)
    {//必须用private进行修饰,否则无效
        switch (view.getId()) {
            case R.id.btn_title_view_left:  //返回键
                LOG("返回键");
                if (rel_run_map.getVisibility()==View.VISIBLE) ShowOrHideMapView();
                else{
                    canFinish();
                }
                break;
            case R.id.btn_title_view_right:  //完成
                canFinish();
                break;
            case R.id.btn_runc_pause:    //暂停
                runningAnim(mRunningUtil.RUNNING_PAUSE);
                continueLocation(false);
                break;
            case R.id.btn_runc_continue: //继续
                runningAnim(mRunningUtil.RUNNING_CONTINUE);
                continueLocation(true);
                break;
            case R.id.btn_runc_finish:   //结束
                runningFinish();
                break;
            case R.id.btn_rund_map:   //地图
                ShowOrHideMapView();
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        if (hasFocus && !hasAnimationStarted) {
            mHandler.sendEmptyMessageAtTime(mHandler.ANIMATION_START, 16);
        }
    }

    // 点击完成或者返回键，判断是否能保存数据
    private void canFinish()
    {
        runningAnim(mRunningUtil.RUNNING_PAUSE);
        continueLocation(false);

        final String RunReminder = mRunningUtil.canFinish();
        final String hint;
        hint = RunReminder.equals("")?"当前运动数据有效，确定保存数据并退出吗？":("无法保存记录，因为" + RunReminder + "。确定要放弃吗？");
        new AlertDialog
                .Builder(RunActivity.this)
//                    .setTitle("标题")
                .setMessage(hint)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if(RunReminder.equals("")){
                            commitRunningInfo();
                        }else{
                            finish();
                        }
                    }
                })
                .setNegativeButton("继续运动", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        runningAnim(mRunningUtil.RUNNING_CONTINUE);
                        continueLocation(true);
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    /** 跑步结束 */
    private void runningFinish(){
        setRunningMapEnable(false);

        String RunReminder = mRunningUtil.canFinish();
        if(RunReminder.equals("")){ //运动有效，保存运动数据
            commitRunningInfo();
        }else{
            new AlertDialog
                    .Builder(RunActivity.this)
//                    .setTitle("标题")
                    .setMessage("无法保存记录，因为" + RunReminder + "。确定要放弃吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            runningAnim(mRunningUtil.RUNNING_CONTINUE); //继续运动
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }
    }

    private void ShowOrHideMapView()
    {
        if (rel_run_map.getVisibility()==View.VISIBLE) {
            rel_run_map.setVisibility(View.GONE);

            setStatusBarColor(false, true, mContext.getResources().getColor(R.color.back_run_color));
            mBtnLeft.setBackgroundResource(R.drawable.btn_back_white);
            setTitleBackgroudColor(R.color.back_run_color);
            setTittle("正在跑步", R.color.white);
            setRightButtonText("完成", R.color.main_color);
            setHideTitleLine();
        } else {
            rel_run_map.setVisibility(View.VISIBLE);
            drawMapLine();

            setStatusBarColor(false, true, mContext.getResources().getColor(R.color.white));
            mBtnLeft.setBackgroundResource(R.drawable.btn_back);
            setTitleBackgroudColor(R.color.title_color_bg);
            setTittle("运动轨迹", R.color.title_color_title);
            mBtnRight.setVisibility(View.GONE);
            mTitleLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (rel_run_map.getVisibility()==View.VISIBLE) {
                ShowOrHideMapView();
            } else {
                canFinish();
            }
            return true;
        }
        return super.onKeyDown(event.getKeyCode(), event);
    }


    @Override
    protected void pauseClose()
    {
        super.pauseClose();
        mMapView.onPause();
    }

    @Override
    protected void destroyClose()
    {
        super.destroyClose();
        if (mHandler != null) mHandler.removeCallbacksAndMessages(null);
        if (mRunningUtil != null) {
            mRunningUtil.closeRunning();
        }
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient = null;
        }
        mMapView.onDestroy();
    }
}
