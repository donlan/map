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
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import dong.lan.mapeye.R;

/**
 * Created by 梁桂栋 on 16-10-26 ： 下午10:02.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: ToggleButton
 */

public class ToggleButton extends View {

    private static final String TAG = "ToggleButton";

    private boolean isChecked = false;

    private int checkColor = 0xff15b17f;
    private int uncheckColor = 0xffA0E4B0;
    private int checkedBgColor = 0xff73ccae;
    private int unCheckedBgColor = 0xffcbc9d5;

    private int circleRadius = 5;

    private int shadowSize = 2;
    private int shadowDx = 2;
    private int shadowDy = 2;

    private int width;

    private int height;

    private int rectRadius;


    private Paint circlePaint;

    private Paint rectBgPaint;

    private RectF bgRect;

    public ToggleButton(Context context) {
        this(context, null);
    }

    public ToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE,null); //保证能通过setShadowLayout设置阴影
        int shadowColor = Color.LTGRAY;
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ToggleButton);
            checkColor = ta.getColor(R.styleable.ToggleButton_tb_check_color, checkColor);
            checkedBgColor = ta.getColor(R.styleable.ToggleButton_tb_check_bg_color, checkedBgColor);
            unCheckedBgColor = ta.getColor(R.styleable.ToggleButton_tb_uncheck_bg_color, unCheckedBgColor);
            circleRadius = (int) ta.getDimension(R.styleable.ToggleButton_tb_circle_radius, circleRadius);
            shadowSize = (int) ta.getDimension(R.styleable.ToggleButton_tb_shadow_radius,shadowSize);
            shadowColor = ta.getColor(R.styleable.ToggleButton_tb_shadow_color,shadowColor);
            uncheckColor = ta.getColor(R.styleable.ToggleButton_tb_uncheck_color,uncheckColor);
            ta.recycle();
        }else{
            circleRadius = dp2px(circleRadius);
            shadowSize = dp2px(shadowSize);
            shadowDx = dp2px(shadowDx);
            shadowDy = dp2px(shadowDy);
        }
        width = 4 * circleRadius + shadowSize * 2 + shadowDx;
        height = (shadowSize + circleRadius) * 2 + shadowDy;
        int rectHeight = circleRadius;
        rectRadius = rectHeight / 2;

        circlePaint = new Paint();
        circlePaint.setColor(uncheckColor);
        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);
        circlePaint.setShadowLayer(shadowSize,shadowDx,shadowDy, shadowColor);

        rectBgPaint = new Paint();
        rectBgPaint.setDither(true);
        rectBgPaint.setColor(unCheckedBgColor);
        rectBgPaint.setAntiAlias(true);

        int sy = (height - rectHeight - shadowSize -shadowDy)/2;
        bgRect = new RectF(shadowSize,sy,width - shadowSize * 2-shadowDx, rectHeight +sy);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureWidth(width);
        setMeasuredDimension(width, height);
    }


    /**
     * 确保没设置按钮半径的情况使用默认的半径
     * @param measureSpec
     */
    private void measureWidth(int measureSpec) {
        int size = MeasureSpec.getSize(measureSpec);
        int mode = MeasureSpec.getMode(measureSpec);
        if (mode == MeasureSpec.AT_MOST) {
            width = Math.max(width, size);
        }
    }

    public void setChecked(boolean isChecked){
        this.isChecked = isChecked;
        invalidate();
    }

    public boolean isChecked(){
        return isChecked;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isChecked) {
            circlePaint.setColor(checkColor);
            rectBgPaint.setColor(checkedBgColor);
            canvas.drawRoundRect(bgRect,rectRadius,rectRadius, rectBgPaint);
            canvas.drawCircle(width-circleRadius -shadowSize - shadowDx,circleRadius+shadowDy,circleRadius, circlePaint);

        } else {
            circlePaint.setColor(uncheckColor);
            rectBgPaint.setColor(unCheckedBgColor);
            canvas.drawRoundRect(bgRect,rectRadius,rectRadius, rectBgPaint);
            canvas.drawCircle(circleRadius+shadowDx,circleRadius+shadowDy,circleRadius, circlePaint);
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int ac = event.getAction();
        switch (ac){
            case MotionEvent.ACTION_DOWN:
                return true;//返回true当前view才会获得后续的MOVE ，UP 等动作的处理权
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(isChecked ){
                    isChecked = false;
                    invalidate();
                }else {
                    isChecked = true;
                    invalidate();
                }
                if(changeListener!=null)
                    changeListener.onChanged(isChecked);
                return true;
        }
        return super.onTouchEvent(event);
    }

    private int dp2px(int dpVal) {
        return (int) (getResources().getDisplayMetrics().density * dpVal + 0.5);
    }

    OnCheckChangeListener changeListener = null;

    public void setOnCheckChangeListener(OnCheckChangeListener listener){
        changeListener = listener;
    }

    /**
     * 按钮状态改变的回调接口
     */
    public interface OnCheckChangeListener{
        void onChanged(boolean isChecked);
    }
}
