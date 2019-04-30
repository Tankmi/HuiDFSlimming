package com.huidf.slimming.net.base;

/**
 * 业务逻辑基类
 * @param <T>
 */
public interface BasePresenter<T> {
    void attachView(T view);
    void detachView();
    void getNetData();
}
