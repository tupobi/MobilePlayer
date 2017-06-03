package com.example.administrator.mobileplayer.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 作者：尚硅谷-杨光福 on 2016/7/25 11:58
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：xxxx
 */
public class DensityUtil {
    private static DisplayMetrics displayMetrics = new DisplayMetrics();

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreenWidth(){
        return displayMetrics.widthPixels;
    }
    public static int getScreenHeight(){
        return displayMetrics.heightPixels;
    }
}
