package com.huidf.slimming.util.run;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.maps.model.LatLng;
import com.huidf.slimming.context.ApplicationData;
import com.huidf.slimming.entity.today_movement.run.SportRunningEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import huitx.libztframework.utils.UnitConversion;

/**
 * 跑步数据管理:  时间，步数，距离，坐标系
 *
 * @author ZhuTao
 * @date 2017/5/18
 * @params
 */

public class RunningUtil implements SensorEventListener {
    /** 开始运动 */
    public final int RUNNING_START = 0;
    /** 暂停运动 */
    public final int RUNNING_PAUSE = 1;
    /** 继续运动 */
    public final int RUNNING_CONTINUE = 2;
    /** 结束运动 */
    public final int RUNNING_FINISH = 3;
    /** 运动坐标集合 */
    private List<LatLng> mCoordinates;
    /** 运动距离 */
    private float runDistance = 0.00f;
    /**  0，开始；1，暂停；2，继续；3，结束 */
    private int runState = 3;
    /**  运动时间（秒）  */
    private int runDuration = 0;
    private String runningTimes = "00:00:00";
    Handler handler = new Handler();

    private static RunningUtil mRunningUtils;

    public static RunningUtil getInstance() {
        synchronized (RunningUtil.class) {
            if (mRunningUtils == null) {
                mRunningUtils = new RunningUtil();
            }
        }
        return mRunningUtils;
    }

    private float userWeight;
    private float userHeight;
    /** 步长  米 */
    private float stepLength = 0.7f;

    /** 设置用户信息用以计算卡路里等动态数据 */
    public void setUserInfo(float weight,float height){
        this.userWeight = weight;
        this.userHeight = height;
        stepLength = UnitConversion.preciseNumber(height*0.01f*0.4f,2);
    }

    /** 获取消耗的卡路里 */
    private int getCalories(float distance){
        return (int)UnitConversion.preciseNumber(userWeight * 1.036f * distance,0);
    }

    /**
     * 新增运动坐标
     * @param mLatlng
     * @return 是否新增成功
     */
    public boolean addCoordinates(LatLng mLatlng) {
        if (mCoordinates == null) {
            mCoordinates = new ArrayList<LatLng>();
        }
        if (mCoordinates.size() > 0) {
            LatLng oldData = mCoordinates.get(mCoordinates.size() - 1);
            DPoint olddPoint = new DPoint();
            DPoint newdPoint = new DPoint();
            newdPoint.setLongitude(mLatlng.longitude);
            newdPoint.setLatitude(mLatlng.latitude);
            olddPoint.setLongitude(oldData.longitude);
            olddPoint.setLatitude(oldData.latitude);
            float distance = CoordinateConverter.calculateLineDistance(newdPoint, olddPoint);
            if (distance > 0) {
                LOG("定位增加运动距离   " + distance);
                mCoordinates.add(mLatlng);
                runDistance += distance;
                return true;
            } else {
                return false;
            }
        } else {
            mCoordinates.add(mLatlng);
            return true;
        }
    }

    /** 获取坐标系列表 */
    public List<LatLng> getLatLngs(){
        if (mCoordinates != null) {
            return mCoordinates;
        }
        return null;
    }

    /** 清空坐标系列表 */
    public void clearCoordinates(){
        if (mCoordinates != null) {
            mCoordinates.clear();
        }
    }

    /**
     * 归零
     */
    private void makeZero() {
        runDistance = 0.00f;
        runDuration = 0; //开始运动时，时间归零
        runningTimes = "00:00:00";
        stepNum = 0;
        SportRunningEntity.makeZero();
    }


    /**
     * 设置跑步状态
     *
     * @param state: RUNNING_START 0，开始；RUNNING_PAUSE 1，暂停；
     *               RUNNING_CONTINUE 2，继续；RUNNING_FINISH 3，结束
     */
    public void setRunningState(int state) {
        runState = state;
        returnRunningInfo();
        if (state == RUNNING_START || state == RUNNING_CONTINUE) {
            if (state == RUNNING_START) {
                makeZero();
                initSensor();
            }
//            continueLocation(true);
            handler.postDelayed(runnable, 1000);
        }else if (state == RUNNING_PAUSE ) {   //暂停
//            continueLocation(false);
        }else if (state == RUNNING_FINISH) {   //结束运动，暂停时已经关闭了计时线程，所以得重新走
//            handler.post(runnable);
//            continueLocation(false);
            returnRunningInfo();
            makeZero();
        }
    }

    /**
     * 获取跑步状态
     * 0，开始；1，暂停；2，继续；3，结束
     */
    public int getRunningState() {
        return runState;
    }

    /**
     * 获取格式化后的运动时间 00:00:00
     */
    public String getRunningTimes() {
        return runningTimes;
    }

    /**
     * 获取运动距离(米)
     */
    private float getRunDistance(){
        float stepDistance = (stepLength * stepNum);
        stepDistance = stepDistance > runDistance ? stepDistance : runDistance;
        return stepDistance;
    }

