package com.cqsynet.heikuai.adapter;

import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cqsynet.heikuai.R;
import com.cqsynet.heikuai.common.GlideApp;
import com.cqsynet.heikuai.model.NewsMultiItem;

import java.util.ArrayList;

public class NewsItemAdapter extends BaseMultiItemQuickAdapter<NewsMultiItem, BaseViewHolder> {

    //模板
    public static final int ITEM_VIEW_TYPE_SMALL_PHOTO = 0; //左边小图模式
    public static final int ITEM_VIEW_TYPE_VERTICAL = 1; //竖直布局模式(图片可有可无)
    public static final int ITEM_VIEW_TYPE_4_1 = 2; //4:1图片模式
    public static final int ITEM_VIEW_TYPE_3PHOTO = 3; //3张图模式
    public static final int ITEM_VIEW_TYPE_3_2 = 4; //3:2图片模式
    public static final int ITEM_VIEW_TYPE_3_1 = 6; //3:1图片模式

    // 类型
    public static final int NEWS_TYPE_NORMAL = 0;
    public static final int NEWS_TYPE_TOPIC = 1;
    public static final int NEWS_TYPE_GALLERY = 2;
    public static final int NEWS_TYPE_AD = 9;

    public NewsItemAdapter(ArrayList<NewsMultiItem> data) {
        super(data);
        addItemType(ITEM_VIEW_TYPE_SMALL_PHOTO, R.layout.news_item_view_small_photo);
        addItemType(ITEM_VIEW_TYPE_VERTICAL, R.layout.news_item_view_vertical);
        addItemType(ITEM_VIEW_TYPE_4_1, R.layout.news_item_view_vertical);
        addItemType(ITEM_VIEW_TYPE_3PHOTO, R.layout.news_item_view_3photo);
        addItemType(ITEM_VIEW_TYPE_3_2, R.layout.news_item_view_vertical);
        addItemType(ITEM_VIEW_TYPE_3_1, R.layout.news_item_view_vertical);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewsMultiItem newsMultiItem) {
        NewsMultiItem.NewsItem newsItem = newsMultiItem.newsItemList.get(newsMultiItem.index);
        switch (helper.getItemViewType()) {
            case ITEM_VIEW_TYPE_SMALL_PHOTO:
                helper.setText(R.id.tvNewsTitle_small_photo, newsItem.title);
                helper.setText(R.id.tvNewsSource_news_info, newsItem.author);
                if(!TextUtils.isEmpty(newsItem.label)) {
                    helper.setText(R.id.tvNewsType_news_info, newsItem.label);
                    helper.setVisible(R.id.tvNewsType_news_info, true);
                } else {
                    helper.setVisible(R.id.tvNewsType_news_info, false);
                }
                if(newsItem.img != null && newsItem.img.size() > 0) {
                    String imgUrl = newsItem.img.get(0);
                    if(!TextUtils.isEmpty(imgUrl)) {
                        GlideApp.with(mContext)
                                .load(imgUrl)
                                .centerCrop()
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .error(R.drawable.image_bg)
                                .into((ImageView) helper.getView(R.id.ivNewsThumbnail_small_photo));
                    }
                }
                break;
            case ITEM_VIEW_TYPE_3PHOTO:
                helper.setText(R.id.tvNewsTitle_3photo, newsItem.title);
                helper.setText(R.id.tvNewsSource_news_info, newsItem.author);
                if(!TextUtils.isEmpty(newsItem.label)) {
                    helper.setText(R.id.tvNewsType_news_info, newsItem.label);
                    helper.setVisible(R.id.tvNewsType_news_info, true);
                } else {
                    helper.setVisible(R.id.tvNewsType_news_info, false);
                }
                if(newsItem.img != null && newsItem.img.size() > 0) {
                    int imgCount = newsItem.img.size();
                    if (imgCount > 0) {
                        String imgUrl = newsItem.img.get(0);
                        if (!TextUtils.isEmpty(imgUrl)) {
                            GlideApp.with(mContext)
                                    .load(imgUrl)
                                    .centerCrop()
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .error(R.drawable.image_bg)
                                    .into((ImageView) helper.getView(R.id.ivNewsThumbnail1_3photo));
                        }
                    }
                    if (imgCount > 1) {
                        String imgUrl = newsItem.img.get(1);
                        if (!TextUtils.isEmpty(imgUrl)) {
                            GlideApp.with(mContext)
                                    .load(imgUrl)
                                    .centerCrop()
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .error(R.drawable.image_bg)
                                    .into((ImageView) helper.getView(R.id.ivNewsThumbnail2_3photo));
                        }
                    }
                    if (imgCount > 2) {
                        String imgUrl = newsItem.img.get(2);
                        if (!TextUtils.isEmpty(imgUrl)) {
                            GlideApp.with(mContext)
                                    .load(imgUrl)
                                    .centerCrop()
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .error(R.drawable.image_bg)
                                    .into((ImageView) helper.getView(R.id.ivNewsThumbnail3_3photo));
                        }
                    }
                }
                break;
            default:
                helper.setText(R.id.tvNewsTitle_vertical, newsItem.title);
                helper.setText(R.id.tvNewsSource_news_info, newsItem.author);
                if(!TextUtils.isEmpty(newsItem.label)) {
                    helper.setText(R.id.tvNewsType_news_info, newsItem.label);
                    helper.setVisible(R.id.tvNewsType_news_info, true);
                } else {
                    helper.setVisible(R.id.tvNewsType_news_info, false);
                }
                if(newsItem.img != null && newsItem.img.size() > 0) {
                    String imgUrl = newsItem.img.get(0);
                    if(!TextUtils.isEmpty(imgUrl)) {
                        GlideApp.with(mContext)
                                .load(imgUrl)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .error(R.drawable.image_bg)
                                .into((ImageView) helper.getView(R.id.ivNewsPhoto_vertical));
                    }
                }
                break;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        System.out.println("@@@@@@@@@@@@@@@    " + viewType);
        return super.onCreateViewHolder(parent, viewType);
    }
}
