package com.wishes.assistant.myapplication.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.majeur.cling.Cling;
import com.majeur.cling.ClingManager;
import com.majeur.cling.ViewTarget;
import com.wishes.assistant.customs.interpolator.JellyInterpolator;
import com.wishes.assistant.io.sharepreference.SharePreferenceUtils;
import com.wishes.assistant.myapplication.R;
import com.wishes.assistant.net.LibraryCrawler;
import com.wishes.assistant.otherutils.UserGuide;

import es.dmoral.toasty.Toasty;
import skin.support.app.SkinCompatActivity;

/**
 * 用于登录图书馆的登录界面
 * Created by 郑龙 on 2018/5/6.
 */

public class LibraryLoginPageActivity extends SkinCompatActivity implements View.OnClickListener {
    //Android cline
    private ClingManager mClingManager;
    private static String START_TUTORIAL_KEY = "LibraryLoginPageActivityShowedGuide";

    //Broadcast接收器
    public static final String action = "Refresh UI";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private TextView mBtnLogin, mTitle, mFirstText, mSubText;
    private ImageView mBtnBack, mImgCode;
    private EditText mAccount, mPassWd, mCaptcha;
    private LottieAnimationView animationView;

    private View progress;

    private View mInputLayout;

    private float mWidth, mHeight;

    private RelativeLayout mLayout;


