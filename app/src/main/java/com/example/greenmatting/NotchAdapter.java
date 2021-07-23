package com.example.greenmatting;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Method;

public class NotchAdapter {

    public final static int DEVICE_BRAND_OPPO = 0x0001;
    public final static int DEVICE_BRAND_HUAWEI = 0x0002;
    public final static int DEVICE_BRAND_VIVO = 0x0003;
    public static final int DEVICE_BRAND_LENOVO = 0x0004;
    public static final int DEVICE_BRAND_XIAOMI = 0x0005;

    @SuppressLint("DefaultLocale")
    public static int getDeviceBrand() {
        String brand = Build.BRAND.trim().toUpperCase();
        if (brand.contains("HUAWEI")) {
            Log.d("device brand", "HUAWEI " + brand);
            return DEVICE_BRAND_HUAWEI;
        } else if (brand.contains("OPPO")) {
            Log.d("device brand", "OPPO "+ brand);
            return DEVICE_BRAND_OPPO;
        } else if (brand.contains("VIVO")) {
            Log.d("device brand", "VIVO "+ brand);
            return DEVICE_BRAND_VIVO;
        } else if (brand.contains("LENOVO")) {
            Log.d("device brand", "LENOVO "+ brand);
            return DEVICE_BRAND_LENOVO;
        } else if (brand.contains("XIAOMI")) {
            Log.d("device brand", "XIAOMI "+ brand);
            return DEVICE_BRAND_XIAOMI;
        }
        return 0;
    }

    /**
     * 刘海的位置   0 : left  1 : top   2 : right   3 : bottom
     */
    private static int[] safeInset = new int[4];

    private static int notchHeight = -1;
    private static int notchWidth = -1;

    private static boolean hasNotch = false;


    public static int[] getSafeInset() {
        return safeInset;
    }

    public static int getSafeInsetLeft() {
        return safeInset[0];
    }

    public static int getSafeInsetTop() {
        return safeInset[1];
    }

    public static int getSafeInsetRight() {
        return safeInset[2];
    }

    public static int getSafeInsetBottom() {
        return safeInset[3];
    }


    public static int getNotchHeight() {
        return Math.abs(notchHeight);
    }

    public static int getNotchWidth() {
        return notchWidth;
    }

    public static boolean isHasNotch() {
        return hasNotch;
    }

