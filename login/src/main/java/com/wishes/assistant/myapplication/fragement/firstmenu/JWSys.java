package com.wishes.assistant.myapplication.fragement.firstmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wishes.assistant.myapplication.R;
import com.wishes.assistant.myapplication.activity.submenu.ClassTable;
import com.wishes.assistant.myapplication.activity.submenu.Grade;
import com.wishes.assistant.myapplication.activity.submenu.OneKey;
import com.wishes.assistant.myapplication.activity.submenu.Test;
import com.wishes.assistant.otherutils.Utils;

/**
 * 用来显示和教务系统相关的一些个功能按钮们
 * Created by 郑龙 on 2018/5/5.
 */

public class JWSys extends Fragment implements View.OnClickListener {
    private RelativeLayout warnLayout;
    private LinearLayout mainContent;
    private CardView mBtnGrade, mBtnOnekey, mBtnTest, mBtnTable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_jwsys, container, false);
        initView(v);//初始化绑定控件

        //根据登录状态显示不同的页面
        if (Utils.isLoginJWSystem(getActivity())) {
            //如果已登陆，则显示正文
            mainContent.setVisibility(View.VISIBLE);//显示正文
            warnLayout.setVisibility(View.GONE);//隐藏提示文本

            mBtnGrade.setOnClickListener(this);//设置监听事件
            mBtnOnekey.setOnClickListener(this);
            mBtnTest.setOnClickListener(this);
            mBtnTable.setOnClickListener(this);
        } else {
            //如果未登陆，则显示提示文本
            mainContent.setVisibility(View.GONE);//隐藏正文
            warnLayout.setVisibility(View.VISIBLE);//显示提示文本
        }
        return v;
    }

    private void initView(View v) {
        warnLayout = (RelativeLayout) v.findViewById(R.id.warn_layout);
        mainContent = (LinearLayout) v.findViewById(R.id.main_content);
        mBtnGrade = (CardView) v.findViewById(R.id.btn_grade);
        mBtnOnekey = (CardView) v.findViewById(R.id.btn_onekey);
        mBtnTest = (CardView) v.findViewById(R.id.btn_test);
        mBtnTable = (CardView) v.findViewById(R.id.btn_table);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_grade:
                //跳转到成绩查询页面
                intent.setClass(getActivity(), Grade.class);
                startActivity(intent);

                // startActivity(new Intent(getActivity(), Grade.class), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                break;
            case R.id.btn_onekey:
                //跳转到成绩查询页面
                intent.setClass(getActivity(), OneKey.class);
                startActivity(intent);
                break;
            case R.id.btn_test:
                //跳转到成绩查询页面
                intent.setClass(getActivity(), Test.class);
                startActivity(intent);
                break;
            case R.id.btn_table:
                intent.setClass(getActivity(), ClassTable.class);
                startActivity(intent);
                break;
        }
    }
}
