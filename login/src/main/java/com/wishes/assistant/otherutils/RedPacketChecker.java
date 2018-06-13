package com.wishes.assistant.otherutils;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.wishes.assistant.Constants;
import com.wishes.assistant.net.OKHttpUtils;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 郑龙 on 2018/5/20.
 */

public class RedPacketChecker {
    /**
     * 从网络获取最新红包数据
     *
     * @return
     */
    public static RedPacket checkRedPacket(Context context, String name) {

        String ResultStr = null;//
        OkHttpClient client = OKHttpUtils.getInstanceClient();
        RequestBody body = new FormBody.Builder().add("name", name).build();
        Request request = new Request.Builder().url(Constants.redPcketCheckUrl).post(body).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.d("updateCheck", "success!\t");
                ResultStr = response.body().string();
                RedPacket redPacket = JSONObject.parseObject(ResultStr, RedPacket.class);
                return redPacket;
            } else {
                Log.e("updateCheck", "红包请求数据失败！网络响应没有成功！!\t");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("updateCheck", "get RedPacket fail! with Exception:\t" + e);
        }


        return null;
    }

    /**
     * 基本红包对象
     */
    public static class RedPacket {
        private int id;
        private String redPacketName, redPacketContent, updateTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRedPacketName() {
            return redPacketName;
        }

        public void setRedPacketName(String redPacketName) {
            this.redPacketName = redPacketName;
        }

        public String getRedPacketContent() {
            return redPacketContent;
        }

        public void setRedPacketContent(String redPacketContent) {
            redPacketContent = redPacketContent.replace("\\n", "\n");
            this.redPacketContent = redPacketContent;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}
