package com.wishes.assistant.otherutils;

import android.content.Context;

import com.wishes.assistant.io.sharepreference.SharePreferenceUtils;


/**
 * 集合了一些杂七杂八的功能的工具类
 * Created by 郑龙 on 2018/5/6.
 */

public class Utils {
    /**
     * 判断是否登录
     *
     * @return
     */
    public static boolean isLoginJWSystem(Context context) {
        return SharePreferenceUtils.isLoginJWSystem(context);
    }

    /**
     * 判断是否登录图书馆
     *
     * @param context
     * @return
     */
    public static boolean isLoginLibSystem(Context context) {
        return SharePreferenceUtils.isLoginLibSystem(context);
    }

    /**
     * 判官是否登录体测系统
     *
     * @param context
     * @return
     */
    public static boolean isLoginSportSystem(Context context) {
        return SharePreferenceUtils.isLoginSportSystem(context);
    }

}
