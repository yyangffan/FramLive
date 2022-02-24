package com.tyxh.framlive.base;


public class Constant {
    public static final boolean is_debug = true;

    public static final String URL_DEFAULT = "//appshop.bkxinli.com/";

    public static final String BASE_URL = "https:"+URL_DEFAULT+(is_debug?"portalTest/":"portal/");
    /*im聊天及直播通用*/
    public static final String SECRETKEY = is_debug ?
            "d861ed6966ddb7a241fd9de3b78592f1b8e06263a98de464460d27d146172119" : "5f0edf244ede892ff43f701c0cfab05085ced7c5eef4265c54e3ed07558a87d8";//聊天key
    public static final int SDKAPPID = is_debug ? 1400532861 : 1400522274;//聊天APPID
    /*腾讯云直播*/
    public static final String LICENCEURL = GenerateGlobalConfig.LICENSEURL; // 获取到的 licence url
    public static final String LICENCEKEY = GenerateGlobalConfig.LICENSEURLKEY; // 获取到的 licence key


}
