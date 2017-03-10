/*
 *
 *   Copyright (C) 2016 author : 梁桂栋
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   Email me : stonelavender@hotmail.com
 *
 */

package dong.lan.mapeye.views.customsView;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 梁桂栋 on 16-12-4 ： 下午6:41.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class PinView extends View {

    private int radius = 0;
    private int padding = 10;
    private Paint paint;
    private Paint textPaint;
    private int w;
    private int h;
    private String text = "";
    private int color = Color.YELLOW;
    private int tw;
    private int th;


    public PinView(Context context, int radius, int color, String text) {
        this(context);
        this.radius = dp2px(radius);
        this.text = text;
        this.color = color;
    }

    public PinView(Context context) {
        this(context, null);
    }

    public PinView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);

        textPaint = new Paint();
        textPaint.setColor(Color.DKGRAY);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(dp2px(18));

        padding = dp2px(padding);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        tw = (int) textPaint.measureText(text);
        th = (int) (textPaint.getFontMetrics().bottom - textPaint.getFontMetrics().top);
        w = radius * 2 + getPaddingRight() + getPaddingLeft() + padding;
        w = w < tw ? tw + padding : w;
        if (radius == 0)
            radius = w / 2;
        h = w / 2 * 3 + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(w, h);
    }

    private RectF arcRect;
    private RectF textRect;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (text.length() < 10) {
            canvas.drawCircle(w / 2, w / 2, radius, paint);
            canvas.translate(0, radius / 2);
            if (arcRect == null)
                arcRect = new RectF(0, radius, w, h);
            canvas.drawArc(arcRect, -120, 60, true, paint);
            canvas.drawText(text, (w - tw) / 2, (w - th) / 2, textPaint);
        } else {
            if (textRect == null) {
                textRect = new RectF(0, 0, w, th + 2 * padding);
            }
            canvas.drawRoundRect(textRect, dp2px(8), dp2px(8), paint);
            if (arcRect == null)
                arcRect = new RectF(0, 0, w, w);
            canvas.drawArc(arcRect, -120, 60, true, paint);
            canvas.drawText(text, (w - tw) / 2, th + padding / 2, textPaint);
        }

    }

    private int dp2px(int dpVal) {
        return (int) (getResources().getDisplayMetrics().density * dpVal + 0.5);
    }
}
