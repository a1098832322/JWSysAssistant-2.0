package com.wishes.assistant.myapplication.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wishes.assistant.myapplication.R;

import skin.support.app.SkinCompatActivity;

/**
 * 帮助页
 * Created by 郑龙 on 2018/5/19.
 */

public class Help extends SkinCompatActivity {
    private ImageView mImgBack;
    private TextView mAppName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        //设置沉浸式状态栏
        getWindow().setStatusBarColor(Color.argb(255, 0, 167, 207));
        mImgBack = (ImageView) findViewById(R.id.title_btn_back);
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAppName = findViewById(R.id.tv_app_name);
        //设置字体
        Typeface tf2 = Typeface.createFromAsset(getAssets(), "fonts/fzse.ttf");
        mAppName.setTypeface(tf2);
    }
}
