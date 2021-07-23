package com.example.greenmatting;


public interface  BaseView {

    /**
     * 显示加载中
     */
     void  showLoading(String msg);

    /**
     * 隐藏加载
     */
     void hideLoading();

    /**
     * 数据获取失败
     * @param errMessage
     */
     void onError(String errMessage);

    /**
     *  弹出 toast 提示
     * @param msg
     */
    void onToast(String msg, boolean longShow);
}
