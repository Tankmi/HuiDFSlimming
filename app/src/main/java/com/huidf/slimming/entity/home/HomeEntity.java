package com.huidf.slimming.entity.home;

import java.util.List;

public class HomeEntity {
	public int code;
	public String cost;
	public String msg;
	public Data data;

	public static class Data {
		/** 目标体重 */
		public float targetWeight;
		/** 轻了多少斤 */
		public float lostWeight;
		/** 进度百分比 */
		public float lostPercent;
		/** 运动营养方案  每周减重 */
		public float targetAvg;
		/** 运动记录 今日运动百分比 */
		public float sportPercent;
		/** 体重记录  最近体重 */
		public float latestWeight;
		public List<Weight> weightList;
		public static class Weight {
			public float weight;
		}
		/** 睡眠记录  昨日睡眠 */
		public float latestSleep;
	}
}
