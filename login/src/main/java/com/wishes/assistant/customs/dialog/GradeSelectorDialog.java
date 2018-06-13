package com.wishes.assistant.customs.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.aigestudio.wheelpicker.WheelPicker;
import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.wishes.assistant.myapplication.R;
import com.wishes.assistant.myapplication.activity.submenu.Grade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 郑龙 on 2018/5/17.
 */

public class GradeSelectorDialog extends AAH_FabulousFragment {
    private static String mXn = "2014";
    private static String mXq = "第一学期";
    private static String mType = "原始成绩";

    private WheelPicker yearsPicker, xueqiPicker, otherPicker;
    List<String> yearList = new ArrayList<>();
    List<String> xueqiList = new ArrayList<>();
    List<String> otherList = new ArrayList<>();


    ImageButton imgbtn_refresh, imgbtn_apply;
    private ArrayList<Map<String, String>> params = new ArrayList<>();


    public static GradeSelectorDialog newInstance() {
        GradeSelectorDialog dialog = new GradeSelectorDialog();
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        params = ((Grade) getActivity()).getParams();

        initListValues();
    }

    private void initListValues() {
        //先清空
        yearList.clear();
        xueqiList.clear();
        otherList.clear();

        yearList.add("2014-2015");
        yearList.add("2015-2016");
        yearList.add("2016-2017");
        yearList.add("2017-2018");

        xueqiList.add("第一学期");
        xueqiList.add("第二学期");

        otherList.add("原始成绩");
        otherList.add("有效成绩");
    }

    @Override

    public void setupDialog(final Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.fragment_menu_layout, null);

        RelativeLayout rl_content = (RelativeLayout) contentView.findViewById(R.id.rl_content);
        LinearLayout ll_buttons = (LinearLayout) contentView.findViewById(R.id.ll_buttons);
        imgbtn_refresh = (ImageButton) contentView.findViewById(R.id.imgbtn_refresh);
        imgbtn_apply = (ImageButton) contentView.findViewById(R.id.imgbtn_ok);
        imgbtn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<>();
                mXn = yearList.get(yearsPicker.getSelectedItemPosition());
                map.put("xn", mXn.substring(0, mXn.indexOf("-")));
                params.add(map);

                mXq = xueqiList.get(xueqiPicker.getSelectedItemPosition());
                map.put("xq", mXq.equals("第一学期") ? "0" : "1");
                params.add(map);

                mType = otherList.get(otherPicker.getSelectedItemPosition());
                map.put("type", mType.equals("原始成绩") ? "0" : "1");
                params.add(map);

                closeFilter(params);//动画关闭
            }
        });
        imgbtn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFilter(null);//动画关闭
            }
        });


        yearsPicker = contentView.findViewById(R.id.yearpicker);
        yearsPicker.setData(yearList);
        yearsPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                Log.d("select", position + "\t" + data.toString());
                yearsPicker.setSelectedItemPosition(position);
            }
        });

        xueqiPicker = contentView.findViewById(R.id.xuenianpicker);
        xueqiPicker.setData(xueqiList);
        xueqiPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                Log.d("select", position + "\t" + data.toString());
                xueqiPicker.setSelectedItemPosition(position);
            }
        });

        otherPicker = contentView.findViewById(R.id.otherpicker);
        otherPicker.setData(otherList);
        otherPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                Log.d("select", position + "\t" + data.toString());
                otherPicker.setSelectedItemPosition(position);
            }
        });


        //params to set
        setAnimationDuration(400); //optional; default 500ms
        setPeekHeight(300); // optional; default 400dp
        setCallbacks((Callbacks) getActivity()); //optional; to get back result
        setViewgroupStatic(ll_buttons); // optional; layout to stick at bottom on slide
        setViewMain(rl_content); //necessary; main bottomsheet view
        setMainContentView(contentView); // necessary; call at end before super
        super.setupDialog(dialog, style); //call super at last
    }


}
