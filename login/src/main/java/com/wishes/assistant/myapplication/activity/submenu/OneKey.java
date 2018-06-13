package com.wishes.assistant.myapplication.activity.submenu;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.majeur.cling.ClingManager;
import com.wishes.assistant.Constants;
import com.wishes.assistant.customs.dialog.OnekeySelectorDialog;
import com.wishes.assistant.customs.dialog.ReLoginDialog;
import com.wishes.assistant.customs.listview.adapter.MyCustomsBaseAdapter;
import com.wishes.assistant.customs.listview.adapter.SwingRightInAnimationAdapter;
import com.wishes.assistant.io.sharepreference.SharePreferenceUtils;
import com.wishes.assistant.io.sqlite.SQLiteHelper;
import com.wishes.assistant.myapplication.R;
import com.wishes.assistant.net.OKHttpUtils;
import com.wishes.assistant.net.analysis.DecodeHTML;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import skin.support.app.SkinCompatActivity;

import static android.content.ContentValues.TAG;


/**
 * Created by 10988 on 2017/4/26.
 */

public class OneKey extends SkinCompatActivity implements AAH_FabulousFragment.Callbacks {
    //Android cline
    private ClingManager mClingManager;
    private static String START_TUTORIAL_KEY = "GradeActivityShowedGuide";

    private ArrayList<Map<String, String>> params = new ArrayList<>();
    private LottieAnimationView lottieAnimationView;
    private FloatingActionButton fab;
    private OnekeySelectorDialog dialogFrag;

    private String mXn, mXq;

    private ListView listView = null;
    private TextView mRefresh = null;
    private View mRealTitleView;

    private ImageView mImgBack;

    private View mProgressView;
    private View mGradeBodyView;
    private List<HashMap<String, String>> data = new ArrayList<>();
    private RelativeLayout mLayout;

    //是否点击显示菜单按钮
    private boolean isclicked = true;

    //网络
    private getData mGetDataTask = null;
    //重新登录
    private ReLoginTask mReloginTask = null;


