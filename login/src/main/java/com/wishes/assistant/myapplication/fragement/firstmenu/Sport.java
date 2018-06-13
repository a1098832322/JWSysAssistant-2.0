package com.wishes.assistant.myapplication.fragement.firstmenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.linroid.filtermenu.library.FilterMenu;
import com.linroid.filtermenu.library.FilterMenuLayout;
import com.wishes.assistant.myapplication.R;
import com.wishes.assistant.otherutils.Utils;

import es.dmoral.toasty.Toasty;

/**
 * Created by 10988 on 2018/5/5.
 */

public class Sport extends Fragment {
    private RelativeLayout warnLayout;
    private LinearLayout mainContent;
    private FilterMenuLayout layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sport, container, false);
        initView(v);
        if (Utils.isLoginSportSystem(getActivity())) {
            //如果已登陆，则显示正文
            mainContent.setVisibility(View.VISIBLE);//显示正文
            warnLayout.setVisibility(View.GONE);//隐藏提示文本
        } else {
            //如果未登陆，则显示提示文本
            mainContent.setVisibility(View.GONE);//隐藏正文
            warnLayout.setVisibility(View.VISIBLE);//显示提示文本
        }

        FilterMenu menu = new FilterMenu.Builder(getActivity())
                //.addItem(R.drawable.ic_autorenew_black_24dp)
                //.addItem(R.drawable.ic_person_black_24dp)
                .inflate(R.menu.relogin_btn_menu)//inflate  menu resource
                .attach(layout)
                .withListener(new FilterMenu.OnMenuChangeListener() {
                    @Override
                    public void onMenuItemClick(View view, int position) {
                        switch (position) {
                            case 0:
                                Toasty.info(getActivity().getApplicationContext(), "刷新", Toast.LENGTH_SHORT, true).show();
                                break;
                            case 1:
                                Toasty.info(getActivity().getApplicationContext(), "登录", Toast.LENGTH_SHORT, true).show();
                                break;
                        }
                    }

                    @Override
                    public void onMenuCollapse() {
                    }

                    @Override
                    public void onMenuExpand() {
                    }
                })
                .build();

        return v;
    }

    /**
     * 初始化绑定控件
     *
     * @param v
     */
    private void initView(View v) {
        warnLayout = (RelativeLayout) v.findViewById(R.id.warn_layout);
        mainContent = (LinearLayout) v.findViewById(R.id.main_content);
        layout = (FilterMenuLayout) v.findViewById(R.id.filter_menu);
    }
}
