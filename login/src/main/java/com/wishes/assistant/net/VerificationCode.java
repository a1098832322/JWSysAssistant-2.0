package com.wishes.assistant.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wishes.assistant.Constants;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 郑龙 on 2017/4/25.
 */

public class VerificationCode {
    public static final int getCodeSuccess = 1;
    public static final int getCodeError = 0;


    public static void getCode(final Handler handler) {
        try {
            OkHttpClient mOkhttpClient = OKHttpUtils.getInstanceClient();
            Request request = new Request.Builder().url(Constants.VerificationCodeUrl).build();
            mOkhttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("TAG", "请求失败!");
                    Message msg = Message.obtain();
                    msg.what = getCodeError;
                    handler.sendMessage(msg);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    InputStream is = response.body().byteStream();
                    Bitmap code = BitmapFactory.decodeStream(is);
                    //使用Handler传出图片
                    Message message = Message.obtain();
                    message.what = getCodeSuccess;
                    message.obj = code;
                    handler.sendMessage(message);
                }

            });

        } catch (Exception e) {
            Log.e("TAG", "一些异常：" + e);
            Message message = Message.obtain();
            message.what = getCodeError;
            handler.sendMessage(message);
        }
    }

}
