/*
 *
 *   Copyright (C) 2017 author : 梁桂栋
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
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import dong.lan.mapeye.utils.DisplayUtils;


/**
 * Created by 梁桂栋 on 17-3-21 ： 下午10:48.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: SmartTrip
 */

public class MapPinNumView extends View {

    private static final String TAG = MapPinNumView.class.getSimpleName();
    private String num;
    private int bgColor;
    private int textSize;
    private int textColor;


    private Paint paint;
    private int textWidth;
    private int mWidth;
    private float textHeigth;
    private RectF bound;
    private int radius;

    public MapPinNumView(Context context) {
        super(context);
    }

    public MapPinNumView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init("0", Color.YELLOW, 18, Color.DKGRAY);
    }

    public MapPinNumView(Context context, String num, int bgColor, int textSize, int textColor) {
        super(context);
        init(num, bgColor, textSize, textColor);

    }

    private void init(String num, int bgColor, int textSize, int textColor) {
        this.num = num;
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.textSize = DisplayUtils.sp2px(getContext(), textSize);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(bgColor);
        paint.setTextSize(this.textSize);
        bound = new RectF(0, 0, 0, 0);
    }

    public void setNum(String num) {
        this.num = num;
        invalidate();
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
        invalidate();
    }

    public void setTextSize(int textSize) {
        this.textSize = DisplayUtils.sp2px(getContext(), textSize);
        invalidate();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        radius = 0;
        textWidth = (int) paint.measureText(num);
        textHeigth = paint.getFontMetrics().descent - paint.getFontMetrics().ascent;
        mWidth = textWidth + DisplayUtils.dip2px(getContext(), 20);
        radius = mWidth / 2;
        bound.set(0, 0, radius, radius);
        setMeasuredDimension(mWidth + getPaddingLeft() + getPaddingRight(),
                (int) (mWidth + radius / Math.sin(Math.PI / 4)) + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mWidth / 2, mWidth / 2);
        canvas.rotate(45);
        canvas.drawRect(bound, paint);
        canvas.restore();
        paint.setColor(bgColor);
        canvas.drawCircle(radius, mWidth / 2, mWidth / 2, paint);
        paint.setColor(textColor);
        canvas.drawText(num, (mWidth - textWidth) / 2, (mWidth + textHeigth) / 2, paint);

    }
}
