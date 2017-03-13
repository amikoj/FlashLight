package enjoytoday.com.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import enjoytoday.com.R;
import enjoytoday.com.utils.ViewUtils;
import enjoytoday.com.views.Bubble.BubbleManager;

/**
 * Created by hfcai on 10/03/17.
 */

public class BubbleView extends View {


    private float width;
    private float height;
//    private int[] bubbleColors=new int[]{Color.parseColor("#44fff0f5"),
//            Color.parseColor("#44fdf5e6"),Color.parseColor("#44f8f8ff"),Color.parseColor("#44eee0e5")};


    private int innerCircleColor= Color.parseColor("#66D1EEEE");
    private int middleCircleColor= Color.parseColor("#44D1EEEE");
    private int outCircleColor= Color.parseColor("#33D1EEEE");
    private float innerCircleRadius= ViewUtils.dp2px(getContext(),35);
    private float middleCircleRadius= ViewUtils.dp2px(getContext(),45);
    private float outCircleRadius= ViewUtils.dp2px(getContext(),55);

    private BubbleManager bubbleManager;



    private String text="点亮";
    private int textColor=getContext().getResources().getColor(R.color.colorPrimary);
    private float textSize=ViewUtils.dp2px(getContext(),22);


    private Paint mPaint;


    private float rx;
    private float ry;








    public BubbleView(Context context) {
        this(context,null,0);
    }

    public BubbleView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        bubbleManager=BubbleManager.getInstance();
    }



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }




    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getMeasureWidth(widthMeasureSpec), getMeasureHeight(heightMeasureSpec));
    }


    /**
     *
     * @param widthMeasureSpec
     * @return
     * 测量 width
     */
    private int getMeasureWidth(int widthMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int width = 0;
        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                width = (int)this.width ;
                break;
            case MeasureSpec.EXACTLY:
                this.width=widthSize;
                width=widthSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                width = (int) this.width;
                break;
        }
        return width;
    }

    /**
     *
     * @param heightMeasureSpec
     * @return
     * 测量 height
     */
    private int getMeasureHeight(int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
            this.height = height;
        } else {
            this.height=getHeight();
            height = (int) this.height;
        }
        return height;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (mPaint==null){
            mPaint=new Paint();
        }




        //绘制中心部分的三个圆
        if (rx==0 || ry==0){
            rx=width/2f;
            ry=height/2f;
        }


        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        //最外环的圆
        mPaint.setColor(outCircleColor);
        canvas.drawCircle(rx,ry,outCircleRadius,mPaint);

        //中间的圆
        mPaint.setColor(middleCircleColor);
        canvas.drawCircle(rx,ry,middleCircleRadius,mPaint);

        //里面的圆
        mPaint.setColor(innerCircleColor);
        canvas.drawCircle(rx,ry,innerCircleRadius,mPaint);


        //绘制文字
        mPaint.setColor(textColor);
        mPaint.setTextSize(textSize);
        RectF targetRect=new RectF(rx-innerCircleRadius,ry-innerCircleRadius,rx+innerCircleRadius,ry+innerCircleRadius);
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline= (int) ((targetRect.bottom+targetRect.top-fontMetrics.bottom-fontMetrics.top)/2);
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(text,targetRect.centerX(),baseline,mPaint);






        //其他颜色的圆形

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {


        int action=event.getAction();

        if ((action==MotionEvent.ACTION_UP || action==MotionEvent.ACTION_CANCEL) && innerCircleColor==Color.parseColor("#aaBBFFFF")){
            Log.e("dispatch","action_up or action_cancel");
            innerCircleColor= Color.parseColor("#66D1EEEE");
            invalidate();


        }else if (action==MotionEvent.ACTION_DOWN ){
            Log.e("dispatch","ACTION_DOWN");
            innerCircleColor= Color.parseColor("#aaBBFFFF");
            invalidate();

        }

        return super.dispatchTouchEvent(event);
    }




}
