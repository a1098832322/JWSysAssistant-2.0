package com.wishes.assistant.otherutils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;


import com.alibaba.fastjson.JSONObject;
import com.wishes.assistant.Constants;
import com.wishes.assistant.net.OKHttpUtils;

import java.io.IOException;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 用于检测软件更新
 * Created by 郑龙 on 2018/5/18.
 */

public class UpdateAPKChecker {
    private static Context mContext;
    private static String mCurrentVersion = "1.0";
    private static String newestVersion = "1.0";
    private static Version mVersion = new Version();


    /**
     * 从网络获取最新包数据
     *
     * @return
     */
    public static boolean checkNewestVersion(Context context) {
        mContext = context;
        getAppVersionName();//获取当前版本

        String ResultStr = null;//
        OkHttpClient client = OKHttpUtils.getInstanceClient();
        Request request = new Request.Builder().url(Constants.updateCheckUrl).get().build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.d("updateCheck", "success!\t");
                ResultStr = response.body().string();
                mVersion = JSONObject.parseObject(ResultStr, Version.class);
                newestVersion = mVersion.getVersion();//最新版本
            } else {
                Log.e("updateCheck", "请求数据失败！网络响应没有成功！!\t");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("updateCheck", "fail! with Exception:\t" + e);
        }


        //对比
        int result = compared(mCurrentVersion, newestVersion);

        return result == 1 ? true : false;
    }


    /**
     * 检测当前版本
     *
     * @return
     */
    public static String getAppVersionName() {
        String versionName = "";
        int versionCode = 0;
        try {
            // ---get the package info---
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), 0);
            versionName = pi.versionName;
            versionCode = pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (versionName == null || versionName.length() <= 0) {
            versionName = "";
        }

        mCurrentVersion = versionName;
        return versionName;
    }

    public static Version getmVersion() {
        return mVersion;
    }

    /**
     * 对比版本号判断是否需要更新<br>
     * 0不需要<br>
     * 1需要<br>
     *
     * @param current
     * @param newest
     * @return
     */
    private static int compared(String current, String newest) {
        current = current.replace(".", "");
        newest = newest.replace(".", "");

        //对比数值
        if (Double.parseDouble(newest) > Double.parseDouble(current)) {
            return 1;
        }

        return 0;
    }

    /**
     * 版本管理
     */
    public static class Version {
        private String content, name, updateTime, version;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            content = content.replace("\\n", "\n");
            this.content = content;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
