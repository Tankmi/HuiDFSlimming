package com.huidf.slimming.dynamic.presenter;

/**
 * 作者：ZhuTao
 * 创建时间：2019/3/7 : 15:39
 * 描述：选择照片业务逻辑接口
 */
public interface SelectPictureBasePresenter<T> {

    void attachView(T view);

    void detachView();

    void forCamera();

    void  forPhoto();
}
