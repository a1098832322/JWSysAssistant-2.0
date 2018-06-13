package com.wishes.assistant.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jetradar.desertplaceholder.DesertPlaceholder;
import com.majeur.cling.Cling;
import com.majeur.cling.ClingManager;
import com.majeur.cling.ViewTarget;
import com.wishes.assistant.io.sharepreference.SharePreferenceUtils;
import com.wishes.assistant.myapplication.R;
import com.wishes.assistant.net.download.NewPackageDownloadService;
import com.wishes.assistant.otherutils.RedPacketChecker;
import com.wishes.assistant.otherutils.UpdateAPKChecker;
import com.wishes.assistant.otherutils.UserGuide;
import com.yasic.library.particletextview.View.ParticleTextView;

import cn.refactor.lib.colordialog.PromptDialog;
import es.dmoral.toasty.Toasty;
import skin.support.app.SkinCompatActivity;


/**
 * Created by 郑龙 on 2017/4/28.
 */

public class About extends SkinCompatActivity {
    //Android cline
    private ClingManager mClingManager;
    private static String START_TUTORIAL_KEY = "AboutActivityShowedGuide";
    private ImageView mImgBack;
    private TextView mAppVersion;
    private ParticleTextView particleTextView;
    private DesertPlaceholder mPlaceholder;

    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //不用升级
                    new PromptDialog(About.this)
                            .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                            .setAnimationEnable(true)
                            .setTitleText("恭喜！")
                            .setContentText("你已安装最新版！")
                            .setPositiveListener("确定", new PromptDialog.OnPositiveListener() {
                                @Override
                                public void onClick(PromptDialog dialog) {
                                    dialog.dismiss();
                                }
                            }).show();
                    break;
                case 1:
                    //需要升级
                    UpdateAPKChecker.Version version = UpdateAPKChecker.getmVersion();
                    new PromptDialog(About.this)
                            .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                            .setAnimationEnable(true)
                            .setTitleText("更新提示")
                            .setContentText("版本：\t" + version.getVersion() + "\n\n" + version.getContent() + "\n\n更新时间：\t" + version
                                    .getUpdateTime())
                            .setPositiveListener("更新", new PromptDialog.OnPositiveListener() {
                                @Override
                                public void onClick(PromptDialog dialog) {
                                    Intent service = new Intent(About.this, NewPackageDownloadService.class);
                                    startService(service);
                                    Toasty.info(About.this, "已开始更新，稍后将进行自动安装", Toast.LENGTH_SHORT, true).show();
                                    dialog.dismiss();
                                }
                            })
                            .show();


                    break;
            }
        }
    };

    private Handler redPacketHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 521:
                    //我比较喜欢这个数字~
                    RedPacketChecker.RedPacket redPacket = (RedPacketChecker.RedPacket) msg.obj;

                    if (redPacket != null) {
                        //如果响应体不为空
                        new PromptDialog(About.this)
                                .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                                .setAnimationEnable(true)
                                .setTitleText(redPacket.getRedPacketName())
                                .setContentText(redPacket.getRedPacketContent() + "\n\n手快有，手慢无！\n" + redPacket.getUpdateTime())
                                .setPositiveListener("我知道啦", new PromptDialog.OnPositiveListener() {
                                    @Override
                                    public void onClick(PromptDialog dialog) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //设置沉浸式状态栏
        getWindow().setStatusBarColor(Color.argb(255, 216, 243, 255));

        mImgBack = (ImageView) findViewById(R.id.title_btn_back);

        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPlaceholder = findViewById(R.id.placeholder);
        mPlaceholder.setOnButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检测是否有更新
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if (UpdateAPKChecker.checkNewestVersion(About.this)) {
                            //如果需要更新
                            Message msg = Message.obtain();
                            msg.what = 1;
                            updateHandler.sendMessage(msg);
                        } else {
                            Message msg = Message.obtain();
                            msg.what = 0;
                            updateHandler.sendMessage(msg);
                        }
                    }
                }).start();
            }
        });

        //检测是否显示红包
        showRedPacket();

        TextView mAppName = findViewById(R.id.tv_app_name);
        //设置字体
        Typeface tf2 = Typeface.createFromAsset(getAssets(), "fonts/fzse.ttf");
        mAppName.setTypeface(tf2);

        mAppVersion = findViewById(R.id.tv_version);
        mAppVersion.setText("当前应用版本：" + UpdateAPKChecker.getAppVersionName());

    }

    /**
     * 显示红包相关
     */
    private void showRedPacket() {

        //判断是否登录教务系统
        if (SharePreferenceUtils.isLoginJWSystem(About.this)) {
            //如果登录
            SharedPreferences share = About.this.getSharedPreferences("student", Context.MODE_PRIVATE);

            final String name = share.getString("name", null);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = Message.obtain();
                    msg.obj = RedPacketChecker.checkRedPacket(About.this, name);
                    msg.what = 521;
                    redPacketHandler.sendMessage(msg);
                }
            }).start();
        } else {
            //否则不显示
            Message msg = Message.obtain();
            msg.what = 0;
            redPacketHandler.sendMessage(msg);
        }

    }

    /**
     * 用于显示帮助
     */
    @Override
    protected void onResume() {
        if (UserGuide.isFirstStart(About.this, START_TUTORIAL_KEY)) {
            //当是第一次启动Activity时
            mClingManager = new ClingManager(this);

            // When no Target is set, Target.NONE is used
            mClingManager.addCling(new Cling.Builder(this)
                    .setTitle("关于")
                    .setContent("这是关于界面，点击下方检测更新按钮可以检测最新版App")
                    .build());

            mClingManager.addCling(new Cling.Builder(this)
                    .setTitle("Tips")
                    .setContent("点击这里可以返回主界面")
                    .setMessageBackground(getResources().getColor(R.color.colorPrimary))
                    .setTarget(new ViewTarget(this, R.id.title_btn_back))
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
                    if (position == 1) {
                        //Toasty.success(About.this, "真棒！居然这么快就学会了！", Toast.LENGTH_SHORT, false).show();
                        mClingManager = null;
                        UserGuide.changeActivityStartTimes(About.this, START_TUTORIAL_KEY);
                    }
                }
            });

            mClingManager.start();

        }

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
