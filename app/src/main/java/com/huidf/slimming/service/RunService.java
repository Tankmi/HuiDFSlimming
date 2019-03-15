package com.huidf.slimming.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.amap.api.track.AMapTrackClient;
import com.amap.api.track.ErrorCode;
import com.amap.api.track.OnTrackLifecycleListener;
import com.amap.api.track.TrackParam;
import com.amap.api.track.query.entity.LocationMode;
import com.amap.api.track.query.model.AddTerminalRequest;
import com.amap.api.track.query.model.AddTerminalResponse;
import com.amap.api.track.query.model.AddTrackRequest;
import com.amap.api.track.query.model.AddTrackResponse;
import com.amap.api.track.query.model.QueryTerminalRequest;
import com.amap.api.track.query.model.QueryTerminalResponse;
import com.huidf.slimming.R;
import com.huidf.slimming.activity.toady_movement.run.RunActivity;
import com.huidf.slimming.context.ApplicationData;
import com.huidf.slimming.util.run.RunningUtil;
import java.lang.ref.WeakReference;

import huitx.libztframework.utils.LOGUtils;
import huitx.libztframework.utils.TransitionTime;
import huitx.libztframework.utils.NumberConversion;

/**
 * 作者：ZhuTao
 * 创建时间：2018/12/28 : 14:58
 * 描述：
 */
public class RunService extends Service implements SensorEventListener,AMapLocationListener {

    private final IBinder mBinder = new LocalBinder();
    protected RunningUtil mRunningUtil; //运动控制类

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        initRunningUtil();
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        LOG("onStartCommand");
//        continueLocation(true);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private boolean isInitialize;
    public boolean initialize(){
        if(isInitialize)return false;
        if (mHandler == null) mHandler = new MyHandler(this);
        initLocation();
        initRunningUtil();
        initSensor();
        isInitialize = true;
        return isInitialize;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
//        mRunningUtil = null;
        close();
    }

    /** true开始定位，false停止定位 */
    public void continueLocation(boolean state) {
        if(state) {
            LOG("启动定位");
            if (isServiceRunning) {
                aMapTrackClient.stopTrack(new TrackParam(Constants.SERVICE_ID, terminalId), onTrackLifecycleListener);
            } else {
                startTrack();
            }
//            aMapTrackClient.setTrackId(trackId);
//            aMapTrackClient.startGather(onTrackLifecycleListener);
        }else{
            LOG("停止定位");
            if (isGatherRunning) {
                aMapTrackClient.stopGather(onTrackLifecycleListener);
            }
        }
//        if(state) {
//            LOG("启动定位");
//            mlocationClient.startLocation();
//        }else{
//            if (mlocationClient != null) {
//                LOG("停止定位");
//                mlocationClient.stopLocation();
////                mlocationClient.onDestroy();
////            mlocationClient = null;
//            }
//        }
    }

