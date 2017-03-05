package enjoytoday.com.control;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;

/**
 * Created by hfcai on 05/03/17.
 * api 23 and over control class.
 */

@TargetApi(23)
public class ControlUtils1 extends FlashLight {
    private static CameraManager cameraManager;




    @Override
    public void turnNormalLightOn(Context context) {
        if (cameraManager==null){
            cameraManager= (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        }
        try {
            cameraManager.setTorchMode("0",true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void turnLightOff(Context context) {
        if (cameraManager==null){
            cameraManager= (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        }
        try {
            cameraManager.setTorchMode("0",false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void switchFlashLight(Context context) throws CameraAccessException {
        if (cameraManager==null){
            cameraManager= (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        }


    }


}
