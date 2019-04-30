package com.huidf.slimming.home;

import com.huidf.slimming.entity.home.HomeEntity;
import com.huidf.slimming.net.base.BasePresenter;
import com.huidf.slimming.net.base.BaseView;
import com.huidf.slimming.net.model.BaseHttpEntity;

import okhttp3.ResponseBody;

/**
 * 作者：ZhuTao
 * 创建时间：2019/3/25 : 16:36
 * 描述：负责home的mvp
 */
public class HomeController {

    public interface HomePresenter extends BasePresenter<HomeView> {

    }

    public interface HomeView extends BaseView<HomePresenter> {
        void setData(HomeEntity weatherBean);
    }

    public interface HomeModel {
        void getModel(BaseHttpEntity<ResponseBody> entity);
    }
}
