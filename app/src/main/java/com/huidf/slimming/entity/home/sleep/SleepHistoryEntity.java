/**
 * 
 */
package com.huidf.slimming.entity.home.sleep;

import java.util.List;

/**
 * 睡眠历史记录
 * @author ZhuTao
 * @date 2017/10/26 
 * @params 
*/

public class SleepHistoryEntity {

   public int code;
   public String cost;
   public String msg;
    public Data data;
   public static class Data {
//        /** 选中当天的睡眠质量 */
//       public String quality;
//       /** 总睡眠时间 */
//       public String totalSleep;

       public String deepSleep; //深睡时间  分钟  客户端换算小时、分钟
       public String shallowSleep;  //浅睡时间 分钟  客户端换算小时、分钟
       public String noSleepCount;  //清醒时间 分钟  客户端换算小时、分钟
       /** 指定日期睡眠详情柱状图 */
       public List<SleepCTEntity> list1;
       /** 近七天睡眠质量 */
       public List<SleepQuality> list2;
       /** 最近七天的睡眠质量 */
       public static class SleepQuality {
           /** 睡眠质量 */
           public String quality;
           public String createTime;

       }

   }

}
