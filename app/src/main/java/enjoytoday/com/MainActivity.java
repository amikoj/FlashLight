package enjoytoday.com;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.umeng.analytics.MobclickAgent;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import enjoytoday.com.control.FlashLight;
import enjoytoday.com.control.FlashLightFactory;
import enjoytoday.com.control.LambStateChangeListener;
import enjoytoday.com.utils.DeivceUtils;
import enjoytoday.com.utils.LightUtils;
import enjoytoday.com.views.Image3DSwitchView;
import enjoytoday.com.views.SwitchButton;

public class MainActivity extends Activity implements LambStateChangeListener, SwitchButton.OnSwitchChangeListener {


    protected final int TIMER_CHANGED_TICK = 10000;
    private FlashLight flashLight;
    private ImageView controlImageView;
//    private TimerChangedReceiver timerChangedReceiver;
    private SharedPreferences sharedPreferences;
    private Image3DSwitchView image3DSwitchView;


    private View flashView;
    private View screenView;
    private View settingsView;
    private SwitchButton switchTab;


    private String[] tabTexts;
    private View[] views;


    private String[] colors = new String[]{"#ff0000", "#00ff00", "#0000ff", "#ffffff", "#ff00cc", "#00ffff"};
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private final static int SUBMIT_MESSAGE_RESPONES=10000;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==SUBMIT_MESSAGE_RESPONES){
                Toast.makeText(MainActivity.this,msg.obj.toString(),Toast.LENGTH_LONG).show();
            }
        }
    };

    /**
     * 格式化时间
     *
     * @param times
     * @return
     */
    private String formatTimes(final long times) {
        SimpleDateFormat simpleDataFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date(times);
        return simpleDataFormat.format(date);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        MobclickAgent.setScenarioType(MainActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        initViews();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void initViews() {
        controlImageView = (ImageView) this.findViewById(R.id.lamb);

        flashView = findViewById(R.id.flash);
        settingsView = findViewById(R.id.settings);
        screenView = findViewById(R.id.screen);
        switchTab = (SwitchButton) findViewById(R.id.switch_tab);
        image3DSwitchView = (Image3DSwitchView) findViewById(R.id.image_switch_view);


        tabTexts = this.getResources().getStringArray(R.array.tab_texts);

        switchTab.setTabTexts(tabTexts);
        views = new View[]{screenView, flashView, settingsView};


        switchTab.setOnSwitchChangeListener(this);

        setVisibleView(switchTab.getCurrentTab());
//        timerChangedReceiver = new TimerChangedReceiver(MainActivity.this);
        flashLight = FlashLightFactory.creatFlashLight();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
//        registerReceiver(timerChangedReceiver, intentFilter);

        if (flashLight != null) {
            flashLight.setLambStateChangeListener(MainActivity.this);
        } else {
            LogUtils.setDebug("flashLight is null.");
        }

        flashLight.turnNormalLightOn(MainActivity.this);

        image3DSwitchView.setOnImageClickListener(new Image3DSwitchView.OnImageClickListener() {
            @Override
            public void onImageClick(int currentImage, Image3DSwitchView view) {
                Intent intent = new Intent(MainActivity.this, ScreenLightActivity.class);
                intent.putExtra("color", colors[currentImage]);
                MainActivity.this.startActivity(intent);


            }
        });


    }


    /**
     * 显示当前的tab
     *
     * @param position
     */
    private void setVisibleView(int position) {
        if (views == null || views.length == 0 || tabTexts == null || tabTexts.length == 0) {
            LogUtils.setDebug("views and tabTexts is not null.");
            return;
        } else if (views.length != tabTexts.length) {
            LogUtils.setDebug("views and tabTexts is not same length.");
            return;
        }

        for (int i = 0; i < views.length; i++) {
            if (i == position) {
                views[i].setVisibility(View.VISIBLE);
            } else {
                views[i].setVisibility(View.GONE);
            }
        }

    }


    @Override
    public void onStateChanged(int state, FlashLight flashLight) {
        LogUtils.setDebug("get state:" + state);
        if (state == 0) {
            controlImageView.setImageDrawable(null);
        } else if (state == 1) {
            controlImageView.setImageDrawable(this.getResources().getDrawable(R.drawable.lamb_light_smaller));

        }
    }


    @Override
    protected void onResume() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        MobclickAgent.onResume(this);

        LightUtils.setBrightness(this, 200);
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
        super.onResume();
    }


    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        super.onPause();
    }


    /**
     * open/close flashlight
     *
     * @param view
     */
    public void click(View view) {
        if (view.getId() == R.id.switcher) {
            if (flashLight != null) {
                try {
                    flashLight.switchFlashLight(MainActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return;
        } else if (view.getId() == R.id.submit_message) {
            submitMessage(this);
        }

    }


    private Dialog dialog = null;
    private EditText messageEditText;
    private Button submitButton;
    private Button submitCancelButton;
    private SubmitOnClickListener submitOnClickListener;

    private void submitMessage(Context context) {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(context).create();
            dialog.show();
            dialog.getWindow().setContentView(R.layout.submit_message_layout);

            submitOnClickListener=new SubmitOnClickListener();
            messageEditText = (EditText) dialog.findViewById(R.id.message);
            submitButton = (Button) dialog.findViewById(R.id.submit);
            submitCancelButton = (Button) dialog.findViewById(R.id.submit_cancel);
            submitButton.setOnClickListener(submitOnClickListener);
            submitCancelButton.setOnClickListener(submitOnClickListener);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        } else {
            dialog.show();
        }

    }


    /**
     * handle submit any things.
     */
    class SubmitOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(final View v) {
            if (v.getId()==R.id.submit){

               final String message=messageEditText.getText().toString().trim();
                if (message==null || message.length()==0){
                    Toast.makeText(v.getContext(),v.getContext().getResources().getString(R.string.no_message_tips),Toast.LENGTH_LONG).show();
                }else {

                    if (dialog!=null){
                        dialog.dismiss();
                    }else {
                        LogUtils.setDebug("UNKNOWN ERROR.");
                        MainActivity.this.finish();
                    }
                    new Thread(){
                        @Override
                        public void run() {
                            //put message to server.
                            Map<String,String> params=new ConcurrentHashMap<String, String>();
                            String model= DeivceUtils.getModel();
                            if (!TextUtils.isEmpty(model)){
                                params.put("model",model);
                            }
                            String deviceId=DeivceUtils.getDeviceId(MainActivity.this);
                            if (!TextUtils.isEmpty(deviceId)){
                                params.put("deviceId",deviceId);
                            }
                            String manufacturers=DeivceUtils.getManufacturers();

                            if (!TextUtils.isEmpty(manufacturers)){
                                params.put("manufacturers",manufacturers);
                            }
                            String version=DeivceUtils.getAppVersion(MainActivity.this);
                            if (TextUtils.isEmpty(version)) {
                                params.put("version",version);
                            }

                            String channel=DeivceUtils.getChannel(MainActivity.this);
                            if (!TextUtils.isEmpty(channel)){
                                params.put("channel",channel);
                            }

                            String pkgName=MainActivity.this.getPackageName();
                            if (!TextUtils.isEmpty(pkgName)){
                                params.put("pkg_name",MainActivity.this.getPackageName());
                            }

                            if (!TextUtils.isEmpty(message)) {
                                params.put("feedback", message);
                            }
                            postMessageToServer(params);
                        }
                    }.start();

                }
            }else if (v.getId()==R.id.submit_cancel){
                if (dialog!=null){
                    dialog.dismiss();
                }else {
                    LogUtils.setDebug("UNKNOWN ERROR.");
                    MainActivity.this.finish();
                }

            }


        }
    }









    private static final String postUrl="http://www.enjoytoday.cn:8080/web/postFeedback";
    /**
     * 上传数据到后台服务器
     * @param params
     */
    private void postMessageToServer(Map<String,String> params){
        HttpURLConnection httpURLConnection;
        URL url;
        try{

            url=new URL(postUrl);
            httpURLConnection= (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setConnectTimeout(10*1000);
            httpURLConnection.setRequestProperty("Charset", "UTF-8");


            LogUtils.setDebug("map toString:"+params.toString());
            for (Map.Entry<String,String> entry:params.entrySet()){
                if (entry.getKey()==null || entry.getKey().length()==0 || entry.getValue()==null || entry.getValue().length()==0){
                    continue;
                }
                httpURLConnection.setRequestProperty(entry.getKey(),entry.getValue());
            }

            int code=httpURLConnection.getResponseCode();
            if (code==200){
                Message.obtain(mHandler,SUBMIT_MESSAGE_RESPONES,MainActivity.this.getResources().getString(R.string.submit_success_tips)).sendToTarget();
            }
            Message.obtain(mHandler,SUBMIT_MESSAGE_RESPONES,MainActivity.this.getResources().getString(R.string.submit_success_tips)).sendToTarget();
        }catch (Exception e){
            e.printStackTrace();
            if (isNetworkAvailable(MainActivity.this)){
                Message.obtain(mHandler,SUBMIT_MESSAGE_RESPONES,MainActivity.this.getResources().getString(R.string.submit_success_tips)).sendToTarget();
            }else {
                Message.obtain(mHandler,SUBMIT_MESSAGE_RESPONES,MainActivity.this.getResources().getString(R.string.submit_failed_tips)).sendToTarget();
            }
        }




    }




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






    @Override
    protected void onDestroy() {
        if (flashLight != null) {
            flashLight.turnLightOff(this);
        }
        super.onDestroy();
    }


    @Override
    public void onSwitchChange(int position, String text, SwitchButton switchButton) {
        setVisibleView(position);

    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}


/**
 * 检测时间变化广播
 */
//class TimerChangedReceiver extends BroadcastReceiver{
//    private MainActivity activity;
//
//    protected TimerChangedReceiver(MainActivity activity){
//        this.activity=activity;
//    }
//
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        String action=intent.getAction();
//        if (action.equals(Intent.ACTION_TIME_TICK)){
////            Message.obtain(activity.mHandler,activity.TIMER_CHANGED_TICK).sendToTarget();
//        }
//
//    }

//}




