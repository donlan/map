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
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import dong.lan.mapeye.R;


/**
 * Created by 梁桂栋 on 2016/9/28.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */

public class BlurLinearLayout extends LinearLayout {
    Bitmap bg = null;
    Paint paint = null;
    public BlurLinearLayout(Context context) {
        this(context,null);
    }

    public BlurLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BlurLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BlurRelativeLayout);

        int resId = typedArray.getResourceId(typedArray.getIndex(R.styleable.BlurRelativeLayout_backgroundImg),-1);
        if(resId==-1){
            bg = BlurUtils.scaleAndBlur(context,R.drawable.popup_view_bg,10);
        }else
            bg = BlurUtils.scaleAndBlur(context,resId,10);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setAlpha(120);
        typedArray.recycle();
        this.setBackground(new BitmapDrawable(bg));
    }

}
