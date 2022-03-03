package com.tyxh.framlive.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.superc.yyfflibrary.utils.titlebar.TitleUtils;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMSendCallback;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.liteav.demo.scene.showlive.view.ShowAnchorFunctionView;
import com.tencent.qcloud.tuicore.TUILogin;
import com.tencent.qcloud.tuikit.tuipusher.view.TUIPusherView;
import com.tencent.qcloud.tuikit.tuipusher.view.listener.TUIPusherViewListener;
import com.tyxh.framlive.R;
import com.tyxh.framlive.base.GenerateGlobalConfig;
import com.tyxh.framlive.base.LiveBaseActivity;
import com.tyxh.framlive.callback.CommonCallback;
import com.tyxh.framlive.dig_pop.ConfirmDialogFragment;
import com.tyxh.framlive.utils.URLUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveAnchorActivity extends LiveBaseActivity implements TUIPusherViewListener {

    @BindView(R.id.anchor_function_view)
    ShowAnchorFunctionView mShowAnchorFunctionView;
    @BindView(R.id.anchor_pusher_view)
    TUIPusherView mTUIPusherView;
    private String roomId = "5";//userId

    @Override
    public int getContentLayoutId() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initStatusBar();
        return R.layout.activity_live_anchor;
    }

    @Override
    public void init() {
        ButterKnife.bind(this);
        TitleUtils.setStatusTextColor(false, this);
        initFunctionView();
        initViews();
        loginIm();
//        IMRoomManager();
    }

    private void initFunctionView() {
        mShowAnchorFunctionView.setTUIPusherView(mTUIPusherView);
        mShowAnchorFunctionView.setRoomId(roomId);
        mShowAnchorFunctionView.setListener(new ShowAnchorFunctionView.OnFunctionListener() {
            @Override
            public void onClose() {
//                showCloseDialog();
                finish();
            }
        });
        mShowAnchorFunctionView.setVisibility(View.GONE);
    }

    private void initViews() {
        startPush();
    }

    private void startPush() {
        String pushUrl = URLUtils.generatePushUrl(roomId, URLUtils.PushType.RTC);
        mTUIPusherView.start(pushUrl);
        mTUIPusherView.setTUIPusherViewListener(this);
    }

    @Override
    public void onPushStarted(TUIPusherView pushView, String url) {
        mShowAnchorFunctionView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPushStoped(TUIPusherView pushView, String url) {

    }

    @Override
    public void onPushEvent(TUIPusherView pusherView, TUIPusherEvent event, String message) {

    }

    @Override
    public void onClickStartPushButton(TUIPusherView pushView, String url, ResponseCallback callback) {
        creatRoom(new CommonCallback() {
            @Override
            public void onCallback(int code, String msg) {
                if(code == 0) {
                    mShowAnchorFunctionView.startRecordAnimation();
                    mTUIPusherView.setGroupId(roomId);
                    startTimer();
                    callback.response(true);
                }
            }
        });

    }

    @Override
    public void onReceivePKRequest(TUIPusherView pushView, String userId, ResponseCallback callback) {

    }

    @Override
    public void onRejectPKResponse(TUIPusherView pusherView, int reason) {

    }

    @Override
    public void onCancelPKRequest(TUIPusherView pusherView) {

    }

    @Override
    public void onStartPK(TUIPusherView pusherView) {

    }

    @Override
    public void onStopPK(TUIPusherView pusherView) {

    }

    @Override
    public void onPKTimeout(TUIPusherView pusherView) {

    }

    @Override
    public void onReceiveJoinAnchorRequest(TUIPusherView pushView, String userId, ResponseCallback callback) {
        showLinkDialog(userId, callback);
    }

    @Override
    public void onCancelJoinAnchorRequest(TUIPusherView pusherView) {

    }

    @Override
    public void onStartJoinAnchor(TUIPusherView pusherView) {

    }

    @Override
    public void onStopJoinAnchor(TUIPusherView pusherView) {

    }

    @Override
    public void onJoinAnchorTimeout(TUIPusherView pusherView) {

    }
    private ConfirmDialogFragment  mLinkDialog;
    private void showLinkDialog(String userId, final TUIPusherViewListener.ResponseCallback callback) {
        if (isFinishing()) {
            return;
        }
        if (mLinkDialog == null) {
            mLinkDialog = new ConfirmDialogFragment();
            mLinkDialog.setMessage(userId + getString(R.string.app_request_link));
            mLinkDialog.setNegativeText(getString(R.string.app_reject));
            mLinkDialog.setNegativeClickListener(new ConfirmDialogFragment.NegativeClickListener() {
                @Override
                public void onClick() {
                    callback.response(false);
                    mLinkDialog.dismiss();
                }
            });
            mLinkDialog.setPositiveText(getString(R.string.app_accept));
            mLinkDialog.setPositiveClickListener(new ConfirmDialogFragment.PositiveClickListener() {
                @Override
                public void onClick() {
                    callback.response(true);
                    mLinkDialog.dismiss();
                }
            });
        }
        mLinkDialog.show(getFragmentManager(), "confirm_link");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        mTUIPusherView.stop();
    }

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private Timer mBroadcastTimer;        // 定时的 Timer
    private BroadcastTimerTask mBroadcastTimerTask;    // 定时任务
    private long mSecond = 0;       // 开播的时间，单位为秒

    private void startTimer() {
        if (mBroadcastTimer == null) {
            mBroadcastTimer = new Timer(true);
            mBroadcastTimerTask = new BroadcastTimerTask();
            mBroadcastTimer.schedule(mBroadcastTimerTask, 1000, 1000);
        }
    }

    private void stopTimer() {
        if (null != mBroadcastTimer) {
            mBroadcastTimerTask.cancel();
        }
    }

    /**
     * 直播时间记时器类；
     */
    private class BroadcastTimerTask extends TimerTask {
        public void run() {
            ++mSecond;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mShowAnchorFunctionView.updateBroadcasterTimeUpdate(mSecond);
                }
            });
        }
    }
