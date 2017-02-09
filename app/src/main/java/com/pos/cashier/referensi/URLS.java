package com.pos.cashier.referensi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class URLS {

    private static String getBaseUrl(Context context) {
        SharedPreferences appsPref = context.getSharedPreferences(URLS.PREF_NAME, Activity.MODE_PRIVATE);
        return appsPref.getString(Constant.KEY_BASE_URL, "");
    }

    public static String getSalesmanUrl(Context context) {
        return getBaseUrl(context) + "weblayer/template/api,SPGApps.vm?cmd=1";
    }

    public static String getStockUrl(Context context) {
        return getBaseUrl(context) + "weblayer/template/api,SPGApps.vm?cmd=2&loccode=HO";
    }

    // SharedPreferences
    public static String PREF_NAME = "PosCashier";
}
