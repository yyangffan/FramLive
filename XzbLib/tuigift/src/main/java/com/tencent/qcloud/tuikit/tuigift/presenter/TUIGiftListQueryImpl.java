package com.tencent.qcloud.tuikit.tuigift.presenter;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.tencent.qcloud.tuikit.tuigift.model.TUIGiftBean;
import com.tencent.qcloud.tuikit.tuigift.model.TUIGiftModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 礼物数据查询实现
 */
public class TUIGiftListQueryImpl extends TUIGiftListQuery implements TUIHttpGetRequest.HttpListener {
    private static final String TAG = "TUIGiftListQueryImpl";

    private static final int    CORE_POOL_SIZE = 5;
    private static final String GIFT_DATA_URL  = "https://liteav.sdk.qcloud.com/app/res/picture/live/gift/gift_data.json";

    private GiftBeanThreadPool      mGiftBeanThreadPool;
    private TUIGiftCallBack.OnGiftListQueryCallback mOnGiftListQueryCallback;

    @Override
    public void queryGiftInfoList(final TUIGiftCallBack.OnGiftListQueryCallback callback) {
        mOnGiftListQueryCallback = callback;
        ThreadPoolExecutor threadPoolExecutor = getThreadExecutor();
        TUIHttpGetRequest request = new TUIHttpGetRequest(GIFT_DATA_URL, this);
        threadPoolExecutor.execute(request);
    }

    private synchronized ThreadPoolExecutor getThreadExecutor() {
        if (mGiftBeanThreadPool == null || mGiftBeanThreadPool.isShutdown()) {
            mGiftBeanThreadPool = new GiftBeanThreadPool(CORE_POOL_SIZE);
        }
        return mGiftBeanThreadPool;
    }

    @Override
    public void success(String response) {
        handleResponseMessage(response);
    }

    @Override
    public void onFailed(String message) {
        if (mOnGiftListQueryCallback != null) {
            mOnGiftListQueryCallback.onGiftListQueryFailed(message);
        }
    }

    private void handleResponseMessage(String response) {
        if (response == null) {
            return;
        }
        Gson gson = new Gson();
        TUIGiftBean TUIGiftBean = gson.fromJson(response, TUIGiftBean.class);
        Log.i("summerccao","    "+ TUIGiftBean.toString());
        TUIGiftBean.GiftListBean giftBean2 = gson.fromJson(response, TUIGiftBean.GiftListBean.class);
        Log.i("summerccao2","    "+giftBean2.toString());
        final List<TUIGiftModel> giftDataList = transformGiftInfoList(TUIGiftBean);
        if (giftDataList != null) {
            if (mOnGiftListQueryCallback != null) {
                mOnGiftListQueryCallback.onGiftListQuerySuccess(giftDataList);
            }
        }
    }

    private List<TUIGiftModel> transformGiftInfoList(TUIGiftBean TUIGiftBean) {
        if (TUIGiftBean == null) {
            return null;
        }
        List<TUIGiftBean.GiftListBean> giftBeanList = TUIGiftBean.getGiftList();
        if (giftBeanList == null) {
            return null;
        }
        List<TUIGiftModel> giftInfoList = new ArrayList<>();
        for (TUIGiftBean.GiftListBean bean : giftBeanList) {
            TUIGiftModel giftModel = new TUIGiftModel();
            giftModel.giftId = bean.getGiftId();
            giftModel.giveDesc = bean.getTitle();
            giftModel.normalImageUrl = bean.getGiftImageUrl();
            giftModel.animationUrl = bean.getLottieUrl();
            giftInfoList.add(giftModel);
        }
        return giftInfoList;
    }

    public static class GiftBeanThreadPool extends ThreadPoolExecutor {
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        public GiftBeanThreadPool(int poolSize) {
            super(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(),
                    Executors.defaultThreadFactory(), new AbortPolicy());
        }
    }
}
