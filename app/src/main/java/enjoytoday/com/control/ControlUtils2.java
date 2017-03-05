package enjoytoday.com.control;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.*;
import android.hardware.camera2.CameraAccessException;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import enjoytoday.com.LogUtils;

/**
 * Created by hfcai on 05/03/17.
 */

public class ControlUtils2 extends FlashLight {

    private static Camera camera;

    @Override
   public void turnNormalLightOn(Context context) {

        if (camera==null ){
            if (Camera.open()==null){
                Toast.makeText(context,"camera can not open,please push this error to MessageCenter .",Toast.LENGTH_LONG).show();
                LogUtils.setDebug("camera can not open !");
                return;
            }else {
                camera = Camera.open();
            }
        }
       Parameters parameters = camera.getParameters();
        if (parameters == null) {
            Toast.makeText(context,"Current Camera settings is not support .",Toast.LENGTH_LONG).show();
            LogUtils.setDebug("Current Camera settings is not support .");
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        String flashMode = parameters.getFlashMode();
        // Check if camera flash exists
        if (flashModes == null) {
            Toast.makeText(context,"Current Camera flash is not support .",Toast.LENGTH_LONG).show();
            LogUtils.setDebug("Current Camera flash is not support .");
            return;
        }
        if (!Parameters.FLASH_MODE_OFF.equals(flashMode)) {
            // Turn off the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
            } else {
                Log.e(TAG, "FLASH_MODE_OFF not supported");
            }
        }

    }

    @Override
    public  void turnLightOff(Context context) {

        if (camera==null ){
            if (Camera.open()==null){
                Toast.makeText(context,"camera can not open,please push this error to MessageCenter .",Toast.LENGTH_LONG).show();
                LogUtils.setDebug("camera can not open !");
                return;
            }else {
                camera = Camera.open();
            }
        }
        Parameters parameters = camera.getParameters();
        if (parameters == null) {
            Toast.makeText(context,"Current Camera settings is not support .",Toast.LENGTH_LONG).show();
            LogUtils.setDebug("Current Camera settings is not support .");
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        String flashMode = parameters.getFlashMode();
        // Check if camera flash exists
        if (flashModes == null) {
            Toast.makeText(context,"Current Camera flash is not support .",Toast.LENGTH_LONG).show();
            LogUtils.setDebug("Current Camera flash is not support .");
            return;
        }
        if (!Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
            // Turn off the flash
            if (flashModes.contains(Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
            } else {
                Log.e(TAG, "FLASH_MODE_OFF not supported");
            }
        }
    }

    @Override
    public void switchFlashLight(Context context) throws CameraAccessException {
        if (camera==null ){
            if (Camera.open()==null){
                Toast.makeText(context,"camera can not open,please push this error to MessageCenter .",Toast.LENGTH_LONG).show();
                LogUtils.setDebug("camera can not open !");
                return;
            }else {
                camera = Camera.open();
            }
        }

       Parameters parameters=camera.getParameters();
        if (parameters == null) {
            Toast.makeText(context,"Current Camera settings is not support .",Toast.LENGTH_LONG).show();
            LogUtils.setDebug("Current Camera settings is not support .");
            return;
        }
        String mode=parameters.getFlashMode();

        if (mode.equals(Parameters.FLASH_MODE_OFF)){
            this.turnNormalLightOn(context);
        }else if (mode.equals(Parameters.FLASH_MODE_ON)){
            this.turnLightOff(context);
        }
    }
}
