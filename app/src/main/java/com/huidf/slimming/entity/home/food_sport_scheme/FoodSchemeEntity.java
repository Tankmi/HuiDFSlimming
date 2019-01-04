package com.huidf.slimming.entity.home.food_sport_scheme;

import java.util.List;

/**
 * 饮食方案
 * @author ZhuTao
 * @date 2018/12/14 
 * @params 
*/

public class FoodSchemeEntity {

   public int code;
   public String cost;
   public String msg;
   public Data data;

   public static class Data {
       public List<FoodScheme> list;  //饮食方案

       public static class FoodScheme {
           /** 图文标识 1图 2图文” */
           public int flag;
           /** 1早餐 2午餐 3晚餐 */
           public int type;
           /** 图片 */
           public String image;
           /** 卡路里 */
           public int calorie;
       }

   }

}
