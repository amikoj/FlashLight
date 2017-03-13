package enjoytoday.com.views.Bubble;

import java.util.Random;

/**
 * 气泡实体类
 */

public class Bubble {

    /**
     *
     * @param id bubble编号
     */
    public Bubble(int id) {
        this.id = id;
    }

    /**
     * 移动的范围
     */
    private final  static float Interval=10f;

    /**
     * bubble编号
     */
    private int id;

    /**
     * bubble的圆心x轴坐标
     */
    private float rx;

    /**
     * bubble的圆心的y轴坐标
     */
    private float ry;


    /**
     * bubble的半径
     */
    private float radius=30f;

    /**
     * bubble的背景色
     */
    private int color;


    /**
     * 下次的移动位置,需要考虑所有气泡之间的退避算法,以及每次的移动位置范围
     */
    public void  nextPosition(){
        Random random = new Random();
        rx= random.nextFloat()*Interval*(random.nextInt(2)==0?-1:1)+rx;
        ry= random.nextFloat()*Interval*(random.nextInt(2)==0?-1:1)+ry;

    }




    public float getRx() {
        return rx;
    }

    public void setRx(float rx) {
        this.rx = rx;
    }

    public float getRy() {
        return ry;
    }

    public void setRy(float ry) {
        this.ry = ry;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
