package com.huidf.slimming.context;

/**
 * @author : Zhutao
 * @version 创建时间：@2016年12月19日
 * @Description:
 * @params：
 */
public class UrlConstant {

//	public static final String API_BASE = "http://182.92.109.146:9995/"; // 正式服务器地址
//	public static final String API_BASEH5 = "http://182.92.109.146:9995/"; // H5正式服务器地址

//    public static final String API_BASE = "http://192.168.0.126:8090/"; // 测试服务器地址  张琪
    public static final String API_BASE = "http://192.168.0.28:8090/"; // 测试服务器地址  高玲
    public static final String API_BASEH5 = "http://192.168.0.142:8090/"; // H5测试服务器地址
    /**
     * 示例： 接口介绍
     * @param 参数名  ：参数介绍 ...
     */
    public static final String API_XXX = API_BASE + "...";

    /**
     * 获取精选内容
     */
    public static final String POST_POST_MAINPAGE = API_BASE + "main/mainPage.do";


    // ***********************************我的亲友

    /**
     * 登录
     * @param user ：用户名
     * @param psw ：密码md5(psw)
     * @param imei  ：手机imei号
     */
    public static final String API_LOGIN = API_BASE + "sys/login.do";

    /** 微信登录 */
    public static final String API_WX_LOGIN = API_BASE + "sys/wechatLogin.do";

    /** 微信登录 */
    public static final String API_WX_BIND = API_BASE + "sys/bind.do";

    /** QQ登录 */
    public static final String API_QQ_LOGIN = API_BASE + "sys/qqLogin.do";

    /**
     * 获取验证码
     *
     * @param phone  ：手机号
     * @param imei ：手机imei号
     */
    public static final String API_VERIFICATION = API_BASE + "sys/validcode.do";


    /**
     * 注册成功后补全信息
     *
     * @param birthday" : "生日"
     * @param "height" : "升高"   double类型
     * @param isDiabetes" : "是否糖尿病"  0是  1否
     * @param "isKedneyDisease" : "是否肾功能不全"  0是  1否
     */
    public static final String API_SYSISALL = API_BASE + "user/isall.do";

    /**
     * 修改用户信息
     *
     * @param birthday" : "生日"
     * @param "height" : "升高"   double类型
     * @param isDiabetes" : "是否糖尿病"  0是  1否
     * @param "isKedneyDisease" : "是否肾功能不全"  0是  1否
     */
    public static final String API_UPDATEPERSONAL = API_BASE + "personal/updatePersonal.do";

    /** 上传头像 * @param header：头像二进制字节流 */
    public static final String API_USER_CHANGEHEADER = API_BASE + "user/chgheader.do";

    // ***********************************个人中心

    // ***********************************首页

    public static final String API_INSERTSPORT= API_BASE + "sports/insertSport.do";  //今日运动，添加

    // **********************************用户个人信息
    /** 获取用户个人信息 */
    public static final String GET_PERSONAL_CENTER= API_BASE + "personal/myCenter.do";

    /** 修改绑定的手机号 */
    public static String API_CHANGE_PHONE = API_BASE + "user/chgphone.do";

}
