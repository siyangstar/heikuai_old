package com.cqsynet.heikuai.model;

import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.ArrayList;

public class NewsMultiItem implements MultiItemEntity {

    public ArrayList<NewsItem> newsItemList;
    public int index;

    public NewsMultiItem() {
        newsItemList = new ArrayList<>();
        index = 0;
    }

    public class NewsItem {
        public String planId; //排期id
        public String id; // 新闻列表项id(若为广告,则为广告id)
        public String type; // 按新闻内容来区分的类型。包括 活动，专题，广告，等类型
        public String title; // 新闻标题
        public String author; // 新闻来源
        public String template; // 新闻列表项View类型。有右侧图片文字混排，纯文本，一张水平大图，三张并排小图，广告灯类型。
        public String label; //新闻标签
        public String restTime; //活动剩余时间
        public String status; //活动进展状态
        public String url; // 点击跳转url
        public ArrayList<String> img; // 要显示的图片列表
        public String plan;
    }

    @Override
    public int getItemType() {
        NewsItem newsItem = newsItemList.get(index);
        if(!TextUtils.isEmpty(newsItem.template)) {
            return Integer.parseInt(newsItem.template);
        } else {
            return 1;
        }
    }

    /**
     * 轮播下一个
     */
    public void turnToNext() {
        if(newsItemList != null && newsItemList.size() > 0) {
            index = ++index % newsItemList.size();
        } else {
            index = 0;
        }
    }
}
