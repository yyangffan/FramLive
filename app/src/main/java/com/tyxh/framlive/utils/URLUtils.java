package com.tyxh.framlive.utils;

import com.tencent.qcloud.tuicore.TUILogin;
import com.tyxh.framlive.base.Constant;
import com.tyxh.framlive.base.GenerateGlobalConfig;

import java.io.File;

/**
 * MLVB 移动直播地址生成
 * 详情请参考：「https://cloud.tencent.com/document/product/454/7915」
 * <p>
 * <p>
 * Generating Streaming URLs
 * See [https://cloud.tencent.com/document/product/454/7915].
 */
public class URLUtils {

    public static final String WEBRTC      = "webrtc://";
    public static final String RTMP        = "rtmp://";
    public static final String HTTP        = "http://";
    public static final String TRTC        = "trtc://";
    public static final String TRTC_DOMAIN = "cloud.tencent.com";
    public static final String APP_NAME    = "live";

    /**
     * 生成推流地址
     * Generating Publishing URLs
     *
     * @param streamId
     * @param type
     * @return
     */
    public static String generatePushUrl(String roomId, PushType type) {
        String pushUrl = "";
        if (type == PushType.RTC) {
  /*          pushUrl = TRTC + TRTC_DOMAIN + "/push/" + streamId + "?sdkappid=" + TUILogin.getSdkAppId() + "&userid="
                    + TUILogin.getUserId() + "&usersig=" + TUILogin.getUserSig();  */
            pushUrl = TRTC + TRTC_DOMAIN + "/push/" + roomId + "?sdkappid=" + Constant.SDKAPPID + "&userid="
                    + roomId + "&usersig=" +  GenerateGlobalConfig.genTestUserSig(roomId);
        }
        return pushUrl;
    }

    /**
     * 生成拉流地址
     * Generating Playback URLs
     *
     * @param streamId
     * @param type
     * @return
     */
    public static String generatePlayUrl(String streamId, PlayType type) {
        String playUrl = "";
        if (type == PlayType.RTC) {
            playUrl = "trtc://cloud.tencent.com/play/" + streamId + "?sdkappid=" +  Constant.SDKAPPID + "&userid="
                    + "23" + "&usersig=" + GenerateGlobalConfig.genTestUserSig("23");
        } else if (type == PlayType.RTMP) {
            playUrl = HTTP + GenerateGlobalConfig.PLAY_DOMAIN + File.separator + APP_NAME + File.separator + streamId
                    + ".flv";
        } else if (type == PlayType.WEBRTC) {
            playUrl = WEBRTC + GenerateGlobalConfig.PLAY_DOMAIN + File.separator + APP_NAME + File.separator + streamId;
        }
        return playUrl;
    }

    public enum PushType {
        RTC,
        RTMP
    }


    public enum PlayType {
        RTC,
        RTMP,
        WEBRTC
    }
}
