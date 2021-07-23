package com.example.greenmatting;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 弹出框工具类
 */
public class ToastUtil {

    Context context;
    Toast toast;
    public ToastUtil(Context context) {
        this.context = context;
    }

    /**
     * @param context 内容器实体
     * @param text    提示文字内容
     */


    public void toastShortShow(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (toast!=null){
                toast.cancel();
            }
            toast = Toast.makeText(context,text,Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public void toastLongShow(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (toast!=null){
                toast.cancel();
            }
            toast = Toast.makeText(context,text,Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public void clean(){
        if (toast!=null){
            toast.cancel();
            toast = null;
            context=null;
        }
    }

    public static void toastShortShow(Context context, String text) {
        if (!TextUtils.isEmpty(text)) {
            final Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            final Timer timer = new Timer();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    toast.cancel();
                    timer.cancel();
                }
            }, 500);
        }
    }

    public static void toastLongShow(Context context, String text) {
        if (!TextUtils.isEmpty(text)) {
            final Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            final Timer timer = new Timer();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    toast.cancel();
                    timer.cancel();
                }
            }, 2000);
        }
    }
}
