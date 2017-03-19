package enjoytoday.com.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import enjoytoday.com.LogUtils;
import enjoytoday.com.R;
import enjoytoday.com.dao.HttpResponseListener;
import enjoytoday.com.dao.RequestMethod;

/**
 * Created by hfcai on 19/03/17.
 */

public class HttpUtils {

    private HttpUtils() throws IllegalAccessException {
        throw new IllegalAccessException("access denied.");
    }


    /**
     *
     * @param listener
     * @param map
     * 单条请求,并发需要后期扩展
     *
     */
    public static void  sendHttpMessage(HttpResponseListener listener, Map<String,String> map, String postUrl, RequestMethod method){
        LogUtils.setDebug("send http message to :"+postUrl);

        HttpURLConnection httpURLConnection;
        URL url=null;
        String response="app internal network error.";
        StringBuffer content=new StringBuffer();
        int code=1000;
        try{
            url=new URL(postUrl);
            httpURLConnection= (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod(method.value());
            httpURLConnection.setConnectTimeout(10*1000);
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            for (Map.Entry<String,String> entry:map.entrySet()){
                if (entry.getKey()==null || entry.getKey().length()==0 || entry.getValue()==null || entry.getValue().length()==0){
                    continue;
                }
                /**
                 * 在header里面直接添加数据是否可取?有待商榷
                 */
                httpURLConnection.setRequestProperty(entry.getKey(),entry.getValue());
            }

            LogUtils.setDebug("http request map :"+map.toString());
            httpURLConnection.connect();
            code=httpURLConnection.getResponseCode();
            if (code==200){
                response=httpURLConnection.getResponseMessage();
                BufferedReader in=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                String line="";
                while ((line=in.readLine())!=null){
                    content.append(line);
                }
                if (listener!=null){
                    listener.callBack(code,response,content.toString());
                }
                LogUtils.setDebug("response string:"+content.toString());
            }else {
                if (listener!=null){
                    listener.callBack(code,response,null);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            if (listener!=null){
                listener.callBack(code,response,null);
            }

        }

    }


    /**
     *
     * @return
     * 基本的请求参数
     */
    public static Map<String,String> getBasicRequestMap(Context context){
        Map<String,String> params=new ConcurrentHashMap<String, String>();
        String model= DeviceUtils.getModel();
        if (!TextUtils.isEmpty(model)){
            params.put("model",model);
        }
        String deviceId= DeviceUtils.getDeviceId(context);
        if (!TextUtils.isEmpty(deviceId)){
            params.put("deviceid",deviceId);
        }
        String manufacturers= DeviceUtils.getManufacturers();

        if (!TextUtils.isEmpty(manufacturers)){
            params.put("manufacturers",manufacturers);
        }
        String version= DeviceUtils.getAppVersion(context);
        if (!TextUtils.isEmpty(version)) {
            params.put("version",version);
        }

        String channel= DeviceUtils.getChannel(context);
        if (!TextUtils.isEmpty(channel)){
            params.put("channel",channel);
        }

        String pkgName=context.getPackageName();
        if (!TextUtils.isEmpty(pkgName)){
            params.put("pkg_name",context.getPackageName());
        }
        String appName=context.getResources().getString(R.string.app_name);
        if (!TextUtils.isEmpty(appName)){
            params.put("app_name",appName);
        }
        return params;
    }


    public final static class HttpRequestDomain{
        public static final String DOMAIN="http://192.168.1.113:8080/web";
        public static final String POST_MESSAGE=DOMAIN+"/postFeedback";
        public static final String ACTIVE=DOMAIN+"/active";

        public static final String STATUS="status";

    }


    /**
     * app的状态收集,后续可能会扩展
     */
    public enum APP_STATUS{
        START("start"),EXIT("exit");
        private String value="start";

        APP_STATUS(String value){
            this.value=value;
        }
        public String value(){
            return value;
        }
    }







}
