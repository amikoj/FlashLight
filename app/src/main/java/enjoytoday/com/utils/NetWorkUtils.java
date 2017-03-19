package enjoytoday.com.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by hfcai on 19/03/17.
 */

public class NetWorkUtils {

    private NetWorkUtils() throws IllegalAccessException {
        throw  new IllegalAccessException("can not access.");
    }


    /**
     *
     * @param context
     * @return
     * 该方法存在问题,当时局域网不可联网的状态无法判断
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }





}