    public static void initAdapter(Activity activity) {
        if (activity == null){
            Log.e(" LangSong Error   ", "  initAdapter()  error   activity is null. " );
            return;
        }
        /**
         *  Android P 或以上的刘海获取操作
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            NotchAdapter.getNotch(activity.getWindow().getDecorView());
        }
        /**
         * 刘海适配 Android P 以下
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            NotchAdapter.getNotchUnderP(activity.getApplicationContext());
        }

    }

    //--------------------------------------------- Android 9 以下 ---------------------------------------------

    /**
     * OPPO 判断是否有刘海     刘海高度是 80px
     *
     * @param context
     * @return
     */

    private static boolean getNotchAtOPPO(Context context) {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    private static int getNotchHeightAtOPPO() {
        return 80;
    }

    /**
     * 华为 判断是否有刘海    使用状态栏高度
     *
     * @param context
     * @return
     */
    private static boolean getNotchAtHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e("Notch", "hasNotchAtHuawei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("Notch", "hasNotchAtHuawei NoSuchMethodException");
        } catch (Exception e) {
            Log.e("Notch", "hasNotchAtHuawei Exception");
        }
        return ret;
    }

    private static int getNotchHeightAtHuawei(Context context) {
        return MeasureUtil.getStatusBarHeight(context);
    }


    /**
     * VIVO 判断是否有刘海    默认是 27dp
     *
     * @param context
     * @return
     */
    private static final int VIVO_NOTCH = 0x00000020;//是否有刘海

    private static boolean getNotchAtVIVO(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
        } catch (ClassNotFoundException e) {
            Log.e("Notch", "hasNotchAtVivo ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("Notch", "hasNotchAtVivo NoSuchMethodException");
        } catch (Exception e) {
            Log.e("Notch", "hasNotchAtVivo Exception");
        }
        return ret;
    }

    private static int getNotchHeightAtVIVO(Context context) {
        return MeasureUtil.dip2px(context, 27);
    }

    /**
     * 获取联想的刘海  使用状态栏高度
     *
     * @param context
     * @return
     */
    private static boolean getNotchLenovo(Context context) {
        int resourceId = context.getResources().getIdentifier("config_screen_has_notch", "bool", "android");
        if (resourceId > 0) {
            return context.getResources().getBoolean(resourceId);
        }
        return false;
    }

    private static int getNotchHeightAtLenovo(Context context) {
        return MeasureUtil.getStatusBarHeight(context);
    }

    /**
     * 获取小米刘海屏    使用状态高度
     *
     * @param context
     * @return
     */
    private static boolean getNotchXiaomi(Context context) {
        int resourceId = context.getResources().getIdentifier("notch_height", "dimen", "android");
        if (resourceId > 0) {
            int result = context.getResources().getDimensionPixelSize(resourceId);
            if (result != 0) {
                return true;
            }
        }
        return false;
    }

    private static int getNotchHeightAtXiaomi(Context context) {
        return MeasureUtil.getStatusBarHeight(context);
    }

    /**
     * Android P 或以下  判断是否有刘海
     *
     * @param context
     */
    public static void getNotchUnderP(Context context) {
        switch (getDeviceBrand()) {
            case NotchAdapter.DEVICE_BRAND_HUAWEI:
                hasNotch = NotchAdapter.getNotchAtHuawei(context);
                if (hasNotch) {
                    notchHeight = getNotchHeightAtHuawei(context);
                }
                break;
            case NotchAdapter.DEVICE_BRAND_OPPO:
                hasNotch = NotchAdapter.getNotchAtOPPO(context);
                if (hasNotch) {
                    notchHeight = getNotchHeightAtOPPO();
                }
                break;
            case NotchAdapter.DEVICE_BRAND_VIVO:
                hasNotch = NotchAdapter.getNotchAtVIVO(context);
                if (hasNotch) {
                    notchHeight = getNotchHeightAtVIVO(context);
                }
                break;
            case NotchAdapter.DEVICE_BRAND_LENOVO:
                hasNotch = NotchAdapter.getNotchLenovo(context);
                if (hasNotch) {
                    notchHeight = getNotchHeightAtLenovo(context);
                }
                break;
            case NotchAdapter.DEVICE_BRAND_XIAOMI:
                hasNotch = NotchAdapter.getNotchXiaomi(context);
                if (hasNotch) {
                    notchHeight = getNotchHeightAtXiaomi(context);
                }
                break;
            default:
                break;
        }
        return;
    }


//------------------------------------------------ Android 9 或 以上 ------------------------------------------------


    /**
     * Android P 或以上 判断是否有刘海  在 onAttachedToWindow()中调用
     * （二选一）
     *
     * @return 是否有刘海  有刘海则在 safeInset 记录了位置 以及宽高
     */
    @TargetApi(28)
    public static void getNotch(View decorView) {
        WindowInsets insets = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            insets = decorView.getRootWindowInsets();
        }
        if (insets != null) {
            DisplayCutout displayCutout = insets.getDisplayCutout();
            if (displayCutout != null && displayCutout.getBoundingRects().size() > 0) {
                safeInset[0] = displayCutout.getSafeInsetLeft();
                safeInset[1] = displayCutout.getSafeInsetTop();
                safeInset[2] = displayCutout.getSafeInsetRight();
                safeInset[3] = displayCutout.getSafeInsetBottom();
                notchWidth = safeInset[2] - safeInset[0];
                notchHeight = safeInset[3] - safeInset[1];
                hasNotch = true;
            } else {
                hasNotch = false;
            }
        }
        return;
    }

    /**
     * Android P 或以上 判断是否有刘海
     * *DecorView.setsetOnApplyWindowInsetsListener() onApplyWindowInsets（）回调中调用
     * （二选一）
     *
     * @param insets
     * @return 是否有刘海   有刘海则在 safeInset 记录了位置 以及宽高
     */
    public static void getNotch(WindowInsets insets) {
        DisplayCutout displayCutout = insets.getDisplayCutout();
        if (displayCutout != null) {
            if (displayCutout != null && displayCutout.getBoundingRects().size() > 0) {
                safeInset[0] = displayCutout.getSafeInsetLeft();
                safeInset[1] = displayCutout.getSafeInsetTop();
                safeInset[2] = displayCutout.getSafeInsetRight();
                safeInset[3] = displayCutout.getSafeInsetBottom();
                notchWidth = safeInset[2] - safeInset[0];
                notchHeight = safeInset[3] - safeInset[1];
                hasNotch = true;
            } else {
                hasNotch = false;
            }
        }
        return;
    }

//------------------------------------------------  沉浸式  -----------------------------------------------------

