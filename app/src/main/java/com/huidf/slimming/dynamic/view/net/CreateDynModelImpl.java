package com.huidf.slimming.dynamic.view.net;

import com.google.gson.Gson;
import com.huidf.slimming.context.ApplicationData;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.entity.user.UserEntity;
import com.huidf.slimming.net.DefaultObserver;
import com.huidf.slimming.net.RetrofitHelper;
import com.huidf.slimming.net.model.BaseHttpEntity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import huitx.libztframework.utils.LOGUtils;
import huitx.libztframework.utils.PreferencesUtils;
import huitx.libztframework.utils.StringUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * 作者：ZhuTao
 * 创建时间：2019/3/26 : 15:21
 * 描述：
 */
public class CreateDynModelImpl implements DynamicController.CreateDynamicModel{

    @Override
    public void executePicture(final BaseHttpEntity<ResponseBody> entity, File file) {

        String userId = PreferencesUtils.getString(ApplicationData.context, PreferenceEntity.KEY_USER_ID, "");

        MediaType textType = MediaType.parse("text/plain");
        RequestBody uId = RequestBody.create(textType, userId);

        //构建requestbody
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data;charset=UTF-8"), file);
        //将resquestbody封装为MultipartBody.Part对象
        MultipartBody.Part body = MultipartBody.Part.createFormData("pic", file.getName(), requestFile);

        RetrofitHelper.getService().getDynamicServiceApi().uploadingPicture(uId, body)
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

    @Override
    public void executeDynamic(final BaseHttpEntity<ResponseBody> entity, Map<String,String> map) {
        RetrofitHelper.getService().getDynamicServiceApi().createDynamic(map)
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

