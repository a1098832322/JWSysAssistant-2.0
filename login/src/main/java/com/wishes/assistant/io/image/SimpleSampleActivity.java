/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.wishes.assistant.io.image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.majeur.cling.Cling;
import com.majeur.cling.ClingManager;
import com.majeur.cling.ViewTarget;
import com.wishes.assistant.customs.dependce.OnMatrixChangedListener;
import com.wishes.assistant.customs.dependce.OnPhotoTapListener;
import com.wishes.assistant.customs.dependce.OnSingleFlingListener;
import com.wishes.assistant.customs.dependce.PhotoView;
import com.wishes.assistant.io.sqlite.SQLiteHelper;
import com.wishes.assistant.myapplication.R;
import com.wishes.assistant.myapplication.activity.dto.ActivityTransferTarget;
import com.wishes.assistant.otherutils.UserGuide;

import java.io.IOException;

import es.dmoral.toasty.Toasty;


public class SimpleSampleActivity extends AppCompatActivity {
    //Android cline
    private ClingManager mClingManager;
    private static String START_TUTORIAL_KEY = "SimpleSampleActivityShowedGuide";

    private PhotoView mPhotoView;

    private String name;//文件名
    private Bitmap mBitmap;//用于存放储存用的Bitmap

    private Toast mCurrentToast;

    @Override
    protected void onResume() {
        if (UserGuide.isFirstStart(SimpleSampleActivity.this, START_TUTORIAL_KEY)) {
            //当是第一次启动Activity时
            mClingManager = new ClingManager(this);
            // When no Target is set, Target.NONE is used
            mClingManager.addCling(new Cling.Builder(this)
                    .setTitle("查看图片详情")
                    .setContent("你可在当前界面下放大查看图片细节或者保存图片哟~")
                    .build());

            mClingManager.addCling(new Cling.Builder(this)
                    .setTitle("Tips")
                    .setContent("在这里你会看到你的成绩的图片详情。双击可放大查看。")
                    .setMessageBackground(getResources().getColor(R.color.colorPrimary))
                    .setTarget(new ViewTarget(this, R.id.iv_photo))
                    .build());
            mClingManager.addCling(new Cling.Builder(this)
                    .setTitle("Tips")
                    .setContent("点击左侧箭头可以返回主界面，点击右侧菜单可保存图片(需要先授予存储权限噢~)")
                    .setMessageBackground(getResources().getColor(R.color.colorPrimary))
                    .setTarget(new ViewTarget(this, R.id.appbar))
                    .build());

            mClingManager.setCallbacks(new ClingManager.Callbacks() {
                @Override
                public boolean onClingClick(int position) {
                    // We return false to tell to cling manager that we didn't handle this,
                    // so it can perform the default action (ie. showing the next Cling).
                    // This is the default value returned by super.onClingClick(position), so
                    // in a real project, we would just leave this method unoverriden.
                    return false;
                }

                @Override
                public void onClingShow(int position) {

                }

                @Override
                public void onClingHide(int position) {
                    // Last Cling has been shown, tutorial is ended.
                    if (position == 2) {
                        //Toasty.success(Grade.this, "真棒！居然这么快就学会了！", Toast.LENGTH_SHORT, false).show();
                        mClingManager = null;
                        UserGuide.changeActivityStartTimes(SimpleSampleActivity.this, START_TUTORIAL_KEY);
                    }
                }
            });

            mClingManager.start();

        }
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imginfo_layout);


        //获取Intent
        Intent intent = getIntent();
        //获取图片名称
        name = intent.getStringExtra("bitmapName");
        //获取传入Activity名称
        String fromActivity = intent.getStringExtra("from");

        SQLiteHelper helper = SQLiteHelper.getInstance(getApplicationContext());
        //获取文件名和图片内容
        ActivityTransferTarget target = helper.getSavedImg(fromActivity, name);
        mBitmap = target.getBitmap();

        //读取完就删掉缓存
        helper.deleteImgCache(fromActivity, name);

        //默认为默认App主色调的颜色
        int color = Color.argb(255, 106, 96, 169);
        //根据不同的传入Activity，设置不同的标题颜色
        switch (fromActivity) {
            case "Grade":
                color = Color.argb(255, 98, 133, 199);
                break;
            case "Test":
                color = Color.argb(255, 203, 117, 117);
                break;
            case "ClassTable":
                color = Color.argb(255, 255, 193, 7);
                break;
        }
        //设置Titlebar和沉浸式状态栏颜色
        getWindow().setStatusBarColor(color);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("查看详情");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //保存图片到本地
                if (item.getItemId() == R.id.menu_zoom_saver) {

                    try {
                        SaveBitmap.save(name, mBitmap);
                        Toasty.success(getApplicationContext(), "保存成功！文件保存于：\n/sdcard/MyJwSysInfo/"
                                + name + ".png", Toast.LENGTH_LONG, true).show();
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toasty.error(getApplicationContext(), "保存失败，请授予App存储权限后再重试！", Toast.LENGTH_SHORT, true).show();
                        return false;
                    }

                }
                return false;
            }
        });
        toolbar.setBackgroundColor(color);

        mPhotoView = (PhotoView) findViewById(R.id.iv_photo);

        //从Intent中取出值
        //        byte[] bis = target.getBitmap();
        //        mBitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);

        Drawable bitmap = new BitmapDrawable(mBitmap);
        mPhotoView.setImageDrawable(bitmap);

        // Lets attach some listeners, not required though!
        mPhotoView.setOnMatrixChangeListener(new MatrixChangeListener());
        mPhotoView.setOnPhotoTapListener(new PhotoTapListener());
        mPhotoView.setOnSingleFlingListener(new SingleFlingListener());
    }

    private class PhotoTapListener implements OnPhotoTapListener {

        @Override
        public void onPhotoTap(ImageView view, float x, float y) {
            float xPercentage = x * 100f;
            float yPercentage = y * 100f;

            //不需要显示触摸位置
            //showToast(String.format(PHOTO_TAP_TOAST_STRING, xPercentage, yPercentage, view == null ? 0 : view.getId()));
        }
    }

    private void showToast(CharSequence text) {
        if (mCurrentToast != null) {
            mCurrentToast.cancel();
        }

        mCurrentToast = Toast.makeText(SimpleSampleActivity.this, text, Toast.LENGTH_SHORT);
        mCurrentToast.show();
    }

    private class MatrixChangeListener implements OnMatrixChangedListener {

        @Override
        public void onMatrixChanged(RectF rect) {
            //不需要显示触摸位置
            //mCurrMatrixTv.setText(rect.toString());
        }
    }

    private class SingleFlingListener implements OnSingleFlingListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