    /**
     *     完全沉浸式
     *                 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
     *                 | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
     *                 | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
     *                 | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
     *                 | View.SYSTEM_UI_FLAG_FULLSCREEN
     *                 | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
     *
     *                window.setStatusBarColor(Color.TRANSPARENT);
     *                window.setNavigationBarColor(Color.TRANSPARENT);
     *
     *
     *  内容侵入状态栏、导航栏， 不隐藏 并且背景透明
     *
     *             | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
     *             | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
     *
     *             window.setStatusBarColor(Color.TRANSPARENT);
     *             window.setNavigationBarColor(Color.TRANSPARENT);
     *
     *
     *   内容占用系统状态栏的空间  状态栏透明  导航栏不变化
     *             | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
     *             | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
     *
     *              window.setStatusBarColor(Color.TRANSPARENT);
     */


    /**
     *  (???? 建议Activity的onWindowFocusChanged()方法中使用  可能会出现失去焦点沉浸式失效问题)
     *
     *  切换应用沉浸式导致失效问题
     *   解决办法：onCreate()中添加
     *
     *          getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
     *             @Override
     *             public void onSystemUiVisibilityChange(int visibility) {
     *                 // 重新调用设置沉浸式方法
     *             }
     *         });
     */


    /**
     * 只设置状态栏沉浸式 并且隐藏状态栏
     *
     * @param window
     */
    public static void immersiveShowStatusBar(final Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setOnSystemUiVisibilityChangeListener(null);

            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            window.getDecorView().setSystemUiVisibility(uiOptions);

            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);

            /**
             *  解决bug , activity消失时 会导致沉浸式失效
             */
            window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN;
                    window.getDecorView().setSystemUiVisibility(uiOptions);

                    window.setStatusBarColor(Color.TRANSPARENT);
                    window.setNavigationBarColor(Color.TRANSPARENT);
                }
            });
        }
    }

    /**
     * 设置完全沉浸式 并且隐藏 状态栏 导航栏
     *
     * @param window
     */
    public static void fullScreenImmersive(final Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setOnSystemUiVisibilityChangeListener(null);

            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            window.getDecorView().setSystemUiVisibility(uiOptions);

            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);

            /**
             *  解决bug , activity消失时 会导致沉浸式失效
             */
            window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                    window.getDecorView().setSystemUiVisibility(uiOptions);
                }
            });
        }
    }


    /**
     * 设置状态栏 导航栏 沉浸式
     * 显示 状态栏 导航栏 为透明色
     *
     * @param window
     */
    public static void notchImmersive(final Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setOnSystemUiVisibilityChangeListener(null);
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    ;
            window.getDecorView().setSystemUiVisibility(uiOptions);

            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);

            window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                            ;
                    window.getDecorView().setSystemUiVisibility(uiOptions);
                }
            });
        }
    }

    /**
     * 有刘海屏   不设置沉浸式  显示状态栏为透明色
     *
     * @param window
     */
    public static void notchNoImmersive(final Window window, final int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setOnSystemUiVisibilityChangeListener(null);
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    ;
            window.getDecorView().setSystemUiVisibility(uiOptions);

            window.setStatusBarColor(statusBarColor);
            window.setNavigationBarColor(Color.TRANSPARENT);

            window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                            ;
                    window.getDecorView().setSystemUiVisibility(uiOptions);
                }
            });
        }
    }

    /**
     *  内容侵入导航栏 状态栏
     *  且 导航栏 状态栏 都为透明
     * @param window
     */
    public static void transparentNavBarImmersive(final Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setOnSystemUiVisibilityChangeListener(null);
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    ;
            window.getDecorView().setSystemUiVisibility(uiOptions);

            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);

            window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                            ;
                    window.getDecorView().setSystemUiVisibility(uiOptions);
                }
            });
        }
    }



    /**
     *  沉浸式适配    getDecorView.post()中执行的
     *
     *  有刘海则调用 {@link #adapterStatusBarImmersive(Window)} (最终效果 全屏 + 透明状态栏 + 无导航栏 或者是 完全沉浸式)
     *
     *  ,无刘海调用 {@link #fullScreenImmersive(Window)} (最终效果  全屏 + 无状态栏 导航栏)
     *
     * @param window
     */
    public static void immersive(final Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    if (hasNotch) {
                        adapterStatusBarImmersive(window);
                    } else {
                        fullScreenImmersive(window);
                    }
                }
            });
        }
    }


    /**
     *  伪完成沉浸式
     *  内容侵入状态栏
     *  但是显示状态栏为透明色    导航栏隐藏
     *
     * @param window
     */
    public static void noNavBarImmersive(final Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setOnSystemUiVisibilityChangeListener(null);
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    ;
            window.getDecorView().setSystemUiVisibility(uiOptions);

            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);

            window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                            ;
                    window.getDecorView().setSystemUiVisibility(uiOptions);
                }
            });
        }
    }

    /**
     *  由于 有刘海的屏幕使用完全沉浸式方案  会出现状态栏黑底 且无法侵入问题  使用该方法
     *       无刘海的屏幕使用完全沉浸式方案不会有问题
     * @param window
     */
    public static void adapterStatusBarImmersive(final Window window){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setOnSystemUiVisibilityChangeListener(null);

            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    ;
            window.getDecorView().setSystemUiVisibility(uiOptions);

            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);

            int screenHeight = MeasureUtil.getScreenHeight(window.getContext());
            int navigationHeight = MeasureUtil.getNavigationBarHeight(window.getContext());