    private void startTrack() {
        aMapTrackClient.queryTerminal(new QueryTerminalRequest(Constants.SERVICE_ID, Constants.TERMINAL_NAME), new SimpleOnTrackListener() {
            @Override
            public void onQueryTerminalCallback(QueryTerminalResponse queryTerminalResponse) {
                if (queryTerminalResponse.isSuccess()) {
                    if (queryTerminalResponse.isTerminalExist()) {
                        // 当前终端已经创建过，直接使用查询到的terminal id
                        terminalId = queryTerminalResponse.getTid();
                        if (uploadToTrack) {
                            aMapTrackClient.addTrack(new AddTrackRequest(Constants.SERVICE_ID, terminalId), new SimpleOnTrackListener() {
                                @Override
                                public void onAddTrackCallback(AddTrackResponse addTrackResponse) {
                                    if (addTrackResponse.isSuccess()) {
                                        // trackId需要在启动服务后设置才能生效，因此这里不设置，而是在startGather之前设置了track id
                                        trackId = addTrackResponse.getTrid();
                                        TrackParam trackParam = new TrackParam(Constants.SERVICE_ID, terminalId);
                                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            trackParam.setNotification(createNotification());
                                        }
                                        aMapTrackClient.startTrack(trackParam, onTrackLifecycleListener);
                                    } else {
                                        LOG( "1网络请求失败，" + addTrackResponse.getErrorMsg());
                                    }
                                }
                            });
                        } else {
                            // 不指定track id，上报的轨迹点是该终端的散点轨迹
                            TrackParam trackParam = new TrackParam(Constants.SERVICE_ID, terminalId);
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                trackParam.setNotification(createNotification());
                            }
                            aMapTrackClient.startTrack(trackParam, onTrackLifecycleListener);
                        }
                    } else {
                        // 当前终端是新终端，还未创建过，创建该终端并使用新生成的terminal id
                        aMapTrackClient.addTerminal(new AddTerminalRequest(Constants.TERMINAL_NAME, Constants.SERVICE_ID), new SimpleOnTrackListener() {
                            @Override
                            public void onCreateTerminalCallback(AddTerminalResponse addTerminalResponse) {
                                if (addTerminalResponse.isSuccess()) {
                                    terminalId = addTerminalResponse.getTid();
                                    TrackParam trackParam = new TrackParam(Constants.SERVICE_ID, terminalId);
                                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        trackParam.setNotification(createNotification());
                                    }
                                    aMapTrackClient.startTrack(trackParam, onTrackLifecycleListener);
                                } else {
                                    LOG( "2网络请求失败，" + addTerminalResponse.getErrorMsg());
                                }
                            }
                        });
                    }
                } else {
                    LOG( "3网络请求失败，" + queryTerminalResponse.getErrorMsg() + "  ErrorCode:  " + queryTerminalResponse.getErrorCode());
                }
            }
        });
    }

    /**
     * 设置跑步状态
     *
     * @param state: RUNNING_START 0，开始；RUNNING_PAUSE 1，暂停；
     *               RUNNING_CONTINUE 2，继续；RUNNING_FINISH 3，结束
     */
    public void setRunningState(int state) {
        runState = state;
        mRunningUtil.setRunningStates(state);
        returnRunningInfo();
        if (state == mRunningUtil.RUNNING_START || state == mRunningUtil.RUNNING_CONTINUE) {
            if (state == mRunningUtil.RUNNING_START) {
//                mRunningUtil.makeZero();
//                initSensor();
            }
            continueLocation(true);
            mHandler.postDelayed(runnable, 1000);
        }else if (state == mRunningUtil.RUNNING_PAUSE ) {   //暂停
            continueLocation(false);
        }else if (state == mRunningUtil.RUNNING_FINISH) {   //结束运动，暂停时已经关闭了计时线程，所以得重新走
//            mHandler.post(runnable);
//            continueLocation(false);
            returnRunningInfo();
//            mRunningUtil. makeZero();
        }
    }

    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //用来设置发起定位的模式和相关参数。
