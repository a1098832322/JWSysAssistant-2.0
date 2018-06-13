package com.wishes.assistant.myapplication.activity.submenu;

/**
 * 用于给适配器绑定键值对
 * <p>
 * Created by 郑龙 on 2017/4/26.
 */

public class ValueMap {
    private String key = "";
    private int value;

    public ValueMap() {
        //初始情况
        key = "";
        value = -1;
    }

    public ValueMap(String _key, int _value) {
        this.key = _key;
        this.value = _value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key.toString();
    }

    public String getKey() {
        return key;
    }
}
