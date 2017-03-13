package enjoytoday.com.control;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.*;
import android.hardware.camera2.CameraAccessException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutionException;

import enjoytoday.com.LogUtils;

/**
 * Created by hfcai on 05/03/17.
 */

public class ControlUtils2 extends FlashLight {

    private static Camera camera;
    private SharedPreferences sharedPreferences;

    @Override
   public void turnNormalLightOn(Context context) {
        if (context==null){
            return;
        }
        if (camera==null ){
            try{
                camera=Camera.open();
            }catch (Exception e){
                e.printStackTrace();
                LogUtils.setDebug("Camera open non parameters failed.");
                try {
                    camera=Camera.open(Camera.getNumberOfCameras()-1);
                }catch (Exception e1){
                    e1.printStackTrace();
                    Toast.makeText(context,"Camera access is occupied by other applications, can not open！",Toast.LENGTH_LONG).show();
                    LogUtils.setDebug("Camera open one parameters failed. Camera can not open.");
                    return;
                }
            }
        }


        if (sharedPreferences==null){
            sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
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
        if (!Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
            // Turn on the flash
            if (flashModes.contains(Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                camera.startPreview();
                if (this.lambStateChangeListener!=null){
                    this.lambStateChangeListener.onStateChanged(1,this);
                }
                sharedPreferences.edit().putBoolean("flash_mode",true).commit();
            } else {
                Log.e(TAG, "FLASH_MODE_OFF not supported");
            }
        }

    }

    @Override
    public  void turnLightOff(Context context) {

        if (context==null){
            return;
        }

        if (camera==null ){
            try{
                camera=Camera.open();
            }catch (Exception e){
                e.printStackTrace();
                LogUtils.setDebug("Camera open non parameters failed.");
                try {
                    camera=Camera.open(Camera.getNumberOfCameras()-1);
                }catch (Exception e1){
                    e1.printStackTrace();
//                    Toast.makeText(context,"Camera access is occupied by other applications, can not open！",Toast.LENGTH_LONG).show();
                    LogUtils.setDebug("Camera open one parameters failed. Camera can not open.");
                    return;
                }
            }
        }


        if (sharedPreferences==null){
            sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        }

        Parameters parameters = camera.getParameters();
        if (parameters == null) {
//            Toast.makeText(context,"Current Camera settings is not support .",Toast.LENGTH_LONG).show();
            LogUtils.setDebug("Current Camera settings is not support .");
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        String flashMode = parameters.getFlashMode();
        // Check if camera flash exists
        if (flashModes == null) {
//            Toast.makeText(context,"Current Camera flash is not support .",Toast.LENGTH_LONG).show();
            LogUtils.setDebug("Current Camera flash is not support .");
            return;
        }
        if (!Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
            // Turn off the flash
            if (flashModes.contains(Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
                camera.release();
                if (this.lambStateChangeListener!=null){
                    this.lambStateChangeListener.onStateChanged(0,this);
                }
                sharedPreferences.edit().putBoolean("flash_mode",false).commit();
                camera=null;
            } else {
                Log.e(TAG, "FLASH_MODE_OFF not supported");
            }
        }
    }

    @Override
    public void switchFlashLight(Context context) throws CameraAccessException {
        if (camera==null ){
            try{
                camera=Camera.open();
            }catch (Exception e){
                e.printStackTrace();
                LogUtils.setDebug("Camera open non parameters failed.");
                try {
                    camera=Camera.open(Camera.getNumberOfCameras()-1);
                }catch (Exception e1){
                    e1.printStackTrace();
                    Toast.makeText(context,"Camera access is occupied by other applications, can not open！",Toast.LENGTH_LONG).show();
                    LogUtils.setDebug("Camera open one parameters failed. Camera can not open.");
                    return;
                }
            }
        }

       Parameters parameters=camera.getParameters();
        if (parameters == null) {
            Toast.makeText(context,"Current Camera settings is not support .",Toast.LENGTH_LONG).show();
            LogUtils.setDebug("Current Camera settings is not support .");
            return;
        }
        String mode=parameters.getFlashMode();

        if(mode==null || mode.length()==0){
            Toast.makeText(context,"Current Camera settings is not support .",Toast.LENGTH_LONG).show();
        }else if (mode.equals(Parameters.FLASH_MODE_OFF)){
            this.turnNormalLightOn(context);
        }else if (mode.equals(Parameters.FLASH_MODE_TORCH)){
            this.turnLightOff(context);
        }
    }
}