//    public AMapLocationClientOption mLocationOption = null;
    /** 猎鹰 */
    public AMapTrackClient aMapTrackClient  = null;


    //以前的定位点
    private LatLng oldLatLng;

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
        } else {
//            LOG("新增坐标失败");
        }
    }

    private void initLocation(){
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(ApplicationData.context);
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //初始化定位参数
            aMapTrackClient   = new AMapTrackClient(ApplicationData.context);
            //设置采集周期为2s,上报周期为30s,默认值（2,20）定位信息采集周期的范围应该是1s~60s，上报周期的范围是采集周期的5～50倍。
            aMapTrackClient.setInterval(2, 30);
            // 配置本地缓存大小为20M，默认为50M
            aMapTrackClient.setCacheSize(20);
             /*
             Hight_Accuracy，高精度定位模式（会同时使用网络定位和GPS定位，优先返回最高精度的定位结果，以及对应的地址描述信息）；
             Battery_Saving，低功耗模式(不会使用GPS和其他传感器，只会使用网络定位（Wi-Fi和基站定位））；
             Device_Sensors， 设备模式，不需要网络(只使用GPS进行定位，这种模式下不支持室内环境的定位，需要在室外环境下才可以成功定位。)
             */
            aMapTrackClient.setLocationMode(LocationMode.HIGHT_ACCURACY);
        }
    }

    private long terminalId;
    private long trackId;
    private boolean uploadToTrack = false;
    
    private boolean isServiceRunning;
    private boolean isGatherRunning;

    final OnTrackLifecycleListener onTrackLifecycleListener = new OnTrackLifecycleListener() {

        @Override
        public void onBindServiceCallback(int i, String s) {

        }

        @Override
        public void onStartGatherCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.START_GATHER_SUCEE || status == ErrorCode.TrackListen.START_GATHER_ALREADY_STARTED) {   //开启成功 || 已经开启
                isGatherRunning = true;
                LOG("定位采集开启成功");
            } else {
                LOG("定位采集启动异常，" + msg);
            }
        }

        @Override
        public void onStartTrackCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.START_TRACK_SUCEE ||
                    status == ErrorCode.TrackListen.START_TRACK_SUCEE_NO_NETWORK ||
                    status == ErrorCode.TrackListen.START_TRACK_ALREADY_STARTED) {
                isServiceRunning = true;
                // 服务启动成功，继续开启收集上报
                aMapTrackClient.startGather(this);
            } else {
                LOG( "轨迹上报服务服务启动异常，" + msg);
            }
        }


        @Override
        public void onStopGatherCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.STOP_GATHER_SUCCE) {
                LOG( "定位采集停止成功");
                isGatherRunning = false;
//                updateBtnStatus();
            } else {
                LOG("定位采集停止异常, status: " + status + ", msg: " + msg);
            }
        }

        @Override
        public void onStopTrackCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.STOP_TRACK_SUCCE) {
                // 成功停止
                LOG( "轨迹上报停止服务成功，" + msg);
                isServiceRunning = false;
                isGatherRunning = false;
//                updateBtnStatus();
            } else {
                LOG( "轨迹上报停止服务异常，" + msg);

            }
        }
    };

    private static final String CHANNEL_ID_SERVICE_RUNNING = "CHANNEL_ID_SERVICE_RUNNING";
    /**
     * 在8.0以上手机，如果app切到后台，系统会限制定位相关接口调用频率
     * 可以在启动轨迹上报服务时提供一个通知，这样Service启动时会使用该通知成为前台Service，可以避免此限制
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private Notification createNotification() {
//        Notification.Builder builder;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_SERVICE_RUNNING, "app service", NotificationManager.IMPORTANCE_LOW);
//            nm.createNotificationChannel(channel);
//            builder = new Notification.Builder(getApplicationContext(), CHANNEL_ID_SERVICE_RUNNING);
//        } else {
//            builder = new Notification.Builder(getApplicationContext());
//        }
//        Intent nfIntent = new Intent(this, RunActivity.class);
//        nfIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("猎鹰sdk运行中")
//                .setContentText("猎鹰sdk运行中");
//        Notification notification = builder.build();

        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        Intent nfIntent = new Intent(this, RunActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("正在后台对运动轨迹进行定位")
                .setContentText("定位进行中")
                .setWhen(System.currentTimeMillis());
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;
        //调用这个方法把服务设置成前台服务
        startForeground(110, notification);

        return notification;
    }
    
    //定义Sensor管理器
    private SensorManager mSensormanager;

    private Sensor mSensor;     //加速度传感器

    private Sensor stepCounter;//单次步伐传感器
    //    private Sensor stepDetector;//步伐总数传感器
    private SensorEventListener stepCounterListener;//步伐总数传感器事件监听器
//    private SensorEventListener stepDetectorListener;//单次步伐传感器事件监听器

    /**
     * 上一次的陀螺仪坐标
     */
    private float[] preCoordinate;
    private float WALKING_THRESHOLD = 30;
    /**
     * 当前计算出的行走步数
     */
    private int stepNum;
    /**
     * 今日的行走步数
     */
//    private int todayStepNum;

    private void initSensor() {
//        LOG("实例化传感器");
        //获得传感器管理服务
        mSensormanager = (SensorManager) ApplicationData.context.getSystemService(ApplicationData.context.SENSOR_SERVICE);
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER) &&
                getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR)
                ){  //设备支持传感器计步
            stepCounter=mSensormanager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);//获取计步总数传感器
