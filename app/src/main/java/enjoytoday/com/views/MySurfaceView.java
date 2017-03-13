package enjoytoday.com.views;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Map;

public class MySurfaceView extends GLSurfaceView{

    private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器
    public int openLightNum=2;         //开灯数量标记，1为一盏灯，2，为两盏灯...
    private String color;


    private Handler mHandler=new Handler();
    private Thread animationThread=new Thread(){
        @Override
        public void run() {
                mRenderer.ball.mAngleX += 300 * TOUCH_SCALE_FACTOR;//设置沿x轴旋转角度
                mRenderer.ball.mAngleZ += 300 * TOUCH_SCALE_FACTOR;//设置沿z轴旋转角度
                requestRender();//重绘画面
                mHandler.postDelayed(this,40);
        }
    };


    public void startAnimation(){
       mHandler.postDelayed(animationThread,10);
    }


    public void stopAnimation(){
        if (mHandler!=null && animationThread!=null){
            mHandler.removeCallbacks(animationThread);
        }

    }


    public MySurfaceView(Context context,String color) {
        super(context);
        this.color=color;
        mRenderer = new SceneRenderer();    //创建场景渲染器
        setRenderer(mRenderer);             //设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);//设置渲染模式为主动渲染
    }

    //触摸事件回调方法
    @Override
    public boolean onTouchEvent(MotionEvent e) {
       return super.onTouchEvent(e);
    }



    private class SceneRenderer implements GLSurfaceView.Renderer {
        Ball ball=new Ball(5);

        public SceneRenderer(){

        }
        public void onDrawFrame(GL10 gl){
            gl.glShadeModel(GL10.GL_SMOOTH);
            gl.glEnable(GL10.GL_LIGHTING);//允许光照
            initMaterialWhite(gl);//初始化材质为白色
            float[] positionParams0={2,1,0,1};//最后的1表示是定位光，此为0号灯位置参数。
            float[] positionParams1={-2,1,0,1};//最后的1表示是定位光，此为1号灯位置参数。
            float[] positionParams2={0,0,2,1};//最后的1表示是定位光，此为2号灯位置参数。
            float[] positionParams3={1,1,2,1};//最后的1表示是定位光，此为3号灯位置参数。
            float[] positionParams4={-1,-1,2,1};//最后的1表示是定位光，此为4号灯位置参数。
            gl.glDisable(GL10.GL_LIGHT0);   //每次绘制前，取消已开启的灯光效果
            gl.glDisable(GL10.GL_LIGHT1);   //每次绘制前，取消已开启的灯光效果
            gl.glDisable(GL10.GL_LIGHT2);   //每次绘制前，取消已开启的灯光效果
            gl.glDisable(GL10.GL_LIGHT3);   //每次绘制前，取消已开启的灯光效果
            gl.glDisable(GL10.GL_LIGHT4);   //每次绘制前，取消已开启的灯光效果

            switch(openLightNum){
                case 1:                 //开启一盏灯
                    initLight0(gl);//初始化0号灯

                    gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, positionParams0,0);
                    break;
                case 2:                 //开启两盏灯
                    initLight0(gl);//初始化0号灯
                    gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, positionParams0,0);

