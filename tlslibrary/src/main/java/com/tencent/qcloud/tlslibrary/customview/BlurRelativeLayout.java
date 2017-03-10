package com.tencent.qcloud.tlslibrary.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.tencent.qcloud.tlslibrary.R;
import com.tencent.qcloud.tlslibrary.helper.BlurUtils;

/**
 * Created by 梁桂栋 on 2016/9/28.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */

public class BlurRelativeLayout extends RelativeLayout {
    Bitmap bg = null;
    Paint paint = null;
    public BlurRelativeLayout(Context context) {
        this(context,null);
    }

    public BlurRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BlurRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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
