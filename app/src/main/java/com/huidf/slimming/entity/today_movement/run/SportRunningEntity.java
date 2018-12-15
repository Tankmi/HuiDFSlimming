package com.huidf.slimming.entity.today_movement.run;

import android.content.Context;
import android.util.Log;

import com.huidf.slimming.context.ApplicationData;

import huitx.libztframework.utils.LOGUtils;
import huitx.libztframework.utils.PreferencesUtils;
import huitx.libztframework.utils.TransitionTime;

/**
 * 运动跑步实体类
 * @author ZhuTao
 * @date 2017/5/11 
 * @params 
*/

public class SportRunningEntity {

    /** 缓存数据的日期 */
    public static final String KEY_SPORT_RUN_DATE= "sport_run_date";
    public static final String KEY_SPORT_RUN_STEP_NUM= "sport_run_step_num";
    public static final String KEY_SPORT_RUN_STEP_DISTANCE= "sport_run_step_distance";
    public static final String KEY_SPORT_RUN_STEP_ACTIVE_TIME= "sport_run_step_active_time";
    public static final String KEY_SPORT_RUN_STEP_CALORIE= "sport_run_step_calorie";

    /** 单日最高跑步步数 */
    public static int stepRunHighestNum = 0;
    /** 跑步步数 */
    public static int stepRunNum = 0;
    /** 跑步里程 */
    public static float stepRunDistance = 0;
    /** 跑步活跃时间 */
    public static int stepRunActiveTime = 0;
    /** 跑步卡路里 */
    public static int stepRunCalorie = 0;

    /** 保存获得的数据 */
    public static void saveData(){
        Context mContext = ApplicationData.context;
        PreferencesUtils.putLong(mContext,KEY_SPORT_RUN_DATE,System.currentTimeMillis());
        int lastStepNum =    PreferencesUtils.getInt(mContext,KEY_SPORT_RUN_STEP_NUM,0);
        if(stepRunNum > lastStepNum){
            PreferencesUtils.putInt(mContext,KEY_SPORT_RUN_STEP_NUM,stepRunNum);
            stepRunHighestNum = stepRunNum;
        }
        int lastCalorie =    PreferencesUtils.getInt(mContext,KEY_SPORT_RUN_STEP_CALORIE,0);
        stepRunCalorie += lastCalorie;
        PreferencesUtils.putInt(mContext,KEY_SPORT_RUN_STEP_CALORIE,stepRunCalorie);

//        PreferencesUtils.putFloat(mContext,KEY_SPORT_RUN_STEP_DISTANCE,stepRunDistance);
//        PreferencesUtils.putInt(mContext,KEY_SPORT_RUN_STEP_ACTIVE_TIME,stepRunActiveTime);

    }

    /** 通过上次缓存数据，初始化本地数据 */
    public static void initData(){
        Context mContext = ApplicationData.context;
        TransitionTime transitionTime = new TransitionTime();
        long yesterday = PreferencesUtils.getLong(mContext,KEY_SPORT_RUN_DATE,System.currentTimeMillis());
        long today = System.currentTimeMillis();
        String yseterdayDate = transitionTime.convert(yesterday + "","yyyy-MM-dd");
        String todayDate = transitionTime.convert(today + "","yyyy-MM-dd");
        if(!yseterdayDate.equals(todayDate)){
            LOGUtils.LOG("不是一天");
//            SportBLEEntity.clearData();
            clearData();
        }else{
            LOGUtils.LOG("是一天");
        }

        stepRunHighestNum =  PreferencesUtils.getInt(mContext,KEY_SPORT_RUN_STEP_NUM,stepRunHighestNum);
        stepRunCalorie =  PreferencesUtils.getInt(mContext,KEY_SPORT_RUN_STEP_CALORIE,stepRunCalorie);
//        stepRunDistance =  PreferencesUtils.getFloat(mContext,KEY_SPORT_RUN_STEP_DISTANCE,stepRunDistance);
//        stepRunActiveTime =   PreferencesUtils.getInt(mContext,KEY_SPORT_RUN_STEP_ACTIVE_TIME,stepRunActiveTime);
    }

    /** 每日清空数据 */
    public static void clearData(){
        Context mContext = ApplicationData.context;
        PreferencesUtils.putInt(mContext,KEY_SPORT_RUN_STEP_NUM,0);
        PreferencesUtils.putInt(mContext,KEY_SPORT_RUN_STEP_CALORIE,0);
    }

    /** 归零 */
    public static void makeZero(){
        stepRunNum = 0;
        stepRunDistance = 0;
        stepRunCalorie = 0;
        stepRunActiveTime = 0;
    }

    /** 计算消耗的卡路里 */
    public static void calculateCalories(){
        stepRunCalorie = (int)(70f * SportRunningEntity.stepRunDistance * 1.036f);
    }


    private static int lastStep = 0;

    /**  返回是否需要向云端更新一次数据 */
    public static boolean isInsertData(){
        LOGUtils.LOG("判断是否需要录入数据lastStep:" + lastStep + "     stepRunNum:" + stepRunNum);
        if(stepRunNum == 0) return false;
    if(lastStep == 0) {
        lastStep = stepRunNum;
        return true;
    }else if((stepRunNum - lastStep)>=50){
        lastStep = stepRunNum;
        return true;
    }
        return false;
    }
}
