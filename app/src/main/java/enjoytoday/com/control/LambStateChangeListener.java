package enjoytoday.com.control;

/**
 * Created by hfcai on 06/03/17.
 */

public interface LambStateChangeListener {

    /**
     *
     * @param state //lamb state <p> 0: off<p/> <p> 1:on<p/>
     * @param flashLight flashlight instance.
     */
    void onStateChanged(int state,FlashLight flashLight);
}
