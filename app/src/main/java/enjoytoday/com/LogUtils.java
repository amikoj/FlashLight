package enjoytoday.com;

import android.util.Log;

/**
 * Created by hfcai on 05/03/17.
 */

public class LogUtils {

    private final static String TAG=LogUtils.class.getPackage().getName();

    private volatile static  boolean isDebug=false;


    public static void setDebug(String msg){
        if (isDebug){
            Log.e(TAG,msg);
        }
    }














}