/*    private LiveRoomSimpleMsgListener mSimpleListener;
    private LiveRoomGroupListener     mGroupListener;
    private IMRoomManager() {

        mSimpleListener = new LiveRoomSimpleMsgListener();
        mGroupListener = new LiveRoomGroupListener();
    }*/

    /*--------------------------------------Im创建房间--------------------------------------*/

    private void loginIm(){
        String userSig = GenerateGlobalConfig.genTestUserSig(roomId);
        TUILogin.login(roomId, userSig, new V2TIMCallback() {
            @Override
            public void onError(int i, String s) {
                ToastShow("登录失败");
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "login im success.");
                ToastShow("登录成功");
            }
        });



    }

    private void creatRoom(final CommonCallback callback){
        V2TIMManager.getInstance().createGroup(V2TIMManager.GROUP_TYPE_AVCHATROOM, roomId, "我的房间", new V2TIMValueCallback<String>() {
            @Override
            public void onError(int code, String s) {
                String msg = s;
                if (code == 10036) {
                    msg = LiveAnchorActivity.this.getString(R.string.app_create_room_limit);
                }
                if (code == 10037) {
                    msg = LiveAnchorActivity.this.getString(R.string.app_create_or_join_group_limit);
                }
                if (code == 10038) {
                    msg = LiveAnchorActivity.this.getString(R.string.app_group_member_limit);
                }
                if (code == 10025) {
                    // 10025 表明群主是自己，那么认为创建房间成功
                    onSuccess("success");
                } else {
                    Log.e(TAG, "create room fail, code:" + code + " msg:" + msg);
                    if (callback != null) {
                        callback.onCallback(code, msg);
                    }
                }
            }

            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "createGroup onSuccess s: " + s);
//                V2TIMManager.getInstance().addSimpleMsgListener(mSimpleListener);
//                V2TIMManager.getInstance().setGroupListener(mGroupListener);
                if (callback != null) {
                    callback.onCallback(0, "create room success.");
                }
            }
        });
    }

}