package enjoytoday.com;

import android.app.Activity;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import enjoytoday.com.control.FlashLight;
import enjoytoday.com.control.FlashLightFactory;

public class MainActivity extends Activity {


    private FlashLight flashLight;
    private TextView timerTextView;
    private ImageView controlImageView;
    private Typeface typeface;
    private SharedPreferences sharedPreferences;

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
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean("flash_mode",true).commit();

    }





    private void initViews(){
        timerTextView= (TextView) this.findViewById(R.id.timer);
        controlImageView= (ImageView) this.findViewById(R.id.switcher);
        typeface= Typeface.createFromAsset(this.getAssets(),"fonts/timer.ttf");
        timerTextView.setTypeface(typeface);


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

        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1,0.9f,1,0.9f,
                Animation.RELATIVE_TO_SELF,0.9f,Animation.RELATIVE_TO_SELF,0.9f);
        scaleAnimation.setDuration(200);
        animationSet.addAnimation(scaleAnimation);
        //将AlphaAnimation这个已经设置好的动画添加到 AnimationSet中
        controlImageView.startAnimation(animationSet);
        if (flashLight!=null){
            try {
                flashLight.switchFlashLight(MainActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}


