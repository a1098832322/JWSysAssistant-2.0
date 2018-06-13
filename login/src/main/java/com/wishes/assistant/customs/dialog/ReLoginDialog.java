package com.wishes.assistant.customs.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.wishes.assistant.myapplication.R;
import com.wishes.assistant.net.VerificationCode;

import java.lang.reflect.Field;

/**
 * Created by 10988 on 2017/4/26.
 */

public class ReLoginDialog extends AlertDialog {

    private DialogCallBackListener mDialogCallBackListener = null;

    private Context context;
    private AlertDialog dialog = null;
    private ImageView img = null;
    private EditText code = null;

    public Handler ImgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    img.setImageBitmap((Bitmap) msg.obj);
                    break;
                case 0:
                    Log.e("TAG", "hanlder获取验证码失败！");
                    break;
            }
        }
    };


    public ReLoginDialog(@NonNull Context context, DialogCallBackListener listener) {
        super(context);
        this.context = context;
        this.mDialogCallBackListener = listener;
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.relogin_dialog_layout, (ViewGroup) findViewById(R.id.relogin_dialog));

        img = (ImageView) layout.findViewById(R.id.dialog_img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerificationCode.getCode(ImgHandler);
            }
        });

        //创建时自动刷新并显示验证码
        VerificationCode.getCode(ImgHandler);

        code = (EditText) layout.findViewById(R.id.dialog_code);

        //设置dialog的button监听事件
        DialogInterface.OnClickListener dialogListener = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE:
                        reLogin();
                        break;
                    case Dialog.BUTTON_NEGATIVE:
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
        dialog = builder.create();
    }

    public interface DialogCallBackListener {//通过该接口回调Dialog需要传递的值

        public void callBack(String msg);//具体方法
    }


    public void reLogin() {
        String codeStr = code.getText().toString();
        if (isCodeValid(codeStr) && mDialogCallBackListener != null) {
            mDialogCallBackListener.callBack(codeStr);
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
            code.setError("别闹，验证码有4位！");
            Toast.makeText(this.context, "验证码无效！", Toast.LENGTH_SHORT).show();
            // 利用JAVA反射机制,条件不成立不能关闭 AlertDialog 窗口
            try {
                Field field = dialog.getClass().getSuperclass()
                        .getDeclaredField("mShowing"); // 这个是reflect的那个Field
                field.setAccessible(true);
                field.set(dialog, false); // false -/ 使之不能关闭
            } catch (Exception e) {
                Log.e(e.getMessage(), null);
                e.printStackTrace();
            }
        }
    }

    private boolean isCodeValid(String code) {
        //判断验证码是否有效
        return code.length() == 4 ? true : false;
    }

    @Override
    public void show() {
        dialog.show();
    }
}
