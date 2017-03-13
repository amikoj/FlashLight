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

import java.util.Arrays;
import java.util.List;

import enjoytoday.com.LogUtils;
import enjoytoday.com.utils.ViewUtils;

/**
 * 选项卡，切换界面
 */

public class SwitchButton extends View {

    private float height;
    private float width;
    private float textSize=ViewUtils.dp2px(getContext(),20f);
    private float defaultMargin=ViewUtils.dp2px(getContext(),30f);
    private float defaultPadding=ViewUtils.dp2px(getContext(),10f);
    private float rx=ViewUtils.dp2px(getContext(),8f);
    private float ry=ViewUtils.dp2px(getContext(),5f);
    private int selectNum=1;
    private int frameColor= Color.parseColor("#436EEE");
    private int clickColor=Color.parseColor("#436EEE");
    private int textColor=Color.parseColor("#436EEE");
    private int backgroundColor= Color.WHITE;
    private static final String[] defaultStrings=new String[]{"Flash","Screen","Explain"};
    private List<String> tabTexts= Arrays.asList(defaultStrings);
    private OnSwitchChangeListener onSwitchChangeListener;
    private Paint mPaint;
    private  float divisionWidth;

    public SwitchButton(Context context) {
        this(context,null,0);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }




    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getMeasureWidth(widthMeasureSpec), getMeasureHeight(heightMeasureSpec));
    }




    public int getCurrentTab(){
        return selectNum;
    }



    public Object[] getTabList(){
        return tabTexts.toArray();
    }


    public void setTabTexts(String[] texts){
        tabTexts= Arrays.asList(texts);
        invalidate();

    }

    public void setTabTexts(List<String> texts){
        tabTexts=texts;
        invalidate();

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

    public OnSwitchChangeListener getOnSwitchChangeListener() {
        return onSwitchChangeListener;
    }

    public void setOnSwitchChangeListener(OnSwitchChangeListener onSwitchChangeListener) {
        this.onSwitchChangeListener = onSwitchChangeListener;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (tabTexts==null || tabTexts.size()<2){
            return;
        }

        int counter=tabTexts.size();
        divisionWidth=(width-2*defaultMargin)/counter;

        if (mPaint==null){
            mPaint=new Paint();
        }

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        //绘制圆角矩形
        mPaint.setColor(backgroundColor);

        RectF rectF=new RectF(defaultMargin,defaultPadding,width-defaultMargin,height-defaultPadding);
        canvas.drawRoundRect(rectF,rx,ry,mPaint);


        //绘制分割线
        for (int i=0;i<counter-1;i++){
            mPaint.setColor(frameColor);
            canvas.drawLine((i+1)*divisionWidth+defaultMargin,defaultPadding,(i+1)*divisionWidth+defaultMargin,height-defaultPadding,mPaint);
        }


        mPaint.setColor(clickColor);
        //选中背景
        if (selectNum==0){
            //第一个

            RectF rectF1=new RectF(defaultMargin,defaultPadding,defaultMargin+divisionWidth/2f,height-defaultPadding);
            canvas.drawRoundRect(rectF1,rx,ry,mPaint);
            canvas.drawRect(defaultMargin+rx,defaultPadding,defaultMargin+divisionWidth,height-defaultPadding,mPaint);

        }else if (selectNum==counter-1){
            RectF rectF1=new RectF(width-defaultMargin-divisionWidth/2f,defaultPadding,width-defaultMargin,height-defaultPadding);
            canvas.drawRoundRect(rectF1,rx,ry,mPaint);
            //最后一个
            canvas.drawRect(width-defaultMargin-divisionWidth,defaultPadding,width-defaultMargin-rx, height-defaultPadding,mPaint);

        }else {
            canvas.drawRect(selectNum*divisionWidth+defaultMargin,defaultPadding,(selectNum+1)*divisionWidth+defaultMargin,
                    height-defaultPadding,mPaint);
        }


        mPaint.setTextSize(textSize);
        /**
         * 绘制文字
         */
        RectF targetRect=null;
        for (int i=0;i<counter;i++){

            if (selectNum==i){
                mPaint.setColor(backgroundColor);
            }else {
                mPaint.setColor(textColor);
            }
            targetRect=new RectF(i*divisionWidth+defaultMargin,defaultPadding,(i+1)*divisionWidth+defaultMargin,
                    height-defaultPadding);
            Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
            int baseline= (int) ((targetRect.bottom+targetRect.top-fontMetrics.bottom-fontMetrics.top)/2);
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(tabTexts.get(i),targetRect.centerX(),baseline,mPaint);
        }


    }






    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {


        if (selectNum>=tabTexts.size()){
            LogUtils.setDebug("array size overflow.");
            return  super.dispatchTouchEvent(event);
        }

        if (event.getAction()==MotionEvent.ACTION_DOWN){
            float x=event.getX();
            float y=event.getY();
            float division=x-defaultMargin;
            int a= (int) (division/divisionWidth);

            if (division>0 && (y>defaultPadding && y<height-defaultPadding) && selectNum!=a){
                selectNum=a;
                if (this.onSwitchChangeListener!=null){
                    onSwitchChangeListener.onSwitchChange(selectNum,tabTexts.get(selectNum),this);
                }
                invalidate();

            }

        }

        return super.dispatchTouchEvent(event);
    }

    /**
     * tab切换监听
     */
  public interface  OnSwitchChangeListener{

        /**
         *
         * @param position  位置,从左到右
         * @param text      显示text
         * @param switchButton  当前操作的SwitchButton对象
         */
    void  onSwitchChange(int position,String text,SwitchButton switchButton);

    }


}
