package enjoytoday.com.views;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * @date 17-3-7.
 * @className MyGLSurfaceView
 * @serial 1.0.0
 */

public class MyGLSurfaceView extends GLSurfaceView {
    private MyRender myRender;


    public MyGLSurfaceView(Context context) {
        this(context,null);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setEGLContextClientVersion(2);
        myRender = new MyRender(context);
        setRenderer(myRender);
        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}
