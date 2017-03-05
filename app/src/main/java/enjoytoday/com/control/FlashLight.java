package enjoytoday.com.control;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;

/**
 * Created by hfcai on 05/03/17.
 * basic control interface.
 */

public abstract class FlashLight {
    protected final static String TAG=FlashLight.class.getSimpleName();

    protected FlashLight(){}


    /**
     *
     * @param context
     * open flashlight by normal method.
     */
    public abstract void turnNormalLightOn(Context context);

    /**
     *
     * @param context
     * according to screen control flashlight.
     */
    protected void turnScreenLightOn(Context context){

        //add any code.



    }


    /**
     *
     * @param context
     * close screen light.
     */
    protected void turnScreenLightOff(Context context){

        //add any code.

    }



    /**
     * @param context
     * close flashlight.
     */
    public abstract  void turnLightOff(Context context);


    /**
     *
     * @param context
     * switch flashlight state.
     */
    public abstract void switchFlashLight(Context context) throws CameraAccessException;



}
