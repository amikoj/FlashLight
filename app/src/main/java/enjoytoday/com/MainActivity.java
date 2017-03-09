package enjoytoday.com;

import android.app.Activity;

import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.Date;

import enjoytoday.com.control.FlashLight;
import enjoytoday.com.control.FlashLightFactory;
import enjoytoday.com.control.LambStateChangeListener;
import enjoytoday.com.utils.LightUtils;
import enjoytoday.com.views.SwitchButton;

public class MainActivity extends Activity  implements  LambStateChangeListener,SwitchButton.OnSwitchChangeListener{


    protected final int TIMER_CHANGED_TICK=10000;
    private FlashLight flashLight;
    private TextView timerTextView;
    private ImageView controlImageView;
    private TimerChangedReceiver timerChangedReceiver;
    private SharedPreferences sharedPreferences;




    private View flashView;
    private View screenView;
    private View settingsView;
    private SwitchButton switchTab;


    private String[] tabTexts;
    private View[] views;









    protected Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {

            if (msg.what==TIMER_CHANGED_TICK){
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long times=System.currentTimeMillis();
                        String formatTimes=formatTimes(times);
                        timerTextView.setText(formatTimes);
                    }
                });

            }
        }
    };;


    /**
     * 格式化时间
     * @param times
     * @return
     */
    private String formatTimes(final long times){
        SimpleDateFormat simpleDataFormat=new SimpleDateFormat("HH:mm");
        Date date=new Date(times);
        return  simpleDataFormat.format(date);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        MobclickAgent.setScenarioType(MainActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        initViews();


    }






    private void initViews(){
        timerTextView= (TextView) this.findViewById(R.id.timer);
        controlImageView= (ImageView) this.findViewById(R.id.lamb);

        flashView=findViewById(R.id.flash);
        settingsView=findViewById(R.id.settings);
        screenView=findViewById(R.id.screen);
        switchTab= (SwitchButton) findViewById(R.id.switch_tab);


        tabTexts=this.getResources().getStringArray(R.array.tab_texts);

        switchTab.setTabTexts(tabTexts);
        views=new View[]{screenView,flashView,settingsView};



        switchTab.setOnSwitchChangeListener(this);

        setVisibleView(switchTab.getCurrentTab());
        timerChangedReceiver=new TimerChangedReceiver(MainActivity.this);
        flashLight= FlashLightFactory.creatFlashLight();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timerChangedReceiver,intentFilter);

        flashLight.turnNormalLightOn(MainActivity.this);
        if (flashLight!=null){
                    flashLight.setLambStateChangeListener(MainActivity.this);
         }else {
                    LogUtils.setDebug("flashLight is null.");
        }

        /**
         * refresh.
         */
        Message.obtain(mHandler,TIMER_CHANGED_TICK).sendToTarget();


    }


    /**
     * 显示当前的tab
     * @param position
     */
    private void setVisibleView(int position){
        if (views==null || views.length==0 || tabTexts==null || tabTexts.length==0){
            LogUtils.setDebug("views and tabTexts is not null.");
            return;
        }else if (views.length!=tabTexts.length){
            LogUtils.setDebug("views and tabTexts is not same length.");
            return;
        }

        for (int i=0; i<views.length;i++){
            if (i==position){
                views[i].setVisibility(View.VISIBLE);
            }else {
                views[i].setVisibility(View.GONE);
            }
        }

    }









    @Override
    public void onStateChanged(int state, FlashLight flashLight) {

        LogUtils.setDebug("get state:"+state);
        if (state==0){
            controlImageView.setImageDrawable(null);
        }else if (state==1){
            controlImageView.setImageDrawable(this.getResources().getDrawable(R.drawable.lamb_light));

        }
    }


    @Override
    protected void onResume() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        MobclickAgent.onResume(this);

        LightUtils.setBrightness(this,200);
        if (sharedPreferences==null){
            sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
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
     * @param view
     */
    public void click(View view){
        String color="red";
        if (view.getId()==R.id.switcher) {
            if (flashLight != null) {
                try {
                    flashLight.switchFlashLight(MainActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return;
        }

    }



    @Override
    protected void onDestroy() {
        if (flashLight!=null){
            flashLight.turnLightOff(this);
        }
        if (timerChangedReceiver!=null) {
            unregisterReceiver(timerChangedReceiver);
        }
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }


    @Override
    public void onSwitchChange(int position, String text, SwitchButton switchButton) {
        setVisibleView(position);

    }


















}


/**
 * 检测时间变化广播
 */
class TimerChangedReceiver extends BroadcastReceiver{
    private MainActivity activity;

    protected TimerChangedReceiver(MainActivity activity){
        this.activity=activity;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if (action.equals(Intent.ACTION_TIME_TICK)){
            Message.obtain(activity.mHandler,activity.TIMER_CHANGED_TICK).sendToTarget();
        }

    }

}




