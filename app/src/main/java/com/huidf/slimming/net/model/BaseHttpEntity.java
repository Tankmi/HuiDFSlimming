package com.huidf.slimming.net.model;

public interface BaseHttpEntity<T> {

    void onSuccess(T data);
    void onError(String error);
    void onFinish();
}
