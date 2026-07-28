package com.gdpt.huantu.core.network;

/**
 * 统一网络回调接口
 */
public interface NetworkCallback<T> {

    /**
     * 请求成功
     */
    void onSuccess(T data);

    /**
     * 请求失败
     */
    void onError(int code, String message);

    /**
     * 网络异常
     */
    void onFailure(Throwable t);
}
