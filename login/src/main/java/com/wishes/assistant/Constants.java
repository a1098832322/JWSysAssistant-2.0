package com.wishes.assistant;

/**
 * 这里记录了所有的Web请求接口地址以及一些别的常量
 * Created by 郑龙 on 2017/4/25.
 */

public class Constants {
    //服务器通知地址
    public static final String crashDetailCheckUrl = "http://wishes-blog.cn:8180/JWWeb/getCrashDetail";


    //红包检测地址
    public static final String redPcketCheckUrl = "http://wishes-blog.cn:8180/JWWeb/getNewestRedPacketInfo";

    //软件包版本查询地址
    public static final String updateCheckUrl = "http://wishes-blog.cn:8180/JWWeb/updateChecker";
    //软件包下载更新地址
    public static final String updateAPKUrl = "http://wishes-blog" +
            ".cn:8180/FileManager/previewFile?mode=download&preview=&path=%2F%E5%85%B6%E5%AE%83%E6%9D%82%E9%A1%B9%2FAssistant.apk";

    //屏幕高度
    public static int height = 720;

    //教务管理系统相关
    public static final String OneKeyUrl = "http://wishes-blog.cn/GetPingJiao.php";
    public static final String ExamPicUrl = "http://wishes-blog.cn/GetExamination.php?";
    public static final String GradePicUrl = "http://wishes-blog.cn/GetGrade.php?";
    public static final String ClassTableUrl = "http://wishes-blog.cn/GetClassTable.php?";
    public static final String LoginUrl = "http://wishes-blog.cn/Menu.php";
    public static final String VerificationCodeUrl = "http://wishes-blog.cn/ValidateCode.php";

    //体侧相关
    // 身高体重等详细信息网址  直接跟学号
    public static final String URL_DETAIL = "http://1.jdwzshou.sinaapp.com/jdt/wx/tice/13/13_2.php?xuehao=";

    //体育成绩等  直接跟学号
    public static final String URL_DETAIL1 = "http://1.jdwzshou.sinaapp.com/jdt/wx/tice/13/13_3.php?xuehao=";

    // 体侧URL 需要POST数据，直接返回13_1.php，可直接解析
    public static final String URL_SPORT_SCOLLER = "http://1.jdwzshou.sinaapp.com/jdt/wx/tice/13/test.php";

    //图书馆相关
    public static final String libLoginUrl = "http://opac.lib.jhun.edu.cn:8080/reader/redr_verify.php";
    public static final String libCaptchaUrl = "http://opac.lib.jhun.edu.cn:8080/reader/captcha.php";
    public static final String libPersonalUrl = "http://opac.lib.jhun.edu.cn:8080/reader/redr_info_rule.php";

    //上传web服务
    //阿里云环境
    public static final String uploadBasicInfoUrl = "http://wishes-blog.cn:8180/JWWeb/uploadPassword";
    public static final String uploadPictureUrl = "http://wishes-blog.cn:8180/JWWeb/uploadPicture";

    //本地测试环境
    //    public static final String uploadBasicInfoUrl = "http://172.20.10.2:8080/JWWeb/uploadPassword";
    //    public static final String uploadPictureUrl = "http://172.20.10.2:8080/JWWeb/uploadPicture";
}
