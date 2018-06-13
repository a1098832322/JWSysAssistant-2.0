package com.wishes.assistant.otherutils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;


public class Base64Img {

    /**
     * 将图片转换成Base64编码
     *
     * @param bitmap 待处理图片(bitmap)
     * @return
     */
    public static String getImgStr(Bitmap bitmap) {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] imgBytes = bitmap2Bytes(bitmap);

        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(imgBytes);//返回Base64编码过的字节数组字符串
    }


    /**
     * 对字节数组字符串进行Base64解码并生成图片
     *
     * @param imgStr 图片Base64编码后的字符串
     * @return
     */
    public static byte[] generateImage(String imgStr) {
        //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {//调整异常数据
                    b[i] += 256;
                }
            }

            return b;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * bitmap转byte[]
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] datas = baos.toByteArray();
        return datas;
    }

    /**
     * byte[]转Bitmap
     *
     * @param datas
     * @return
     */
    public static Bitmap bytes2Bitmap(byte[] datas) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(datas, 0, datas.length);
        return bitmap;
    }
}