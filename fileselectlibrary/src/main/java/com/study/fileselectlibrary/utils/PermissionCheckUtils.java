package com.study.fileselectlibrary.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/25.
 */

public class PermissionCheckUtils {
    private static OnWantToOpenPermissionListener listener;

    /**
     * 检测Activity所需要的权限
     *
     * @param activity    当前Activity对象
     * @param permissions 当前Activity对象所需要的权限
     * @param requestCode 申请权限的请求码
     * @param fragment    当前fragment
     */
    public static int checkActivityPermissions(Activity activity, String[] permissions, int requestCode, Fragment fragment) {
        if (permissions == null || activity == null) {
            return 0;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                String permiss = permissions[i];
                int resultCode = ContextCompat.checkSelfPermission(activity, permiss);
                if (resultCode != PackageManager.PERMISSION_GRANTED) {
                    permissionsList.add(permiss);
                }
            }
            if (permissionsList.size() > 0) {
                requestActivityPermissions(activity, permissionsList, requestCode, fragment);
            }
            return permissionsList.size();
        } else {
            return 0;
        }
    }

    public static void requestActivityPermissions(Activity activity, List<String> permissions, int requestCode, Fragment fragment) {
        List<String> permissionList = new ArrayList<>();
        String[] strings = new String[permissions.size()];
        for (int i = 0; i < permissions.size(); i++) {
            String str = permissions.get(i);
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, str)) {
                permissionList.add(str);
            }
            strings[i] = str;
        }
        if (permissionList.size() > 0) {
            if (listener != null) {
                listener.onWantToOpenPermission();
            }
        } else {
            if (fragment == null) {
                ActivityCompat.requestPermissions(activity, strings, requestCode);
            } else {
                fragment.requestPermissions(strings, requestCode);
            }
        }
    }

    public static void setOnOnWantToOpenPermissionListener(OnWantToOpenPermissionListener defineListener) {
        listener = defineListener;
    }

    public interface OnWantToOpenPermissionListener {
        void onWantToOpenPermission();
    }

    /**
     * 检查当前设备的sd卡是否存在，基本上没有用，现在的手机基本都是内置存储
     *
     * @return
     */
    public static boolean checkExternalStorageIsExists() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}
