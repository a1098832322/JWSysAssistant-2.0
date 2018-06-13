package com.wishes.assistant.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.majeur.cling.Cling;
import com.majeur.cling.ClingManager;
import com.majeur.cling.ViewTarget;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.wishes.assistant.Constants;
import com.wishes.assistant.customs.viewpager.WrapContentHeightViewPager;
import com.wishes.assistant.io.sharepreference.SharePreferenceUtils;
import com.wishes.assistant.io.sqlite.SQLiteHelper;
import com.wishes.assistant.myapplication.activity.About;
import com.wishes.assistant.myapplication.activity.JWSystemLoginPageActivity;
import com.wishes.assistant.myapplication.fragement.firstmenu.JWSys;
import com.wishes.assistant.myapplication.fragement.firstmenu.Library;
import com.wishes.assistant.myapplication.fragement.firstmenu.Sport;
import com.wishes.assistant.net.download.NewPackageDownloadService;
import com.wishes.assistant.otherutils.CrashChecker;
import com.wishes.assistant.otherutils.UpdateAPKChecker;
import com.wishes.assistant.otherutils.UserGuide;

import java.util.ArrayList;
import java.util.List;

import cn.refactor.lib.colordialog.PromptDialog;
import es.dmoral.toasty.Toasty;
import skin.support.SkinCompatManager;
import skin.support.app.SkinCardViewInflater;
import skin.support.constraint.app.SkinConstraintViewInflater;
import skin.support.design.app.SkinMaterialViewInflater;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //Android cline
    private ClingManager mClingManager;
    private static String START_TUTORIAL_KEY = "MainActivityShowedGuide";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private WrapContentHeightViewPager mViewPager;
    private View view;
    private TextView tvName, tvNumber;
    private List<Fragment> mList = null;
    private FlowingDrawer mDrawer = null;

    private SQLiteDatabase sqlitedb = null;

    //检查并显示登录信息
    private SharedPreferences sp = null;
    private boolean isLoginFlag = false;
    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;

    //Fragment类型
    private enum TYPE {
        EMPTY, NOT_EMPTY
    }

    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //不用升级
                    break;
                case 1:
                    //需要升级
                    UpdateAPKChecker.Version version = UpdateAPKChecker.getmVersion();
                    new PromptDialog(MainActivity.this)
                            .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                            .setAnimationEnable(true)
                            .setTitleText("更新提示")
                            .setContentText("版本：\t" + version.getVersion() + "\n\n" + version.getContent() + "\n\n更新时间：\t" + version
                                    .getUpdateTime())
                            .setPositiveListener("更新", new PromptDialog.OnPositiveListener() {
                                @Override
                                public void onClick(PromptDialog dialog) {
                                    Intent service = new Intent(MainActivity.this, NewPackageDownloadService.class);
                                    startService(service);
                                    Toasty.info(MainActivity.this, "已开始更新，稍后将进行自动安装", Toast.LENGTH_SHORT, true).show();
                                    dialog.dismiss();
                                }
                            })
                            .show();


                    break;
            }
        }
    };

    private Handler CrashHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            //我比较喜欢这个数字~
            CrashChecker.CrashDetail crashDetail = (CrashChecker.CrashDetail) msg.obj;

            if (crashDetail != null) {
                //如果响应体不为空
                new PromptDialog(MainActivity.this)
                        .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                        .setAnimationEnable(true)
                        .setTitleText(crashDetail.getCrashName())
                        .setContentText(crashDetail.getCrashContent() + "\n\n" + crashDetail.getUpdateTime())
                        .setPositiveListener("确定", new PromptDialog.OnPositiveListener() {
                            @Override
                            public void onClick(PromptDialog dialog) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        //设置沉浸式状态栏
        getWindow().setStatusBarColor(Color.argb(255, 0, 167, 207));

        //皮肤相关
        SkinCompatManager.withoutActivity(getApplication())                         // 基础控件换肤初始化
                .addInflater(new SkinMaterialViewInflater())            // material design 控件换肤初始化[可选]
                .addInflater(new SkinConstraintViewInflater())          // ConstraintLayout 控件换肤初始化[可选]
                .addInflater(new SkinCardViewInflater())                // CardView v7 控件换肤初始化[可选]
                .setSkinStatusBarColorEnable(false)                     // 关闭状态栏换肤，默认打开[可选]
                .setSkinWindowBackgroundEnable(false)                   // 关闭windowBackground换肤，默认打开[可选]
                .loadSkin();

        //实例化SharePreferences
        sp = getApplicationContext().getSharedPreferences("student", Context.MODE_PRIVATE);
        //创建数据库
        SQLiteHelper database = SQLiteHelper.getInstance(this);
        sqlitedb = database.getReadableDatabase();// 获取数据库对象

        //检测是否有更新
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (UpdateAPKChecker.checkNewestVersion(MainActivity.this)) {
                    //如果需要更新
                    Message msg = Message.obtain();
                    msg.what = 1;
                    updateHandler.sendMessage(msg);
                }
            }
        }).start();

        //检测服务器通知消息
        showCrashDetail();

        //先获取屏幕高度
        Display display = getWindowManager().getDefaultDisplay();
        int height = display.getHeight();
        Constants.height = height;

        //绑定toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //初始化Fragment的list
        mList = new ArrayList<>();
        //显示UI
        initUI();

        /**
         * Create Navigation View
         */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);


        //动态生成navigationLatout的head
        view = navigationView.getHeaderView(0);
        tvName = (TextView) view.findViewById(R.id.username);
        tvNumber = (TextView) view.findViewById(R.id.usernumber);


        //检测SharedPreferences
        //判断是否登录过
        if (SharePreferenceUtils.isLoginJWSystem(getApplicationContext())) {
            //如果以前登录过则更新UI，显示登录后的界面
            refreshUI();
        }
        //否则则默认显示登录前的界面
        navigationView.setNavigationItemSelectedListener(this);

        IntentFilter filter = new IntentFilter(JWSystemLoginPageActivity.action);
        registerReceiver(receiver, filter);


    }

    /**
     * 显示服务器通知消息
     */
    private void showCrashDetail() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.obj = CrashChecker.checkCrashDetail(MainActivity.this);
                CrashHandler.sendMessage(msg);
            }
        }).start();
    }

    //利用广播刷新UI
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            tvName.setText(intent.getExtras().getString("name"));
            tvNumber.setText(intent.getExtras().getString("number"));
        }
    };

    @Override
    protected void onResume() {
        if (UserGuide.isFirstStart(MainActivity.this, START_TUTORIAL_KEY)) {
            //当是第一次启动Activity时
            mClingManager = new ClingManager(this);

            // When no Target is set, Target.NONE is used
            mClingManager.addCling(new Cling.Builder(this)
                    .setTitle("欢迎使用教务小助手哟！")
                    .setContent("看样子好像你是第一次打开这个App哟，下面我将带你快速熟悉一下软件的操作！(本App需要授予存储和网络权限)")
                    .build());

            mClingManager.addCling(new Cling.Builder(this)
                    .setTitle("Tips")
                    .setContent("这里是任务栏标题,点击最左边或者在这里向右滑动手指可以展开登录菜单")
                    .setMessageBackground(getResources().getColor(R.color.colorPrimary))
                    .setTarget(new ViewTarget(this, R.id.toolbar))
                    .build());
            mClingManager.addCling(new Cling.Builder(this).setTitle("Tips")
                    .setContent("这里是主功能区，当你登录后这里会显示具体的菜单界面")
                    .setMessageBackground(getResources().getColor(R.color.colorPrimary))
                    .setTarget(new ViewTarget(this, R.id.content))
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
                        //Toasty.success(MainActivity.this, "真棒！居然这么快就学会了！", Toast.LENGTH_SHORT, false).show();
                        mClingManager = null;
                        UserGuide.changeActivityStartTimes(MainActivity.this, START_TUTORIAL_KEY);
                    }
                }
            });

            mClingManager.start();

        }
        //刷新ui
        initUI();
        super.onResume();
    }

    /**
     * 按两下返回键退出应用
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
                Toasty.info(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT, true).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        //退出时注销已注册的广播
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    /**
     * 初始化显示UI
     */
    private void initUI() {
        mList.clear();//清空List

        JWSys mJwsysPage = new JWSys();
        Library mLibPage = new Library();
        Sport mSportPage = new Sport();//还没写完

        mList.add(mJwsysPage);
        mList.add(mLibPage);
        mList.add(mSportPage);

        //构建Fragment
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), mList, TYPE.NOT_EMPTY);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (WrapContentHeightViewPager) findViewById(R.id.content);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(1);//设置装载的Fragment数量


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        //        tabLayout.setupWithViewPager(mViewPager);
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    /**
     * 刷新显示UI
     */
    private void refreshUI() {
        String username = null;
        String usernumber = null;

        //设置未登录状态下默认显示的文字
        usernumber = sp.getString("number", null);
        if (usernumber != null) {
            //如果已登陆，则显示姓名和账号，以及隐藏“登录”按钮
            tvName.setText(sp.getString("name", "(●'◡'●)"));
            tvNumber.setText(usernumber);
        } else {
            tvName.setText("(●'◡'●)");
            tvNumber.setText("如果你看到这行小字说明你还没有登录噢");
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = new Intent();
        if (id == R.id.nav_login) {
            if (SharePreferenceUtils.isLoginJWSystem(getApplicationContext())) {
                //Toast.makeText(getApplicationContext(), "你已经登陆过啦！", Toast.LENGTH_SHORT).show();
                Toasty.error(getApplicationContext(), "你已经登陆过啦！请不要重复登录!", Toast.LENGTH_SHORT, true).show();
            } else {
                //跳转页面
                intent.setClass(MainActivity.this, JWSystemLoginPageActivity.class);
                startActivity(intent);
            }


        } else if (id == R.id.nav_logout) {
            SharePreferenceUtils.clearData(SharePreferenceUtils.ACCOUNT_TYPE.JWSYS, getApplicationContext());//清空SharePreference存储信息
            refreshUI();//刷新UI
            initUI();//重绘主界面UI
            Toasty.info(getApplicationContext(), "已注销，请重新登录!", Toast.LENGTH_SHORT, true).show();
        } else if (id == R.id.nav_about) {
            //Toast.makeText(this, "关于方法", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent();
            intent1.setClass(MainActivity.this, About.class);
            startActivity(intent1);
        }
//        else if (id == R.id.nav_help) {
//            Toasty.info(getApplicationContext(), "后台完善中...暂未开放！", Toast.LENGTH_SHORT, true).show();
//            //            Intent intent2 = new Intent();
//            //            intent2.setClass(MainActivity.this, Help.class);
//            //            startActivity(intent2);
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> mList = null;
        boolean misLoginFlag = false;
        TYPE mType;
        int mChildCount = -1;

        public SectionsPagerAdapter(FragmentManager fm, List<Fragment> mFragmentList, TYPE type) {
            super(fm);
            mList = mFragmentList;
            misLoginFlag = isLoginFlag;
            mType = type;
        }

        @Override
        public void notifyDataSetChanged() {
            mChildCount = getCount();
            super.notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            return mList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            // 重写getItemPosition,保证每次获取时都强制重绘UI
            if (mChildCount > 0) {
                mChildCount--;
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            //            if (mType == TYPE.EMPTY) {
            //                return 1;
            //            } else {
            //                return mList.size();
            //            }
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (mType == TYPE.EMPTY) {
                return "";
            } else {
                switch (position) {
                    case 0:
                        return "教务管理系统";
                    case 1:
                        return "图书馆系统";
                    case 2:
                        return "体测查询系统";
                }
            }
            return null;
        }
    }

}
