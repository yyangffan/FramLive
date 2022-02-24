package com.tyxh.framlive.ui.act;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.superc.yyfflibrary.utils.titlebar.TitleUtils;
import com.superc.yyfflibrary.views.lowhurdles.TabFragmentAdapter;
import com.tencent.imsdk.v2.V2TIMSDKConfig;
import com.tencent.imsdk.v2.V2TIMSDKListener;
import com.tencent.qcloud.tuicore.TUILogin;
import com.tyxh.framlive.R;
import com.tyxh.framlive.base.Constant;
import com.tyxh.framlive.base.LiveBaseActivity;
import com.tyxh.framlive.ui.frag.HomeFragment;
import com.tyxh.framlive.ui.frag.MineFragment;
import com.tyxh.framlive.views.MainViewpager;
import com.tyxh.framlive.views.tabviews.TabContainerView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends LiveBaseActivity implements ViewPager.OnPageChangeListener {
    @BindView(R.id.tab_pager)
    MainViewpager mPager;
    @BindView(R.id.ll_tab_container)
    TabContainerView mTabLayout;

    private HomeFragment mHomeFragment;
    private MineFragment mMineFragment;
    /*tab图标集合  未选中  选中*/
    private final int ICONS_RES[][] = {{R.drawable.home_se, R.drawable.home_se}, {R.drawable.home_se, R.drawable.home_se}};
    /* tab 颜色值*/
    private final int[] TAB_COLORS = new int[]{R.color.gray, R.color.black};
    private String[] titles = new String[]{"首页", "我的"};
    private Fragment[] fragments = null;


    @Override
    public int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void init() {
        ButterKnife.bind(this);
        TitleUtils.setStatusTextColor(true, this);
        initViews();
        initTim();
    }

    private void initTim() {
        V2TIMSDKConfig config = new V2TIMSDKConfig();
        config.setLogLevel(V2TIMSDKConfig.V2TIM_LOG_DEBUG);
        TUILogin.init(this, Constant.SDKAPPID, config,
                new V2TIMSDKListener() {
                    @Override
                    public void onConnecting() {
                    }

                    @Override
                    public void onConnectSuccess() {

                    }

                    @Override
                    public void onConnectFailed(int code, String error) {
                        Log.e(TAG, "init im sdk error.");
                    }
                });

    }

    private void initViews() {
        mHomeFragment = new HomeFragment();
        mMineFragment = new MineFragment();

        fragments = new Fragment[]{mHomeFragment, mMineFragment};
        TabFragmentAdapter mAdapter = new TabFragmentAdapter(getSupportFragmentManager(), fragments);
//        mPager.setNoScroll(true);
        mPager.setOffscreenPageLimit(3);
        mPager.setAdapter(mAdapter);


        mTabLayout.setOnPageChangeListener(this);
        mTabLayout.initContainer(titles, ICONS_RES, TAB_COLORS, false);
        int width = getResources().getDimensionPixelSize(R.dimen.tab_icon_width);
        int height = getResources().getDimensionPixelSize(R.dimen.tab_icon_height);
        mTabLayout.setContainerLayout(R.layout.tab_container_view, R.id.iv_tab_icon, R.id.tv_tab_text, width, height);
        mTabLayout.setViewPager(mPager);
        mPager.setCurrentItem(getIntent().getIntExtra("tab", 0));
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int index = 0, len = fragments.length; index < len; index++) {
            fragments[index].onHiddenChanged(index != position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}