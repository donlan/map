package dong.lan.mapeye.views.customsView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import dong.lan.mapeye.R;


/**
 * Created by 梁桂栋 on 16-10-31 ： 下午11:42.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class LoadingTextView extends TextView {

    private float roundRadius = 0;
    private boolean clickAnimation = true;
    private Paint bgPaint;
    private float shadowRadius = 0;
    private RectF bgRect;
    private RectF loadingRect;
    private int startAngle = 0;
    private int loadingRadius = 10;
    private Paint loadingPaint;
    private String preText;

    private boolean isLoading = false;
    private int cIndex = 0;
    private int colors[] = new int[]{
            R.color.md_green_600,
            R.color.md_amber_500,
            R.color.md_red_500,
            R.color.md_deep_purple_500,
            R.color.md_white_1000};


    public LoadingTextView(Context context) {
        this(context, null);
    }

    public LoadingTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int bgColor = 0xFF4CAF50;
        int shadowColor = 0XFF777777;
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LabelTextView);
            bgColor = ta.getColor(R.styleable.LabelTextView_bg_color, bgColor);
            roundRadius = ta.getDimension(R.styleable.LabelTextView_radius, roundRadius);
            clickAnimation = ta.getBoolean(R.styleable.LabelTextView_clickAnimation, clickAnimation);
            shadowRadius = ta.getDimension(R.styleable.LabelTextView_shadowRadius, shadowRadius);
            shadowColor = ta.getColor(R.styleable.LabelTextView_shadowColor, shadowColor);
            ta.recycle();
        }
        bgPaint = new Paint();
        bgPaint.setColor(bgColor);
        bgPaint.setAntiAlias(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        bgPaint.setShadowLayer(shadowRadius, 0, 0, shadowColor);
        loadingPaint = new Paint();
        loadingPaint.setAntiAlias(true);
        loadingPaint.setColor(Color.WHITE);
        preText = getText().toString();
    }


    public void startLoading(String loadingText) {
        loadingRadius = 1;
        isLoading = true;
        setText(loadingText);
        invalidate();
    }

    public void stopLoading() {
        isLoading = false;
        setText(preText);
        invalidate();
    }


    public boolean isLoading() {
        return isLoading;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int wm = MeasureSpec.getMode(widthMeasureSpec);
        int hm = MeasureSpec.getMode(heightMeasureSpec);
        if (wm == MeasureSpec.AT_MOST) {
            float textWidth = getPaint().measureText(getText().toString());
            w = (int) (textWidth + getPaddingLeft() + getPaddingRight());
        }
        if (hm == MeasureSpec.AT_MOST) {
            float textHeight = getPaint().getFontMetrics().bottom - getPaint().getFontMetrics().top;
            h = (int) (textHeight + getPaddingTop() + getPaddingBottom());
        }

        setMeasuredDimension((int) (w + shadowRadius), (int) (h + shadowRadius));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bgRect == null) {
            float r = shadowRadius / 2;
            bgRect = new RectF(r, r, getWidth() - r, getHeight() - r);
        }
        if (loadingRect == null) {
            int m = (Math.min(getWidth(), getHeight()) - getPaddingTop() * 2) * 2;
            loadingRect = new RectF(0, 0, m / 2, m);
        }
        canvas.drawRoundRect(bgRect, roundRadius, roundRadius, bgPaint);
        if (isLoading) {
            int r = getWidth()/2;
            if (loadingRadius > r) {
                loadingRadius = 5;
            }
            int increase = 2;
            loadingRadius += increase;
            canvas.drawCircle(r,getHeight()/2, loadingRadius, loadingPaint);
            postInvalidateDelayed(20);
        } else {
            setText(preText);
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isLoading)
            return false;
        if (clickAnimation && event.getAction() == MotionEvent.ACTION_DOWN) {
            ObjectAnimator.ofFloat(this, "scaleX", 1.0f, 0.7f, 1.0f).setDuration(300).start();
            ObjectAnimator.ofFloat(this, "scaleY", 1.0f, 0.7f, 1.0f).setDuration(300).start();
            ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.5f, 1.0f).setDuration(300).start();
        }
        return super.onTouchEvent(event);
    }
}
