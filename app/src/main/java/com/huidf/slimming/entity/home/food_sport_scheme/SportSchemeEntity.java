package com.huidf.slimming.entity.home.food_sport_scheme;

import java.util.List;

/**
 * 运动方案
 * @author ZhuTao
 * @date 2018/12/14 
 * @params 
*/

public class SportSchemeEntity {

   public int code;
   public String cost;
   public String msg;
   public Data data;

   public static class Data {
       public List<SportScheme> sportList;  //本地编辑  运动方案

       public List<SportScheme> list;  //运动方案

       public String key4;  //运动时间
       public String key5;  //运动强度
       public static class SportScheme {
           /** HIIT 编号 */
           public int planId;
           /** HIIT名称 */
           public String planName;
           public String restTime;
//           /** 本地字段 时长 */
//           public int time;
//           /** 本地字段 时长单位 */
//           public String unit;
           /** 本地字段 条目类型  1，动画，2，文本 */
           public int type;
           /** 本地字段 动画格式下 图片地址 */
           public int image;
           public String key4;  //本地字段 文本格式下 运动时间
           public String key5;  //本地字段 文本格式下 运动强度

       }


   }

}
