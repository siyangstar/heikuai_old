/*
 * Copyright (C) 2014 重庆尚渝
 * 版权所有
 *
 * 功能描述：http请求头,json格式
 *
 *
 * 创建标识：zhaosy 20141103
 */
package com.cqsynet.swifi.model;

import android.content.Context;
import android.text.TextUtils;

import com.cqsynet.swifi.util.AppUtil;
import com.cqsynet.swifi.util.SharedPreferencesInfo;

public class RequestHeader {
	public static RequestHeader mRequestHead;
	public String osType; // android，iOS
	public String osVer;
	public String phoneModel;
	public String brand;
	public String channelId;
	public String imei;
	public String imsi;
	public String mac; // 手机mac地址
	public String resolution;
	public String longitude; //经度
	public String latitude; //纬度

	public static synchronized RequestHeader initHeader(Context context) {
		if (mRequestHead == null) {
			mRequestHead = new RequestHeader();
			mRequestHead.osType = "android";
			mRequestHead.osVer = android.os.Build.VERSION.RELEASE;
			mRequestHead.phoneModel = AppUtil.getPhoneModel(); 
			mRequestHead.brand = android.os.Build.BRAND;
			mRequestHead.channelId = AppUtil.getMetaData(context, "channelKey");
			mRequestHead.imei = AppUtil.getIMEI(context);
			mRequestHead.imsi = AppUtil.getIMSI(context);
			try {
				mRequestHead.resolution = AppUtil.getScreenResolution(context);
			} catch(Exception e) {
				e.printStackTrace();
			}			
		}
		mRequestHead.latitude = SharedPreferencesInfo.getTagString(context, "latitude");
		mRequestHead.longitude = SharedPreferencesInfo.getTagString(context, "longitude");
		mRequestHead.mac = AppUtil.getMac(context);
		if(TextUtils.isEmpty(mRequestHead.imei)) {
			mRequestHead.imei = "111111111111111";
		}
		if(TextUtils.isEmpty(mRequestHead.imsi)) {
			mRequestHead.imsi = "111111111111111";
		}
		if(TextUtils.isEmpty(mRequestHead.mac)) {
			mRequestHead.mac = "11:11:11:11:11:11";
		}
		if(TextUtils.isEmpty(mRequestHead.resolution)) {
			mRequestHead.resolution = "720*1280";
		}
		return mRequestHead;
	}
}