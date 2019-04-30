package com.huidf.slimming.net;

import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.dynamic.view.net.CreateDynamicService;
import com.huidf.slimming.home.HomeService;

/**
 * 3、RetrofitHelper
 */
public class RetrofitHelper {
    private static RetrofitHelper mHelper;
    //http://v.juhe.cn/weather/index?cityname=%E5%8C%97%E4%BA%AC&dtype=&format=&key=69edcefe693173e720380d6334d15e1f
    private String BASE_URL = UrlConstant.API_BASE;

    private RetrofitHelper() {
//        BASE_URL = constans.baseUrl;
    }

    public static RetrofitHelper getService() {
        if (mHelper == null) mHelper = new RetrofitHelper();
        return mHelper;
    }

    public HomeService getApi() {
        return RetrofitApi.getApiService(HomeService.class, BASE_URL);
    }

    public CreateDynamicService getDynamicServiceApi() {
        return RetrofitApi.getApiService(CreateDynamicService.class, BASE_URL);
    }
}
