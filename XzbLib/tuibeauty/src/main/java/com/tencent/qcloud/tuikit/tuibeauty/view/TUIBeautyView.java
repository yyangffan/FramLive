package com.tencent.qcloud.tuikit.tuibeauty.view;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tencent.liteav.beauty.TXBeautyManager;
import com.tencent.qcloud.tuikit.tuibeauty.R;
import com.tencent.qcloud.tuikit.tuibeauty.model.TUIBeautyInfo;
import com.tencent.qcloud.tuikit.tuibeauty.model.TUIBeautyItemInfo;
import com.tencent.qcloud.tuikit.tuibeauty.model.TUIBeautyTabInfo;
import com.tencent.qcloud.tuikit.tuibeauty.presenter.ITUIBeautyPresenter;
import com.tencent.qcloud.tuikit.tuibeauty.presenter.TUIBeautyPresenter;
import com.tencent.qcloud.tuikit.tuibeauty.view.adapter.TUIBeautyItemAdapter;
import com.tencent.qcloud.tuikit.tuibeauty.view.adapter.TUIBeautyTabAdapter;
import com.tencent.qcloud.tuikit.tuibeauty.view.internal.TCHorizontalScrollView;
import com.tencent.qcloud.tuikit.tuibeauty.view.utils.TUIBeautyResourceParse;

/**
 * 美颜面板
 */
public class TUIBeautyView extends BottomSheetDialog {
    private static final String TAG = "TUIBeautyView";

    private Context                mContext;
    private TCHorizontalScrollView mScrollTabView;
    private TCHorizontalScrollView mScrollItemView;
    private LinearLayout           mRelativeSeekBarLayout;
    private SeekBar                mSeekBarLevel;
    private TextView               mTextLevelHint;
    private TextView               mTextLevelValue;

    private TUIBeautyInfo       mTUIBeautyInfo;
    private TUIBeautyTabInfo    mCurrentTabInfo;
    private TUIBeautyItemInfo[] mCurrentItemInfo;
    private int                 mCurrentTabPosition = 0;
    private int[]               mCurrentItemPosition;
    private OnBeautyListener    mOnBeautyListener;
    private TXBeautyManager     mBeautyManager;
    private ITUIBeautyPresenter mPresenter;

    public TUIBeautyView(Context context, TXBeautyManager manager) {
        super(context, R.style.TUIBeautyDialogTheme);
        setContentView(R.layout.tuibeauty_view_panel);
        this.mContext = context;
        this.mBeautyManager = manager;
        Window window = getWindow();
        if (window != null) {
            window.findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        }
        initPresenter();
        initView(context);
    }

    private void initPresenter() {
        if (mPresenter == null) {
            mPresenter = new TUIBeautyPresenter(mContext, mBeautyManager);
        }
        mPresenter.clear();
        mTUIBeautyInfo = mPresenter.getDefaultBeauty();
        initData();
        //本地路径
        mPresenter.fillingMaterialPath(mTUIBeautyInfo);
    }

    //初始化美颜面板界面参数
    private void initData() {
        int tabSize = mTUIBeautyInfo.getBeautyTabList().size();
        mCurrentItemPosition = new int[tabSize];
        mCurrentItemInfo = new TUIBeautyItemInfo[tabSize];

        for (int i = 0; i < tabSize; i++) {
            TUIBeautyTabInfo TUIBeautyTabInfo = mTUIBeautyInfo.getBeautyTabList().get(i);
            mCurrentItemPosition[i] = TUIBeautyTabInfo.getTabItemListDefaultSelectedIndex();
            mCurrentItemInfo[i] = TUIBeautyTabInfo.getTabItemList().get(TUIBeautyTabInfo.getTabItemListDefaultSelectedIndex());
            mPresenter.setBeautySpecialEffects(TUIBeautyTabInfo, i, mCurrentItemInfo[i], mCurrentItemPosition[i]);
        }
    }

