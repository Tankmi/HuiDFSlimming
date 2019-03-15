package com.huidf.slimming.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.huidf.slimming.R;
import com.huidf.slimming.activity.toady_movement.run.RunActivity;

/**
 * 作者：ZhuTao
 * 创建时间：2018/12/28 : 14:58
 * 描述：
 */
public class LocationService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Android O上才显示通知栏
        if(Build.VERSION.SDK_INT >= 26) {
            Log.i("LocationService","显示通知栏");
            showNotify();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //显示通知栏
    public void showNotify(){
//        ServiceRecord r = findServiceLocked(className, token, userId);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        Intent nfIntent = new Intent(this, RunActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("正在后台对运动轨迹进行定位")
                .setContentText("定位进行中")
                .setWhen(System.currentTimeMillis());
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;
        //调用这个方法把服务设置成前台服务
        startForeground(110, notification);
    }

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }
}
