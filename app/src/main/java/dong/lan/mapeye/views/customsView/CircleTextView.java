package dong.lan.mapeye.views.customsView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;


/**
 * Created by 梁桂栋 on 16-10-31 ： 下午11:42.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class CircleTextView extends TextView {

    private int bgColor =0xFF4CAF50;
    private String text;
    private Paint bgPaint;
    private Paint textPaint;
    private int radius =10;


    public CircleTextView(Context context) {
        this(context,null);
    }

    public CircleTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bgPaint = new Paint();
        textPaint = new Paint();
        bgColor = getDrawingCacheBackgroundColor();
        bgPaint.setColor(bgColor);
        bgPaint.setAntiAlias(true);
        textPaint.setAntiAlias(true);
        textPaint.setColor(getTextColors().getDefaultColor());
        textPaint.setTextSize(getTextSize());
        setLayerType(LAYER_TYPE_SOFTWARE,null);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        bgColor = getCurrentHintTextColor();
        bgPaint.setColor(bgColor);
        text = getText().toString();
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int m = Math.min(w,h);
        radius = m /10;
        w+=radius;
        h+=radius;
        setMeasuredDimension(w,h);
        bgPaint.setShadowLayer(radius,0,0,0XFF777777);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = Math.min(getWidth()-radius,getHeight()-radius);
        float textWidth = textPaint.measureText(text);
        float textHeight = textPaint.descent() + textPaint.ascent();
        canvas.drawCircle((w+radius)/2,(w + radius)/2,(w-radius)/2,bgPaint);
        canvas.drawText(text,(w +radius - textWidth)/2,(w+radius - textHeight)/2,textPaint);
    }


    public void setBgColor(int color){
        this.bgColor = color;
        this.bgPaint.setColor(bgColor);
        invalidate();
    }
    public void setBgColorFromRes(int resColorId){
        this.bgColor = getResources().getColor(resColorId);
        this.bgPaint.setColor(bgColor);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            ObjectAnimator.ofFloat(this, "scaleX", 1.0f, 0.7f, 1.0f).setDuration(300).start();
            ObjectAnimator.ofFloat(this, "scaleY", 1.0f, 0.7f, 1.0f).setDuration(300).start();
            ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.5f, 1.0f).setDuration(300).start();
        }
        return super.onTouchEvent(event);
    }
}
