package com.cqsynet.heikuai.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cqsynet.heikuai.R;
import com.cqsynet.heikuai.fragment.MineFragment;
import com.cqsynet.heikuai.fragment.NewsMainFragment;
import com.cqsynet.heikuai.fragment.WidgetsFragment;

public class HomeActivity extends BaseActivity {

    private Toolbar mToolbar;
    private BottomNavigationView mBottomNavi;
    private Fragment mCurrentFragment;
    private Fragment mNewsMainFragment;
    private Fragment mSocialFragment;
    private Fragment mMineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolbar = findViewById(R.id.toolbar_activity_home);
        mBottomNavi = findViewById(R.id.bottom_navigation_activity_home);

        setBottomNaviColor();

        mBottomNavi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_navigation_news:
                        if(mCurrentFragment != mNewsMainFragment) {
                            showHideFragment(mCurrentFragment, mNewsMainFragment);
                            mCurrentFragment = mNewsMainFragment;
                        }
                        return true;
                    case R.id.bottom_navigation_social:
                        if(mCurrentFragment != mSocialFragment) {
                            showHideFragment(mCurrentFragment, mSocialFragment);
                            mCurrentFragment = mSocialFragment;
                        }
                        return true;
                    case R.id.bottom_navigation_mine:
                        if(mCurrentFragment != mMineFragment) {
                            showHideFragment(mCurrentFragment, mMineFragment);
                            mCurrentFragment = mMineFragment;
                        }
                        return true;
                }
                return false;
            }
        });

        mNewsMainFragment = new NewsMainFragment();
        mSocialFragment = new WidgetsFragment();
        mMineFragment = new MineFragment();
        mCurrentFragment = mNewsMainFragment;
        getFragmentManager().beginTransaction().add(R.id.flContainer_activity_home, mNewsMainFragment, mNewsMainFragment.getClass().getSimpleName()).commitAllowingStateLoss();
    }

    /**
     * 设置底部导航栏颜色
     */
    private void setBottomNaviColor() {
        int[][] states = new int[][] {
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        };

        int[] colors = new int[]{getResources().getColor(R.color.grey_primary),
                getResources().getColor(R.color.colorAccent)
        };
        ColorStateList csl = new ColorStateList(states, colors);
        mBottomNavi.setItemTextColor(csl);
        mBottomNavi.setItemIconTintList(csl);
    }


    /**
     * 切换模块
     * @param outFragment 隐藏的模块
     * @param inFragment 显示的模块
     * @param <T>
     */
    public <T extends Fragment> void showHideFragment(T outFragment, T inFragment) {
        FragmentManager fManager = getFragmentManager();
        // 如果要隐藏的fragment非空，隐藏。
        if (outFragment != null) {
            fManager.beginTransaction()
                    .hide(outFragment)
                    .commit();
        }
        // 先从栈中看是否存在要显示的fagment。
        String tag = inFragment.getClass().getSimpleName();
        Fragment tempFragment = fManager.findFragmentByTag(tag);
        if(tempFragment != null) { // 存在则直接显示。
            fManager.beginTransaction()
                    .show(inFragment)
                    .commitAllowingStateLoss();
        } else { // 不存在就添加并显示。
            fManager.beginTransaction()
                    .add(R.id.flContainer_activity_home, inFragment, tag)
                    .addToBackStack(tag)
                    .commitAllowingStateLoss();
        }
    }

    /**
     * 添加角标
     * @param index 需要添加到的item序号
     * @param content 需要添加的内容
     */
    private void addBadge(int index, String content) {
        //获取整个的NavigationView
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) mBottomNavi.getChildAt(0);
        //这里就是获取所添加的每一个Tab(或者叫menu)，
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(index);
        View badge;
        //若已添加角标,获取即可,否则新创建的一个角标view
        if(itemView.getChildCount() > 2) {
            badge = itemView.getChildAt(2);
        } else {
            badge = LayoutInflater.from(HomeActivity.this).inflate(R.layout.view_badge, menuView, false);
            itemView.addView(badge);
        }
        TextView tvNum = badge.findViewById(R.id.tvNum_badge);
        tvNum.setText(content);
        badge.setVisibility(View.VISIBLE);
    }

    /**
     * 移除角标
     * @param index
     */
    private void removeBadge(int index) {
        //获取整个的NavigationView
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) mBottomNavi.getChildAt(0);
        //这里就是获取所添加的每一个Tab(或者叫menu)，
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(index);
        View badge;
        //若已添加角标,获取即可,否则新创建的一个角标view
        if(itemView.getChildCount() > 2) {
            badge = itemView.getChildAt(2);
            badge.setVisibility(View.GONE);
        }
    }
}
