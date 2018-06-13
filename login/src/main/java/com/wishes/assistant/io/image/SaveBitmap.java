package com.wishes.assistant.io.image;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * 保存图片方法
 * Created by 郑龙 on 2017/4/27.
 */

public class SaveBitmap {
    public static void save(String bitName, Bitmap mBitmap) throws IOException {
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyJwSysImg/";
        File f = new File(dir);
        if (!f.exists())
            f.mkdirs();//如果没有这个文件夹的话，会报file not found错误
        f = new File(dir + bitName + ".png");
        f.createNewFile();
        try {
            FileOutputStream out = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            Log.i(TAG, e.toString());
        }

    }
}
