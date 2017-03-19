package enjoytoday.com;

import android.app.Application;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import enjoytoday.com.dao.HttpResponseListener;
import enjoytoday.com.dao.RequestMethod;
import enjoytoday.com.utils.HttpUtils;

/**
 * Created by hfcai on 19/03/17.
 */

public class FlashLightApplication extends Application {
    private HttpUtils.APP_STATUS app_status;
    private ExecutorService httpExecutor=null;
    public static FlashLightApplication application;


    @Override
    public void onCreate() {
        super.onCreate();

        LogUtils.setDebug("application onCreate");
        application=this;
        app_status= HttpUtils.APP_STATUS.START;
        final Map<String,String> map=HttpUtils.getBasicRequestMap(getApplicationContext());
        map.put(HttpUtils.HttpRequestDomain.STATUS,app_status.value());
        getHttpExecutorService().submit(new Runnable() {
            @Override
            public void run() {
                HttpUtils.sendHttpMessage(new HttpResponseListener() {
                    @Override
                    public void callBack(int responseCode, String response, String content) {
                        LogUtils.setDebug("response_code="+responseCode+",response="+response+",content="+content);
                    }
                },map,HttpUtils.HttpRequestDomain.ACTIVE, RequestMethod.POST);
            }
        });

    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        app_status= HttpUtils.APP_STATUS.EXIT;
        final Map<String,String> map=HttpUtils.getBasicRequestMap(getApplicationContext());
        map.put(HttpUtils.HttpRequestDomain.STATUS,app_status.value());
        getHttpExecutorService().submit(new Runnable() {
            @Override
            public void run() {
                HttpUtils.sendHttpMessage(new HttpResponseListener() {
                    @Override
                    public void callBack(int responseCode, String response, String content) {
                        LogUtils.setDebug("response_code="+responseCode+",response="+response+",content="+content);
                    }
                },map,HttpUtils.HttpRequestDomain.ACTIVE, RequestMethod.POST);
            }
        });
    }


    /**
     *
     * @return
     * 创建一个单线程的线程池子
     */
    public ExecutorService getHttpExecutorService(){
        if (httpExecutor==null){
            httpExecutor= Executors.newSingleThreadExecutor();
        }
        return  httpExecutor;
    }
}