//            LIIILog.loge("  window.getDecorView().getHeight()   ",window.getDecorView().getHeight() ,
//                    "  screenHeight  ",screenHeight  , " navigationHeight ",navigationHeight);
//            LIIILog.loge(" notch height   ",getNotchHeight());

            if (window.getDecorView().getHeight() < (screenHeight + navigationHeight + getNotchHeight() - 10) || inspectDevice()){
                 // decorView高度 小于 全屏高度说明 全屏方案 不能够侵入状态栏( 黑底 且 内容无法侵入 )  改用其他方法
                noNavBarImmersive(window);
                return;
            }else {
                // 如果可以侵入状态栏  延用全屏方案
                fullScreenImmersive(window);
            }

        }

    }

    /**
     *   当前机型可以完全全屏 没有状态栏 自己测试的
     * @return
     */
    private static boolean inspectDevice(){
        if ("MI8".equalsIgnoreCase(Build.MODEL.trim())){
            return true;
        }else if ("V2072A".equalsIgnoreCase(Build.MODEL.trim())){
            return true;
        }
        return false;
    }

    /**
     * 移动view 确保不会被刘海遮住  设置padding 可以在状态栏保留view的背景色
     *
     * @param view 需要偏移的view
     */
    public static void viewMoveToSafeInset(View view) {
        if (hasNotch && view != null && notchHeight != -1) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + notchHeight, view.getPaddingRight(), view.getPaddingBottom());
        }
    }


    /**
     * 设置 margin 则是整体下移 状态栏背景色将是以设置的颜色为准 (需要获取到view的父容器类型的layoutParams 自行添加 )
     */
    public static void viewMoveToSafeInset2(View ...view) {
        if (view == null){
            return;
        }
        for (int i = 0; i < view.length; i++) {
            View currentView = view[i];
            // 获取父布局的类名
            String parentName = currentView.getParent().getClass().getName();
                switch (parentName) {
                    case "android.widget.LinearLayout":
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) currentView.getLayoutParams();
                        layoutParams.setMargins(layoutParams.leftMargin , layoutParams.topMargin + getNotchHeight() ,
                                layoutParams.rightMargin , layoutParams.bottomMargin );
                        currentView.setLayoutParams(layoutParams);
                        return;
                    case "android.widget.RelativeLayout":
                        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) currentView.getLayoutParams();
                        layoutParams1.setMargins(layoutParams1.leftMargin , layoutParams1.topMargin + getNotchHeight(),
                                layoutParams1.rightMargin , layoutParams1.bottomMargin );
                        currentView.setLayoutParams(layoutParams1);
                        return;
                    case "android.widget.FrameLayout":
                        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) currentView.getLayoutParams();
                        layoutParams2.setMargins(layoutParams2.leftMargin , layoutParams2.topMargin + getNotchHeight(),
                                layoutParams2.rightMargin , layoutParams2.bottomMargin );
                        currentView.setLayoutParams(layoutParams2);
                        return;
                    case "androidx.recyclerview.widget.RecyclerView":
                        RecyclerView.LayoutParams layoutParams3 = (RecyclerView.LayoutParams) currentView.getLayoutParams();
                        layoutParams3.setMargins(layoutParams3.leftMargin , layoutParams3.topMargin + getNotchHeight(),
                                layoutParams3.rightMargin , layoutParams3.bottomMargin );
                        currentView.setLayoutParams(layoutParams3);
                        return;
                    default:
                        break;
            }
            Log.e("TAG", " 当前view无法下移 ");

        }


    }

}
