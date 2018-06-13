package com.wishes.assistant.otherutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.wishes.assistant.Constants;
import com.wishes.assistant.net.OKHttpUtils;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 用于响应服务器通知
 * Created by 郑龙 on 2018/5/20.
 */

public class CrashChecker {
    /**
     * 从网络获取最新服务器通知数据
     *
     * @return
     */
    public static CrashDetail checkCrashDetail(Context context) {

        String ResultStr = null;//
        OkHttpClient client = OKHttpUtils.getInstanceClient();
        Request request = new Request.Builder().url(Constants.crashDetailCheckUrl).get().build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.d("updateCheck", "success!\t");
                ResultStr = response.body().string();
                CrashDetail crashDetail = JSONObject.parseObject(ResultStr, CrashDetail.class);

                //本地检测是否有崩溃更新提示
                SharedPreferences share = context.getSharedPreferences("student", Context.MODE_PRIVATE);
                int crashid = share.getInt("crashid", 0);

                if (crashDetail != null) {
                    if (crashid == crashDetail.getId()) {
                        //如果相等则不显示
                        return null;
                    } else {
                        //将新信息存入本地
                        SharedPreferences.Editor edit = share.edit(); //编辑文件
                        edit.putInt("crashid", crashDetail.getId());
                        edit.commit();  //保存数据信息


                        //显示最新信息
                        return crashDetail;
                    }
                }


            } else {
                Log.e("updateCheck", "服务器通知请求数据失败！网络响应没有成功！!\t");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("updateCheck", "get crash fail! with Exception:\t" + e);
        }


        return null;
    }

    public static class CrashDetail {
        private int id;
        private String crashName, crashContent, updateTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCrashName() {
            return crashName;
        }

        public void setCrashName(String crashName) {
            this.crashName = crashName;
        }

        public String getCrashContent() {
            return crashContent;
        }

        public void setCrashContent(String crashContent) {
            crashContent = crashContent.replace("\\n", "\n");
            this.crashContent = crashContent;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}
