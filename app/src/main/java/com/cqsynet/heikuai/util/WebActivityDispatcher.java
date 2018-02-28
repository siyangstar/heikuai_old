/*
 * Copyright (C) 2015 重庆尚渝
 * 版权所有
 *
 * 用于分发不同webview的跳转:
 * 1.常规内网内容页面
 * 2.带有requestCode的内网内容页面
 * 3.有赞页面
 *
 * 创建标识：zhaosy 20160729
 */
package com.cqsynet.heikuai.util;

import android.content.Context;
import android.content.Intent;

/**
 * Created by USER-PC on 2016/7/29.
 */
public class WebActivityDispatcher {

    private int mType;
    private static final int NORMAL = 0;
    private static final int NORMAL_RESPONSE = 1;
    private static final int YOUZAN = 2;

    /**
     *
     * @param intent
     * @param context
     */
    public void dispatch(Intent intent, Context context) {
        dispatch(intent, context, -1);
    }

    /**
     *
     * @param intent
     * @param context
     * @param requestCode
     */
    public void dispatch(Intent intent, Context context, int requestCode) {
//        String url = intent.getStringExtra("url");
//        if (url.toLowerCase().contains("&yz=1") || url.toLowerCase().contains("?yz=1")) {
//            mType = YOUZAN;
//        } else if(requestCode != -1) {
//            mType = NORMAL_RESPONSE;
//        } else {
//            mType = NORMAL;
//        }
//        switch (mType) {
//            case NORMAL:
//                intent.setClass(context, WebActivity.class);
//                context.startActivity(intent);
//                break;
//            case NORMAL_RESPONSE:
//                intent.setClass(context, WebActivity.class);
//                ((Activity)context).startActivityForResult(intent, requestCode);
//                break;
//            case YOUZAN:
//                intent.setClass(context, YouzanWebActivity.class);
//                context.startActivity(intent);
//                break;
//        }
    }

}
