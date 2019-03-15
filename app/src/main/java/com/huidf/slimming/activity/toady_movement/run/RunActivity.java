package com.huidf.slimming.activity.toady_movement.run;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.huidf.slimming.R;
import com.huidf.slimming.activity.personal_center.UserInfoActivity;
import com.huidf.slimming.activity.personal_center.select_photo.SelectPhotoActivity;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.service.LocationService;
import com.huidf.slimming.service.RunService;
import com.huidf.slimming.util.run.RunningUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

import huitx.libztframework.utils.MathUtils;
import huitx.libztframework.utils.ToastUtils;


/**
 * 跑步
 *
 * @author ZhuTao
 * @date 2018/12/11
 * @params
 */
@ContentView(R.layout.activity_run)
public class RunActivity extends RunBaseActivity {

    /** 后台保活service */
//    private Intent serviceLocationIntent = null;


    public RunActivity()
    {
        TAG = getClass().getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mMapView.onCreate(savedInstanceState);

//        serviceLocationIntent = new Intent();
//        serviceLocationIntent.setClass(this,LocationService.class);
    }

    @Override
    protected void initHead() {
        setStatusBarColor(false, true, mContext.getResources().getColor(R.color.back_run_color));
        mBtnLeft.setBackgroundResource(R.drawable.btn_back_white);
        setTitleBackgroudColor(R.color.back_run_color);
        setTittle("正在跑步", R.color.white);
        setRightButtonText("完成", R.color.main_color);
        setHideTitleLine();
        if (mHandler == null) mHandler = new MyHandler(this);

        mRunningUtil = RunningUtil.getInstance();
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
//        if (null != serviceLocationIntent) {
//            stopService(serviceLocationIntent);
//        }
        isVisible = true;
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
//                continueLocation(false);
                break;
            case R.id.btn_runc_continue: //继续
                runningAnim(mRunningUtil.RUNNING_CONTINUE);
//                continueLocation(true);
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
//        continueLocation(false);

//        final String RunReminder = mRunningUtil.canFinish();
        final String RunReminder;
        if(mRunService != null) {
            RunReminder = mRunService.canFinish();
        }else {
            RunReminder = "运动服务出错";
//            return;
        }
        LOG(RunReminder);
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
//                        continueLocation(true);
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    /** 跑步结束 */
    private void runningFinish(){
        setRunningMapEnable(false);

        String RunReminder;
        if(mRunService != null) {
            RunReminder = mRunService.canFinish();
        }else {
            RunReminder = "运动服务出错";
//            return;
        }
        LOG(RunReminder);

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
        if (rel_run_map.getVisibility()==View.VISIBLE) {    //隐藏地图view
            rel_run_map.setVisibility(View.GONE);

            setStatusBarColor(false, true, mContext.getResources().getColor(R.color.back_run_color));
            mBtnLeft.setBackgroundResource(R.drawable.btn_back_white);
            setTitleBackgroudColor(R.color.back_run_color);
            setTittle("正在跑步", R.color.white);
            setRightButtonText("完成", R.color.main_color);
            setHideTitleLine();
        } else {    //显示地图view
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
        isVisible = false;
        mMapView.onPause();
//        if (!isRunfinish && null != serviceLocationIntent) {
//            startService(serviceLocationIntent);
//        }
//        if (!isRunfinish && null != serviceIntent) {
//            startService(serviceIntent);
//            continueLocation(false);
//        }
//        //启动后台定位，第一个参数为通知栏ID，建议整个APP使用一个
//        if (mlocationClient != null) {
//            mlocationClient.enableBackgroundLocation(2001, buildNotification());
//        }
    }

    @Override
    protected void destroyClose()
    {
        super.destroyClose();
        if (mHandler != null) mHandler.removeCallbacksAndMessages(null);
        if (mRunningUtil != null) {
            mRunningUtil.closeRunning();
        }
//        if (null != serviceLocationIntent) {
//            stopService(serviceLocationIntent);
//        }
//        if (null != serviceIntent) {
//            stopService(serviceIntent);
//        }
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
//            //关闭后台定位，参数为true时会移除通知栏，为false时不会移除通知栏，但是可以手动移除
//            mlocationClient.disableBackgroundLocation(true);
            mlocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
            mlocationClient = null;
        }
        mMapView.onDestroy();

        if(mRunService!=null){
            try{
                unbindService(mServiceConnection);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }




}