                    initLight1(gl);//初始化1号灯
                    gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, positionParams1,0);
                    break;
                case 3:                 //开启三盏灯
                    initLight0(gl);//初始化0号灯
                    gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, positionParams0,0);

                    initLight1(gl);//初始化1号灯
                    gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, positionParams1,0);

                    initLight2(gl);//初始化2号灯
                    gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_POSITION, positionParams2,0);
                    break;
                case 4:                 //开启四盏灯
                    initLight0(gl);//初始化0号灯
                    gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, positionParams0,0);

                    initLight1(gl);//初始化1号灯
                    gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, positionParams1,0);

                    initLight2(gl);//初始化2号灯
                    gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_POSITION, positionParams2,0);

                    initLight3(gl);//初始化3号灯
                    gl.glLightfv(GL10.GL_LIGHT3, GL10.GL_POSITION, positionParams3,0);
                    break;
                case 5:                 //开启五盏灯
                    initLight0(gl);//初始化0号灯
                    gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, positionParams0,0);

                    initLight1(gl);//初始化1号灯
                    gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, positionParams1,0);

                    initLight2(gl);//初始化2号灯
                    gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_POSITION, positionParams2,0);

                    initLight3(gl);//初始化3号灯
                    gl.glLightfv(GL10.GL_LIGHT3, GL10.GL_POSITION, positionParams3,0);

                    initLight4(gl);//初始化4号灯
                    gl.glLightfv(GL10.GL_LIGHT4, GL10.GL_POSITION, positionParams4,0);
                    break;
            }
            //清除颜色缓存
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            //设置当前矩阵为模式矩阵
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            //设置当前矩阵为单位矩阵
            gl.glLoadIdentity();

            gl.glTranslatef(0, 0f, -1.8f);
            ball.drawSelf(gl);
            gl.glLoadIdentity();
        }
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置
            gl.glViewport(0, 0, width, height);
            //设置当前矩阵为投影矩阵
            gl.glMatrixMode(GL10.GL_PROJECTION);
            //设置当前矩阵为单位矩阵
            gl.glLoadIdentity();
            //计算透视投影的比例
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
        }


        /**
         *
         * @param color
         * @return  返回顺序 a r  b
         */
        private int[] parseFromColor(String color){
            if (color==null || color.length()<7 && color.length()>9){
                return new int[]{255,255,255,255};
            }else if (color.length()==7){
                return new int[]{255,HexString2D(color.substring(1,3)),HexString2D(color.substring(3,5)),HexString2D(color.substring(5,7))};
            }else if (color.length()==9){
                return new int[]{HexString2D(color.substring(1,3)),HexString2D(color.substring(3,5)),HexString2D(color.substring(5,7)),HexString2D(color.substring(7,9))};
            }else {
                return new int[]{255,255,255,255};
            }

        }




        private int HexString2D(String hexString){
            if (hexString==null || hexString.length()==0){
                return 0;
            }else {
                char[] chars=hexString.toLowerCase().toCharArray();
                int num=0;

                for (int i=0;i<chars.length;i++){
                    int j;
                    if ("1234567890".contains(chars[i]+"")){
                        j= Integer.parseInt(chars[i]+"");
                    }else if ("aAbBcCdDeEfF".contains(chars[i]+"")){
                       j= chars[i]-'a'+10;
                    }else {
                        return 0;
                    }

                   num+= Math.pow(16,(chars.length-i-1))*j;

                }

                return num;


            }


        }












        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //关闭抗抖动
            gl.glDisable(GL10.GL_DITHER);
            //设置特定Hint项目的模式，这里为设置为使用快速模式
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,GL10.GL_FASTEST);

            int[] colors=parseFromColor(color);
            Log.e("color","MySurfaceView color:"+colors[0]+","+colors[1]+","+colors[2]+","+colors[3]);

            gl.glClearColor(colors[1],colors[2],colors[3],colors[0]);

            //设置着色模型为平滑着色
            gl.glShadeModel(GL10.GL_SMOOTH);//GL10.GL_SMOOTH  GL10.GL_FLAT
            //启用深度测试
            gl.glEnable(GL10.GL_DEPTH_TEST);
        }
    }
    private void initLight0(GL10 gl){
        gl.glEnable(GL10.GL_LIGHT0);//打开0号灯  ，白色
        //环境光设置
        float[] ambientParams={0.1f,0.1f,0.1f,1.0f};//光参数 RGBA
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambientParams,0);
        //散射光设置
        float[] diffuseParams={0.5f,0.5f,0.5f,1.0f};//光参数 RGBA
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, diffuseParams,0);
        //反射光设置
        float[] specularParams={1.0f,1.0f,1.0f,1.0f};//光参数 RGBA
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, specularParams,0);
    }
    private void initLight1(GL10 gl) {
        gl.glEnable(GL10.GL_LIGHT1);//打开1号灯  ,红色
        //环境光设置
        float[] ambientParams={0.2f,0.03f,0.03f,1.0f};//光参数 RGBA
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, ambientParams,0);
        //散射光设置
        float[] diffuseParams={0.5f,0.1f,0.1f,1.0f};//光参数 RGBA
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, diffuseParams,0);
        //反射光设置
        float[] specularParams={1.0f,0.1f,0.1f,1.0f};//光参数 RGBA
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPECULAR, specularParams,0);
    }
    private void initLight2(GL10 gl) {
        gl.glEnable(GL10.GL_LIGHT2);//打开1号灯  ,蓝色
        //环境光设置
        float[] ambientParams={0.03f,0.03f,0.2f,1.0f};//光参数 RGBA
        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_AMBIENT, ambientParams,0);
        //散射光设置
        float[] diffuseParams={0.1f,0.1f,0.5f,1.0f};//光参数 RGBA
        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_DIFFUSE, diffuseParams,0);
        //反射光设置
        float[] specularParams={0.1f,0.1f,1.0f,1.0f};//光参数 RGBA
        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_SPECULAR, specularParams,0);
    }
    private void initLight3(GL10 gl) {
        gl.glEnable(GL10.GL_LIGHT3);//打开3号灯  ,绿色
        //环境光设置
        float[] ambientParams={0.03f,0.2f,0.03f,1.0f};//光参数 RGBA
        gl.glLightfv(GL10.GL_LIGHT3, GL10.GL_AMBIENT, ambientParams,0);
        //散射光设置
        float[] diffuseParams={0.1f,0.5f,0.1f,1.0f};//光参数 RGBA
        gl.glLightfv(GL10.GL_LIGHT3, GL10.GL_DIFFUSE, diffuseParams,0);
        //反射光设置
        float[] specularParams={0.1f,1.0f,0.1f,1.0f};//光参数 RGBA
        gl.glLightfv(GL10.GL_LIGHT3, GL10.GL_SPECULAR, specularParams,0);
    }
    private void initLight4(GL10 gl) {
        gl.glEnable(GL10.GL_LIGHT4);//打开3号灯  ,黄色
        //环境光设置
        float[] ambientParams={0.2f,0.2f,0.03f,1.0f};//光参数 RGBA
        gl.glLightfv(GL10.GL_LIGHT4, GL10.GL_AMBIENT, ambientParams,0);
        //散射光设置
        float[] diffuseParams={0.5f,0.5f,0.1f,1.0f};//光参数 RGBA
        gl.glLightfv(GL10.GL_LIGHT4, GL10.GL_DIFFUSE, diffuseParams,0);
        //反射光设置
        float[] specularParams={1.0f,1.0f,0.1f,1.0f};//光参数 RGBA
        gl.glLightfv(GL10.GL_LIGHT4, GL10.GL_SPECULAR, specularParams,0);
    }
    private void initMaterialWhite(GL10 gl) {//材质为白色时什么颜色的光照在上面就将体现出什么颜色
        //环境光为白色材质
        float ambientMaterial[] = {0.4f, 0.4f, 0.4f, 1.0f};
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, ambientMaterial,0);
        //散射光为白色材质
        float diffuseMaterial[] = {0.8f, 0.8f, 0.8f, 1.0f};
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, diffuseMaterial,0);
        //高光材质为白色
        float specularMaterial[] = {1.0f, 1.0f, 1.0f, 1.0f};
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, specularMaterial,0);
        //高光反射区域,数越大高亮区域越小越暗
        float shininessMaterial[] = {1.5f};
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, shininessMaterial,0);
    }
}