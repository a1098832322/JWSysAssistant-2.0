package com.wishes.assistant.customs.listview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wishes.assistant.myapplication.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by 10988 on 2017/4/28.
 */

public class MyCustomsBaseAdapter extends BaseAdapter {
    private List data;// 要绑定的数据
    private int resource;// 绑定的一个条目界面的id，此例中即为item.xml
    private LayoutInflater inflater;// 布局填充器，它可以使用一个xml文件生成一个View对象，可以通过Context获取实例对象

    public MyCustomsBaseAdapter(Context context, List data, int resource) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.data = data;
    }

    @Override
    public int getCount() {// 得到要绑定的数据总数
        return data.size();
    }

    @Override
    public Object getItem(int position) {// 给定索引值，得到索引值对应的对象
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {// 获取条目id
        return position;
    }

    // ListView有缓存功能，当显示第一页页面时会创建页面对象，显示第二页时重用第一页创建好了的对象
    // 取得条目界面:position代表当前条目所要绑定的数据在集合中的索引值
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView nameView = null;
        if (convertView == null) {// 显示第一页的时候convertView为空
            convertView = inflater.inflate(resource, null);// 生成条目对象
            nameView = (TextView) convertView.findViewById(R.id.item_name);
            ViewCache cache = new ViewCache();
            cache.nameView = nameView;
            convertView.setTag(cache);
        } else {
            ViewCache cache = (ViewCache) convertView.getTag();
            nameView = cache.nameView;
        }
        // 实现数据绑定
        HashMap<String, String> map = (HashMap<String, String>) data.get(position);
        String str = map.get("text");
        nameView.setText(str);
        return convertView;
    }

    private final class ViewCache {
        public TextView nameView;
    }
}
