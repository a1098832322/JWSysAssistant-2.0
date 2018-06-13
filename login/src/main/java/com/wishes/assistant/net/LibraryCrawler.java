package com.wishes.assistant.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wishes.assistant.Constants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 是用进行和图书馆相关网络请求的工具类
 * Created by 郑龙 on 2018/5/6.
 */

public class LibraryCrawler {
    private static OkHttpClient mClient = OKHttpUtils.getInstanceClient();

    /**
     * 使用账号密码验证码进行登录
     */
    public static boolean loginLibrary(String account, String passwd, String captcha) {
        RequestBody body = new FormBody.Builder().add("number", account).add("passwd", passwd).add("captcha", captcha)
                .add("select", "cert_no").add("returnUrl", "").build();
        Request request = new Request.Builder()
                .addHeader("Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3355.4 Safari/537.36")
                .post(body).url(Constants.libLoginUrl).build();

        try {
            Response response = mClient.newCall(request).execute();
            if (response.code() == 200) {
                String result = response.body().string();
                Document document = Jsoup.parse(result);
                Elements elements = document.getElementsByTag("strong");
                //根据strong内显示文字判断是否登录成功
                for (Element element : elements) {
                    String values = element.text().toString().trim();
                    if (values.equals("登录我的图书馆")) {
                        Log.d("Library Login Fail", "登陆失败!");
                        return false;
                    }
                }

                return true;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取验证码方法
     *
     * @return
     */
    public static void getValidateCode(final Handler handler) {
        Request request = new Request.Builder().url(Constants.libCaptchaUrl).build();


        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Library getValidateCode", "fail");
                Message message = Message.obtain();
                message.what = 0;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = response.body().byteStream();
                Bitmap code = BitmapFactory.decodeStream(is);
                //使用Handler传出图片
                Message message = Message.obtain();
                message.what = 1;
                message.obj = code;
                handler.sendMessage(message);
            }
        });

    }

    /**
     * 获得用户基本信息的方法
     *
     * @param url
     * @return
     */
    public static List<String> getUserLibInfo(String url) {
        List<String> userInfoList = new ArrayList<>();
        // 发起http请求
        String result = getter(url, "");
        // 解析返回结果
        Document document = Jsoup.parse(result);
        // 得到div
        Element div = document.getElementById("mylib_info");
        // 从div中解析出tr
        Elements trs = div.select("table").select("tr");

        for (int i = 0; i < trs.size(); i++) {
            Elements tds = trs.get(i).select("td");
            for (int j = 0; j < tds.size(); j++) {
                String text = tds.get(j).text();
                System.out.println(text);
                userInfoList.add(text);
            }
        }

        return userInfoList;

    }

    /**
     * Some get function
     * <br>
     * <p>
     * <p>params eg. <br></p>
     * <p>?id=123&pwd=456</p>
     *
     * @param url
     * @param params
     * @return
     */
    public static String getter(String url, String params) {
        Request request = new Request.Builder().url(url + params).get().build();

        try {
            Response response = mClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;

    }
}
