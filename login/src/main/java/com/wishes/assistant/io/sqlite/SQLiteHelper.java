package com.wishes.assistant.io.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wishes.assistant.myapplication.activity.dto.ActivityTransferTarget;
import com.wishes.assistant.otherutils.Base64Img;

/**
 * Created by 郑龙 on 2017/4/26.
 */

public class SQLiteHelper extends SQLiteOpenHelper {
    /* 错误代码们 */
    private static final int DATABASE_INSERT_ERROR = -1;
    private static final int DATEBASE_INSERT_SUCCESS = 1;

    /* 相关变量们 */
    // 设置本地SQLite数据库名字
    private static final String DATABASE_NAME = "Information";
    // 设置本地数据库表的名字（如果有多张表则可以写多个）
    private static final String TABLE_NAME1 = "REFRESHTIMETABLE";// （用于存放刷新时间）
    private static final String TABLE_NAME2 = "IMG";// （用于存放图片）
    // 所要执行的详细sql语句（如果有很多条SQL语句命令，则可以写多条）

    // 设置数据库版本（默认为1，不用改）
    private final static int DATABASE_VERSION = 1;
    // 初始化一个数据库对象，并置空
    private static SQLiteHelper helper = null;

    // 使用单例设计，私有化构造函数.创建数据库
    private SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 单例的方法返回一个对象
    public static SQLiteHelper getInstance(Context context) {
        if (helper == null) {
            helper = new SQLiteHelper(context);
        }
        return helper;
    }

    //重写其onCreate方法，自定义要创建的数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME1 + " (TABNUM VARCHAR(2), REFRESHTIME VARCHAR(30))");
        db.execSQL("CREATE TABLE " + TABLE_NAME2 + " (TABNAME VARCHAR(30), IMGNAME VARCHAR(15), IMGCONTENT VARCHAR(999999))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql1 = "DROP TABLE IF EXISTS " + TABLE_NAME1;
        String sql2 = "DROP TABLE IF EXISTS " + TABLE_NAME2;
        db.execSQL(sql1);
        db.execSQL(sql2);
        onCreate(db);
    }

    // 获取对应数据库表单的游标位置
    public Cursor select(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        return cursor;
    }

    //获取刷新时间数据
    public String getRefreshTime(String tableName, String tabNum) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, new String[]{"REFRESHTIME"}, "TABNUM = ?", new String[]{tabNum}, null, null, null, null);
        String result = null;
        if (cursor.moveToFirst()) {
            result = cursor.getString(0);
        }
        return result;
    }

    // 插入一条记录
    public long insert(String tableName, String rowName, String tabnumValue, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        String where = "TABNUM" + " = '" + tabnumValue + "'";

        //先判断是否已存在值
        Cursor cursor = query(tableName, where);
        if (cursor.moveToNext()) {
            //如果存在值
            update(tableName, tabnumValue, rowName, value);
        } else {
            //如果不存在
            try {
                cv.put(rowName, value);
                cv.put("TABNUM", tabnumValue);
                long row = db.insert(tableName, null, cv);
                return row;
            } catch (Exception e) {
                Log.e("输入的表名或列名错误，导致插入数据错误！", "RT");
                Log.e("在表：", tableName);
                Log.e("TABNUM", tabnumValue);
                Log.e(rowName, value);
                return DATABASE_INSERT_ERROR;
            }
        }
        return DATEBASE_INSERT_SUCCESS;

    }

    /**
     * @param fromTableName
     * @param imgName
     * @param imgContent
     * @return
     */
    public long insertImgToDb(String fromTableName, String imgName, String imgContent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        String where = "IMGNAME = ?";

        //先判断是否已存在值
        ActivityTransferTarget target = getSavedImg(fromTableName, imgName);
        if (target.getBitmapName() != null && imgName.equals(target.getBitmapName())) {
            //如果存在值
            updateImg(fromTableName, imgName, imgContent, where);
        } else {
            //如果不存在
            try {
                cv.put("TABNAME", fromTableName);
                cv.put("IMGNAME", imgName);
                cv.put("IMGCONTENT", imgContent);
                long row = db.insert(TABLE_NAME2, null, cv);
                return row;
            } catch (Exception e) {
                Log.e("输入的表名或列名错误，导致插入数据错误！", "RT");
                return DATABASE_INSERT_ERROR;
            }
        }
        return DATEBASE_INSERT_SUCCESS;

    }

    // 根据条件查询某张表单上的某数据，返回游标
    public Cursor query(String tableName, String where) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + where, null);
        return cursor;
    }

    /**
     * 删除图像表单上的缓存内容，因为读写是真的很慢。。
     *
     * @param fromTableName
     * @param imgName
     */
    public void deleteImgCache(String fromTableName, String imgName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = "TABNAME = ? and IMGNAME = ?";
        String[] whereValue = {fromTableName, imgName};
        db.delete(TABLE_NAME2, where, whereValue);
    }

    /**
     * @param fromTableName
     * @param imgName
     * @return
     */
    public ActivityTransferTarget getSavedImg(String fromTableName, String imgName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String where = "TABNAME = '" + fromTableName + "' AND IMGNAME = '" + imgName + "'";
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME2 + " WHERE " + where, null);

        ActivityTransferTarget target = new ActivityTransferTarget();
        if (cursor.moveToNext()) {
            target.setFromActivity(cursor.getString(0));
            target.setBitmapName(cursor.getString(1));
            target.setBitmap(Base64Img.bytes2Bitmap(Base64Img.generateImage(cursor.getString(2))));
        }

        return target;
    }

    /**
     * @param table
     * @param name
     * @param content
     * @param where
     */
    public void updateImg(String table, String name, String content, String where) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] whereValue = {name};
        ContentValues cv = new ContentValues();
        cv.put("TABNANE", table);
        cv.put("IMGNAME", name);
        cv.put("IMGCONTENT", content);

        db.update(TABLE_NAME2, cv, where, whereValue);
    }

    // 更新表单记录(Tab数字为查询关键字)
    public void update(String dbTableName, String tableNumValue, String otherTitle, String otherValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = "TABNUM = ?";
        String[] whereValue = {tableNumValue};
        ContentValues cv = new ContentValues();
        cv.put(otherTitle, otherValue);
        db.update(dbTableName, cv, where, whereValue);
    }
}

