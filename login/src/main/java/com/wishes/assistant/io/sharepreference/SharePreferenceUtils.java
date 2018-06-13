package com.wishes.assistant.io.sharepreference;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 郑龙 on 2017/4/24.
 */

public class SharePreferenceUtils {
    public enum ACCOUNT_TYPE {
        LIBRARY, JWSYS, SPORT
    }

    /**
     * 修改Activity启动状态，判断是否是第一次启动
     *
     * @param context
     * @param modeName
     */
    public static void changeActivityStartTimes(Context context, String modeName) {
        //指定操作的文件名称
        SharedPreferences share = context.getSharedPreferences("student", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = share.edit(); //编辑文件
        edit.putInt(modeName, 1);         //根据键值对添加数据
        edit.commit();
    }

    /**
     * 获得Activity启动状态，判断是否是第一次启动
     *
     * @param context
     * @param modeName
     * @return
     */
    public static int getActivityStartTimes(Context context, String modeName) {
        //指定操作的文件名称
        SharedPreferences share = context.getSharedPreferences("student", Context.MODE_PRIVATE);
        return share.getInt(modeName, 0);
    }

    /**
     * 清除数据，注销时候用的
     *
     * @param type
     * @param context
     */
    public static void clearData(ACCOUNT_TYPE type, Context context) {
        //指定操作的文件名称
        SharedPreferences share = context.getSharedPreferences("student", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = share.edit(); //编辑文件

        switch (type) {
            case JWSYS:
                edit.putString("number", null);         //根据键值对添加数据
                edit.putString("passwd", null);
                break;
            case LIBRARY:
                edit.putString("libnumber", null);         //根据键值对添加数据
                edit.putString("libpasswd", null);
                break;
            case SPORT:
                edit.putString("sportnumber", null);         //根据键值对添加数据
                edit.putString("sportpasswd", null);
                break;
        }

        edit.commit();  //保存数据信息
    }

    /**
     * 将一些信息存入SharePreference
     *
     * @param type
     * @param context
     * @param number
     * @param passwd
     * @param name
     */

    public static void SaveData(ACCOUNT_TYPE type, Context context, String number, String passwd, String name) {
        //指定操作的文件名称
        SharedPreferences share = context.getSharedPreferences("student", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = share.edit(); //编辑文件

        switch (type) {
            case JWSYS:
                edit.putString("number", number);         //根据键值对添加数据
                edit.putString("passwd", passwd);
                edit.putString("name", name);
                break;
            case LIBRARY:
                edit.putString("libnumber", number);         //根据键值对添加数据
                edit.putString("libpasswd", passwd);
                break;
            case SPORT:
                edit.putString("sportnumber", number);         //根据键值对添加数据
                edit.putString("sportpasswd", passwd);
                break;
        }


        edit.commit();  //保存数据信息
    }

    /**
     * 判断是否登录教务系统
     *
     * @param context
     * @return
     */
    public static boolean isLoginJWSystem(Context context) {
        SharedPreferences share = context.getSharedPreferences("student", Context.MODE_PRIVATE);

        String name = share.getString("name", null);
        String number = share.getString("number", null);
        if (name != null && number != null) {
            return true;
        }
        return false;
    }


    /**
     * 判断是否登录图书馆
     *
     * @param context
     * @return
     */
    public static boolean isLoginLibSystem(Context context) {
        SharedPreferences share = context.getSharedPreferences("student", Context.MODE_PRIVATE);

        String name = share.getString("libnumber", null);
        if (name != null) {
            return true;
        }
        return false;
    }


    /**
     * 判断是否登录体测系统
     *
     * @param context
     * @return
     */
    public static boolean isLoginSportSystem(Context context) {
        SharedPreferences share = context.getSharedPreferences("student", Context.MODE_PRIVATE);

        String name = share.getString("sportnumber", null);
        if (name != null) {
            return true;
        }
        return false;
    }


    public static List<HashMap<String, String>> LoadData(Context context) {
        //指定操作的文件名称
        SharedPreferences share = context.getSharedPreferences("student", Context.MODE_PRIVATE);

        HashMap<String, String> map = new HashMap<>();
        map.put("number", share.getString("number", ""));
        map.put("passwd", share.getString("passwd", ""));
        map.put("name", share.getString("name", ""));
        List<HashMap<String, String>> student = new ArrayList<>();
        student.add(map);

        return student;

    }
}
