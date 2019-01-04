package com.huidf.slimming.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.huidf.slimming.context.ApplicationData;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.util.run.RunningUtil;

import huitx.libztframework.utils.LOGUtils;
import huitx.libztframework.utils.MathUtils;
import huitx.libztframework.utils.TransitionTime;

/**
 * 作者：ZhuTao
 * 创建时间：2018/12/28 : 14:58
 * 描述：
 */
public class RunService extends Service implements RunningUtil.RunningTimeHome,AMapLocationListener {

    private final IBinder mBinder = new LocalBinder();
    protected RunningUtil mRunningUtil; //运动控制类

    @Override
    public void onCreate() {
        super.onCreate();
        initRunningUtil();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        LOG("onStartCommand");
        continueLocation(true);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mRunningUtil = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
//            //关闭后台定位，参数为true时会移除通知栏，为false时不会移除通知栏，但是可以手动移除
//            mlocationClient.disableBackgroundLocation(true);
            mlocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
            mlocationClient = null;
        }
    }

    private void initRunningUtil(){
        mRunningUtil = RunningUtil.getInstance();
        mRunningUtil.setRunnintTimeObject(this);
        mRunningUtil.setUserInfo(MathUtils.stringToFloatForPreference(PreferenceEntity.KEY_USER_CURRENT_WEIGHT, 66.0f),
                MathUtils.stringToFloatForPreference(PreferenceEntity.KEY_USER_HEIGHT, 170f));
    }

    @Override
    public void runningTimesHome(int velocity, int runDuration, String time, float distance, int stepNum, int calorie, int state)
    {
        LOG("runningTimesHome: " + (runDuration==0?"00:00:00": TransitionTime.getInstance().secondFormating(runDuration)));
    }


    /** true开始定位，false停止定位 */
    public void continueLocation(boolean state) {
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(ApplicationData.context);
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
            /*
             Hight_Accuracy，高精度定位模式（会同时使用网络定位和GPS定位，优先返回最高精度的定位结果，以及对应的地址描述信息）；
             Battery_Saving，低功耗模式(不会使用GPS和其他传感器，只会使用网络定位（Wi-Fi和基站定位））；
             Device_Sensors， 设备模式，不需要网络(只使用GPS进行定位，这种模式下不支持室内环境的定位，需要在室外环境下才可以成功定位。)
             */
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
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
            //设置定位请求超时时间 单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
//            mLocationOption.setHttpTimeOut(20000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
        }

        //        //启动定位
        if(state) {
            LOG("启动定位");
            mlocationClient.startLocation();
        }else{

            if (mlocationClient != null) {
                LOG("停止定位");
                mlocationClient.stopLocation();
                mlocationClient.onDestroy();
            }
            mlocationClient = null;
        }
    }

    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //用来设置发起定位的模式和相关参数。
    public AMapLocationClientOption mLocationOption = null;
    //以前的定位点
    private LatLng oldLatLng;
//    private AMap aMap;
//    private OnLocationChangedListener mListener;
//    @Override
//    public void activate(OnLocationChangedListener onLocationChangedListener) {
//        LOG("activate");
//        this.mListener = onLocationChangedListener;
//        if (mlocationClient == null) {
//            mlocationClient = new AMapLocationClient(ApplicationData.context);
//            //设置定位监听
//            mlocationClient.setLocationListener(this);
//
//            mLocationOption = new AMapLocationClientOption();
//            mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
//            /*
//             Hight_Accuracy，高精度定位模式（会同时使用网络定位和GPS定位，优先返回最高精度的定位结果，以及对应的地址描述信息）；
//             Battery_Saving，低功耗模式(不会使用GPS和其他传感器，只会使用网络定位（Wi-Fi和基站定位））；
//             Device_Sensors， 设备模式，不需要网络(只使用GPS进行定位，这种模式下不支持室内环境的定位，需要在室外环境下才可以成功定位。)
//             */
//            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
//            //设置是否单次定位
//            mLocationOption.setOnceLocation(false);
//            /**
//             * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
//             * 注意：只有在高精度模式下的单次定位有效，其他方式无效
//             */
//            mLocationOption.setGpsFirst(true);
//            // 设置发送定位请求的时间间隔,最小值为1000ms,1秒更新一次定位信息
//            mLocationOption.setInterval(2000);
//            //设置是否返回地址描述信息（默认返回地址信息）
//            mLocationOption.setNeedAddress(false);
//            //设置是否强制刷新WIFI，默认为true，强制刷新。
//            mLocationOption.setWifiActiveScan(false);
//            //设置是否允许模拟位置,默认为false，不允许模拟位置
//            mLocationOption.setMockEnable(false);
//            //设置定位请求超时时间 单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
////            mLocationOption.setHttpTimeOut(20000);
//
//            //设置定位参数
//            mlocationClient.setLocationOption(mLocationOption);
//            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
//            // 在定位结束后，在合适的生命周期调用onDestroy()方法
//            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
////            mlocationClient.startLocation();
//            LOG("定位功能初始化完毕");
//        }
//
//    }
//
//    @Override
//    public void deactivate()
//    {
//        LOG("deactivate");
//        mListener = null;
//        if (mlocationClient != null) {
//            mlocationClient.stopLocation();
//            mlocationClient.onDestroy();
//        }
//        mlocationClient = null;
//    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {

//        if (mListener != null && amapLocation != null) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
//                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                //定位成功回调信息，设置相关消息
//                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                amapLocation.getLatitude();//获取纬度
//                amapLocation.getLongitude();//获取经度
//                amapLocation.getAccuracy();//获取精度信息
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date = new Date(amapLocation.getTime());
//                df.format(date);//定位时间

                LOG("定位回调：" + amapLocation.getLatitude() + "   " + amapLocation.getLongitude());
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
     * 绘制两个坐标点之间的线段,从以前位置到现在位置
     */
    private void setUpMap(LatLng oldData, LatLng newData)
    {
        if (mRunningUtil.addCoordinates(newData)) {
            LOG("新增坐标成功");
//            if(rel_run_map.getVisibility() == View.VISIBLE){
//                LOG("绘制曲线");
//                // 绘制一个大地曲线
//                aMap.addPolyline((new PolylineOptions())
//                        .add(oldData, newData)
//                        .width(10).color(Color.argb(255, 19, 208, 202)));
////					.geodesic(true).color(mContext.getResources().getColor(R.color.main_color)));
//            }

        } else {
            LOG("新增坐标失败");
        }
    }

    public class LocalBinder extends Binder {
        RunService getService() {
            return RunService.this;
        }
    }

    private void LOG(String data){
        LOGUtils.LOG("RunService:", data);
    }

}
