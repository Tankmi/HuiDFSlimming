package com.huidf.slimming.entity.home.weight;

import java.util.List;

public class WeightHistoryTableEntity {
	public int code;
	public String cost;
	public String msg;
	public Data data;

	public static class Data {
		public List<WeightInfo> list1;  //最近七天
		public List<WeightInfo> list2;  //最近30天
		public List<WeightInfo> list3;  //最近三个月

		public static class WeightInfo {
			public String weight;
			public String createTime;
		}
	}

	/** 横坐标 */
	public String date;
	/** 横坐标 2 */
	public String date1;
	/** 值 */
	public float score;

}
