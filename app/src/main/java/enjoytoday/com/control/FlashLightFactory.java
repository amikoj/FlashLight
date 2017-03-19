package enjoytoday.com.control;

import android.os.Build;

/**
 * Created by hfcai on 05/03/17.
 * flashlight factory.
 */

public class FlashLightFactory {


    private static FlashLight flashLight;


    /**
     *
     * @return
     * flashlight 兼容6.0,7.0
     */
    public static FlashLight createFlashLight(){
        if (Build.VERSION.SDK_INT>=23){
            flashLight=new ControlUtils1();
        }else {
            flashLight=new ControlUtils2();

        }
        return flashLight;
    }



}