//            stepDetector=mSensormanager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);//获取单次计步传感器

            stepCounterListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    LOG(event.values[0]+"---"+event.accuracy+"---"+event.timestamp);
                    if(runState == mRunningUtil.RUNNING_PAUSE || runState == mRunningUtil.RUNNING_FINISH){ //暂停或者结束时不计步
                        return;
                    }
                    LOG("当前步伐计数:"+event.values[0]);
                    LOG("当前步伐时间:"+ TransitionTime.getInstance().
                            convert(event.timestamp/1000000,"yyyy-MM-dd hh:mm:ss"));

//                    stepNum++;
                    if(stepNum >= (int)event.values[0]) return;
                    stepNum = (int)event.values[0];
                    mRunningUtil.setStepNum(stepNum);
//                    LOG("行走步数" + stepNum + ",今日总步数 " + todayStepNum);
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    LOG(sensor.getName()+"---"+accuracy);

                }
            };
            mSensormanager.registerListener(stepCounterListener,stepCounter,SensorManager.SENSOR_DELAY_FASTEST);

        }else{
            mSensor = mSensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//获取加速度传感器对象
            //加速度传感器
            //还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，
            //第三个值是延迟时间的精密度。根据不同应用，需要的反应速率不同，具体根据实际情况设定
            mSensormanager.registerListener(this, mSensor, mSensormanager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(runState == mRunningUtil.RUNNING_PAUSE || runState == mRunningUtil.RUNNING_FINISH){ //暂停或者结束时不计步
            return;
        }
        int sensorType = event.sensor.getType();

        //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
        float[] values = event.values;

        if (sensorType == Sensor.TYPE_ACCELEROMETER) {//加速度传感器

            /*因为一般正常情况下，任意轴数值最大就在9.8~10之间，只有你突然摇动手机
             * 的时候，瞬时加速度才会突然增大或减少。
             * 所以，经过实际测试，只需监听任一轴的加速度大于14的时候，改变你需要的设置
             * 就OK了~~~
             */
//		   if((Math.abs(values[0])>14||Math.abs(values[1])>14||Math.abs(values[2])>14)){
            if ((Math.abs(values[0]) > 11 || Math.abs(values[1]) > 11 || Math.abs(values[2]) > 11)) {

                LOG("行走步数发生变化:" + Math.abs(values[0]) + "," + Math.abs(values[1]) + "," + Math.abs(values[2]));
                analyseData(values);
            }

        }
    }

    /**
     * 行走步数发生改变后触发
     *
     * @param values
     */
    public void analyseData(float[] values) {
        //每隔200ms取加速度力和前一个进行比较
        if (preCoordinate == null) {    //还没有保存过数据
            preCoordinate = new float[3];
            for (int i = 0; i < 3; i++) {
                preCoordinate[i] = values[i];
            }
        } else {    //记录原始坐标的话，就进行比较
            int angle = calculateAngle(values, preCoordinate);
            if (angle >= WALKING_THRESHOLD) {
                stepNum++;
                mRunningUtil.setStepNum(stepNum);
//                todayStepNum++;
//                LOG("行走步数" + stepNum + ",今日总步数 " + todayStepNum);
            }
            for (int i = 0; i < 3; i++) {
                preCoordinate[i] = values[i];
            }
        }
    }

    /**
     * @param newPoints
     * @param oldPoints
     * @return
     * @description 计算加速度矢量角度
     */
    public int calculateAngle(float[] newPoints, float[] oldPoints) {
        int angle = 0;
        float vectorProduct = 0;    //向量积
        float newMold = 0;    //新向量的模
        float oldMold = 0;    //旧向量的模

        for (int i = 0; i < 3; i++) {
            vectorProduct += newPoints[i] * oldPoints[i];
            newMold += newPoints[i] * newPoints[i];
            oldMold += oldPoints[i] * oldPoints[i];
        }
        newMold = (float) Math.sqrt(newMold);
        oldMold = (float) Math.sqrt(oldMold);
        //计算夹角的余弦
        float cosineAngle = (float) (vectorProduct / (newMold * oldMold));
        //通过余弦值求角度
        float fangle = (float) Math.toDegrees(Math.acos(cosineAngle));
        angle = (int) fangle;
        return angle; //返回向量的夹角

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    /**  0，开始；1，暂停；2，继续；3，结束 */
    private int runState = 3;

    /**  运动时间（秒）  */
    private int runDuration = 0;
    private String runningTimes = "00:00:00";

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            LOG("跑步计时" + runState + ":  " + runDuration);
            if (runState == mRunningUtil.RUNNING_START || runState == mRunningUtil.RUNNING_CONTINUE) {
                runDuration++;
            } else if (runState == mRunningUtil.RUNNING_PAUSE) {
                returnRunningInfo();
                return;
            }

            returnRunningInfo();

            mHandler.postDelayed(this, 1000);
        }
    };

    private void initRunningUtil(){
        mRunningUtil = RunningUtil.getInstance();
    }


    public class LocalBinder extends Binder {
        public RunService getService() {
            return RunService.this;
        }
    }

    /**
     * 使用给定的BLE设备后,应用程序必须调用这个方法,以确保正确地释放资源。
     * After using a given BLE device, the app must call this method to ensure
     * resources are released properly.
     */
    private void close() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
