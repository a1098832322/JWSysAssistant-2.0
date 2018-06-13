package com.wishes.assistant.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by 郑龙 on 2017/4/25.
 */

public class OKHttpUtils {
    private final int ERROR = -1;
    private final int SUCCESS = 1;
    private static OkHttpClient mOkhttpClient = null;
    private static OKHttpUtils mUtils = null;

    public static OKHttpUtils getInstanceUtils() {
        if (mUtils == null) {
            mUtils = new OKHttpUtils();
        }
        return mUtils;
    }

    public static OkHttpClient getInstanceClient() {
        if (mOkhttpClient == null) {
            mOkhttpClient = new OkHttpClient.Builder().cookieJar(new CookieJar() {
                //存取Cookies
                private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                @Override
                public void saveFromResponse(HttpUrl httpUrl, List<Cookie> cookies) {
                    cookieStore.put(httpUrl.host(), cookies);
                    for (Cookie cookie : cookies) {
                        //log cookies
                        Log.d("cookie Name:", cookie.name());
                        Log.d("cookie value", cookie.value());
                        Log.d("cookie Path:", cookie.path());
                    }
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                    List<Cookie> cookies = cookieStore.get(httpUrl.host());

                    if (cookies == null) {
                        Log.d(TAG, "没加载到cookie");
                    }
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            }).build();
        }

        return mOkhttpClient;
    }


    //异步GET请求
    public void asynchronousGet(String url, final Handler handler) {
        OkHttpClient client = OKHttpUtils.getInstanceClient();
        try {
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e("TAG", "请求失败!");
                    Message msg = Message.obtain();
                    msg.what = 0;
                    handler.sendMessage(msg);
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    // 注：该回调是子线程，非主线程
                    InputStream is = response.body().byteStream();
                    Bitmap code = BitmapFactory.decodeStream(is);
                    Message msg = Message.obtain();

                    if (code == null) {
                        msg.what = 0;
                    } else {
                        msg.obj = code;
                        msg.what = 1;
                    }
                    handler.sendMessage(msg);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //异步POST请求登录
    public String postFromParameters(final String url, String account, String passwd, String code) {
        OkHttpClient client = OKHttpUtils.getInstanceClient();
        try {

            RequestBody formBody = new FormBody.Builder().add("id", account)
                    .add("pwd", passwd).add("code", code).add("Submit", "Sign+In").build();

            Log.d("Code:", code);
            Log.d(TAG, "用户信息：" + code + "\tnumber:" + account + "\tpasswd:" + passwd);

            Request request = new Request.Builder().addHeader("Content-Type", "application/x-www-form-urlencoded").post(formBody).url(url).build();


            //执行POST请求
            Response response = client.newCall(request).execute();

            //获取返回数据
            String result = response.body().string();
            //Log.d(TAG, "body:" + result);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "postFromParameters: ", e);
        }
        return null;
    }

}
