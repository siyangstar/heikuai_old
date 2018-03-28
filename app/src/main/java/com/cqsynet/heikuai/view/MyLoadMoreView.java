package com.cqsynet.heikuai.view;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.cqsynet.heikuai.R;

public class MyLoadMoreView extends LoadMoreView {

    @Override
    public int getLayoutId() {
        return R.layout.load_more_footer;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }
}
