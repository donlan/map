package com.tencent.qcloud.tlslibrary.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * 项目：BlurUtils
 * 作者：梁桂栋
 * 日期： 4/1/2016  11:45.
 */
public class BlurUtils {

    public static Bitmap doBlur(Bitmap img,int radius,boolean reuseable)
    {
        //虚化度小于1，直接但会空
        if(radius<1)
            return null;
        Bitmap bitmap;
        if(reuseable) {
            bitmap = img;
        }else
        {
            bitmap = img.copy(img.getConfig(),true);
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int pix[] = new int[w*h];
        bitmap.getPixels(pix,0,w,0,0,w,h);


        int wm = w-1;
        int hm = h-1;
        int div = radius*2+1;
        int wh = w*h;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];

        int rsum,gsum,bsum,x,y,i,p,yp,yi,yw;
        int vmin[] = new int[Math.max(w,h)];
        int divsum = (div+1)>>1;
        divsum*=divsum;
        int dv[] = new int[256*divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }


    public static  Bitmap createScaleBitmap(Bitmap img,float scaleRadio)
    {
        Matrix matrix = new Matrix();
        matrix.setScale(scaleRadio,scaleRadio);
        return Bitmap.createBitmap(img,0,0,img.getWidth(),img.getHeight(),matrix,true);
    }


    public static Bitmap createScaleBitmap(int resId,int sampleSize)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds =false;

        return  BitmapFactory.decodeResource(Resources.getSystem(),resId,options);
    }

    public static Bitmap scaleBitmap(Context context,int resId){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap pre = BitmapFactory.decodeResource(context.getResources(),resId,options);

        int w = options.outWidth / 300;
        int h = options.outHeight / 400;
        int s = Math.max(w,h);
        if(s<1)
           s =1;
        options.inJustDecodeBounds = false;
        options.inSampleSize =s;
        return BitmapFactory.decodeResource(context.getResources(),resId,options);
    }

    public static Bitmap scaleAndBlur(Context context,int resId,int radio){
        return doBlur(scaleBitmap(context,resId),radio,false);
    }
    public static  Bitmap scaleAndBlur(Bitmap bmp,int scale,int radio){
        return doBlur(createScaleBitmap(bmp,scale),radio,true);
    }

    public static  Bitmap scaleAndBlur(int resId,int scale,int radio){
        return doBlur(createScaleBitmap(resId,scale),radio,true);
    }
}
