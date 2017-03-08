package enjoytoday.com;

/**
 * @date 17-3-7.
 * @className SeekBarChangListener
 * @serial 1.0.0
 */

import android.app.Activity;
import android.widget.SeekBar;

import enjoytoday.com.utils.LightUtils;

/**
 * seekBar 改变监听
 */
public class SeekBarChangListener implements SeekBar.OnSeekBarChangeListener{
    private final Activity activity;
    private int minLimit=10;

    private int light=0;

    public SeekBarChangListener(Activity activity){
        this.activity=activity;
        light=LightUtils.getScreenBrightness(activity);
    }

    public SeekBarChangListener(Activity activity,int minLimit){
        this.minLimit=minLimit;
        this.activity=activity;
        light=LightUtils.getScreenBrightness(activity);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
        /**
         * seekBar change.
         */


      if (Math.abs(progress-light)>30){
               light=progress;
               LightUtils.setBrightness(activity, progress);
       }else {
          return;
      }




    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}