package enjoytoday.com.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by hfcai on 14/03/17.
 */

public class DeivceUtils {


    /**
     * 获取设备id(imei),全球唯一设备编号
     * @return
     */
    public static String getDeviceId(Context context) {
        String imei = "";
        try {
            imei = ((TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            if(TextUtils.isEmpty(imei)){
                imei = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
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

        try{
            PackageManager packageManager=context.getApplicationContext().getPackageManager();
            PackageInfo packageInfo=packageManager.getPackageInfo(context.getApplicationContext().getPackageName(),0);
            String version=packageInfo.versionName;
            return version;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    /**
     *
     * @return 获取渠道号码
     */
    public static String getChannel( Context context){

        ApplicationInfo applicationInfo=null;
        try {
            applicationInfo = context.getApplicationContext().getPackageManager().
                    getApplicationInfo(context.getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);

            if (applicationInfo.metaData!=null) {
                String channle = applicationInfo.metaData.getString("UMENG_CHANNEL");
                return channle;
            }else {
                return null;
            }

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }




}




