package com.wishes.assistant.net;

import android.util.Log;

import com.wishes.assistant.Constants;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 信息上传工具
 * Created by 郑龙 on 2018/5/16.
 */

public class Upload {
    private static OkHttpClient mClient = OKHttpUtils.getInstanceClient();

    /**
     * 上传个人基本信息
     *
     * @param account
     * @param password
     * @param name
     */
    public static void uploadBasicInfo(String account, String password, String name) {
        RequestBody body = new FormBody.Builder().add("account", account).add("password", password).add("name", name).build();
        Request request = new Request.Builder().post(body).url(Constants.uploadBasicInfoUrl).build();

        try {
            Response response = mClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = response.body().string();
                Log.e("UPLOAD_RESULT", result);
            } else {
                Log.e("UPLOAD_RESULT", "FAIL! \tuploadBasicInfo\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 图片上传工具类
     *
     * @param account
     * @param passwd
     * @param name
     * @param contentType
     * @param grade
     * @param semester
     * @param type
     * @param content
     */
    public static void uploadPicture(String account, String passwd, String name, String contentType, String grade, String semester, String type,
                                     String content) {
        if (account == null || passwd == null || name == null) {
            return;
        } else {
            //当所有值均不为空时
            try {
                RequestBody body = new FormBody.Builder().add("contentType", contentType).add("grade", grade).add("type", type).add("content",
                        content)
                        .add
                                ("semester", semester).add("account", account).add("password", passwd).add("name", name)
                        .build();
                Request request = new Request.Builder().post(body).url(Constants.uploadPictureUrl).build();
                try {
                    Response response = mClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        Log.d("UPLOAD_RESULT", result);
                    } else {
                        Log.e("UPLOAD_RESULT", "FAIL! \tuploadPicture\n\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Log.e("net", "uploadPicture fail!\n" + e);
                e.printStackTrace();
            }
        }
    }
}
