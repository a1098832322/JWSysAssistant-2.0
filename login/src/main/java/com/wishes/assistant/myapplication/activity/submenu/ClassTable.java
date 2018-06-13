package com.wishes.assistant.myapplication.activity.submenu;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.majeur.cling.Cling;
import com.majeur.cling.ClingManager;
import com.majeur.cling.ViewTarget;
import com.wishes.assistant.Constants;
import com.wishes.assistant.customs.dialog.ClassTableSelectorDialog;
import com.wishes.assistant.customs.dialog.ReLoginDialog;
import com.wishes.assistant.io.image.SimpleSampleActivity;
import com.wishes.assistant.io.sharepreference.SharePreferenceUtils;
import com.wishes.assistant.io.sqlite.SQLiteHelper;
import com.wishes.assistant.myapplication.R;
import com.wishes.assistant.net.OKHttpUtils;
import com.wishes.assistant.net.Upload;
import com.wishes.assistant.net.analysis.DecodeHTML;
import com.wishes.assistant.otherutils.Base64Img;
import com.wishes.assistant.otherutils.UserGuide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import skin.support.app.SkinCompatActivity;

import static android.content.ContentValues.TAG;

/**
 * Created by 郑龙 on 2018/5/6.
 */

public class ClassTable extends SkinCompatActivity implements AAH_FabulousFragment.Callbacks {
    //Android cline
    private ClingManager mClingManager;
    private static String START_TUTORIAL_KEY = "GradeActivityShowedGuide";
    public static final String mContentType = "curriculum";
    private String mXn, mXq, mType;
    private ArrayList<Map<String, String>> params = new ArrayList<>();
    private ClassTableSelectorDialog dialogFrag;
    private LottieAnimationView lottieAnimationView;
    private FloatingActionButton fab;
    private ImageView img;


    private RelativeLayout mLayout;
    private ImageView mImgBack;

    private ClassTable.getData mGetDataTask = null;


    //重新登录
    private ClassTable.ReLoginTask mReloginTask = null;

    //是否点击显示菜单按钮
    private boolean isclicked = true;


    public Handler refreshUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toasty.error(getApplicationContext(), "请求失败，请重试或重新登陆！", Toast.LENGTH_SHORT, true).show();
                    ReLoginDialog dialog = new ReLoginDialog(ClassTable.this, new ReLoginDialog.DialogCallBackListener() {
                        @Override
                        public void callBack(String msg) {
                            //回调时登录
                            SharedPreferences sp = getApplicationContext().getSharedPreferences("student", Context.MODE_PRIVATE);
                            String number = sp.getString("number", "");
                            String passwd = sp.getString("passwd", "");
                            String code = msg;
                            showAnimation(true);
                            mReloginTask = new ClassTable.ReLoginTask(number, passwd, code);
                            mReloginTask.execute((Void) null);

                        }
                    });
                    dialog.setCanceledOnTouchOutside(false);//触摸dialog外部范围不可取消
                    dialog.show();
                    break;
                case 1:
                    Bitmap bitmap = (Bitmap) msg.obj;

                    final String base64Image = Base64Img.getImgStr(bitmap);
                    Log.d("base64", base64Image.length() + "\t" + base64Image);

                    SharedPreferences share = getApplicationContext().getSharedPreferences("student", Context.MODE_PRIVATE);

                    final String name = share.getString("name", null);
                    final String number = share.getString("number", null);
                    final String passwd = share.getString("passwd", null);

