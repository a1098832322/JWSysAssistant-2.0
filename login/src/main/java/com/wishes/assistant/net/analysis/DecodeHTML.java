package com.wishes.assistant.net.analysis;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 郑龙 on 2017/4/25.
 */

public class DecodeHTML {
    public boolean isGetImgSuccess(String html) {
        Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByTag("div");
        for (Element e : elements) {
            String tag = e.attr("id");
            if (tag.equalsIgnoreCase("loading")) {
                return false;
            }
        }
        return true;
    }

    public String isLoginSuccess(String html) {
        //从有无form的差别来判断是否获取到了登录页
        Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByTag("form");
        if (elements.size() != 0) {
            //如果存在from，则判断登陆失败
            return null;
        }

        //如果登录
        Elements ele = document.getElementsByTag("center");
        Element mainBody = ele.get(0);
        String result = mainBody.text();
        //获取名字
        String nameArray = result.substring(result.indexOf(']') + 1, result.indexOf(' '));//选出名字

        if (nameArray.length() == 0) {
            return null;
        } else {
            return selectRealName(nameArray);
        }

    }

    public List<String> getClassName(String html) {
        List<String> classNameList = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Elements trs = document.select("table").select("tr");
        int i;
        for (i = 1; i < trs.size(); i++) {
            Elements tds = trs.get(i).select("td");
            for (int j = 0; j < tds.size(); j = j + 2) {
                String txt = tds.get(j).text();
                classNameList.add(txt);
            }
        }
        return classNameList;
    }

    //保留暂时还用不到它
    private String selectRealName(String str) {
        //选出汉字的方法
        String reg = "[^\u4e00-\u9fa5]";
        str = str.replaceAll(reg, "");

        return str;
    }

    public boolean isGetDetailSuccess(String html) {
        Document document = Jsoup.parse(html);
        // 获取表格内容
        Elements elements = document.select("td");
        // 获取的elements的size=8表示没有获取到内容或者登陆不成功！
        if (elements.size() == 8) {
            return true;
        }
        return false;
    }

    public void decodeDetail2(String html) {
        Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByTag("td");
        Person person = Person.getInstance();
        Element element = null;

        for (int i = 0; i < elements.size(); i++) {
            switch (i) {
                case 9:
                    element = elements.get(i);
                    person.setmTall(element.text().toString().trim());
                    break;
                case 17:
                    element = elements.get(i);
                    person.setmWeight(element.text().toString().trim());
                    break;
                case 21:
                    element = elements.get(i);
                    person.setmLung(element.text().toString().trim());
                    break;
                case 25:
                    element = elements.get(i);
                    person.setM50(element.text().toString().trim());
                    break;
                case 29:
                    element = elements.get(i);
                    person.setmJump(element.text().toString().trim());
                    break;
                case 33:
                    element = elements.get(i);
                    person.setmLongRun(element.text().toString().trim());
                    break;
                case 37:
                    element = elements.get(i);
                    person.setmFlexion(element.text().toString().trim());
                    break;
                case 41:
                    element = elements.get(i);
                    person.setmUp(element.text().toString().trim());
                    break;
            }
        }

    }

    public void decodeDtail3(String html) {
        Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByTag("td");
        Element element = null;
        Person person = Person.getInstance();

        for (int i = 0; i < elements.size(); i++) {
            switch (i) {
                case 19:
                    element = elements.get(i);
                    person.setFinalScoller(element.text().toString().trim());
                    break;
                case 22:
                    element = elements.get(i);
                    person.setLevel(element.text().toString().trim());
                    break;
            }
        }

    }

    public void decodeDetail1(String html) {
        Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByTag("td");

        Element element = elements.get(20);
        String count = element.text().toString().trim();
        Person person = Person.getInstance();
        person.setMcount(count);

    }

    public boolean isSprotLoginSuccess(String html) {
        Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByTag("div");
        if (elements.size() <= 2) {
            return false;
        } else {
            return true;
        }

    }
}
