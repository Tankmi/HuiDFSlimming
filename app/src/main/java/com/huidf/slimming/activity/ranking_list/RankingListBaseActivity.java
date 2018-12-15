package com.huidf.slimming.activity.ranking_list;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.huidf.slimming.R;
import com.huidf.slimming.base.BaseFragmentActivityForAnnotation;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.entity.user.UserEntity;
import com.huidf.slimming.util.run.RunningUtil;
import com.huidf.slimming.view.home.today_movement.RunCircleAnimationView;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;

import java.lang.ref.WeakReference;
import java.util.List;

import huitx.libztframework.context.ContextConstant;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.ToastUtils;
import huitx.libztframework.utils.UnitConversion;


public class RankingListBaseActivity extends BaseFragmentActivityForAnnotation implements DialogInterface.OnDismissListener,RunningUtil.RunningTimeHome
        ,AMapLocationListener,LocationSource {

    @ViewInject(R.id.lin_run_distance)private LinearLayout lin_run_distance;
    @ViewInject(R.id.tv_run_distance)private TextView tv_run_distance;
    @ViewInject(R.id.btn_rund_map)private Button btn_rund_map;
    @ViewInject(R.id.lin_run_info)private LinearLayout lin_run_info;
    @ViewInject(R.id.tv_run_info01) private TextView tv_run_info01;
    @ViewInject(R.id.tv_run_info02)private TextView tv_run_info02;
    @ViewInject(R.id.tv_run_info03) private TextView tv_run_info03;
    @ViewInject(R.id.rel_run_control) private RelativeLayout rel_run_control;
    @ViewInject(R.id.btn_runc_pause) private Button mBtnPause;
    @ViewInject(R.id.btn_runc_continue) private Button mBtnContinue;
    @ViewInject(R.id.btn_runc_finish) private Button mBtnFinish;
    @ViewInject(R.id.runcview_animation) private RunCircleAnimationView backgroundView;
    @ViewInject(R.id.rel_run_anim) private RelativeLayout rel_run_anim;
    @ViewInject(R.id.tv_run_anim) private TextView tv_run_anim;
    @ViewInject(R.id.rel_run_map) protected RelativeLayout rel_run_map;
    @ViewInject(R.id.mapview_running) protected MapView mMapView;

    protected RunningUtil mRunningUtil; //运动控制类
    private float calorie = 0;  //消耗的卡路里
    private float runDuration = 0;  //运动时长 秒
    //标记动画是否已经加载过
    protected boolean hasAnimationStarted;
    //渲染动画起始坐标
    final int[] location = new int[2];
    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    //以前的定位点
    private LatLng oldLatLng;
    private AMap aMap;

    public RankingListBaseActivity() {
        super();
    }

    @Override
    protected void initContent() {
        mBtnContinue.setVisibility(View.GONE);
        mBtnFinish.setVisibility(View.GONE);

        tv_run_info01.setCompoundDrawables(null,
                NewWidgetSetting.getInstance().getWeightDrawable(R.drawable.icon_running_velocity,70,70,true), null, null);
        tv_run_info02.setCompoundDrawables(null,
                NewWidgetSetting.getInstance().getWeightDrawable(R.drawable.icon_running_usertime,70,70,true), null, null);
        tv_run_info03.setCompoundDrawables(null,
                NewWidgetSetting.getInstance().getWeightDrawable(R.drawable.icon_running_calorie,70,70,true), null, null);
    }

    @Override
    public void runningTimesHome(int velocity, int runDuration, String time, float distance, int stepNum, int calorie, int state) {
        this.runDuration = runDuration;
        this.calorie = calorie;

        distance = UnitConversion.preciseNumber((distance/1000f),2);
        tv_run_distance.setText(distance + "");
        NewWidgetSetting.getInstance().setIdenticalLineTvColor(tv_run_distance,
                -999, 0.28f, "\r公里", false);

        tv_run_info01.setText(velocity==0?"-.-":tranTimes.secondFormating(velocity));
        NewWidgetSetting.getInstance().setIdenticalLineTvColor(tv_run_info01,
                mContext.getResources().getColor(R.color.text_color_hint), 0.8f, "配速", true);

        tv_run_info02.setText(runDuration==0?"00:00:00":tranTimes.secondFormating(runDuration));
        NewWidgetSetting.getInstance().setIdenticalLineTvColor(tv_run_info02,
                mContext.getResources().getColor(R.color.text_color_hint), 0.8f, "用时", true);

        tv_run_info03.setText(calorie==0?"00":calorie+"");
        NewWidgetSetting.getInstance().setIdenticalLineTvColor(tv_run_info03,
                mContext.getResources().getColor(R.color.text_color_hint), 0.8f, "千卡", true);

//        LOG("运动工具类的回调" + runDuration);
    }

    /**
     * 提交运动数据
     */
    protected void commitRunningInfo()
    {
        RequestParams params = PreferenceEntity.getLoginParams();
        params.addBodyParameter("sportType", "23");
        params.addBodyParameter("sportKcal", "" + calorie);
//        params.addBodyParameter("sportTime", "" + runDuration);
        params.addBodyParameter("sportTime", "" + UnitConversion.preciseNumber((runDuration/60.0f),0));
        params.addBodyParameter("type", "1");
        mgetNetData.GetData(this, UrlConstant.API_INSERTSPORT, mHandler.API_INSERTSPORT, params);
        setLoading(true, "");
    }

    @Override
    public void paddingDatas(String mData, int type) {
        setLoading(false, "");
        Gson mGson = new Gson();
        UserEntity mUserEntity;
        try {
            mUserEntity = mGson.fromJson(mData, UserEntity.class);
        } catch (Exception e) {
            return;
        }
        if (mUserEntity.code == ContextConstant.RESPONSECODE_200) {
            if (type == mHandler.API_INSERTSPORT) {    //录入运动信息
                finish();
            }
        } else if (mUserEntity.code == ContextConstant.RESPONSECODE_310) {    //登录信息过时跳转到登录页
            reLoading();
        } else {
            ToastUtils.showToast(NewWidgetSetting.filtrationStringbuffer(mUserEntity.msg, "接口信息异常！"));
        }
    }

    @Override
    public void error(String msg, int type) {
        LOG(msg);
    }

    protected MyHandler mHandler;

    protected class MyHandler extends Handler{
        protected final int ANIMATION_START = 100;  //启动倒计时背景渲染动画
        private final int ANIMATION_STATE = 101;    //更新倒计时动画进度
        private final int API_INSERTSPORT = 1001;

        // SoftReference<Activity> 也可以使用软应用 只有在内存不足的时候才会被回收
        private final WeakReference<Activity> mActivity;

        protected MyHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = mActivity.get();
            if (activity != null){
                //做操作
                switch (msg.what) {
                    case API_INSERTSPORT: // 录入运动信息

                        break;
                    case ANIMATION_START: // 启动倒计时背景渲染动画
                        setStartRunState(true);

//                        hasAnimationStarted = true;
//
//                        lin_run_distance.setVisibility(View.VISIBLE);
//                        lin_run_info.setVisibility(View.VISIBLE);
//                        rel_run_control.setVisibility(View.VISIBLE);
//                        continueLocation(true);
//                        mRunningUtil.setRunningState(mRunningUtil.RUNNING_START);

                        break;
                    case ANIMATION_STATE: // 更新倒计时动画进度
                        int state = msg.arg1;
                        switch (state){
                            case 4:
                                startCountDownAnim(state - 1,"2");
                                break;
                            case 3:
                                startCountDownAnim(state - 1,"1");
                                break;
                            case 2:
                                startCountDownAnim(state - 1,"GO");
                                break;
                            case 1: //缩放动画
                                setStartRunState(false);
                                break;
                            case 0: //开始跑步
                                lin_run_distance.setVisibility(View.VISIBLE);
                                lin_run_info.setVisibility(View.VISIBLE);
                                rel_run_control.setVisibility(View.VISIBLE);
                                continueLocation(true);
                                mRunningUtil.setRunningState(mRunningUtil.RUNNING_START);
                                break;
                        }
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void initLocation() {
        mLayoutUtil.setIsFullScreen(true);
//        mLayoutUtil.drawViewRBLayout(rel_run_control, 0, 0, 120, 120, -1, -1);
        mLayoutUtil.drawViewRBLinearLayout(btn_rund_map, 70, 70, -1, -1, -1, -1);
        mLayoutUtil.drawViewRBLayout(mBtnPause, 190, 190, -1, -1, -1, -1);
        mLayoutUtil.drawViewRBLayout(mBtnContinue, 190, 190, -1, -1, -1, -1);
        mLayoutUtil.drawViewRBLayout(mBtnFinish, 190, 190, -1, -1, -1, -1);
    }

    /** 运动开始页面渲染动画加载 */
    protected void setStartRunState(final boolean isStart) {
        if(isStart){
            backgroundView.setOnStateChangeListener(new RunCircleAnimationView.OnStateChangeListener() {

                @Override
                public void onStateChange(int state, boolean playState) {
                    if (state == RunCircleAnimationView.STATE_FINSHED) {
                        if(playState){
                            LOG("动画结束:" + "展示");
                            rel_run_anim.setVisibility(View.VISIBLE);
                            startCountDownAnim(4,"3");
                        }else{
                            LOG("动画结束:" + "缩放");
                            Message msg = new Message();
                            msg.arg1 = 0;
                            msg.what = mHandler.ANIMATION_STATE;
                            mHandler.sendMessage(msg);
                        }
                    }
                }
            });
            //获取动画开始位置
            mBtnPause.getLocationOnScreen(location);
            location[0] = location[0] + mLayoutUtil.getWidgetWidth(95,true);
            location[1] = location[1] + mLayoutUtil.getWidgetHeight(95);
            rel_run_control.setVisibility(View.GONE);

            //启动动画
            backgroundView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    backgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
                    backgroundView.setFillPaintColor(mContext.getResources().getColor(R.color.main_color));
                    backgroundView.startFromLocation(location);
                    hasAnimationStarted = true;
                    return true;

                }
            });
            backgroundView.getViewTreeObserver().dispatchOnDraw();
        }else{
            backgroundView.endToLocation(location);
        }
    }

    /** 运动开始倒计时动画加载 */
    private void startCountDownAnim(final int state, String msg){
        tv_run_anim.setText(msg);
        ObjectAnimator mObjAniX = ObjectAnimator.ofFloat(tv_run_anim, "scaleX",0.0f, 2.0f, 1.5f, 2.0f, 0.0f);
        ObjectAnimator mObjAniY = ObjectAnimator.ofFloat(tv_run_anim, "scaleY",0.0f, 4.0f, 2.8f, 4.0f, 0.0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(mObjAniX).with(mObjAniY);
        animSet.setDuration(1000);
        animSet.start();

        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                Message msg = new Message();
                msg.arg1 = state;
                msg.what =  mHandler.ANIMATION_STATE;
                mHandler.sendMessage(msg);
            }
        });
    }

    /** 状态切换时控制按钮不可点击，否则会出现事件冲突 */
    public void setRunningMapEnable(boolean enable){
        mBtnContinue.setEnabled(enable);
        mBtnPause.setEnabled(enable);
        mBtnFinish.setEnabled(enable);
    }

    private float excursionX;    //偏移量

    /**
     * 动画切换
     * @param state 0，开始；1，暂停；2，继续；3，结束
     */
    public void runningAnim(int state) {
        mRunningUtil.setRunningState(state);
        setRunningMapEnable(false);

        AnimatorSet animSet;
        ObjectAnimator CTLAniAlpha = null;
        ObjectAnimator CTLAniTran = null;
        ObjectAnimator CTRAniAlpha = null;
        ObjectAnimator CTRAniTran = null;
        ObjectAnimator CBTAniAlpha = null;
        animSet = new AnimatorSet();
        AnimatorAddListener(animSet,state);
        excursionX = mBtnPause.getX();

//        if(state == 2 || state == 3){
        if(state == mRunningUtil.RUNNING_CONTINUE){ //继续
            //继续按钮归位
            CTLAniAlpha = ObjectAnimator.ofFloat(mBtnContinue, "alpha", 1.0f, 0.0f);
            CTLAniTran = ObjectAnimator.ofFloat(mBtnContinue, "translationX", -excursionX, 0);
            //结束按钮归位
            CTRAniAlpha = ObjectAnimator.ofFloat(mBtnFinish, "alpha", 1.0f, 0.0f);
            CTRAniTran = ObjectAnimator.ofFloat(mBtnFinish, "translationX", excursionX, 0);
            //暂停按钮显示
            CBTAniAlpha = ObjectAnimator.ofFloat(mBtnPause, "alpha", 0.0f, 1.0f);
        }else if(state == mRunningUtil.RUNNING_PAUSE){  //暂停
            //继续按钮左移动画
            CTLAniAlpha = ObjectAnimator.ofFloat(mBtnContinue, "alpha", 0.0f, 1.0f);
            CTLAniTran = ObjectAnimator.ofFloat(mBtnContinue, "translationX", 0, -excursionX);
            //结束按钮右移
            CTRAniAlpha = ObjectAnimator.ofFloat(mBtnFinish, "alpha", 0.0f, 1.0f);
            CTRAniTran = ObjectAnimator.ofFloat(mBtnFinish, "translationX", 0, excursionX);
            //暂停按钮隐藏
            CBTAniAlpha = ObjectAnimator.ofFloat(mBtnPause, "alpha", 1.0f, 0.0f);
        }
        if(CTLAniAlpha !=null){
            animSet.play(CTLAniAlpha).with(CTLAniTran);
            animSet.play(CTRAniAlpha).with(CTRAniTran);
            animSet.play(CBTAniAlpha);
            animSet.setDuration(300);
            animSet.start();
        }
    }

    private void AnimatorAddListener(AnimatorSet animSet,final int state){
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (state == mRunningUtil.RUNNING_PAUSE) {
                    mBtnContinue.setVisibility(View.VISIBLE);
                    mBtnFinish.setVisibility(View.VISIBLE);
                } else if (state == mRunningUtil.RUNNING_CONTINUE) {
                    mBtnPause.setSelected(true);
                    mBtnPause.setVisibility(View.VISIBLE);
                } else if (state == mRunningUtil.RUNNING_FINISH) {
                    mBtnPause.setSelected(false);
                    mBtnPause.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setRunningMapEnable(true);
                if (state == mRunningUtil.RUNNING_PAUSE) {
                    mBtnPause.setVisibility(View.GONE);
                } else if (state == mRunningUtil.RUNNING_CONTINUE || state == mRunningUtil.RUNNING_FINISH) {
                    mBtnContinue.setVisibility(View.GONE);
                    mBtnFinish.setVisibility(View.GONE);
                }
            }
        });
    }


    private OnLocationChangedListener mListener;
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener)
    {   this.mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //Hight_Accuracy，高精度定位模式； Battery_Saving，低功耗模式；Device_Sensors，设备模式，不需要网络
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
            //设置是否单次定位
            mLocationOption.setOnceLocation(false);
            /**
             * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
             * 注意：只有在高精度模式下的单次定位有效，其他方式无效
             */
            mLocationOption.setGpsFirst(true);
            // 设置发送定位请求的时间间隔,最小值为1000ms,1秒更新一次定位信息
            mLocationOption.setInterval(2000);
            //设置是否返回地址描述信息（默认返回地址信息）
            mLocationOption.setNeedAddress(false);
            //设置是否强制刷新WIFI，默认为true，强制刷新。
            mLocationOption.setWifiActiveScan(false);
            //设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setMockEnable(false);

            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//            mlocationClient.startLocation();
            LOG("定位功能初始化完毕");
        }

    }

    @Override
    public void deactivate()
    {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }


    /** true开始定位，false停止定位 */
    protected void continueLocation(boolean state) {
//        if (mlocationClient == null) {
//            mlocationClient = new AMapLocationClient(ApplicationData.context);
//            //初始化定位参数
//            mLocationOption = new AMapLocationClientOption();
//            //设置定位监听
//            mlocationClient.setLocationListener(this);
//            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
//            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//            //设置定位间隔,单位毫秒,默认为2000ms
//            mLocationOption.setInterval(2000);
//            //设置定位参数
//            mlocationClient.setLocationOption(mLocationOption);
//        }
//
//        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
//        // 在定位结束后，在合适的生命周期调用onDestroy()方法
//        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//        //启动定位
        if(state) {
            mlocationClient.startLocation();
        }else{
            mlocationClient.stopLocation();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {

        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                //定位成功回调信息，设置相关消息
//                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                amapLocation.getLatitude();//获取纬度
//                amapLocation.getLongitude();//获取经度
//                amapLocation.getAccuracy();//获取精度信息
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date = new Date(amapLocation.getTime());
//                df.format(date);//定位时间

//                LOG("定位回调：" + amapLocation.getLatitude() + "   " + amapLocation.getLongitude());
                //定位成功
                LatLng newLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                if(oldLatLng == null || !oldLatLng.equals(newLatLng)){
                    setUpMap(oldLatLng, newLatLng);
                    oldLatLng = newLatLng;
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                LOG("AmapError " + "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    /**
     * 初始化地图视图各个参数
     */
    protected void initMapLocation() {
        if (aMap == null) aMap = mMapView.getMap();
        //定位
		aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位 LOCATION_TYPE_LOCATE、跟随 LOCATION_TYPE_MAP_FOLLOW 或地图根据面向方向旋转 LOCATION_TYPE_MAP_ROTATE
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);

        //画线
        // 缩放级别（zoom）：地图缩放级别范围为【4-20级】，值越大地图越详细
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        //使用 aMap.setMapTextZIndex(2) 可以将地图底图文字设置在添加的覆盖物之上
        aMap.setMapTextZIndex(2);
//        setUpMap(new LatLng(43.828, 87.621), new LatLng(43.800, 87.621));
    }


    protected void drawMapLine()
    {
        List<LatLng> latLngs = mRunningUtil.getLatLngs();
        LOG("drawMapLine 画线" + latLngs.size());
        Polyline polyline = aMap.addPolyline(new PolylineOptions().
                addAll(latLngs).width(10).color(Color.argb(255, 19, 208, 202)));
    }

    /**
     * 绘制两个坐标点之间的线段,从以前位置到现在位置
     */
    private void setUpMap(LatLng oldData, LatLng newData)
    {
        if (mRunningUtil.addCoordinates(newData)) {
            LOG("新增坐标成功");
            if(rel_run_map.getVisibility() == View.VISIBLE){
                LOG("绘制曲线");
                // 绘制一个大地曲线
                aMap.addPolyline((new PolylineOptions())
                        .add(oldData, newData)
                        .width(10).color(Color.argb(255, 19, 208, 202)));
//					.geodesic(true).color(mContext.getResources().getColor(R.color.main_color)));
            }

        } else {
            LOG("新增坐标失败");
        }
    }

    @Override
    protected void initHead() { }

    @Override
    protected void initLogic() { }

    @Override
    protected void pauseClose()  {

    }

    @Override
    protected void destroyClose() {

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        LOG("onDismiss");
    }



}