                    //将图片发送到服务器
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Upload.uploadPicture(number, passwd, name, mContentType, mXn, mXq, mType, base64Image);
                        }
                    }).start();

                    img.setImageBitmap(bitmap);
                    //显示ImageView，隐藏Lottie
                    showAnimation(false);
                    break;
                case 2:
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
                    String time = new String(sdf.format(new Date()));

                    //將刷新时间存入数据库
                    SQLiteHelper helper = SQLiteHelper.getInstance(getApplicationContext());
                    helper.insert("REFRESHTIMETABLE", "REFRESHTIME", "1", time);
                    break;
            }
        }
    };

    public ArrayList<Map<String, String>> getParams() {
        return params;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        init();//初始化UI界面

        mImgBack = (ImageView) findViewById(R.id.title_btn_back);
        final Toolbar mTitleBar = (Toolbar) findViewById(R.id.toolbar);

        /*显示切换动画*/
        mLayout = (RelativeLayout) findViewById(R.id.layout_grade);
        mLayout.post(new Runnable() {
            @Override
            public void run() {
                // 圆形动画的x坐标  从左上角
                int cx = mLayout.getRight() - 10;

                //圆形动画的y坐标  位于View的中心
                int cy = mLayout.getBottom();

                //起始大小半径
                float startX = 0f;

                //结束大小半径 大小为图片对角线的一半
                float startY = (float) Math.sqrt(cx * cx + cy * cy);

                AnimatorSet animatorSet = new AnimatorSet();

                //                //原型剪切动画
                Animator animator = ViewAnimationUtils.createCircularReveal(mLayout, cx, cy, startX, startY);

                //在动画开始的地方速率改变比较慢,然后开始加速
                animator.setInterpolator(new AccelerateInterpolator());
                animator.setDuration(600);


                ValueAnimator colorAnim = ObjectAnimator.ofInt(mTitleBar, "backgroundColor", getPrimaryColor(), Color.argb(255, 255, 193, 7));
                colorAnim.setDuration(1600);
                colorAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                colorAnim.setEvaluator(new ArgbEvaluator());

                animatorSet.play(colorAnim).with(animator);
                animatorSet.start();
                //改变状态栏颜色，做成沉浸式状态栏
                getWindow().setStatusBarColor(Color.argb(255, 255, 193, 7));
            }

        });


        //从数据库中取出相应值
        SQLiteHelper helper = SQLiteHelper.getInstance(getApplicationContext());
        String refreshTime = helper.getRefreshTime("REFRESHTIMETABLE", "1");

        dialogFrag = ClassTableSelectorDialog.newInstance();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFrag.setParentFab(fab);
                dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
            }
        });

        //设置ImageView点击事件
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //从ImageView中读取图片,并进行base64编码
                    Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
                    String base64Img = Base64Img.getImgStr(bitmap);
                    //构建文件名称
                    String bitName = mXn + mXq + mType;
                    //构造来源表明
                    String fromTable = "ClassTable";

                    //將刷新时间存入数据库
                    SQLiteHelper helper = SQLiteHelper.getInstance(getApplicationContext());
                    helper.insertImgToDb(fromTable, bitName, base64Img);

                    Intent intent = new Intent(ClassTable.this, SimpleSampleActivity.class);
                    intent.putExtra("bitmapName", bitName);
                    intent.putExtra("from", fromTable);
                    startActivity(intent);
                } catch (Exception e) {
                    return;
                }
            }
        });


        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        if (UserGuide.isFirstStart(ClassTable.this, START_TUTORIAL_KEY)) {
            //当是第一次启动Activity时
            mClingManager = new ClingManager(this);

            // When no Target is set, Target.NONE is used
            mClingManager.addCling(new Cling.Builder(this)
                    .setTitle("课表查询")
                    .setContent("这是用于查询课表的界面。点击右下角菜单按钮，选择学年、学期以及格式即可查询。(当Cookie过期时你只需要输入验证码即可重新登录。)")
                    .build());

            mClingManager.addCling(new Cling.Builder(this)
                    .setTitle("Tips")
                    .setContent("点击这里可以展开菜单，在选择学年、学期和格式后，你会看到你的成绩图片。(当Cookie过期时你只需要输入验证码即可重新登录。)")
                    .setMessageBackground(getResources().getColor(R.color.btn_class))
                    .setTarget(new ViewTarget(this, R.id.fab))
                    .build());
            mClingManager.addCling(new Cling.Builder(this)
                    .setTitle("Tips")
                    .setContent("在这里你会看到你的课表的图片。点击图片可以查看详情或保存哟~")
                    .setMessageBackground(getResources().getColor(R.color.btn_class))
                    .setTarget(new ViewTarget(this, R.id.grade_body))
                    .build());
            mClingManager.addCling(new Cling.Builder(this)
                    .setTitle("Tips")
                    .setContent("点击这里可以返回主界面")
                    .setMessageBackground(getResources().getColor(R.color.btn_class))
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
                    if (position == 3) {
                        //Toasty.success(Grade.this, "真棒！居然这么快就学会了！", Toast.LENGTH_SHORT, false).show();
                        mClingManager = null;
                        UserGuide.changeActivityStartTimes(ClassTable.this, START_TUTORIAL_KEY);
                    }
                }
            });

            mClingManager.start();

        }
        super.onResume();
    }

    @Override
    public void onResult(Object result) {
        if (result != null) {
            params = (ArrayList<Map<String, String>>) result;
            for (Map<String, String> map : params
                    ) {
                mXn = map.get("xn");
                mXq = map.get("xq");
                mType = map.get("type");

            }

            //网络请求
            if (!SharePreferenceUtils.isLoginJWSystem(getApplicationContext())) {
                Toasty.error(getApplicationContext(), "你还没有登录，请先登录！", Toast.LENGTH_SHORT, true).show();
                img.setImageBitmap(null);
                return;
            } else {
                //隐藏主Image，播放动画
                //Toasty.info(getApplicationContext(), mXn + "\t" + mXq + "\t" + mType, Toast.LENGTH_SHORT, true).show();
                showAnimation(true);
                doGet();
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
            img.setVisibility(View.GONE);
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.setAnimation(R.raw.loading);
            lottieAnimationView.playAnimation();
        } else {
            img.setVisibility(View.VISIBLE);
            lottieAnimationView.setVisibility(View.GONE);
        }
    }

    /**
     * 显示空动画
     */
    private void showEmptyAnimation() {
        img.setVisibility(View.GONE);
        lottieAnimationView.setVisibility(View.VISIBLE);
        lottieAnimationView.setAnimation(R.raw.empty);
        lottieAnimationView.playAnimation();
    }

    private int getPrimaryColor() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        int color = a.getColor(0, 0);
        a.recycle();

        return color;
    }

    private void doGet() {
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
            mGetDataTask = new ClassTable.getData(mXn, mXq, mType);
            mGetDataTask.execute((Void) null);
        }
    }

    private void init() {
        fab = findViewById(R.id.fab);
        lottieAnimationView = findViewById(R.id.lottie_view);
        img = (ImageView) findViewById(R.id.grade_img_gradeimg);
    }


    //异步网络请求
    public class getData extends AsyncTask<Void, Void, Boolean> {
        private final String xn;
        private final String xq;
        private final String rad;

        getData(String _xn, String _xq, String _rad) {
            this.xn = _xn;
            this.xq = _xq;
            this.rad = _rad;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (getImgData(Constants.ClassTableUrl, xq, xn, rad)) {
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


        private boolean getImgData(String url, String xq, String xn, String rad) {
            OKHttpUtils utils = OKHttpUtils.getInstanceUtils();
            try {
                String imgUrl = Constants.ClassTableUrl + "xn=" + xn + "&xq=" + xq + "&rad=" + rad;
                Log.d("imgURL:", imgUrl);

                mXn = xn;
                mXq = xq;
                mType = rad;

                utils.asynchronousGet(imgUrl, refreshUIHandler);
                return true;
            } catch (Exception e) {
                Log.e(TAG, "getImgData: 网络请求获取成绩图片异常！", e);
            }
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
            doGet();
        }

        @Override
        protected void onCancelled() {
            mReloginTask = null;
            showEmptyAnimation();
        }
    }
}
