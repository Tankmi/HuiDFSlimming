package huitx.libztframework.utils.image_loader;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class ImageLoaderUtils{
	
	/**
	 * 设置加载时的背景图片
	 * @param drawable 默认背景图
	 * @param state	图片形状，0正常，1，圆角，2，圆形
	 * @return	返回DisplayImageOptions对象
	 */
	public static DisplayImageOptions setImageOptionsLoadImg(Drawable drawable, int state){
		RoundedBitmapDisplayer round;
		DisplayImageOptions options = null;
		if(state == 1){
			round = new RoundedBitmapDisplayer(15);
		}else if(state == 2){
			round = new RoundedBitmapDisplayer(120);
		}else{
			options = new DisplayImageOptions.Builder().cacheInMemory() // 缓存在内存中
					.cacheOnDisc() // 磁盘缓存
					.showImageOnLoading(drawable) // resource or
					.showImageForEmptyUri(drawable) // resource
					// or
					.showImageOnFail(drawable) // resource or
					// drawable
					.resetViewBeforeLoading(false) // default
//					.delayBeforeLoading(1000)
					.cacheInMemory(true) // default //使用缓存！	
					.cacheOnDisk(true) // default
					.considerExifParams(false) // default
					.imageScaleType(ImageScaleType.EXACTLY_STRETCHED) // default
					.bitmapConfig(Bitmap.Config.RGB_565) // default
					.displayer(new SimpleBitmapDisplayer()) // default
					.handler(new Handler()) // default
					.build();
			return options;
		}
		options = new DisplayImageOptions.Builder().cacheInMemory() // 缓存在内存中
				.cacheOnDisc() // 磁盘缓存
				.showImageOnLoading(drawable) // resource or
				.showImageForEmptyUri(drawable) // resource
				// or
				.showImageOnFail(drawable) // resource or
				// drawable
				.resetViewBeforeLoading(false) // default
//				.delayBeforeLoading(1000)
				.cacheInMemory(true) // default //使用缓存！
				.cacheOnDisk(true) // default
				.considerExifParams(false) // default
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED) // default
				.bitmapConfig(Bitmap.Config.RGB_565) // default
				.displayer(new SimpleBitmapDisplayer()) // default
//				.displayer(new RoundedBitmapDisplayer(15))//设置图片为圆角显示！
				.displayer(round)//设置图片为圆角显示！
				.handler(new Handler()) // default
				.build();
		return options;
	}
	
}
