package dong.lan.mapeye.views.customsView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import dong.lan.mapeye.R;


/**
 * Created by 梁桂栋 on 16-10-31 ： 下午11:42.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class LabelTextView extends TextView {

    private static final String TAG = "LabelTextView";
    private float roundRadius = 0;
    private boolean clickAnimation = true;
    private Paint bgPaint;
    private float shadowRadius = 0;
    private RectF bgRect;


    public LabelTextView(Context context) {
        this(context, null);
    }

    public LabelTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabelTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int bgColor = 0xFF4CAF50;
        int shadowColor = 0XFF777777;
        int strokeWidth = 0;
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LabelTextView);
            bgColor = ta.getColor(R.styleable.LabelTextView_bg_color, bgColor);
            roundRadius = ta.getDimension(R.styleable.LabelTextView_radius, roundRadius);
            clickAnimation = ta.getBoolean(R.styleable.LabelTextView_clickAnimation, clickAnimation);
            shadowRadius = ta.getDimension(R.styleable.LabelTextView_shadowRadius, shadowRadius);
            shadowColor = ta.getColor(R.styleable.LabelTextView_shadowColor, shadowColor);
            strokeWidth = (int) ta.getDimension(R.styleable.LabelTextView_strokeWidth, 0);
            ta.recycle();
        }
        bgPaint = new Paint();
        if (strokeWidth > 0) {
            bgPaint.setStyle(Paint.Style.STROKE);
            bgPaint.setStrokeWidth(strokeWidth);
            bgPaint.setStrokeCap(Paint.Cap.ROUND);
            bgPaint.setStrokeJoin(Paint.Join.ROUND);
        }
        bgPaint.setColor(bgColor);
        bgPaint.setAntiAlias(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        bgPaint.setShadowLayer(shadowRadius, 0, 0, shadowColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int w = MeasureSpec.getSize(widthMeasureSpec);
//        int h = MeasureSpec.getSize(heightMeasureSpec);
//        int wm = MeasureSpec.getMode(widthMeasureSpec);
//        int hm = MeasureSpec.getMode(heightMeasureSpec);
//        if (hm == MeasureSpec.AT_MOST || hm == MeasureSpec.UNSPECIFIED) {
//            float textHeight = getPaint().getFontMetrics().bottom - getPaint().getFontMetrics().top;
//            h = (int) (textHeight + getPaddingTop() + getPaddingBottom());
//
//        }
//        if (wm == MeasureSpec.AT_MOST) {
//            float textWidth = getPaint().measureText(getText().toString());
//            w = (int) (textWidth + getPaddingLeft() + getPaddingRight());
//        }
//        setMeasuredDimension((int) (w + shadowRadius), (int) (h + shadowRadius));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        bgRect = null;
        super.setText(text, type);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bgRect == null) {
            float r = shadowRadius / 2;
            bgRect = new RectF(r, r, getWidth() - r, getHeight() - r);
        }
        canvas.drawRoundRect(bgRect, roundRadius, roundRadius, bgPaint);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (clickAnimation && event.getAction() == MotionEvent.ACTION_DOWN) {
            ObjectAnimator.ofFloat(this, "scaleX", 1.0f, 0.9f, 1.0f).setDuration(200).start();
            ObjectAnimator.ofFloat(this, "scaleY", 1.0f, 0.9f, 1.0f).setDuration(200).start();
            ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.5f, 1.0f).setDuration(200).start();
        }
        return super.onTouchEvent(event);
    }
}
