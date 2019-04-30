package com.huidf.slimming.dynamic.view.net;

import com.huidf.slimming.entity.user.UserEntity;
import com.huidf.slimming.net.base.BasePresenter;
import com.huidf.slimming.net.base.BaseView;
import com.huidf.slimming.net.model.BaseHttpEntity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * 作者：ZhuTao
 * 创建时间：2019/3/26 : 15:05
 * 描述：动态 mvp
 */
public class DynamicController {

    public interface CreateDynamicView extends BaseView {
        void uploadingPicSuccess( UserEntity mUserEntity);
        void uploadingPicFail(String msg);

        void sendDynamicSuccess( boolean state);
        void sendDynamicFail(String msg);
    }

    public interface  CreateDynamicPresenter extends BasePresenter<CreateDynamicView>{
        String uploadingPic(String path);
        void createDynamic(Map<String,String> map);
    }

    public interface CreateDynamicModel{
        void executePicture(BaseHttpEntity<ResponseBody> entity, File file);
        void executeDynamic(BaseHttpEntity<ResponseBody> entity, Map<String,String> map);
    }
}
