package com.cqsynet.heikuai.util;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.cqsynet.heikuai.common.AppConstants;
import com.cqsynet.heikuai.common.Globals;
import com.cqsynet.heikuai.model.CloseFreeWifiRequestBody;
import com.cqsynet.heikuai.network.WebServiceIf;

import org.json.JSONException;

import java.util.Iterator;

/**
 * Author: sayaki
 * Date: 2017/8/24
 */
public class LogoutUtil {

    /**
     * 断开外网连接
     */
    public static void disconnectWifi(Context context) {
        CloseFreeWifiRequestBody requestBody = new CloseFreeWifiRequestBody();
        WebServiceIf.closeFreeWifi(context, requestBody, new WebServiceIf.IResponseCallback() {
            @Override
            public void onResponse(String response) throws JSONException {}

            @Override
            public void onErrorResponse() {}
        });

        Intent intent = new Intent(AppConstants.ACTION_SOCKET_LOGOUT);
        context.sendBroadcast(intent);

        cleanLoginInfo(context);
    }

    /**
     * 清除用户信息
     */
    public static void cleanLoginInfo(Context context) {
        SharedPreferencesInfo.setTagLong(context, SharedPreferencesInfo.FREE_WIFI_START_TIME, 0l);

        SharedPreferencesInfo.setTagInt(context, SharedPreferencesInfo.IS_LOGIIN, 0);
        SharedPreferencesInfo.removeData(context, SharedPreferencesInfo.ACCOUNT);
        SharedPreferencesInfo.removeData(context, SharedPreferencesInfo.PUSH_TAG_LIST);
        SharedPreferencesInfo.removeData(context, SharedPreferencesInfo.READED);
        SharedPreferencesInfo.removeData(context, SharedPreferencesInfo.RSA_KEY);
        SharedPreferencesInfo.removeData(context, SharedPreferencesInfo.PHONE_NUM);
        SharedPreferencesInfo.removeData(context, SharedPreferencesInfo.CHANNELS);
        Globals.g_userInfo = null;
        Globals.g_isUpdateUserGroup = false;

        //@@@@临时注释
//        Intent timerIntent = new Intent(context, TimerService.class);
//        context.getApplicationContext().startService(timerIntent);

        //清除通知栏提示
        if(Globals.g_notificationList != null) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Iterator<Integer> it = Globals.g_notificationList.iterator();
            while (it.hasNext()) {
                int id = it.next();
                nm.cancel(id);
            }
            Globals.g_notificationList.clear();
        }
    }
}
