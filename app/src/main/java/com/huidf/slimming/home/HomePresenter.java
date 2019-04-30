package com.huidf.slimming.home;

import com.google.gson.Gson;
import com.huidf.slimming.entity.home.HomeEntity;
import com.huidf.slimming.net.model.BaseHttpEntity;

import huitx.libztframework.utils.StringUtils;
import okhttp3.ResponseBody;

/**
 * 作者：ZhuTao
 * 创建时间：2019/3/25 : 16:54
 * 描述：
 */
public class HomePresenter implements HomeController.HomePresenter{
    private HomeController.HomeModel mModel;
    private HomeController.HomeView mView;

    public HomePresenter(){
        mModel = new HomeModel();
    }

    @Override
    public void attachView(HomeController.HomeView view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void getNetData() {
        mView.loadingShow();

        mModel.getModel(new BaseHttpEntity<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody result) {
                Gson gson = new Gson();
                HomeEntity mEntity = null;
                try {
                    String data = StringUtils.replaceJson(result.string());
                    mEntity = gson.fromJson(data, HomeEntity.class);
                    mView.setData(mEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }finally {

                }
            }

            @Override
            public void onError(String error) {
                mView.loadingDissmis();
            }

            @Override
            public void onFinish() {
                mView.loadingDissmis();
            }
        });
    }
}
