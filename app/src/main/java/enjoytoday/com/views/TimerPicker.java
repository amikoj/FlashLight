package enjoytoday.com.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

import enjoytoday.com.utils.ViewUtils;

/**
 * Created by hfcai on 14/03/17.
 */

public class TimerPicker extends View {



    private int width;
    private int height;
    private Paint mPaintLine;
    private Paint mPaintCircle;
    private Paint mPaintHour;
    private Paint mPaintMinute;
    private Paint mPaintSec;
    private Paint mPaintText;
    private Calendar mCalendar;
    public static final int NEED_INVALIDATE = 0X23;

    //每隔一秒，在handler中调用一次重新绘制方法
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case NEED_INVALIDATE:
                    mCalendar = Calendar.getInstance();
                    invalidate();//告诉UI主线程重新绘制
                    handler.sendEmptyMessageDelayed(NEED_INVALIDATE,1000);
                    break;
                default:
                    break;
            }
        }
    };

    public TimerPicker(Context context) {
        super(context);
    }

    public TimerPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        mCalendar = Calendar.getInstance();

        mPaintLine = new Paint();
        mPaintLine.setColor(Color.BLUE);
        mPaintLine.setStrokeWidth(ViewUtils.dp2px(getContext(),4));

        mPaintCircle = new Paint();
        mPaintCircle.setColor(Color.GREEN);//设置颜色
        mPaintCircle.setStrokeWidth(ViewUtils.dp2px(getContext(),4));//设置线宽
        mPaintCircle.setAntiAlias(true);//设置是否抗锯齿
        mPaintCircle.setStyle(Paint.Style.STROKE);//设置绘制风格

        mPaintText = new Paint();
        mPaintText.setColor(Color.BLUE);
        mPaintText.setStrokeWidth(ViewUtils.dp2px(getContext(),4));
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(ViewUtils.dp2px(getContext(),10));

        mPaintHour = new Paint();
        mPaintHour.setStrokeWidth(ViewUtils.dp2px(getContext(),2));
        mPaintHour.setColor(Color.BLUE);

        mPaintMinute = new Paint();
        mPaintMinute.setStrokeWidth(ViewUtils.dp2px(getContext(),2));
        mPaintMinute.setColor(Color.BLUE);

        mPaintSec = new Paint();
        mPaintSec.setStrokeWidth(ViewUtils.dp2px(getContext(),4));
        mPaintSec.setColor(Color.BLUE);

        handler.sendEmptyMessage(NEED_INVALIDATE);//向handler发送一个消息，让它开启重绘
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }










    private float radius;

    private float padding=ViewUtils.dp2px(getContext(),18);

    private float mirroRadius=ViewUtils.px2dip(getContext(),8);







    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (radius==0){
            radius=height/2-padding;
        }
        //画出大圆
        canvas.drawCircle(width / 2, height / 2, radius, mPaintCircle);
        //画出圆中心
        canvas.drawCircle(width / 2, height / 2, mirroRadius, mPaintCircle);
        //依次旋转画布，画出每个刻度和对应数字
        for (int i = 1; i <= 12; i++) {
            canvas.save();//保存当前画布
            canvas.rotate(360/12*i,width/2,height/2);
            //左起：起始位置x坐标，起始位置y坐标，终止位置x坐标，终止位置y坐标，画笔(一个Paint对象)
            canvas.drawLine(width / 2, height / 2 - radius, width / 2, height / 2 - radius + ViewUtils.dp2px(getContext(),10), mPaintCircle);
            //左起：文本内容，起始位置x坐标，起始位置y坐标，画笔
            canvas.drawText(""+i, width / 2, height / 2 - radius + ViewUtils.dp2px(getContext(),20), mPaintText);
            canvas.restore();//
        }

        int minute = mCalendar.get(Calendar.MINUTE);//得到当前分钟数
        int hour = mCalendar.get(Calendar.HOUR);//得到当前小时数
        int sec = mCalendar.get(Calendar.SECOND);//得到当前秒数

        float minuteDegree = minute/60f*360;//得到分针旋转的角度
        canvas.save();
        canvas.rotate(minuteDegree, width / 2, height / 2);
        canvas.drawLine(width / 2, padding+radius/4, width / 2, height / 2 + ViewUtils.dp2px(getContext(),15), mPaintMinute);
        canvas.restore();

        float hourDegree = (hour*60+minute)/12f/60*360;//得到时钟旋转的角度
        canvas.save();
        canvas.rotate(hourDegree, width / 2, height / 2);
        canvas.drawLine(width / 2, padding+radius/2, width / 2, height / 2 + ViewUtils.dp2px(getContext(),13), mPaintHour);
        canvas.restore();

        float secDegree = sec/60f*360;//得到秒针旋转的角度
        canvas.save();
        canvas.rotate(secDegree,width/2,height/2);
        canvas.drawLine(width/2,padding/2,width/2,height/2+ViewUtils.dp2px(getContext(),13),mPaintSec);
        canvas.restore();

    }
}
