package com.huidf.slimming.util.run;

import android.util.Log;

import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.maps.model.LatLng;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.entity.today_movement.run.SportRunningEntity;

import java.util.ArrayList;
import java.util.List;

import huitx.libztframework.utils.MathUtils;
import huitx.libztframework.utils.NumberConversion;

/**
 * 跑步数据管理:  时间，步数，距离，坐标系
 *
 * @author ZhuTao
 * @date 2017/5/18
 * @params
 */

public class RunningUtil {
    /** 开始运动 */
    public final int RUNNING_START = 0;
    /** 暂停运动 */
    public final int RUNNING_PAUSE = 1;
    /** 继续运动 */
    public final int RUNNING_CONTINUE = 2;
    /** 结束运动 */
    public final int RUNNING_FINISH = 3;
    /**  0，开始；1，暂停；2，继续；3，结束 */
    private int runState = 3;
    /** 运动坐标集合 */
    private List<LatLng> mCoordinates;
    /** 通过坐标换算的运动距离 */
    private float runDistance = 0.00f;

    /**  运动时间（秒）  */
    private int runDuration = 0;
    private String runningTimes = "00:00:00";

    private static RunningUtil mRunningUtils;

    public static RunningUtil getInstance() {
        synchronized (RunningUtil.class) {
            if (mRunningUtils == null) {
                mRunningUtils = new RunningUtil();
            }
        }
        return mRunningUtils;
    }

    private float userWeight = MathUtils.getFloatForPreference(PreferenceEntity.KEY_USER_CURRENT_WEIGHT, 66.0f);
    private float userHeight = MathUtils.stringToFloatForPreference(PreferenceEntity.KEY_USER_HEIGHT, 170f);
    /** 步长  米 */
    private float stepLength = 0.7f;

    /** 设置用户信息用以计算卡路里等动态数据 */
//    private static void setUserInfo(float weight,float height){
//        this.userWeight = weight;
//        this.userHeight = height;
//        stepLength = NumberConversion.preciseNumber(height*0.01f*0.4f,2);
//    }

    /** 获取消耗的卡路里 */
    public int getCalories(float distance){
        return (int)NumberConversion.preciseNumber(userWeight * 1.036f * distance,0);
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
    public void setRunningStates(int state) {
        runState = state;
//        returnRunningInfo();
        if (state == RUNNING_START || state == RUNNING_CONTINUE) {
            if (state == RUNNING_START) {
                makeZero();
//                initSensor();
            }
//            continueLocation(true);
//            handler.postDelayed(runnable, 1000);
        }else if (state == RUNNING_PAUSE ) {   //暂停
//            continueLocation(false);
        }else if (state == RUNNING_FINISH) {   //结束运动，暂停时已经关闭了计时线程，所以得重新走
//            handler.post(runnable);
//            continueLocation(false);
//            returnRunningInfo();
            makeZero();
        }
    }

    /**
     * 获取跑步状态
     * 0，开始；1，暂停；2，继续；3，结束
     */
    public int getRunningStates() {
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
    public float getRunDistance(){
        float stepDistance = (stepLength * stepNum);
        stepDistance = stepDistance > runDistance ? stepDistance : runDistance;
        LOG("stepDistance:" + stepDistance + "   runDistance:" + runDistance);
        return stepDistance;
    }

    private int stepNum;    //当前运动步数

    public void setStepNum(int stepNum){
        this.stepNum = stepNum;
    }

    /** 获取配速 每公里所需时间,返回运动一公里所需的时间（秒）  */
    public int getVelocity(float distance){
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

    private void LOG(String str) {
        Log.i("RunningUtil", "RunningService  " + str);
    }

    public void closeRunning() {
        if (mRunningUtils != null) {
            mRunningUtils = null;
        }
//        if (runnable != null) {
//            handler.removeCallbacks(runnable);
//            runnable = null;
//            handler = null;
//        }
//        if (mSensormanager != null) {
//            mSensormanager.unregisterListener(this);
//        }
//        if (mlocationClient != null) {
//            mlocationClient.stopLocation();
//            mlocationClient = null;
//        }
    }

}
