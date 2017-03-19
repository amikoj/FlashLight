package enjoytoday.com;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import enjoytoday.com.control.FlashLight;
import enjoytoday.com.control.FlashLightFactory;
import enjoytoday.com.control.LambStateChangeListener;
import enjoytoday.com.dao.HttpResponseListener;
import enjoytoday.com.dao.RequestMethod;
import enjoytoday.com.utils.HttpUtils;
import enjoytoday.com.utils.LightUtils;
import enjoytoday.com.utils.NetWorkUtils;
import enjoytoday.com.views.Image3DSwitchView;
import enjoytoday.com.views.SwitchButton;

public class MainActivity extends Activity implements LambStateChangeListener, SwitchButton.OnSwitchChangeListener {



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

    private final static int SUBMIT_MESSAGE_RESPONSE=10000;
    private final int EMAIL_PATTERN_MATTER_FAILED=100001;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==SUBMIT_MESSAGE_RESPONSE){
                Toast.makeText(MainActivity.this,msg.obj.toString(),Toast.LENGTH_LONG).show();
            }else if (msg.what==EMAIL_PATTERN_MATTER_FAILED){
                Toast.makeText(MainActivity.this,MainActivity.this.getResources().getString(R.string.email_not_valid),Toast.LENGTH_LONG).show();

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


    /**
     * 清除输入提交的所有文本
     */
    private void formateFeedBack(){

        if (messageEditText!=null){
            messageEditText.setText("");
        }
        if (email!=null){
            email.setText("");
        }

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
        flashLight = FlashLightFactory.createFlashLight();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);

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
    private EditText email;
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
            email= (EditText) dialog.findViewById(R.id.email);

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

                    FlashLightApplication.application.getHttpExecutorService().submit(new Runnable() {
                        @Override
                        public void run() {
                            //put message to server.
                            Map<String,String> params=HttpUtils.getBasicRequestMap(MainActivity.this.getApplicationContext());
                            if (!TextUtils.isEmpty(message)){
                                    params.put("feedback", message);
                            }

                            String emailNumber=email.getText().toString().trim();
                            if (email!=null && emailNumber.length()>0){
                                Pattern pattern=Pattern.compile("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");
                                Matcher matcher=pattern.matcher(emailNumber);
                               if (matcher.matches()) {
                                   params.put("mail",emailNumber);
                                }else {
                                     Message.obtain(mHandler,EMAIL_PATTERN_MATTER_FAILED).sendToTarget();
                                }
                            }
                            postMessageToServer(params);
                        }
                    });
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




    /**
     * 上传数据到后台服务器
     * @param params
     */
    private void postMessageToServer(Map<String,String> params){
        HttpUtils.sendHttpMessage(new HttpResponseListener() {
            @Override
            public void callBack(int responseCode, String response, String content) {
                String tips="";
                if (responseCode== HttpURLConnection.HTTP_OK || NetWorkUtils.isNetworkAvailable(MainActivity.this)){
                    tips=MainActivity.this.getResources().getString(R.string.submit_success_tips);
                    formateFeedBack();
                }else {
                  tips=MainActivity.this.getResources().getString(R.string.submit_failed_tips);
                }
                mHandler.sendMessageDelayed(Message.obtain(mHandler,SUBMIT_MESSAGE_RESPONSE,tips),600);

            }
        },params, HttpUtils.HttpRequestDomain.POST_MESSAGE, RequestMethod.POST);
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




