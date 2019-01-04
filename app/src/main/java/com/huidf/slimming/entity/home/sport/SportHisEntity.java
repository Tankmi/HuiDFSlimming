package com.huidf.slimming.entity.home.sport;

import java.util.LinkedList;
import java.util.List;

/**
 * 运动历史记录
 * @author ZhuTao
 * @date 2018/12/18 
 * @params 
*/

public class SportHisEntity {
	
	public int code;
	public Data data;
	public String msg;
	public String cost;

	public class Data {
		public String kcal;
		public String time;
		public String level;
		public float schedules;
		/** 周总结 A级运动量天数 */
		public String weeklongest;
		/** 周总结 一周总时长 */
		public String weeklevel;
		/** 周总结 一天最长运动时间 */
		public String weeksumtime;

		/** 月总结 A级运动量天数 */
		public String monthlevel;
		/** 月总结 一周总时长 */
		public String monthsumtime;
		/** 月总结 一天最长运动时间 */
		public String monthlongest;
		/** 今日运动列表 */
		public LinkedList<SportGenre> list2;
		/** 周总结列表 */
		public List<SportLevel> list3;
		/** 月总结列表 */
		public List<SportLevel> list4;
		/** 运动类型 */
		public class SportGenre {
			/** 本地字段，根据type判断图片的地址 */
			public String sportName;
			/** 本地字段，根据type判断图片的地址 */
			public int sportIcon;
			public String sporttime;
			public String sporttype;
			public String sportkcal;
		}
		/** 运动分级 */
		public class SportLevel {
			public String daysumtime;
			public String createtime;
			public String level;
		}
	}
}