    /** 返回为空，就表示通过 */
    public String canFinish(){

        if(runDuration >= 60*10){
            if(stepNum >= 150 || getRunDistance() >= 500){
                return "";
            }else{
                return "运动步数太少";
            }
        }else{
            return "运动时间太短";
        }
    }

    /** 获取配速 每公里所需时间,返回运动一公里所需的时间（秒）  */
    private int getVelocity(float distance){
        if(distance > 0 && runDuration>0){
            int mVelocity;
            if(distance > 1000){
                mVelocity = (int) (runDuration/(distance/1000));
            }else{
                mVelocity = (int) (1000/distance*runDuration);
            }
            return mVelocity;
        }
       return 0;
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            LOG("跑步计时" + runState + ":  " + runDuration);
            if (runState == 0 || runState == 2) {
                runDuration++;
            } else if (runState == 1) {
                returnRunningInfo();
                return;
            }

            returnRunningInfo();

            handler.postDelayed(this, 1000);
        }
    };

    /**
     * 统一回调信息
     */
    public void returnRunningInfo() {
        int h, m, s;
        h = runDuration / 3600;
        m = runDuration % 3600 / 60;
        s = runDuration % 3600 % 60;
        runningTimes = "" + (h > 9 ? h : "0" + h) + ":" + (m > 9 ? m : "0" + m) + ":" + (s > 9 ? s : "0" + s);

        float stepDistance = getRunDistance();
        LOG("stepDistance:" + stepDistance + "   runDistance:" + runDistance);
        if(mRunningtimes != null){
            try {
                mRunningtimes.runningTimes(runDuration, stepDistance, stepNum, runState);
            } catch (Exception e) {
                LOG( "RunningUtil :" + "mRunningtimes 回调信息错误");
            }
        }

        try {
//            runningTimes = (runningTimes == null || runningTimes.equals(""))?"00:00:00":runningTimes;
            mRunningtimeHome.runningTimesHome(getVelocity(stepDistance), runDuration, runningTimes, stepDistance, stepNum,
                    getCalories(UnitConversion.preciseNumber((stepDistance/1000f),2)), runState);
        } catch (Exception e) {
            LOG( "RunningUtil :" + "mRunningtimesHome 回调信息错误");
        }
    }



    //定义Sensor管理器
    private SensorManager mSensormanager;
    //传感器对象
    private Sensor mSensor;
    /* (non-Javadoc)
      * @see android.hardware.SensorEventListener#onSensorChanged(android.hardware.SensorEvent)
      * 当传感器的值发生变化的时候调用此方法
      */

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
    private int todayStepNum;

    private void initSensor() {
//        LOG("实例化传感器");
        //获得传感器管理服务
        mSensormanager = (SensorManager) ApplicationData.context.getSystemService(ApplicationData.context.SENSOR_SERVICE);
        //获取加速度传感器对象
        mSensor = mSensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //加速度传感器
        //还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，
        //第三个值是延迟时间的精密度。根据不同应用，需要的反应速率不同，具体根据实际情况设定
        mSensormanager.registerListener(this, mSensor, mSensormanager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(runState == RUNNING_PAUSE || runState == RUNNING_FINISH){ //暂停或者结束时不计步
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
                todayStepNum++;
                LOG("行走步数" + stepNum + ",今日总步数 " + todayStepNum);
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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void LOG(String str) {
        Log.i("spoort_list_run", "RunningService  " + str);
    }

    private RunningTime mRunningtimes;
    private RunningTimeHome mRunningtimeHome;

    public void setRunnintTimeObject(RunningTime mRunningtimes) {
        this.mRunningtimes = mRunningtimes;
    }

    public void setRunnintTimeObject(RunningTimeHome mRunningtimeHome) {
        this.mRunningtimeHome = mRunningtimeHome;
    }

    public interface RunningTime {
        /**
         * 跑步信息回调
         * @param time       运动时长，秒
         * @param distance 运动距离
         * @param stepNum 步数
         * @param state 运动状态 0，开始；1，暂停；2，继续；3，结束
         */
        public void runningTimes(int time, float distance, int stepNum, int state);
    }

    public interface RunningTimeHome {
        /**
         * 首页跑步信息回调
         * @param runDuration   运动时长，秒
         * @param time        格式化后的运动持续时间
         * @param distance 运动距离  米
         * @param stepNum 步数
         * @param calorie 卡路里
         * @param state 运动状态 0，开始；1，暂停；2，继续；3，结束
         */
        void runningTimesHome(int velocity, int runDuration,String time, float distance, int stepNum, int calorie, int state);
    }

    public void closeRunning() {
        if (mRunningUtils != null) {
            mRunningUtils = null;
        }
        if (runnable != null) {
            handler.removeCallbacks(runnable);
            runnable = null;
            handler = null;
        }
        if (mSensormanager != null) {
            mSensormanager.unregisterListener(this);
        }
//        if (mlocationClient != null) {
//            mlocationClient.stopLocation();
//            mlocationClient = null;
//        }
    }

}
