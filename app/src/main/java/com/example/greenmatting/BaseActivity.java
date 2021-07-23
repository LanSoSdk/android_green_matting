package com.example.greenmatting;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.List;

public class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements BaseView {

    protected T mPresenter;

    protected BaseActivity activity = null;




    /**
     * 加载界面的资源id
     */
    protected int mLayoutResID = -1;


    public ToastUtil toastUtil;

    // 权限检测
    private boolean isPermissionOk = false;

    public T getPresenter() {
        return mPresenter;
    }

    protected boolean inspectPresenterHasNull(){
        if (mPresenter == null){
            onToast(" 当前无法进行操作  mPresenter = null ",false);
            return true;
        }
        return false;
    }


    /**
     * @param layoutResID 界面资源文件id
     */
    public BaseActivity(int layoutResID) {
        this.mLayoutResID = layoutResID;
    }

    /**
     * @param layoutResID 界面资源文件id
     */
    public BaseActivity(int layoutResID, boolean saveExistingActivity) {
        this.mLayoutResID = layoutResID;
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        /**
         *  判断刘海屏
         */
        NotchAdapter.initAdapter(this);

        /**
         *   沉浸式
         */
        initImmersive();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mLayoutResID);
        activity = this;


        toastUtil = new ToastUtil(getApplicationContext());


        getIntentData();
        initView();

        /**
         * 键盘弹出不挤压内容 但是要保证输入框在键盘上方
         */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
        );


    }

    /**
     * 得到界面跳转过来的数值，如果上一个界面有数据传递过来，那么这边需要进行重写该方法
     */
    public void getIntentData() {

    }


    public void initView() {

    }




    @Override
    protected void onDestroy() {

        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
        }



        if (mPresenter!=null){
            mPresenter.detachView();
        }


        if (toastUtil != null){
            toastUtil.clean();
            toastUtil = null;
        }
        activity = null;

        super.onDestroy();
    }

    private void initImmersive(){
        if (!setEnableImmersive()){
            return;
        }
        if (customImmersive()){
            return;
        }

        if (NotchAdapter.isHasNotch()) {
            NotchAdapter.notchNoImmersive(getWindow(),getResources().getColor(R.color.colorPrimary));
        } else {
            NotchAdapter.immersiveShowStatusBar(getWindow());
        }

    }

    /**
     *  自行使用沉浸式方案
     * @return  true 表示不再进行默认的适配  ， false 继续进行默认适配
     */
    protected boolean customImmersive(){
        return false;
    }

    protected boolean setEnableImmersive(){
        return true;
    }



    protected Message message ;

    /**
     * 初始化Handler
     */
    @SuppressLint("HandlerLeak")
    protected Handler mHandler = new Handler() {
        @Override

        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            try {
                initHandler(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 设置消息监听
     *
     * @param msg
     */
    protected void initHandler(Message msg) {
        // 子类中实现
    }

    @Override
    public void showLoading(String msg) {
    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void onError(String errMessage) {
        if (toastUtil != null){
            toastUtil.toastLongShow(errMessage);
        }
    }

    @Override
    public void onToast(String msg,boolean longShow) {
        if (toastUtil != null){
            if (longShow) {
                toastUtil.toastLongShow(msg);
            }else {
                toastUtil.toastShortShow(msg);
            }
        }
    }

}
