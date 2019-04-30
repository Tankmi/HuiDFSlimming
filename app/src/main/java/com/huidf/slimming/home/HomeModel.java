package com.huidf.slimming.home;

import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.entity.home.HomeEntity;
import com.huidf.slimming.net.DefaultObserver;
import com.huidf.slimming.net.RetrofitHelper;
import com.huidf.slimming.net.model.BaseHttpEntity;

import java.util.HashMap;

import huitx.libztframework.utils.LOGUtils;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * 作者：ZhuTao
 * 创建时间：2019/3/25 : 16:58
 * 描述：
 */
public class HomeModel implements HomeController.HomeModel{

    public HomeModel(){
    }

    //执行网络请求，获取数据
    @Override
    public void getModel(final BaseHttpEntity<ResponseBody> entity) {

        RetrofitHelper.getService().getApi().getHomeChoiceness()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DefaultObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody data) {
                        LOGUtils.LOG("getModel  onSuccess");
                        entity.onSuccess(data);
                    }

                    @Override
                    public void onError(String error) {
                        LOGUtils.LOG("getModel  onError" + error);
                        entity.onError(error);
                    }

                    @Override
                    public void onFinish() {
                        LOGUtils.LOG("getModel  onFinish");
                        entity.onFinish();
                    }
                });
    }

}
