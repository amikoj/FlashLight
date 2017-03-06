package enjoytoday.com;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.Date;

import enjoytoday.com.control.FlashLight;
import enjoytoday.com.control.FlashLightFactory;

public class MainActivity extends Activity {


    protected final int TIMER_CHANGED_TICK=10000;
    private FlashLight flashLight;
    private TextView timerTextView;
    private ImageView controlImageView;
    private Typeface typeface;
    private TimerChangedReceiver timerChangedReceiver;
    protected Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {

            if (msg.what==TIMER_CHANGED_TICK){
                long times=System.currentTimeMillis();
                String formatTimes=formatTimes(times);
                timerTextView.setText(formatTimes);
            }
        }
    };


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
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initViews();
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        flashLight= FlashLightFactory.creatFlashLight();
        flashLight.turnNormalLightOn(this);
    }





    private void initViews(){
        timerTextView= (TextView) this.findViewById(R.id.timer);
        controlImageView= (ImageView) this.findViewById(R.id.switcher);
        typeface= Typeface.createFromAsset(this.getAssets(),"fonts/timer.ttf");
        timerTextView.setTypeface(typeface);


        timerChangedReceiver=new TimerChangedReceiver(this);
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timerChangedReceiver,intentFilter);

        /**
         * refresh.
         */
        Message.obtain(mHandler,TIMER_CHANGED_TICK).sendToTarget();
    }


    @Override
    protected void onResume() {
        MobclickAgent.onResume(this);
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
        if (flashLight!=null){
            try {
                flashLight.switchFlashLight(MainActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
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

