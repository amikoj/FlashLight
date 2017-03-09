package enjoytoday.com.utils;


import android.content.Context;

/**
 * View操作相关工具方法
 */
public class  ViewUtils{



    /**
     *
     * @throws IllegalAccessException  创建对象抛出访问异常
     * 防止恶意创建对象
     *
     */
    private ViewUtils() throws IllegalAccessException {
        throw  new IllegalAccessException("can not access.");
    }




    public static int dp2px(Context context, float dp){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale +0.5f);

    }


    public static float px2dip(Context context,float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale +0.5f);

    }




}