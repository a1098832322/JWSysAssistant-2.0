package com.wishes.assistant.myapplication.activity.dto;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Avtivity之间的数据传输对象
 * Created by 郑龙 on 2018/5/19.
 */

public class ActivityTransferTarget implements Serializable {
    private String bitmapName, fromActivity;
    private Bitmap bitmap;

    public String getBitmapName() {
        return bitmapName;
    }

    public void setBitmapName(String bitmapName) {
        this.bitmapName = bitmapName;
    }

    public String getFromActivity() {
        return fromActivity;
    }

    public void setFromActivity(String fromActivity) {
        this.fromActivity = fromActivity;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
