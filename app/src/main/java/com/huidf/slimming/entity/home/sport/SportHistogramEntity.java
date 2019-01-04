package com.huidf.slimming.entity.home.sport;

public class SportHistogramEntity {

    //	/** 横坐标 */
//	public String date;
//	/** 值 */
//	public float score;
//	/** 血压舒张压值 */
//	public float score1;
//	/** 级别1偏低warngin 2达标normal 3偏高danger 0普通配色*/
//	public String total;
//	/** 血压舒张压级别1偏低warngin 2达标normal 3偏高danger */
//	public String total1;
//	/** 提示 */
//	public String suggestion;
//
    public int id;
    /**
     * 当前值
     */
    public float value;
    /**
     * 最大值
     */
    public float maxNum;
    /**
     * 等级 ABC
     */
    public String level;
    /**
     * 日期
     */
    public String date;
}
