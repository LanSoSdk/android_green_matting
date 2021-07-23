package com.example.greenmatting;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

/**
 * 权限管理
 */

public class PermissionManager {

    public static final int REQ_PERMISSION_CAMERA = 2; // 拍照
    public static final int REQ_PERMISSION_ALBUM = 3; // 获取相册

    public static boolean checkStorage(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode == 0 ? REQ_PERMISSION_ALBUM : requestCode);
                return false;
            }
        }
        return true;
    }


    /**
     * 拍照权限
     *
     * @param activity
     * @param requestCode
     * @return
     */
    public static boolean checkCamera(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode == 0 ? REQ_PERMISSION_CAMERA : requestCode);
                return false;
            }
        }
        return true;
    }


}
