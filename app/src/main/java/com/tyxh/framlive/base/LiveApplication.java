package com.tyxh.framlive.base;

import android.app.Application;
import android.app.UiModeManager;
import android.content.Context;
import android.net.http.HttpResponseCache;
import android.util.Log;


import java.io.File;
import java.io.IOException;

class LiveApplication extends Application {

    private static volatile LiveApplication instance;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initDevring();
        if(true) {
            init();
        }

    }

    public void init() {


    }

    /*初始化网络框架*/
    private void initDevring() {
      /*  try {
            File cacheDir = new File(getCacheDir(), "https");
            HttpResponseCache.install(cacheDir, 1024 * 1024 * 128);
        } catch (IOException e) {
            Log.e(TAG, "init: " + e.toString());
        }
        DevRing.init(this);
        DevRing.configureHttp().setBaseUrl(Constant.BASE_URL).setIsUseCookie(true).setConnectTimeout(60).setIsUseLog(Constant.PRINT_LOG);
        DevRing.configureOther().setIsUseCrashDiary(true);
        DevRing.configureImage();
        DevRing.create();
        // 获取uimode系统服务---为深色、夜间模式的相关逻辑
        UiModeManager uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
//        int currentMode = uiModeManager.getNightMode();
        *//*设置夜间状态 UiModeManager.MODE_NIGHT_AUTO ⾃动  UiModeManager.MODE_NIGHT_YES 启⽤*//*
        uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_NO); // 停⽤

*/
    }


}