//            //关闭后台定位，参数为true时会移除通知栏，为false时不会移除通知栏，但是可以手动移除
//            mlocationClient.disableBackgroundLocation(true);
            mlocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
            mlocationClient = null;
        }

        if (runnable != null) {
            mHandler.removeCallbacks(runnable);
            runnable = null;
            mHandler = null;
        }
        if (mSensormanager != null) {
            if(stepCounterListener!=null)mSensormanager.unregisterListener(stepCounterListener);
            else  mSensormanager.unregisterListener(this);
        }
    }

    /** 返回为空，就表示通过 */
    public String canFinish(){

        if(runDuration >= 60*10){
            if(stepNum >= 150 || mRunningUtil.getRunDistance() >= 500){
                return "";
            }else{
                return "运动步数太少";
            }
        }else{
            return "运动时间太短";
        }
    }

    /**
     * 统一回调信息
     */
    public void returnRunningInfo() {
        int h, m, s;
        h = runDuration / 3600;
        m = runDuration % 3600 / 60;
        s = runDuration % 3600 % 60;
        runningTimes = "" + (h > 9 ? h : "0" + h) + ":" + (m > 9 ? m : "0" + m) + ":" + (s > 9 ? s : "0" + s);

        float stepDistance = mRunningUtil.getRunDistance();

        try {
//            runningTimes = (runningTimes == null || runningTimes.equals(""))?"00:00:00":runningTimes;
            mRunningInterface.onRunningService(mRunningUtil.getVelocity(stepDistance), runDuration, runningTimes, stepDistance, stepNum,
                    mRunningUtil.getCalories(NumberConversion.preciseNumber((stepDistance/1000f),2)), runState);
        } catch (Exception e) {
            LOG( "RunningUtil :" + "mRunningtimesHome 回调信息错误");
        }
    }

    protected MyHandler mHandler;

    protected class MyHandler extends Handler {

        // SoftReference<Activity> 也可以使用软应用 只有在内存不足的时候才会被回收
        private final WeakReference<Service> mService;

        protected MyHandler(Service service) {
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            Service service = mService.get();
            if (service != null){
                //做操作
                switch (msg.what) {

                }
            }
            super.handleMessage(msg);
        }
    }

    private RunningServiceInterface mRunningInterface;

    public void setRunningServiceInterface(RunningServiceInterface mRunningInterface) {
        this.mRunningInterface = mRunningInterface;
    }

    public interface RunningServiceInterface {
        /**
         * 首页跑步信息回调
         * @param runDuration   运动时长，秒
         * @param time        格式化后的运动持续时间
         * @param distance 运动距离  米
         * @param stepNum 步数
         * @param calorie 卡路里
         * @param state 运动状态 0，开始；1，暂停；2，继续；3，结束
         */
        void onRunningService(int velocity, int runDuration,String time, float distance, int stepNum, int calorie, int state);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    private void LOG(String data){
        LOGUtils.LOG("RunService:", data);
    }

}
