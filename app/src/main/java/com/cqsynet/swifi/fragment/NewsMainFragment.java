package com.cqsynet.swifi.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cqsynet.swifi.R;
import com.cqsynet.swifi.adapter.FragmentAdapter;
import com.cqsynet.swifi.common.AppConstants;
import com.cqsynet.swifi.common.Globals;
import com.cqsynet.swifi.db.StatisticsDao;
import com.cqsynet.swifi.model.ChannelInfo;
import com.cqsynet.swifi.model.ChannelListResponseBody;
import com.cqsynet.swifi.model.ChannelListResponseObject;
import com.cqsynet.swifi.model.ChannnelListRequestBody;
import com.cqsynet.swifi.model.ResponseHeader;
import com.cqsynet.swifi.network.WebServiceIf;
import com.cqsynet.swifi.util.LogUtil;
import com.cqsynet.swifi.util.SharedPreferencesInfo;
import com.cqsynet.swifi.util.ToastUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by allen on 2018/2/9.
 */

public class NewsMainFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ArrayList<ChannelInfo> mChannels = new ArrayList<ChannelInfo>();
    private long mRefreshTime;
    private String mCurrentChannelId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 从ShareInfo里面拿到之前保存的Json格式的频道列表字符串
        mChannels = SharedPreferencesInfo.getNewsChannel(getActivity());
        // 从服务器获取最新频道列表
        getNewsChannel(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View newsView = inflater.inflate(R.layout.fragment_news_main, container, false);
        mTabLayout = newsView.findViewById(R.id.tab_layout_fragment_news_main);
        mViewPager = newsView.findViewById(R.id.view_pager_fragment_news_main);

        initViewPager();

        return newsView;
    }

    private void initViewPager() {
        List<String> titles = new ArrayList<>();
        List<Fragment> fragments = new ArrayList<>();
        Iterator<ChannelInfo> it = mChannels.iterator();
        while (it.hasNext()) {
            ChannelInfo channelInfo = it.next();
            titles.add(channelInfo.name);
            mTabLayout.addTab(mTabLayout.newTab().setText(channelInfo.name));
            NewsListFragment fragment = new NewsListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("channelId", channelInfo.id);
            fragment.setArguments(bundle);
            fragments.add(new NewsListFragment());
        }

//        titles.add("推荐");
//        titles.add("轨道");
//        titles.add("好吃狗");
//        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
//        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));
//        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(2)));
//        fragments.add(new NewsListFragment());
//        fragments.add(new WidgetsFragment());
//        fragments.add(new WidgetsFragment());

        mViewPager.setOffscreenPageLimit(2);

        FragmentAdapter mFragmentAdapter = new FragmentAdapter(getFragmentManager(), fragments, titles);
        mViewPager.setAdapter(mFragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(pageChangeListener);
    }


    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 获取频道列表
     */
    private void getNewsChannel(final Context ctx) {
        ChannnelListRequestBody requestBody = new ChannnelListRequestBody();
        requestBody.ssid = AppConstants.WIFI_SSID;
        WebServiceIf.IResponseCallback callbackIf = new WebServiceIf.IResponseCallback() {

            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                try {
                    ChannelListResponseObject responseObject = gson.fromJson(response, ChannelListResponseObject.class);
                    ResponseHeader header = responseObject.header;
                    if (AppConstants.RET_OK.equals(header.ret)) {
                        ChannelListResponseBody body = responseObject.body;
                        if (body == null || body.add == null || body.add.size() == 0) {
                            ToastUtil.showToast(ctx, "当前无频道");
                            return;
                        }
                        //有变化时刷新界面
                        if(!compareChannel(body.add, mChannels)) {
                            if(Globals.DEBUG) {
                                System.out.println("栏目列表已刷新");
                            }
                            mChannels.clear();
                            mChannels.addAll(body.add);
//                            //设置tab
//                            mSlidingTabs.setViewPager(mViewPager, mSPTSOnPageChangedListener);
//                            mAdapter.notifyDataSetChanged();
                            initViewPager();
                        } else {
                            if(Globals.DEBUG) {
                                System.out.println("栏目列表保持不变");
                            }
                        }

                        SharedPreferencesInfo.setTagString(ctx, SharedPreferencesInfo.CHANNELS, gson.toJson(body));
                        SharedPreferencesInfo.setTagBoolean(ctx, SharedPreferencesInfo.GET_CHANNEL_FAIL, false);

                        //增加第一个页面的栏目阅读量
                        if (mChannels.size() > 0) {
                            mCurrentChannelId = mChannels.get(0).id;
                            StatisticsDao.saveStatistics(getActivity(), "channelView", mCurrentChannelId);
                        }
                    } else {
                        ToastUtil.showToast(ctx, R.string.get_channel_list_fail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse() {
                if (Globals.DEBUG) {
                    LogUtil.e(getActivity(), "HeiKuai", R.string.get_channel_list_fail);
                }
            }
        };
        WebServiceIf.getNewsChannels(getActivity(), requestBody, callbackIf);
    }

    /**
     * 对比两个频道列表的内容是否相同
     *
     * @param a
     * @param b
     * @return
     */
    private boolean compareChannel(ArrayList<ChannelInfo> a, ArrayList<ChannelInfo> b) {
        if (a.size() != b.size()) {
            return false;
        }

        for (int i = 0; i < a.size(); i++) {
            ChannelInfo infoA = a.get(i);
            for (int j = 0; j < b.size(); j++) {
                ChannelInfo infoB = b.get(j);
                if (infoA.id.equals(infoB.id)) {
                    break;
                }
                if (j == b.size() - 1) {
                    return false;
                }
            }
        }

        for (int i = 0; i < b.size(); i++) {
            ChannelInfo infoB = b.get(i);
            for (int j = 0; j < a.size(); j++) {
                ChannelInfo infoA = a.get(j);
                if (infoA.id.equals(infoB.id)) {
                    break;
                }
                if (j == a.size() - 1) {
                    return false;
                }
            }
        }

        return true;
    }
}