    //Hanlder
    public Handler refreshUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toasty.error(getApplicationContext(), "请求失败，请重试或重新登陆！", Toast.LENGTH_SHORT, true).show();
                    ReLoginDialog dialog = new ReLoginDialog(OneKey.this, new ReLoginDialog.DialogCallBackListener() {
                        @Override
                        public void callBack(String msg) {
                            //回调时登录
                            SharedPreferences sp = getApplicationContext().getSharedPreferences("student", Context.MODE_PRIVATE);
                            String number = sp.getString("number", "");
                            String passwd = sp.getString("passwd", "");
                            String code = msg;
                            showAnimation(true);
                            mReloginTask = new ReLoginTask(number, passwd, code);
                            mReloginTask.execute((Void) null);

                        }
                    });
                    dialog.setCanceledOnTouchOutside(false);//触摸dialog外部范围不可取消
                    dialog.show();
                    break;
                case 1:
                    //ListView绑定数据源
                    //先清空上次数据
                    data = new ArrayList<>();
                    for (String s : (List<String>) msg.obj) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("text", s);
                        data.add(map);
                    }

                    //绑定数据到控件
                    MyCustomsBaseAdapter adapter = new MyCustomsBaseAdapter(OneKey.this, data, R.layout.result_list);

                    //设置动画
                    SwingRightInAnimationAdapter swingRightInAnimationAdapter = new SwingRightInAnimationAdapter(adapter);
                    swingRightInAnimationAdapter.setListView(listView);
                    listView.setAdapter(swingRightInAnimationAdapter);

                    showAnimation(false);
                    //显示信息
                    Toasty.success(getApplicationContext(), "恭喜，一键给老师好评成功！", Toast.LENGTH_SHORT, true).show();
                    break;
                case 2:
                    SimpleDateFormat sdf = new SimpleDateFormat("YYYY年MM月dd日HH:mm:ss");
                    String time = new String(sdf.format(new Date()));


                    //將刷新时间存入数据库
                    SQLiteHelper helper = SQLiteHelper.getInstance(getApplicationContext());
                    helper.insert("REFRESHTIMETABLE", "REFRESHTIME", "3", time);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onekey);
        init();
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        final Toolbar mTitleBar = (Toolbar) findViewById(R.id.toolbar);
        /*显示切换动画*/
        mLayout = (RelativeLayout) findViewById(R.id.layout_grade);
        mLayout.post(new Runnable() {
            @Override
            public void run() {
                // 圆形动画的x坐标  位于View的中心
                int cx = 10;

                //圆形动画的y坐标  位于View的中心
                int cy = mLayout.getBottom();

                //起始大小半径
                float startX = 0f;

                //结束大小半径 大小为图片对角线的一半
                float startY = (float) Math.sqrt(cx * cx + cy * cy);
                AnimatorSet animatorSet = new AnimatorSet();

                //原型剪切动画
                Animator animator = ViewAnimationUtils.createCircularReveal(mLayout, cx, cy, startX, startY);

                //在动画开始的地方速率改变比较慢,然后开始加速
                animator.setInterpolator(new AccelerateInterpolator());
                animator.setDuration(600);


                ValueAnimator colorAnim = ObjectAnimator.ofInt(mTitleBar, "backgroundColor", getPrimaryColor(), Color.argb(255, 92, 171, 125));
                colorAnim.setDuration(1600);
                colorAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                colorAnim.setEvaluator(new ArgbEvaluator());

                animatorSet.play(colorAnim).with(animator);
                animatorSet.start();
                getWindow().setStatusBarColor(Color.argb(255, 92, 171, 125));
            }
        });


        //从数据库中取出相应值
        SQLiteHelper helper = SQLiteHelper.getInstance(getApplicationContext());
        String refreshTime = helper.getRefreshTime("REFRESHTIMETABLE", "1");

        dialogFrag = OnekeySelectorDialog.newInstance();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFrag.setParentFab(fab);
                dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
            }
        });

        mImgBack = (ImageView) findViewById(R.id.title_btn_back);

        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void init() {
        fab = findViewById(R.id.fab);
        lottieAnimationView = findViewById(R.id.lottie_view);
        mImgBack = (ImageView) findViewById(R.id.title_btn_back);
        mProgressView = findViewById(R.id.grade_progress);
        mGradeBodyView = findViewById(R.id.grade_body);
        mRealTitleView = findViewById(R.id.grade_body_title_realtitle);
        listView = (ListView) findViewById(R.id.one_list);
        mRefresh = (TextView) findViewById(R.id.grade_tv_refresh);
    }

    public ArrayList<Map<String, String>> getParams() {
        return params;
    }

    private int getPrimaryColor() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        int color = a.getColor(0, 0);
        a.recycle();

        return color;
    }


    @Override
    public void onResult(Object result) {
        if (result != null) {
            params = (ArrayList<Map<String, String>>) result;
            for (Map<String, String> map : params
                    ) {
                mXn = map.get("xn");
                mXq = map.get("xq");


            }

            //网络请求
            if (!SharePreferenceUtils.isLoginJWSystem(getApplicationContext())) {
                Toasty.error(getApplicationContext(), "你还没有登录，请先登录！", Toast.LENGTH_SHORT, true).show();
                return;
            } else {
                //隐藏主Image，播放动画
                showAnimation(true);
                doPost();
            }
        }
    }

    /**
     * 是否显示加载动画
     *
     * @param startOrNot
     */
    private void showAnimation(boolean startOrNot) {
        if (startOrNot) {
            listView.setVisibility(View.GONE);
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.setAnimation(R.raw.loading);
            lottieAnimationView.playAnimation();
        } else {
            listView.setVisibility(View.VISIBLE);
            lottieAnimationView.setVisibility(View.GONE);
        }
    }

    /**
     * 显示空动画
     */
    private void showEmptyAnimation() {
        listView.setVisibility(View.GONE);
        lottieAnimationView.setVisibility(View.VISIBLE);
        lottieAnimationView.setAnimation(R.raw.empty);
        lottieAnimationView.playAnimation();
    }

    private void doPost() {
        if (mGetDataTask != null) {
            return;
        }

        boolean cancle = false;
        SharedPreferences sp = getApplicationContext().getSharedPreferences("student", Context.MODE_PRIVATE);
        String name = sp.getString("name", null);
        if (name == null) {
            cancle = true;
        }


        if (cancle) {
            showEmptyAnimation();
        } else {
            showAnimation(true);
            mGetDataTask = new getData(mXn, mXq);
            mGetDataTask.execute((Void) null);
        }
    }

    //异步网络请求
    public class getData extends AsyncTask<Void, Void, Boolean> {
        private final String xn;
        private final String xq;
        private final String moshi = "0";
        private final String type = "4";

        getData(String _xn, String _xq) {
            this.xn = _xn;
            this.xq = _xq;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (getScoData(Constants.OneKeyUrl, xq, xn, moshi, type)) {
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mGetDataTask = null;
            showAnimation(false);

            Message msg = Message.obtain();
            msg.what = 2;
            refreshUIHandler.sendMessage(msg);

        }

        @Override
        protected void onCancelled() {
            mGetDataTask = null;
            showEmptyAnimation();
        }


        private boolean getScoData(String url, String xq, String xn, String sj, String type) {
            OkHttpClient client = OKHttpUtils.getInstanceClient();
            try {
                RequestBody formBody = new FormBody.Builder().add("xn", xn)
                        .add("xq", xq).add("moshi", moshi).add("type", type).build();

                Request request = new Request.Builder().addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8").post
                        (formBody).url(url)
                        .build();
                //执行POST请求
                Response response = client.newCall(request).execute();

                //获取返回数据
                String result = response.body().string();
                DecodeHTML decoder = new DecodeHTML();
                List<String> name = decoder.getClassName(result);
                if (name.size() != 0) {
                    //如果解析结果不为空
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = name;
                    refreshUIHandler.sendMessage(msg);
                    return true;
                }

            } catch (Exception e) {
                Log.e(TAG, "getScoData: 网络请求获取一键评教异常！", e);
            }
            Message msg = Message.obtain();
            msg.what = 0;
            refreshUIHandler.sendMessage(msg);
            return false;
        }
    }

    //重新登录的网络请求类
    public class ReLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String mNumber;
        private final String mPassword;
        private final String mCodeNumber;

        public ReLoginTask(String number, String password, String code) {
            mNumber = number;
            mPassword = password;
            mCodeNumber = code;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                //再附带Cookies登陆
                OKHttpUtils post = OKHttpUtils.getInstanceUtils();
                String result = post.postFromParameters(Constants.LoginUrl, mNumber, mPassword, mCodeNumber);
                DecodeHTML decode = new DecodeHTML();
                if (decode.isLoginSuccess(result) == null) {
                    return false;
                }
            } catch (Exception e) {
                Log.e("TAG", "doInBackground: ", e);
                return false;
            }


            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mReloginTask = null;
            showAnimation(false);
            doPost();
        }

        @Override
        protected void onCancelled() {
            mReloginTask = null;
            showEmptyAnimation();
        }
    }
}
