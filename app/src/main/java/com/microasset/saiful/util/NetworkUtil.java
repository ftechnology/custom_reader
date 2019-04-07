package com.microasset.saiful.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public final class NetworkUtil {
    public static final int NETWORK_TYPE_NONE = 100;
    public static final int NETWORK_TYPE_2G = 101;
    public static final int NETWORK_TYPE_3G = 102;
    public static final int NETWORK_TYPE_4G = 103;
    public static final int NETWORK_TYPE_H_PLUS = 104;

    private NetworkUtil() {
    }

    /**
     * Take input Context and detect mobile network type
     * @return NETWORK_TYPE_NONE, NETWORK_TYPE_2G, NETWORK_TYPE_3G, NETWORK_TYPE_4G,
     * NETWORK_TYPE_H_PLUS (not implemented)
     */
    public static int getMobileNetworkType(Context context) {
        if (context != null) {
            ConnectivityManager connection = (ConnectivityManager) context.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connection != null) {
                NetworkInfo info = connection.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        int networkType = info.getSubtype();
                        switch (networkType) {
                            case TelephonyManager.NETWORK_TYPE_GPRS:
                            case TelephonyManager.NETWORK_TYPE_EDGE:
                            case TelephonyManager.NETWORK_TYPE_CDMA:
                            case TelephonyManager.NETWORK_TYPE_1xRTT:
                            case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                                return NETWORK_TYPE_2G;
                            case TelephonyManager.NETWORK_TYPE_UMTS:
                            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                            case TelephonyManager.NETWORK_TYPE_HSDPA:
                            case TelephonyManager.NETWORK_TYPE_HSUPA:
                            case TelephonyManager.NETWORK_TYPE_HSPA:
                            case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                            case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                            case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:  //api<25 : replace by 17
                                return NETWORK_TYPE_3G;
                            case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                            case TelephonyManager.NETWORK_TYPE_IWLAN:  //api<25 : replace by 18
                            case 19:  //LTE_CA
                                return NETWORK_TYPE_4G;
                        }
                    }
                }
            }
        }
        return NETWORK_TYPE_NONE;
    }
}
