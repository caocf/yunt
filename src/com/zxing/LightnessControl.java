package com.zxing;

import android.app.Activity;
import android.content.ContentResolver;
import android.provider.Settings;
import android.provider.Settings.System;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by lijianshuang on 14-12-4.
 */
public class LightnessControl {
    // 判断是否�?启了自动亮度调节
    public static boolean isAutoBrightness(Activity act) {
        boolean automicBrightness = false;
        ContentResolver aContentResolver = act.getContentResolver();
        try {
            automicBrightness = Settings.System.getInt(aContentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Exception e) {
            Toast.makeText(act, "无法获取亮度", Toast.LENGTH_SHORT).show();
        }
        return automicBrightness;
    }
    // 改变亮度
    public static void SetLightness(Activity act,int value)
    {
        try {
            System.putInt(act.getContentResolver(),System.SCREEN_BRIGHTNESS,value);
            WindowManager.LayoutParams lp = act.getWindow().getAttributes();
            lp.screenBrightness = (value<=0?1:value) / 255f;
            act.getWindow().setAttributes(lp);
        } catch (Exception e) {
            Toast.makeText(act,"无法改变亮度",Toast.LENGTH_SHORT).show();
        }
    }
    // 获取亮度
    public static int GetLightness(Activity act)
    {
        return System.getInt(act.getContentResolver(),System.SCREEN_BRIGHTNESS,-1);
    }
    // 停止自动亮度调节
    public static void stopAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }
    // �?启亮度自动调�?
    public static void startAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }
}
