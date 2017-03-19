package enjoytoday.com.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.ta.utdid2.android.utils.StringUtils;

import java.util.RandomAccess;

/**
 * Created by hfcai on 14/03/17.
 */

public class DeviceUtils {

    private static final String IMEI_KEY="imei";
    private static final String MODEL_KEY="model";
    private static final String MANUFACTURES_KEY="Manufacturers";
    private static final String APP_VERSION_KEY="app_version";
    private static final String CHANNEL_KEY="channel";


    private DeviceUtils() throws IllegalAccessException {
        throw  new IllegalAccessException("can not access.");
    }



    /**
     * 获取设备id(imei),全球唯一设备编号
     * @return
     */
    public static String getDeviceId(Context context) {
        String androidIMEI= PreferenceManager.getDefaultSharedPreferences(context).getString(IMEI_KEY,null);
        if (StringUtils.isEmpty(androidIMEI)){
            try {
                 androidIMEI = android.provider.Settings.System.getString(context.getContentResolver(), android.provider.Settings.System.ANDROID_ID);
                if (TextUtils.isEmpty(androidIMEI)) {
                    androidIMEI = "imei_not_found";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(IMEI_KEY, androidIMEI).commit();
        }
        return androidIMEI;
    }


    /**
     * 获取机型
     * @return
     */
    public static String getModel() {
        return Build.MODEL;
    }



    /**
     * 获取手机厂商
     * @return
     */
    public static String getManufacturers(){
        return android.os.Build.BRAND;
    }






    /**
     *
     * @return 获取app的版本号码
     */
    public static String getAppVersion(Context context){
        String appVersion=PreferenceManager.getDefaultSharedPreferences(context).getString(APP_VERSION_KEY,null);
        if (StringUtils.isEmpty(appVersion)) {
            try {
                PackageManager packageManager = context.getApplicationContext().getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(context.getApplicationContext().getPackageName(), 0);
                appVersion = packageInfo.versionName;

            } catch (Exception e) {
                e.printStackTrace();
                appVersion= "no_found_version";
            }
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(APP_VERSION_KEY,appVersion).commit();
        }
        return appVersion;

    }


    /**
     *
     * @return 获取渠道号码
     */
    public static String getChannel( Context context){
        String channel=PreferenceManager.getDefaultSharedPreferences(context).getString(CHANNEL_KEY,null);
        if (StringUtils.isEmpty(channel)) {
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = context.getApplicationContext().getPackageManager().
                        getApplicationInfo(context.getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo.metaData != null) {
                    channel= applicationInfo.metaData.getString("UMENG_CHANNEL");

                } else {
                   channel="no_channel";
                }

            } catch (Exception e) {
                e.printStackTrace();
                channel="no_channel";
            }
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(CHANNEL_KEY,channel).commit();
        }
        return channel;
    }






}




