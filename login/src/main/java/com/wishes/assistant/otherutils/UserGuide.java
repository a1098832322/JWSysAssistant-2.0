package com.wishes.assistant.otherutils;

import android.content.Context;

import com.wishes.assistant.io.sharepreference.SharePreferenceUtils;

/**
 * 是否是第一次启动App
 * Created by 郑龙 on 2018/5/18.
 */

public class UserGuide {
    /**
     * 判断是否是第一次启动App
     *
     * @param context
     * @param modeName
     * @return
     */
    public static boolean isFirstStart(Context context, String modeName) {
        int lastVersion = SharePreferenceUtils.getActivityStartTimes(context, modeName);
        if (0 == lastVersion) {
            //如果当前版本大于上次版本，该版本属于第一次启动
            //将当前版本写入preference中，则下次启动的时候，据此判断，不再为首次启动          prefs.edit().putInt("VERSION_KEY",currentVersion).commit();
            return true;
        } else {
            //不是第一次启动，跳过引导页直接到欢迎界面
            return false;
        }
    }

    /**
     * 修改Activity启动次数
     *
     * @param context
     * @param modeName
     */
    public static void changeActivityStartTimes(Context context, String modeName) {
        SharePreferenceUtils.changeActivityStartTimes(context, modeName);
    }
}
