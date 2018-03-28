package com.cqsynet.heikuai.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cqsynet.heikuai.R;
import com.cqsynet.heikuai.adapter.NewsItemAdapter;
import com.cqsynet.heikuai.common.AppConstants;
import com.cqsynet.heikuai.model.NewsItemInfo;
import com.cqsynet.heikuai.model.NewsListRequestBody;
import com.cqsynet.heikuai.model.NewsListResponseBody;
import com.cqsynet.heikuai.model.NewsListResponseObject;
import com.cqsynet.heikuai.model.NewsMultiItem;
import com.cqsynet.heikuai.model.ResponseHeader;
import com.cqsynet.heikuai.network.WebServiceIf;
import com.cqsynet.heikuai.util.ToastUtil;
import com.cqsynet.heikuai.view.MyLoadMoreView;
import com.google.gson.Gson;

import java.util.ArrayList;

public class NewsListFragment extends Fragment{

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ArrayList<NewsMultiItem> mNewsMultiItemList = new ArrayList<>();
    private String mChannelId;
    private String mServerUpdateTime; //上次更新的服务器时间(接口返回)
    private NewsItemAdapter mNewsAdapter;
    private int mNewsCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_fragment_news_list);
        mRecyclerView = view.findViewById(R.id.recycler_view_fragment_news_list);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewsList("");
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNewsAdapter = new NewsItemAdapter(mNewsMultiItemList);
        mNewsAdapter.setEnableLoadMore(true);
        mNewsAdapter.setLoadMoreView(new MyLoadMoreView());
        mNewsAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                        int count = 0; //当前显示的总条数(除开广告)
                        for(int i = 0; i < mNewsMultiItemList.size(); i++) {
                            if(!mNewsMultiItemList.get(i).newsItemList.get(0).type.equals(NewsItemAdapter.NEWS_TYPE_AD + "")) {
                                count++;
                            }
                        }
                        if (count >= mNewsCount) {  //已加载完所有资讯
                            if(mNewsAdapter.isLoading()) {
                                ToastUtil.showToast(getActivity(), R.string.no_more_item);
                            }
                            mNewsAdapter.loadMoreEnd();
                        } else {  //从已加载的最后一条开始加载更多
                            String startId;
                            for(int i = mNewsMultiItemList.size() - 1; i >= 0; i--) {
                                NewsMultiItem.NewsItem item = mNewsMultiItemList.get(i).newsItemList.get(0);
                                if(!item.type.equals(NewsItemAdapter.NEWS_TYPE_AD + "")) {
                                    startId = item.id;
                                    getNewsList(startId);
                                    break;
                                }
                            }

                        }
            }
        }, mRecyclerView);
        mRecyclerView.setAdapter(mNewsAdapter);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        mChannelId = bundle.getString("channelId");
        getNewsList("");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 获取新闻列表
     * @param startId 开始id
     */
    private void getNewsList(final String startId) {
        final NewsListRequestBody body = new NewsListRequestBody();
        body.id = mChannelId;
        body.start = startId;
        if (TextUtils.isEmpty(startId) || TextUtils.isEmpty(mServerUpdateTime)) {
            body.updateTime = "";
        } else {
            body.updateTime = mServerUpdateTime;
        }

        WebServiceIf.IResponseCallback callbackIf = new WebServiceIf.IResponseCallback() {

            @Override
            public void onResponse(String response) {
                mSwipeRefreshLayout.setRefreshing(false);
                    Gson gson = new Gson();
                    try {
                        NewsListResponseObject responseObj = gson.fromJson(response, NewsListResponseObject.class);
                        ResponseHeader header = responseObj.header;
                        if (AppConstants.RET_OK.equals(header.ret)) {
//                            //保存幻灯广告信息
//                            if (responseObj.body.topList != null) {
//                                for (NewsItemInfo newsInfo : responseObj.body.topList) {
//                                    if (newsInfo.type.equals(NewsItemsAdapter.NEWS_TYPE_AD + "")) {
//                                        Globals.g_advMap.put(newsInfo.id, newsInfo);
//                                        int index = 0;
//                                        if (!TextUtils.isEmpty(newsInfo.plan)) {
//                                            index = new Random().nextInt(newsInfo.plan.split(AdvInfoObject.PLAN_SPLIT_CHAR).length);
//                                        }
//                                        Globals.g_advIndexMap.put(newsInfo.id, index);
//                                        Globals.g_advTimeMap.put(newsInfo.id, 0L);
//                                    }
//                                }
//                            }
//                            //保存浮层广告信息
//                            if (responseObj.body.floatingAdv != null) {
//                                NewsItemInfo newsInfo = responseObj.body.floatingAdv;
//                                Globals.g_advMap.put(newsInfo.id, newsInfo);
//                                int index = 0;
//                                if (!TextUtils.isEmpty(newsInfo.plan)) {
//                                    index = new Random().nextInt(newsInfo.plan.split(AdvInfoObject.PLAN_SPLIT_CHAR).length);
//                                }
//                                Globals.g_advIndexMap.put(newsInfo.id, index);
//                                Globals.g_advTimeMap.put(newsInfo.id, 0L);
//                            }
                            mNewsAdapter.loadMoreComplete();
                            boolean isRefresh = TextUtils.isEmpty(startId);
                            refreshNewsList(responseObj.body, isRefresh);
                        } else {
                            mNewsAdapter.loadMoreFail();
                            ToastUtil.showToast(getActivity(), "获取新闻列表失败");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mNewsAdapter.loadMoreFail();
                        ToastUtil.showToast(getActivity(), "获取新闻列表报错");
                    }
            }

            @Override
            public void onErrorResponse() {
                mNewsAdapter.loadMoreFail();
                mSwipeRefreshLayout.setRefreshing(false);
                ToastUtil.showToast(getActivity(), R.string.request_fail_warning);
            }
        };
        WebServiceIf.getNewsList(this.getActivity(), body, callbackIf);
    }

    /**
     * 刷新列表
     * @param body 内容数据
     * @param refresh true:完整刷新 false:加载更多
     */
    private void refreshNewsList(NewsListResponseBody body, boolean refresh) {
        ArrayList<NewsMultiItem> newsMultiItemList = convertData(body);
        if (refresh) {
//            mTopList.clear();
//            mNavList.clear();
//            mTopViewPager.removeAllViews();
//            if (body.topList == null || body.topList.isEmpty()) {
//                // 如果头图无数据，隐藏头图框体
//                mPTRListView.getRefreshableView().removeHeaderView(mTopView);
//                mIsTopViewEnable = false;
//            } else {
//                // 如果头图有数据，显示头图框体
//                if (!mIsTopViewEnable) {
//                    mPTRListView.getRefreshableView().addHeaderView(mTopView);
//                    mIsTopViewEnable = true;
//                }
//                mTopList.addAll(body.topList);
//                mTopPagerAdapter.initPagerIndicator(mTopList);
//                mTopPagerAdapter.notifyDataSetChanged();
////				mTopViewPager.setCurrentItem(mTopList.size() * 10); //这种方式可能会出现卡死
//                //采用反射来设置显示的页,防止卡死
//                try {
//                    Field mFirstLayout = ViewPager.class.getDeclaredField("mFirstLayout");
//                    mFirstLayout.setAccessible(true);
//                    mFirstLayout.set(mTopViewPager, true);
//                    mTopPagerAdapter.notifyDataSetChanged();
//                    mTopViewPager.setCurrentItem(mTopList.size() * 10);
//                    mViewPagerPosition = mTopList.size() * 10;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            //导航
//            if (body.navList == null || body.navList.isEmpty()) {
//                mPTRListView.getRefreshableView().removeHeaderView(mNavGrid);
//                mIsNavEnable = false;
//            } else {
//                if (!mIsNavEnable) {
//                    mPTRListView.getRefreshableView().addHeaderView(mNavGrid);
//                    mIsNavEnable = true;
//                }
//                mNavList.addAll(body.navList);
//                if (mNavList.size() % 3 == 0) {
//                    mNavGrid.setNumColumns(3);
//                } else if (mNavList.size() % 4 == 0) {
//                    mNavGrid.setNumColumns(4);
//                } else if (mNavList.size() % 5 == 0) {
//                    mNavGrid.setNumColumns(5);
//                } else {
//                    mNavGrid.setNumColumns(5);
//                }
//                mNavListAdapter.notifyDataSetChanged();
//            }

            mServerUpdateTime = body.updateTime;
//            showFloatingAdv(body.floatingAdv);
            mRecyclerView.scrollToPosition(0);
            mNewsMultiItemList.clear();
            mNewsMultiItemList.addAll(newsMultiItemList);
//            mNewsAdapter.notifyDataSetChanged();
            mNewsAdapter.setNewData(mNewsMultiItemList);
        } else {
            int oldSize = mNewsMultiItemList.size();
            mNewsMultiItemList.addAll(newsMultiItemList);
            mNewsAdapter.notifyItemRangeInserted(oldSize, newsMultiItemList.size());
//            mNewsAdapter.notifyDataSetChanged();
        }
        mNewsCount = body.newsListCount;
//        body.newsList = mNewsList;
//        body.topList = mTopList;
//        body.navList = mNavList;
//        body.updateTime = mServerUpdateTime;
//        // 保存到数据库
//        mCacheDao.saveNews(mFreshLocalTime, mChannelId, body);
    }

    /**
     * 转换资讯对象适配recyclerView
     * @param body
     */
    private ArrayList<NewsMultiItem> convertData(NewsListResponseBody body) {
        ArrayList<NewsMultiItem> newsMultiItemList = new ArrayList<>();
        for (NewsItemInfo newsInfo : body.newsList) {
            NewsMultiItem newsMultiItem = new NewsMultiItem();
            if (!newsInfo.type.equals(NewsItemAdapter.NEWS_TYPE_AD + "")) { //非广告类型
                NewsMultiItem.NewsItem newsItem = newsMultiItem.new NewsItem();
                newsItem.id = newsInfo.id;
                newsItem.title = newsInfo.title;
                newsItem.author = newsInfo.author;
                newsItem.label = newsInfo.label;
                newsItem.template = newsInfo.template;
                newsItem.type = newsInfo.type;
                newsItem.plan = newsInfo.plan;
                newsItem.restTime = newsInfo.restTime;
                newsItem.status = newsInfo.status;
                if (newsInfo.url != null && newsInfo.url.size() > 0) {
                    newsItem.url = newsInfo.url.get(0);
                }
                newsItem.img = newsInfo.img;
                newsMultiItem.newsItemList.add(newsItem);
            } else { //广告类型
                int size = newsInfo.advId.size();
                for (int i = 0; i < size; i++) {
                    NewsMultiItem.NewsItem newsItem = newsMultiItem.new NewsItem();
                    newsItem.planId = newsInfo.id; //排期id
                    newsItem.id = newsInfo.advId.get(i); //广告id
                    newsItem.title = newsInfo.advTitle.get(i);
                    newsItem.author = newsInfo.advAuthor.get(i);
                    newsItem.label = newsInfo.advLabel.get(i);
                    newsItem.template = newsInfo.advTemplate.get(i);
                    newsItem.type = newsInfo.type;
                    newsItem.plan = newsInfo.plan;
                    newsItem.restTime = newsInfo.restTime;
                    newsItem.status = newsInfo.status;
                    if (newsInfo.url != null && newsInfo.url.size() > 0) {
                        newsItem.url = newsInfo.url.get(0);
                    }
                    newsItem.img = newsInfo.advImg.get(i);
                    newsMultiItem.newsItemList.add(newsItem);
                }

            }
            newsMultiItemList.add(newsMultiItem);
        }
        return newsMultiItemList;
    }
}
