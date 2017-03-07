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
    private Activity activity;
    private int minLimit=10;

    public SeekBarChangListener(Activity activity){
        this.activity=activity;
    }

    public SeekBarChangListener(Activity activity,int minLimit){
        this.minLimit=minLimit;
        this.activity=activity;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        /**
         * seekBar change.
         */
        if (progress < minLimit) {
            return;
        } else {
            LightUtils.setBrightness(activity, progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}