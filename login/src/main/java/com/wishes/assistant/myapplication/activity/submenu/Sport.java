package com.wishes.assistant.myapplication.activity.submenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.wishes.assistant.Constants;
import com.wishes.assistant.customs.dialog.sportReloginDialog;
import com.wishes.assistant.io.sharepreference.SharePreferenceUtils;
import com.wishes.assistant.myapplication.R;
import com.wishes.assistant.net.OKHttpUtils;
import com.wishes.assistant.net.analysis.DecodeHTML;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by 10988 on 2017/4/27.
 */

public class Sport extends Fragment {
    private View mGradeBodyView;
    private View mProgressView;
    private View mGradeBodyTitleView;
    private View mRealTitleView;
    private View mGradeBodyBtn;
    private Button sender;
    private ImageView arrow;

    private String mNumber = null;
    private String mPasswd = null;

    //是否点击显示菜单按钮
    private boolean isclicked = false;

    private getSportData mGetDataTask = null;
    //重新登录
    private ReLoginSportTask mReloginTask = null;


    public Handler refreshUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getActivity().getApplicationContext(), "请求失败，请重试或重新登陆！", Toast.LENGTH_SHORT).show();
                    SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("student", Context.MODE_PRIVATE);
                    String getnum = sp.getString("number", null);

                    sportReloginDialog dialog = new sportReloginDialog(getActivity(), null, new sportReloginDialog.SportDialogCallBackListener() {
                        @Override
                        public void callBack(String name, String passwd) {
                            //回调时登录
                            showProgress(true);
                            mReloginTask = new ReLoginSportTask(name, passwd);
                            mReloginTask.execute((Void) null);
                        }
                    }, getnum);
                    dialog.setCanceledOnTouchOutside(false);//触摸dialog外部范围不可取消
                    dialog.show();
                    break;
                case 1:
                    Toast.makeText(getActivity().getApplicationContext(), "数据刷新成功！", Toast.LENGTH_SHORT).show();

                    //將刷新时间存入数据库
                    //                    SQLiteHelper helper = SQLiteHelper.getInstance(getActivity().getApplicationContext());
                    //                    helper.insert("REFRESHTIMETABLE", "REFRESHTIME", "2", time);
                    break;
                case 2:
                    Toast.makeText(getActivity().getApplicationContext(), "重新登录成功！", Toast.LENGTH_SHORT).show();
                    doGet();
                    break;
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sport, container, false);
        init(v);

        //开始获取数据
        SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("student", Context.MODE_PRIVATE);
        mNumber = sp.getString("number", null);
        mPasswd = mNumber;

        //隐藏菜单功能按钮点击事件
        mGradeBodyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isclicked = !isclicked;//取反改变状态
                showHideMenu(isclicked);
            }
        });

        sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharePreferenceUtils.isLoginJWSystem(getActivity().getApplicationContext())) {
                    Toast.makeText(getActivity().getApplicationContext(), "你还没有登录，请先登录！", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    loginToTest();
                }
            }
        });


        return v;
    }

    private boolean isLogin() {
        SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("student", Context.MODE_PRIVATE);
        String number = sp.getString("number", null);

        if (number != null) {
            if (mNumber == null) {
                mNumber = number;
                mPasswd = mNumber;
                return true;
            }
        }
        return false;
    }

    private void init(View v) {
        mProgressView = v.findViewById(R.id.grade_progress);
        mGradeBodyView = v.findViewById(R.id.grade_body);
        arrow = (ImageView) v.findViewById(R.id.grade_body_showhide_btn_img);
        mGradeBodyTitleView = v.findViewById(R.id.grade_body_title);
        mRealTitleView = v.findViewById(R.id.grade_body_title_realtitle);
        mGradeBodyBtn = v.findViewById(R.id.grade_body_btn);

        sender = (Button) v.findViewById(R.id.grade_btn_showgrade);
    }

    private void showHideMenu(boolean isclicked) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);//设置时间
        //默认情况下未点击button，则点击时显示，再点击则取消
        mRealTitleView.setVisibility(isclicked ? View.VISIBLE : View.GONE);
        if (isclicked) {
            //mGradeBodyView.animate().setDuration(shortAnimTime);
            Animation titleAnim = AnimationUtils.loadAnimation(this.getActivity(), R.anim.titlemenu_in_anim);

            //设置图标旋转
            Animation arrowRotateAnim = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            arrowRotateAnim.setFillAfter(true);
            arrowRotateAnim.setDuration(500);
            arrowRotateAnim.setRepeatCount(0);
            arrowRotateAnim.setInterpolator(new LinearInterpolator());
            arrow.startAnimation(arrowRotateAnim);


            mGradeBodyTitleView.setAnimation(titleAnim);
        } else {
            Animation anim = AnimationUtils.loadAnimation(this.getActivity(), R.anim.titlemenu_out_anim);
            Animation titleHideAnim = AnimationUtils.loadAnimation(this.getActivity(), R.anim.title_hide_anim);

            //设置图标旋转
            Animation arrowRotateAnim = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            arrowRotateAnim.setFillAfter(true);
            arrowRotateAnim.setDuration(300);
            arrowRotateAnim.setRepeatCount(0);
            arrowRotateAnim.setInterpolator(new LinearInterpolator());
            arrow.startAnimation(arrowRotateAnim);

            mRealTitleView.setAnimation(titleHideAnim);
            mGradeBodyTitleView.setAnimation(anim);

        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mGradeBodyView.setVisibility(show ? View.GONE : View.VISIBLE);
            mGradeBodyView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mGradeBodyView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mGradeBodyView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void loginToTest() {
        if (mReloginTask != null) {
            return;
        }

        boolean cancle = false;
        SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("student", Context.MODE_PRIVATE);
        String name = sp.getString("number", null);
        if (name == null) {
            cancle = true;
        }

        if (cancle) {
            showProgress(false);
        } else {
            showProgress(true);

            mReloginTask = new ReLoginSportTask(name, name);
            mReloginTask.execute((Void) null);

            mGetDataTask = new getSportData(mNumber);
            mGetDataTask.execute((Void) null);
        }
    }

    private void doGet() {
        if (mGetDataTask != null) {
            return;
        }

        boolean cancle = false;
        SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("student", Context.MODE_PRIVATE);
        String name = sp.getString("number", null);
        if (name == null) {
            cancle = true;
        }

        if (cancle) {
            showProgress(false);
        } else {
            showProgress(true);
            mGetDataTask = new getSportData(name);
            mGetDataTask.execute((Void) null);
        }
    }

    private class getSportData extends AsyncTask<Void, Void, Boolean> {
        String number;

        public getSportData(String number) {
            this.number = number;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            OkHttpClient client = OKHttpUtils.getInstanceClient();
            try {
                final Request request = new Request.Builder().url(Constants.URL_DETAIL + number).build();

                client.newCall(request).enqueue(new okhttp3.Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("TAG", "请求失败!");
                        Message msg = Message.obtain();
                        msg.what = 0;
                        refreshUIHandler.sendMessage(msg);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().toString();
                        DecodeHTML decoder = new DecodeHTML();
                        if (decoder.isGetDetailSuccess(result)) {

                            decoder.decodeDetail2(result);

                            Message msg = Message.obtain();
                            msg.what = 1;
                            refreshUIHandler.sendMessage(msg);

                        }
                    }
                });
                return true;
            } catch (Exception e) {

            }


            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mGetDataTask = null;
            if (!success) {
                Message msg = Message.obtain();
                msg.what = 0;
                refreshUIHandler.sendMessage(msg);
            } else {
                Message msg = Message.obtain();
                msg.what = 1;
                refreshUIHandler.sendMessage(msg);
            }
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            mGetDataTask = null;
            showProgress(false);
        }
    }

    private class ReLoginSportTask extends AsyncTask<Void, Void, Boolean> {
        String name = null;
        String pass = null;

        public ReLoginSportTask(String number, String pass) {
            this.name = number;
            this.pass = pass;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            OkHttpClient client = OKHttpUtils.getInstanceClient();
            try {
                RequestBody formBody = new FormBody.Builder().add("xuehao", name).add("passwd", pass).build();
                Request request = new Request.Builder().post(formBody).url(Constants.URL_SPORT_SCOLLER).build();
                Log.i(TAG, "用户信息：\tnumber:" + name + "\tpasswd:" + pass);
                Response response = client.newCall(request).execute();

                //获取返回数据
                String result = response.body().string();
                //Log.d(TAG, "body:" + result);
                DecodeHTML decoder = new DecodeHTML();
                if (decoder.isSprotLoginSuccess(result)) {
                    //如果登陆成功
                    decoder.decodeDetail1(result);

                    Message msg = Message.obtain();
                    msg.what = 2;
                    refreshUIHandler.sendMessage(msg);

                    return true;
                }

            } catch (Exception e) {

            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mReloginTask = null;
            if (success) {
                ;
            } else {
                Message msg = Message.obtain();
                msg.what = 0;
                refreshUIHandler.sendMessage(msg);
            }
        }

        @Override
        protected void onCancelled() {
            mReloginTask = null;
            showProgress(false);
        }
    }
}
