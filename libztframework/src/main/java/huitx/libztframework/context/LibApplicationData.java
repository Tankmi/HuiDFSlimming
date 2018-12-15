package huitx.libztframework.context;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.xutils.x;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import huitx.libztframework.utils.CrashHandler;
import huitx.libztframework.utils.LOGUtils;
import huitx.libztframework.utils.file.FileUtils;

public class LibApplicationData extends Application {

    public static Context context;
    /**
     * imei号
     */
    public static String imei;


    //运用list来保存们每一个activity是关键
    private List<Activity> mList = new LinkedList<Activity>();
    //为了实现每次使用该类时不创建新的对象而创建的静态对象  
    private static LibApplicationData ApplicationDatainstance;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());//初始化抓取异常的工具类！

        //xutils3.0 初始化
        x.Ext.init(this);
        x.Ext.setDebug(false); //输出debug日志，开启会影响性能

        context = getApplicationContext();
        initImageLoader(context);
//        getDatas(this);
        getInstance();

//        ShareSDK.initSDK(context);

//		initGotye();
    }

    public void initGotye() {
    }


    /**
     * 获取imei号
     *
     */
    public static void getDatas() {
        // 获取IMEI号
        TelephonyManager tele = (TelephonyManager) context .getSystemService(TELEPHONY_SERVICE);
        imei = tele.getDeviceId();
        LOGUtils.LOG("LibApplicationData 获取IMEI号   " + imei);
    }

    /**
     * 初始化ImageLoader
     *
     * @param context
     */
    public static void initImageLoader(Context context) {

        FileUtils.makeDirs(LibPreferenceEntity.KEY_CACHE_PATH);//创建缓存目录

        @SuppressWarnings("deprecation")
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .discCacheSize(50 * 1024 * 1024)//
                .discCacheFileCount(100)// 缓存一百张图片
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(3).memoryCacheSize(getMemoryCacheSize(context))
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .memoryCache(new WeakMemoryCache())//这个类缓存bitmap的总大小没有限制，唯一不足的地方就是不稳定，缓存的图片容易被回收掉
                //缓存到sd卡把图片！
                .discCache(new UnlimitedDiscCache(new File(LibPreferenceEntity.KEY_CACHE_PATH)))
                .tasksProcessingOrder(QueueProcessingType.LIFO)
//				.writeDebugLogs()//
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    /**
     * 获取缓存大小
     *
     * @param context
     * @return
     */
    private static int getMemoryCacheSize(Context context) {
        int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            int memClass = ((ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
            memoryCacheSize = (memClass / 8) * 1024 * 1024; // 1/8 of app memory
            // limit
        } else {
            memoryCacheSize = 2 * 1024 * 1024;
        }
        return memoryCacheSize;
    }

    //实例化一次  
    public synchronized static LibApplicationData getInstance() {
        if (null == ApplicationDatainstance) {
            ApplicationDatainstance = new LibApplicationData();
        }
        return ApplicationDatainstance;
    }

    // add Activity    
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    //关闭每一个list内的activity  
    public void exit() {
        if (mList.size() == 0) {
            return;
        }
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            System.exit(0);   
        }
    }

    //杀进程  
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

}
