package com.huidf.slimming.activity;

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
import com.huidf.slimming.context.HtmlUrlConstant;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.dynamic.view.CreateDynamicActivity;
import com.huidf.slimming.web.activity.WebViewActivity;

import huitx.libztframework.utils.LOGUtils;


/**
* @Title: ReportCycleActivity.java
* @Package com.huidaifu.liangzi.activity.home
* @Description: TODO(首页)
* @author ZhuTao
* @date 2015年12月29日 下午2:13:06
* @version V1.0
*/
public class HomeActivity extends HomeBaseActivity {

   public HomeActivity() {
       super(R.layout.activity_main);
       TAG = getClass().getSimpleName();
   }

   @Override
   protected void initHead() {
       setStatusBarColor(true, true, mContext.getResources().getColor(R.color.transparency));
       iv_title_status_bar_fill.setBackgroundResource(0x00000000);
       if (mHandler == null) mHandler = new MyHandler(this);
   }

    @Override
   protected void initLogic() {
   }

    @Override
    protected void onResume() {
        super.onResume();
		mHandler.post(getVersionRunnable);
        if(!PreferenceEntity.isSyncUserDatas) inputData();
        if(PreferenceEntity.isGoDynamicView){
            PreferenceEntity.isGoDynamicView = false;
            mFragmentSwitch.changeTag(lin_tab_home_dynamic);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LOG("RankingListActivity onActivityResult requestCode" + requestCode);
    }

    private Runnable getVersionRunnable = new Runnable() {
        public void run() {
            getVersion();
        }
    };

    @Override
   public void onClick(View view) {
       switch(view.getId()){
           case R.id.lin_tab_home_market:
               Intent intent = new Intent(HomeActivity.this, CreateDynamicActivity.class);
//               Intent intent = new Intent(HomeActivity.this, WebViewActivity.class);
//               intent.putExtra("url", HtmlUrlConstant.HTML_RELEASEDYNAMIC);
//               intent.putExtra("is_refresh", false);
               startActivity(intent);
               break;
       }
   }

   //保存页面的缓存信息，在onCreate方法中可以进行数据的初始化
   @Override
   protected void onSaveInstanceState(Bundle outState) {
       // TODO Auto-generated method stub
       super.onSaveInstanceState(outState);
       outState.putString("home_datas", "非正常退出！");
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
       if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
           BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
           BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
           if (mBluetoothAdapter.isEnabled()) {
               boolean disableState = mBluetoothAdapter.disable();// 关闭蓝牙
               LOG("DeviceFragmentActivitys bluetoothAdapter 关闭蓝牙 state" + disableState);
           }
       }

   }


}
