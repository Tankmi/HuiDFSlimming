package com.huidf.slimming.entity.home.food_sport_scheme;

import java.util.List;

/**
 * 食物能量检索
 * @author ZhuTao
 * @date 2018/12/14 
 * @params 
*/
public class FoodCalorieEntity {

   public int code;
   public String cost;
   public String msg;
   public Data data;

   public static class Data {
       public List<FoodCalorie> list;  //饮食方案

       public static class FoodCalorie {
           public String name;
           public String kcal;
       }

   }

}
