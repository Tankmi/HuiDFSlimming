package huitx.libztframework.interf;

/**
 * @Title: ConsultNet.java
 * @Package com.lingjiedian.modou.interf
 * @Description: TODO(网络请求回调接口，降低代码冗余)
 * @author ZhuTao
 * @date 2015年6月17日 上午11:24:27
 * @version V1.0
 */
public interface ConsultNet {
	
	/** 
	 * 请求数据 返回的Gson串
	 * @param mData 返回请求结果串
	 * @param type 用于标记请求类型
	 * */
	void paddingDatas(String mData, int type);
	
	
	/** 出错 
	 * @param msg 返回错误信息
	 * @param type 用于标记请求类型
	 * */
	void error(String msg, int type);
}
