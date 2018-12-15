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
       public String rank1;
       public String rank2;
       public String rank3;
       public List<RankingInfo> list1;  //日
       public List<RankingInfo> list2;  //周
       public List<RankingInfo> list3;  //月

       public static class RankingInfo {
           //非接口值 1运动  2 体重
           public String tragetType;
           //非接口值 1日排行   2周排行   3月排行
           public String type;
           //非接口值 首尾 10 11 20 21 30 31
           public String targets;

           public String percent;   //减重百分比”  int类型 自己拼接%
           public String time;
           public String customerHead;
           public String customerName;
       }

   }

}
