package com.tyxh.framlive.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.superc.yyfflibrary.utils.titlebar.TitleUtils;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.qcloud.tuicore.TUILogin;
import com.tencent.qcloud.tuikit.tuiplayer.view.TUIPlayerView;
import com.tencent.qcloud.tuikit.tuiplayer.view.listener.TUIPlayerViewListener;
import com.tyxh.framlive.R;
import com.tyxh.framlive.base.GenerateGlobalConfig;
import com.tyxh.framlive.base.LiveBaseActivity;
import com.tyxh.framlive.callback.CommonCallback;
import com.tyxh.framlive.utils.URLUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/********************************************************************
 @version: 1.0.0
 @description: 观众端
 @author: admin
 @time: 2022/2/22 14:35
 @变更历史:
 ********************************************************************/
public class LiveAudienceActivity extends LiveBaseActivity {
    @BindView(R.id.live_audience_linear)
    LinearLayout mLinearLayout;

    TUIPlayerView mTUIPlayerView;
    private String mRoomId = "1024";
    private String userId = "23";

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_live_audience;
    }

    @Override
    public void init() {
        TitleUtils.setStatusTextColor(false, this);
        ButterKnife.bind(this);
        loginIm();

    }

    private void startPlay() {
        mTUIPlayerView =new TUIPlayerView(this);
        mLinearLayout.addView(mTUIPlayerView);
        mTUIPlayerView.setGroupID(mRoomId + "");
        String playUrl = URLUtils.generatePlayUrl(mRoomId, URLUtils.PlayType.RTC);
        mTUIPlayerView.startPlay(playUrl);
        mTUIPlayerView.pauseAudio();
        mTUIPlayerView.setTUIPlayerViewListener(new TUIPlayerViewListener() {
            @Override
            public void onPlayStarted(TUIPlayerView playView, String url) {

            }

            @Override
            public void onPlayStoped(TUIPlayerView playView, String url) {

            }

            @Override
            public void onPlayEvent(TUIPlayerView playView, TUIPlayerEvent event, String message) {

            }

            @Override
            public void onRejectJoinAnchorResponse(TUIPlayerView playView, int reason) {

            }
        });
        mTUIPlayerView.resumeVideo();
        mTUIPlayerView.resumeAudio();

    }
    private void loginIm(){
        String userSig = GenerateGlobalConfig.genTestUserSig(userId);
        TUILogin.login(userId, userSig, new V2TIMCallback() {
            @Override
            public void onError(int i, String s) {
                ToastShow("登录失败");
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "login im success.");
                joinGroup(mRoomId, new CommonCallback() {
                    @Override
                    public void onCallback(int code, String msg) {
                        if (code == 0) {
                            startPlay();
                            ToastShow("加入成功");
                        }else{
                            ToastShow("加入失败");
                        }

                    }
                });
            }
        });



    }


    public void joinGroup(final String roomId, final CommonCallback callback) {
        V2TIMManager.getInstance().joinGroup(roomId, "", new V2TIMCallback() {
            @Override
            public void onError(int i, String s) {
                // 已经是群成员了，可以继续操作
                if (i == 10013) {
                    onSuccess();
                } else {
                    Log.e(TAG, "enter room fail, code:" + i + " msg:" + s);
                    if (callback != null) {
                        callback.onCallback(i, s);
                    }
                }
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "enter room success. roomId: " + roomId);
//                V2TIMManager.getInstance().addSimpleMsgListener(mSimpleListener);
//                V2TIMManager.getInstance().setGroupListener(mGroupListener);

                if (callback != null) {
                    callback.onCallback(0, "success");
                }

            }
        });
    }

}