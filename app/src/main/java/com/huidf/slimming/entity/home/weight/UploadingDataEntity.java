package com.huidf.slimming.entity.home.weight;

import java.util.List;

/** 
* @author : Zhutao 
* @version 创建时间：@2017年1月12日
* @Description: 硬件数据信息上传下载
* @params：
*/ 
public class UploadingDataEntity {
	
	public int code;
	public Data data;
	public String msg;
	public String cost;

	public class Data {
		public String weight;
		public String targetWeight;
		public String targetTime;
		public String createTime;
		public String latestWeight;
		public String latestWeightTime;
		public String days;

	

	}
}
