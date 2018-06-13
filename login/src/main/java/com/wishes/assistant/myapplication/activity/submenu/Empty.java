package com.wishes.assistant.myapplication.activity.submenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wishes.assistant.myapplication.R;


/**
 * 一个空的fragment
 * Created by 郑龙 on 2018/5/5.
 */

public class Empty extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_empty, container, false);
        init(v);//初始化UI界面

        return v;
    }

    /**
     * 初始化控件们
     *
     * @param v
     */
    private void init(View v) {
        TextView tv = (TextView) v.findViewById(R.id.tv_empty);
    }
}
