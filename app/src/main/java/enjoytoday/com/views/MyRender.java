package enjoytoday.com.views;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @date 17-3-7.
 * @className MyRender
 * @serial 1.0.0
 */

public class MyRender implements GLSurfaceView.Renderer {
    private Context mContext;


    private static final float r=0.8f;

    private int vCount;
    private FloatBuffer fbv;
    private FloatBuffer mTexCoorBuffer;
    static float[] mMMatrix = new float[16];
    int mProgram;// 自定义渲染管线程序id
    int muMVPMatrixHandle;// 总变换矩阵引用id
    int maPositionHandle; // 顶点位置属性引用id

    public static float[] mProjMatrix = new float[16];// 4x4矩阵 投影用
    public static float[] mVMatrix = new float[16];// 摄像机位置朝向9参数矩阵
    public static float[] mMVPMatrix;// 最后起作用的总变换矩阵



    public MyRender(Context mContext){
        this.mContext=mContext;
    }












    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 1.0f);

        initVertex();

        initShader();

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);



    }







    //初始化数据
    private void initVertex() {
        ArrayList<Float> alVertix = new ArrayList<Float>();// 存放顶点坐标的ArrayList
        final int angleSpan = 10;// 将球进行单位切分的角度
        for (int vAngle = -90; vAngle < 90; vAngle = vAngle + angleSpan)// 垂直方向angleSpan度一份
        {
            for (int hAngle = 0; hAngle <= 360; hAngle = hAngle + angleSpan)// 水平方向angleSpan度一份
            {// 纵向横向各到一个角度后计算对应的此点在球面上的坐标
                float x0 = (float) (r * Math.cos(Math.toRadians(vAngle)) * Math.cos(Math.toRadians(hAngle)));
                float y0 = (float) (r * Math.cos(Math.toRadians(vAngle)) * Math.sin(Math.toRadians(hAngle)));
                float z0 = (float) (r * Math.sin(Math .toRadians(vAngle)));

                float x1 = (float) (r * Math.cos(Math.toRadians(vAngle)) * Math.cos(Math.toRadians(hAngle + angleSpan)));
                float y1 = (float) (r * Math.cos(Math.toRadians(vAngle)) * Math.sin(Math.toRadians(hAngle + angleSpan)));
                float z1 = (float) (r  * Math.sin(Math.toRadians(vAngle)));

                float x2 = (float) (r * Math.cos(Math.toRadians(vAngle + angleSpan)) * Math.cos(Math.toRadians(hAngle + angleSpan)));
                float y2 = (float) (r * Math.cos(Math.toRadians(vAngle + angleSpan)) * Math.sin(Math.toRadians(hAngle + angleSpan)));
                float z2 = (float) (r  * Math.sin(Math.toRadians(vAngle + angleSpan)));

                float x3 = (float) (r * Math.cos(Math.toRadians(vAngle + angleSpan)) * Math.cos(Math.toRadians(hAngle)));
                float y3 = (float) (r * Math.cos(Math.toRadians(vAngle + angleSpan)) * Math.sin(Math.toRadians(hAngle)));
                float z3 = (float) (r * Math.sin(Math.toRadians(vAngle + angleSpan)));

                // 将计算出来的XYZ坐标加入存放顶点坐标的ArrayList
                alVertix.add(x1);
                alVertix.add(y1);
                alVertix.add(z1);
                alVertix.add(x3);
                alVertix.add(y3);
                alVertix.add(z3);
                alVertix.add(x0);
                alVertix.add(y0);
                alVertix.add(z0);

                alVertix.add(x1);
                alVertix.add(y1);
                alVertix.add(z1);
                alVertix.add(x2);
                alVertix.add(y2);
                alVertix.add(z2);
                alVertix.add(x3);
                alVertix.add(y3);
                alVertix.add(z3);
                /*
                2---------------3
                |             / |
                |          /    |
                |       /       |
                |    /          |
                | /             |
                1---------------0
                 */
            }
        }

        vCount = alVertix.size() / 3;
        float vertices[]=new float[alVertix.size()];
        for (int i=0;i<vertices.length;i++) {
            vertices[i]=alVertix.get(i);
        }

        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        fbv = bb.asFloatBuffer();
        fbv.put(vertices);
        fbv.position(0);
    }

    //初始化shader
    private void initShader() {
        String vertex = loadSH("vertex.sh");
        String shader = loadSH("frag.sh");

        int verS = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        if (verS != 0) {
            GLES20.glShaderSource(verS, vertex);
            GLES20.glCompileShader(verS);
        }

        int fragS = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        if (fragS != 0) {
            GLES20.glShaderSource(fragS, shader);
            GLES20.glCompileShader(fragS);
        }
        mProgram = GLES20.glCreateProgram();
        if (mProgram != 0) {
            GLES20.glAttachShader(mProgram, verS);
            GLES20.glAttachShader(mProgram, fragS);
            GLES20.glLinkProgram(mProgram);
        }

        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }




    //将sh文件加载进来
    private String loadSH(String fname) {
        String result = null;
        try {
            InputStream in = mContext.getAssets().open(fname);
            int ch = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((ch = in.read()) != -1) {
                baos.write(ch);
            }
            byte[] buff = baos.toByteArray();
            baos.close();
            in.close();
            result = new String(buff, "UTF-8");
            result = result.replaceAll("\\r\\n", "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }






    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 10);

        Matrix.setLookAtM(mVMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        draw();

    }


    public void draw() {
        GLES20.glUseProgram(mProgram);
        Matrix.setRotateM(mMMatrix, 0, 0, 0, 1, 0);

        mMVPMatrix = new float[16];
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, fbv);

        GLES20.glEnableVertexAttribArray(maPositionHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
    }
}