    /**
     * 登录状态Handler
     */
    public Handler loginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //Toast.makeText(LoginPage.this, "登陆失败，请核对用户名密码以及验证码后再重新登录", Toast.LENGTH_LONG).show();
                    Toasty.error(getApplicationContext(), "登陆失败，请核对用户名密码以及验证码后再重新登录", Toast.LENGTH_LONG, true).show();
                    break;
                case 1:
                    // Toast.makeText(LoginPage.this, "登陆成功", Toast.LENGTH_SHORT).show();
                    Toasty.success(getApplicationContext(), "登陆成功!", Toast.LENGTH_SHORT, true).show();
                    break;
            }
        }
    };

    /**
     * 用于获取并显示验证码的handler
     */
    public final Handler imgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Log.e("TAG", "hanlder获取验证码失败！");
                    break;
                case 1:
                    mImgCode.setImageBitmap((Bitmap) msg.obj);
                    break;
            }
        }
    };


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        initView();

        /*显示切换动画*/
        mLayout = (RelativeLayout) findViewById(R.id.login_main);
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
                animator.setInterpolator(new LinearInterpolator());
                animator.setDuration(600);


                animatorSet.play(animator);
                animatorSet.start();
                //改变状态栏颜色，做成沉浸式状态栏
                getWindow().setStatusBarColor(Color.argb(255, 234, 180, 94));
            }

        });

        //验证码加载时自动刷新
        LibraryCrawler.getValidateCode(imgHandler);

        //显示底部动画
        animationView.loop(true);
        animationView.setAnimation("vr_sickness.json");
        animationView.playAnimation();

    }

    /**
     * 用于显示帮助
     */
    @Override
    protected void onResume() {
        if (UserGuide.isFirstStart(LibraryLoginPageActivity.this, START_TUTORIAL_KEY)) {
            //当是第一次启动Activity时
            mClingManager = new ClingManager(this);

            // When no Target is set, Target.NONE is used
            mClingManager.addCling(new Cling.Builder(this)
                    .setTitle("图书馆系统登录界面")
                    .setContent("这是用于登录图书馆系统的登录界面。在这里输入正确的学号，密码和验证码之后，点击登录即可完成登录。")
                    .build());

            mClingManager.addCling(new Cling.Builder(this)
                    .setTitle("Tips")
                    .setContent("点击这里可以返回主界面")
                    .setMessageBackground(getResources().getColor(R.color.colorPrimary))
                    .setTarget(new ViewTarget(this, R.id.login_title_back))
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
                        Toasty.success(LibraryLoginPageActivity.this, "真棒！居然这么快就学会了！", Toast.LENGTH_SHORT, false).show();
                        mClingManager = null;
                    }
                }
            });

            mClingManager.start();
            UserGuide.changeActivityStartTimes(LibraryLoginPageActivity.this, START_TUTORIAL_KEY);
        }

        super.onResume();
    }

    /**
     * 初始化绑定控件
     */
    private void initView() {
        mBtnBack = (ImageView) findViewById(R.id.login_title_back);
        mImgCode = (ImageView) findViewById(R.id.img_code);
        mBtnLogin = (TextView) findViewById(R.id.main_btn_login);
        progress = findViewById(R.id.layout_progress);
        mInputLayout = findViewById(R.id.input_layout);
        animationView = findViewById(R.id.animator_view);

        mBtnLogin.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mImgCode.setOnClickListener(this);

        mTitle = (TextView) findViewById(R.id.login_title_text);
        mTitle.setText("图书馆登录");

        mAccount = (EditText) findViewById(R.id.login_form_account);
        mAccount.setHint("请输入学号");
        mPassWd = (EditText) findViewById(R.id.login_form_pass);
        mPassWd.setHint("请输入密码");
        mCaptcha = (EditText) findViewById(R.id.login_form_captcha);
        mCaptcha.setHint("请输入验证码");

        mFirstText = (TextView) findViewById(R.id.login_page_first_text);
        mFirstText.setText("江汉大学图书馆");
        mSubText = (TextView) findViewById(R.id.login_page_sub_text);
        mSubText.setText("旧书不厌百回读，熟读精思子自知。——苏轼");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.login_title_back:
                finish();//关闭当前页
                break;
            case R.id.main_btn_login:
                // 计算出控件的高与宽
                mWidth = mBtnLogin.getMeasuredWidth();
                mHeight = mBtnLogin.getMeasuredHeight();
                attemptLogin();

                break;
            case R.id.img_code:
                LibraryCrawler.getValidateCode(imgHandler);
                break;
        }

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mAccount.setError(null);
        mPassWd.setError(null);
        mCaptcha.setError(null);

        // Store values at the time of the login attempt.
        String number = mAccount.getText().toString();
        String password = mPassWd.getText().toString();
        String code = mCaptcha.getText().toString();

        boolean cancel = false;
        View focusView = null;
        Animation anim = AnimationUtils.loadAnimation(LibraryLoginPageActivity.this, R.anim.myanim);
        if (!isCodeValid(code)) {
            mCaptcha.setAnimation(anim);
            mCaptcha.setError(getString(R.string.error_invalid_code));
            focusView = mCaptcha;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
            mPassWd.setAnimation(anim);
            mPassWd.setError(getString(R.string.error_invalid_password));
            focusView = mPassWd;
            cancel = true;
        }
        // Check for a valid school number.
        if (!isNumberValid(number)) {
            mAccount.setAnimation(anim);
            mAccount.setError(getString(R.string.error_invalid_number));
            focusView = mAccount;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            inputAnimator(mInputLayout, mWidth, mHeight);//显示动画
            //开始网络任务
            mAuthTask = new UserLoginTask(number, password, code);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isCodeValid(String code) {
        return code.length() == 4 ? true : false;
    }

    private boolean isNumberValid(String number) {
        return number.length() == 12 ? true : false;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4 ? true : false;
    }

    /**
     * 异步登录请求
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String mNumber;
        private final String mPassword;
        private final String mCodeNumber;

        public UserLoginTask(String number, String password, String code) {
            mNumber = number;
            mPassword = password;
            mCodeNumber = code;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                //再附带Cookies登陆
                boolean isLoginSuccess = LibraryCrawler.loginLibrary(mNumber, mPassword, mCodeNumber);
                if (!isLoginSuccess) {
                    //如果登录不成功
                    Message message = Message.obtain();
                    message.what = 0;
                    loginHandler.sendMessage(message);
                    return false;
                }
                //存入账号密码数据到SharePreference中
                SharePreferenceUtils.SaveData(SharePreferenceUtils.ACCOUNT_TYPE.LIBRARY, getApplicationContext(), mNumber, mPassword, null);

                //返回登陆成功的消息给handler
                Message message = Message.obtain();
                message.what = 1;
                loginHandler.sendMessage(message);
            } catch (Exception e) {
                Log.e("TAG", "doInBackground: ", e);
                Message message = Message.obtain();
                message.what = 0;
                loginHandler.sendMessage(message);
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            onCancleAnimation();//取消显示进度条动画

            if (success) {
                SharedPreferences sp = getApplicationContext().getSharedPreferences("student", Context.MODE_PRIVATE);
                Intent intent = new Intent(action);
                intent.putExtra("name", sp.getString("name", "null"));
                intent.putExtra("number", sp.getString("libnumber", "null"));
                sendBroadcast(intent);
                finish();
            } else {
                mPassWd.setError(getString(R.string.error_login_error));
                mPassWd.requestFocus();
                //并刷新验证码
                LibraryCrawler.getValidateCode(imgHandler);

                //通知登录Handler刷新UI
                Message message = Message.obtain();
                message.what = 0;
                loginHandler.sendMessage(message);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            onCancleAnimation();//取消显示进度条动画
        }
    }


    /**
     * 以下是动画效果
     */


    /**
     * 输入框的动画效果
     *
     * @param view 控件
     * @param w    宽
     * @param h    高
     */
    private void inputAnimator(final View view, float w, float h) {

        AnimatorSet set = new AnimatorSet();

        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view
                        .getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout,
                "scaleX", 1f, 0.5f);
        set.setDuration(400);
        set.setInterpolator(new AccelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                /**
                 * 动画结束后，先显示加载的动画，然后再隐藏输入框
                 */

                progressAnimator(true);
                mInputLayout.setVisibility(View.INVISIBLE);
                mBtnLogin.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });

    }


    /**
     * 出现进度动画
     *
     * @param start
     */
    private void progressAnimator(boolean start) {
        if (start) {
            progress.setVisibility(View.VISIBLE);
            PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                    0.5f, 1f);
            PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                    0.5f, 1f);
            ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(progress,
                    animator, animator2);
            animator3.setDuration(1000);
            animator3.setInterpolator(new JellyInterpolator());
            animator3.start();
        } else {
            progress.setVisibility(View.GONE);
        }

    }

    /**
     * 当取消时显示的动画效果
     */
    private void onCancleAnimation() {
        //先隐藏圈儿
        progress.setVisibility(View.GONE);
        //显示输入部分
        mInputLayout.setVisibility(View.VISIBLE);
        /**
         * 取消动画后显示控件
         */
        AnimatorSet set = new AnimatorSet();
        ValueAnimator animator = ValueAnimator.ofFloat(0, mWidth);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mInputLayout
                        .getLayoutParams();
                params.leftMargin = (int) mWidth - (int) value;
                params.rightMargin = (int) mWidth - (int) value;
                mInputLayout.setLayoutParams(params);
            }
        });
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout,
                "scaleX", 0.5f, 1.0f);
        set.setDuration(200);
        set.setInterpolator(new AccelerateInterpolator());
        set.playTogether(animator2, animator);
        set.start();
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                /**
                 * 动画结束后，让输入框弹一下~
                 */
                PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                        0.8f, 1f);
                PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                        0.8f, 1f);
                ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(mInputLayout,
                        animator, animator2);
                animator3.setDuration(1000);
                animator3.setInterpolator(new JellyInterpolator());
                animator3.start();

                mBtnLogin.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
    }
}
