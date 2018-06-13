package com.wishes.assistant.customs.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.wishes.assistant.myapplication.R;

import java.lang.reflect.Field;

/**
 * Created by 10988 on 2017/4/27.
 */

public class sportReloginDialog extends ReLoginDialog {
    private SportDialogCallBackListener mDialogCallBackListener = null;
    private Context mContext;
    private EditText mName, mPasswd;
    private AlertDialog dialog = null;
    private String number = null;


    public interface SportDialogCallBackListener {//通过该接口回调Dialog需要传递的值

        public void callBack(String name, String password);//具体方法
    }

    public sportReloginDialog(@NonNull Context context, DialogCallBackListener listener, SportDialogCallBackListener spListener, String number) {
        super(context, (ReLoginDialog.DialogCallBackListener) listener);
        this.mContext = context;
        this.mDialogCallBackListener = spListener;
        this.number = number;

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.sport_relogin_layout, (ViewGroup) findViewById(R.id.sport_relogin_dialog));

        mName = (EditText) layout.findViewById(R.id.number);
        //如果已经登陆过了则自动填充账号

        mName.setText(number);

        mPasswd = (EditText) layout.findViewById(R.id.password);

        //设置dialog的button监听事件
        DialogInterface.OnClickListener dialogListener = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case BUTTON_POSITIVE:
                        reInput();
                        break;
                    case BUTTON_NEGATIVE:
                        // 利用JAVA反射机制,条件成立能关闭 AlertDialog 窗口
                        try {
                            Field field = dialog.getClass().getSuperclass()
                                    .getDeclaredField("mShowing");
                            field.setAccessible(true);
                            field.set(dialog, true); // true - 使之可以关闭
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(layout).setPositiveButton("确定", dialogListener).setNegativeButton
                ("取消", dialogListener);
        builder.setTitle("请重新登录！");
        dialog = builder.create();
    }

    public sportReloginDialog(@NonNull Context context, DialogCallBackListener listener) {
        super(context, (ReLoginDialog.DialogCallBackListener) listener);
        this.mContext = context;
    }

    public void reInput() {
        String name = mName.getText().toString();
        String passwd = mPasswd.getText().toString();
        if (isNumberValid(name) && isNumberValid(passwd)) {
            mDialogCallBackListener.callBack(name, passwd);
            // 利用JAVA反射机制,条件成立能关闭 AlertDialog 窗口
            try {
                Field field = dialog.getClass().getSuperclass()
                        .getDeclaredField("mShowing");
                field.setAccessible(true);
                field.set(dialog, true); // true - 使之可以关闭
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mName.setError("账号或密码输入格式错误！请检查！");
            mPasswd.setError("账号或密码输入格式错误！请检查！");
            Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.myanim);
            mName.setAnimation(anim);
            mName.requestFocus();
        }
    }

    private boolean isNumberValid(String number) {
        //判断账号是否有效
        return number.length() == 12 ? true : false;
    }

    private boolean isPasswdValid(String passwd) {
        if (passwd.length() <= 4) {
            return false;
        }
        return true;
    }

    @Override
    public void show() {
        dialog.show();
    }
}
