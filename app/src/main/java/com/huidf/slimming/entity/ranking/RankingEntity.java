/**
 * 
 */
package com.huidf.slimming.entity.ranking;

import com.huidf.slimming.context.ApplicationData;
import com.huidf.slimming.context.PreferenceEntity;

import java.util.List;

import huitx.libztframework.utils.PreferencesUtils;

/**
 * 排行
 * @author ZhuTao
 * @date 2018/12/14 
 * @params 
*/

public class RankingEntity {

   public int code;
   public String cost;
   public String msg;
   public Data data;

   public static class Data {
       public int rank1;
       public int rank2;
       public int rank3;
       public int time1;
       public int time2;
       public int time3;
       public int percent2;
       public int percent3;
       public List<RankingInfo> list;  //总
       public List<RankingInfo> list1;  //日
       public List<RankingInfo> list2;  //周
       public List<RankingInfo> list3;  //月

       public static class RankingInfo {
           /** 非接口值 1运动  2 体重 */
           public int tragetType;
           /** 非接口值 1日排行   2周排行   3月排行 */
           public int type;
           /** 非接口值 首、普通值、尾 1 2 3*/
           public int targets;
           /** 非接口值 排行，从1开始 */
           public int rank;
           /** 非接口值 值 */
           public int data;

           public int percent;   //减重百分比”  int类型 自己拼接%
           public int time;  //运动时间
           public int singleRank;  //排行
           public String customerHead;
           public String customerName;
       }

   }

}
