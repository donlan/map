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
import android.view.View;

/**
 * Created by 梁桂栋 on 16-12-4 ： 下午6:41.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class DotView extends View {

    private int radius = dp2px(5);
    private Paint paint;
    private int color = Color.YELLOW;


    public DotView(Context context, int radius, int color) {
        this(context);
        this.radius = dp2px(radius);
        this.color = color;

    }

    public DotView(Context context) {
        super(context);
        paint = new Paint();
        paint.setAntiAlias(true);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(radius * 2, radius * 2);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(color);
        canvas.drawCircle(radius, radius, radius, paint);
    }

    private int dp2px(int dpVal) {
        return (int) (getResources().getDisplayMetrics().density * dpVal + 0.5);
    }
}
