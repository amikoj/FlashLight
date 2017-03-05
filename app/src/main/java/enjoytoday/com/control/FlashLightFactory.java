package enjoytoday.com.control;

import android.os.Build;

/**
 * Created by hfcai on 05/03/17.
 * flashlight factory.
 */

public class FlashLightFactory {


    private static FlashLight flashLight;

    public static FlashLight creatFlashLight(){
        if (Build.VERSION.SDK_INT>23){
            flashLight=new ControlUtils1();
        }else {
            flashLight=new ControlUtils2();

        }
        return flashLight;
    }



}