    private void initView(Context context) {
        mRelativeSeekBarLayout = (LinearLayout) findViewById(R.id.beauty_rl_seek_bar);
        mSeekBarLevel = (SeekBar) findViewById(R.id.beauty_seek_bar_third);
        mTextLevelHint = (TextView) findViewById(R.id.beauty_tv_seek_bar_hint);
        mTextLevelValue = (TextView) findViewById(R.id.beauty_tv_seek_bar_value);
        TextView textTitle = (TextView) findViewById(R.id.beauty_tv_title);
        mSeekBarLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCurrentItemInfo[mCurrentTabPosition].setItemLevel(progress);
                mTextLevelValue.setText(String.valueOf(progress));
                if (mOnBeautyListener == null || !mOnBeautyListener.onLevelChanged(mCurrentTabInfo, mCurrentTabPosition,
                        mCurrentItemInfo[mCurrentTabPosition], mCurrentItemPosition[mCurrentTabPosition], progress)) {
                    mPresenter.setBeautySpecialEffects(mCurrentTabInfo, mCurrentTabPosition,
                            mCurrentItemInfo[mCurrentTabPosition], mCurrentItemPosition[mCurrentTabPosition]);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mScrollTabView = (TCHorizontalScrollView) findViewById(R.id.beauty_horizontal_picker_view_first);
        mScrollItemView = (TCHorizontalScrollView) findViewById(R.id.beauty_horizontal_picker_second);

        createTabList();
    }

    //创建美颜面板
    private void createTabList() {
        if (mTUIBeautyInfo == null) {
            mTUIBeautyInfo = mPresenter.getDefaultBeauty();
        }
        TUIBeautyTabAdapter TUIBeautyTabAdapter = new TUIBeautyTabAdapter(mContext, mTUIBeautyInfo);
        mScrollTabView.setAdapter(TUIBeautyTabAdapter);
        TUIBeautyTabAdapter.setOnTabClickListener(new TUIBeautyTabAdapter.OnTabChangeListener() {
            @Override
            public void onTabChange(TUIBeautyTabInfo TUIBeautyTabInfo, int position) {
                mCurrentTabInfo = TUIBeautyTabInfo;
                mCurrentTabPosition = position;
                createItemList(TUIBeautyTabInfo, position);
                if (mOnBeautyListener != null) {
                    mOnBeautyListener.onTabChange(TUIBeautyTabInfo, position);
                }
            }
        });
        TUIBeautyTabInfo TUIBeautyTabInfo = mTUIBeautyInfo.getBeautyTabList().get(0);
        mCurrentTabInfo = TUIBeautyTabInfo;
        mCurrentTabPosition = 0;
        createItemList(TUIBeautyTabInfo, 0);
    }

    //创建每一个类型的Item
    private void createItemList(final TUIBeautyTabInfo tabInfo, final int tabPosition) {
        TUIBeautyItemAdapter TUIBeautyItemAdapter = new TUIBeautyItemAdapter(mContext);
        TUIBeautyItemAdapter.setData(tabInfo, mCurrentItemPosition[tabPosition]);
        mScrollItemView.setAdapter(TUIBeautyItemAdapter);
        mScrollItemView.setClicked(mCurrentItemPosition[tabPosition]);
        TUIBeautyItemAdapter.setOnItemClickListener(new TUIBeautyItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TUIBeautyItemInfo itemInfo, int position) {
                mCurrentItemPosition[tabPosition] = position;
                mCurrentItemInfo[tabPosition] = itemInfo;
                createSeekBar(tabInfo, itemInfo);
                if (mOnBeautyListener == null
                        || !mOnBeautyListener.onClick(tabInfo, tabPosition, itemInfo, position)) {
                    mPresenter.setBeautySpecialEffects(tabInfo, tabPosition, itemInfo, position);
                }
            }
        });
        TUIBeautyItemInfo TUIBeautyItemInfo = tabInfo.getTabItemList().get(mCurrentItemPosition[tabPosition]);
        mCurrentItemInfo[tabPosition] = TUIBeautyItemInfo;
        createSeekBar(tabInfo, TUIBeautyItemInfo);
    }

    // 创建强度视图
    private void createSeekBar(TUIBeautyTabInfo TUIBeautyTabInfo, TUIBeautyItemInfo TUIBeautyItemInfo) {
        int visibility;
        if (TUIBeautyItemInfo.getItemLevel() == -1) {
            visibility = View.GONE;
        } else {
            visibility = View.VISIBLE;
            mTextLevelValue.setText(String.valueOf(TUIBeautyItemInfo.getItemLevel()));
            mSeekBarLevel.setProgress(TUIBeautyItemInfo.getItemLevel());
            TUIBeautyResourceParse.setTextViewSize(mTextLevelHint, TUIBeautyTabInfo.getTabItemLevelHintSize());
            TUIBeautyResourceParse.setTextViewColor(mTextLevelHint, TUIBeautyTabInfo.getTabItemLevelHintColor());
            TUIBeautyResourceParse.setTextViewSize(mTextLevelValue, TUIBeautyTabInfo.getTabItemLevelValueSize());
            TUIBeautyResourceParse.setTextViewColor(mTextLevelValue, TUIBeautyTabInfo.getTabItemLevelValueColor());
        }
        mRelativeSeekBarLayout.setVisibility(visibility);
    }

    /**
     * 美颜特效点击回调
     *
     * @param onBeautyListener
     */
    public void setOnBeautyListener(OnBeautyListener onBeautyListener) {
        mOnBeautyListener = onBeautyListener;
    }

    public abstract static class OnBeautyListener {
        public void onTabChange(TUIBeautyTabInfo TUIBeautyTabInfo, int position) {
        }

        public boolean onClose() {
            return true;
        }

        public boolean onClick(TUIBeautyTabInfo TUIBeautyTabInfo, int tabPosition, TUIBeautyItemInfo TUIBeautyItemInfo, int itemPosition) {
            return false;
        }

        public boolean onLevelChanged(TUIBeautyTabInfo TUIBeautyTabInfo, int tabPosition, TUIBeautyItemInfo TUIBeautyItemInfo, int itemPosition, int beautyLevel) {
            return false;
        }
    }
}