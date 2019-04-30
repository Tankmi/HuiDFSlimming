package com.huidf.slimming.dynamic.view.net;

import com.google.gson.Gson;
import com.huidf.slimming.entity.user.UserEntity;
import com.huidf.slimming.net.model.BaseHttpEntity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import huitx.libztframework.context.ContextConstant;
import huitx.libztframework.utils.LOGUtils;
import huitx.libztframework.utils.NewWidgetSetting;
import huitx.libztframework.utils.StringUtils;
import huitx.libztframework.utils.ToastUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * 作者：ZhuTao
 * 创建时间：2019/3/26 : 15:28
 * 描述：
 */
public class DynamicPresenter implements DynamicController.CreateDynamicPresenter {

    private DynamicController.CreateDynamicModel mModel;
    private DynamicController.CreateDynamicView mView;

    public DynamicPresenter() {
        mModel = new CreateDynModelImpl();
    }

    @Override
    public String uploadingPic(String path) {
        mView.loadingShow();
        File file = new File(path);

        mModel.executePicture(new BaseHttpEntity<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody data) {
                mView.loadingDissmis();
                Gson gson = new Gson();
                UserEntity mEntity;
                try {
                    String str = StringUtils.replaceJson(data.string());
                    mEntity = gson.fromJson(str, UserEntity.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (mEntity.code == ContextConstant.RESPONSECODE_200) {
                    mView.uploadingPicSuccess(mEntity);
                } else if (mEntity.code == ContextConstant.RESPONSECODE_310) {    //登录信息过时跳转到登录页
                    mView.loginOut();
                } else {
                    ToastUtils.showToast(NewWidgetSetting.getInstance().filtrationStringbuffer(mEntity.msg, "接口信息异常！"));
                }
            }

            @Override
            public void onError(String error) {
                mView.loadingDissmis();
                mView.uploadingPicFail(error);
            }

            @Override
            public void onFinish() {
                mView.loadingDissmis();

            }
        }, file);
        return null;
    }

    @Override
    public void createDynamic(Map<String, String> map) {
        mView.loadingShow();

        mModel.executeDynamic(new BaseHttpEntity<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody data) {
                mView.loadingDissmis();
                Gson gson = new Gson();
                UserEntity mEntity;
                try {
                    String str = StringUtils.replaceJson(data.string());
                    mEntity = gson.fromJson(str, UserEntity.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                if (mEntity.code == ContextConstant.RESPONSECODE_200) {
                    mView.sendDynamicSuccess(true);
                } else if (mEntity.code == ContextConstant.RESPONSECODE_310) {    //登录信息过时跳转到登录页
                    mView.loginOut();
                } else {
                    ToastUtils.showToast(NewWidgetSetting.getInstance().filtrationStringbuffer(mEntity.msg, "接口信息异常！"));
                }

            }

            @Override
            public void onError(String error) {
                mView.loadingDissmis();
                mView.sendDynamicFail(error);
            }

            @Override
            public void onFinish() {
                mView.loadingDissmis();
            }
        },map);

    }

    @Override
    public void attachView(DynamicController.CreateDynamicView view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        if(mView != null) mView = null;
    }

    @Override
    public void getNetData() {

    }
}
