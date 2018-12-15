package com.huidf.slimming.activity.toady_movement;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;

import com.huidf.slimming.R;
import com.huidf.slimming.context.PreferenceEntity;


/**
 * 今日运动
 * @author ZhuTao
 * @date 2018/12/5 
 * @params 
*/

public class TodayMovementActivity extends TodayMovementBaseActivity {

   public TodayMovementActivity() {
       super(R.layout.activity_today_movement);
       TAG = getClass().getSimpleName();
   }

   @Override
   protected void initHead() {
       setStatusBarColor(false, true, mContext.getResources().getColor(R.color.white));
       setTittle("添加运动");

       if(mHandler == null) mHandler = new MyHandler(TodayMovementActivity.this);
   }

    @Override
   protected void initLogic() {
   }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
   public void onClick(View view) {
       switch(view.getId()){
       }
   }


   @Override
   protected void pauseClose() {
       super.pauseClose();
   }

   @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
   @Override
   protected void destroyClose() {
       super.destroyClose();
       if(mHandler != null) mHandler.removeCallbacksAndMessages(null);

   }


}
