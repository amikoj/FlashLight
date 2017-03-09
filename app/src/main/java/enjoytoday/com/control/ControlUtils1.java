package enjoytoday.com.control;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.preference.PreferenceManager;
import android.util.Log;

import enjoytoday.com.LogUtils;

/**
 * Created by hfcai on 05/03/17.
 * api 23 and over control class.
 */

@TargetApi(23)
public class ControlUtils1 extends FlashLight {
    private static CameraManager cameraManager;
    private static CameraCharacteristics cameraCharacteristics;
    private boolean isSupport;
    private SharedPreferences sharedPreferences;

    @Override
    public void turnNormalLightOn(Context context) {
        if (cameraManager==null){
            cameraManager= (CameraManager) context.getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
        }

        if (sharedPreferences==null){
            sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        }



        try {
            if (cameraCharacteristics==null){
                cameraCharacteristics=  cameraManager.getCameraCharacteristics(CameraCharacteristics.LENS_FACING_FRONT+"");
            }

            isSupport=cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);

            if (isSupport) {
                cameraManager.setTorchMode(CameraCharacteristics.LENS_FACING_FRONT + "", true);
                if (this.lambStateChangeListener!=null){
//                    LogUtils.setDebug("get lambestateChangeListener state=1");
                    this.lambStateChangeListener.onStateChanged(1,this);
                }
                sharedPreferences.edit().putBoolean("flash_mode",true).commit();
            }else {
                LogUtils.setDebug("this devices is not support flashlight.");
                this.turnScreenLightOn(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void turnLightOff(Context context) {
        if (context==null){
            return;
        }
        if (cameraManager==null){
            cameraManager= (CameraManager) context.getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
        }
        if (sharedPreferences==null){
            sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        }

        try {

            if (cameraCharacteristics==null){
                cameraCharacteristics=  cameraManager.getCameraCharacteristics(CameraCharacteristics.LENS_FACING_FRONT+"");
            }
            isSupport=cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            if (isSupport) {
                cameraManager.setTorchMode(CameraCharacteristics.LENS_FACING_FRONT + "", false);
                if (this.lambStateChangeListener!=null){
//                    LogUtils.setDebug("get lambestateChangeListener state=0");
                    this.lambStateChangeListener.onStateChanged(0,this);
                }
                sharedPreferences.edit().putBoolean("flash_mode",false).commit();
            }else {
                LogUtils.setDebug("not need close flashlight.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void switchFlashLight(Context context) throws CameraAccessException {
        if (context==null){
            return;
        }
        if (cameraManager==null){
            cameraManager= (CameraManager) context.getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
        }

        if (sharedPreferences==null){
            sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        }

      CameraCharacteristics cameraCharacteristics=  cameraManager.getCameraCharacteristics(CameraCharacteristics.LENS_FACING_FRONT+"");

       boolean isSupport   = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);

        if (isSupport){
          boolean isOpen=   sharedPreferences.getBoolean("flash_mode",false);
            if (isOpen){
                turnLightOff(context);
            }else {
                turnNormalLightOn(context);
            }
        }else {
            //other method control.
            LogUtils.setDebug("the devices is not support flashlights.");

        }
    }


}
