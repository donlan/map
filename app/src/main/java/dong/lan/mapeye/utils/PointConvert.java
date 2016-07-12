package dong.lan.mapeye.utils;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;

import dong.lan.mapeye.BuildConfig;

/**
 * Created by 梁桂栋 on 2016/7/7.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */

/*
百度
38.019306  112.452233

38.018779  112.449827

38.018845  112.448854

38.020224  112.448735

38.021146  112.448613

38.021301  112.450032

38.021461  112.451539

38.021452  112.452619

38.021538  112.454221

38.020800  112.455353

38.020219  112.455841

38.020484  112.458464

38.020503  112.460395

38.020491  112.460379

38.020564  112.462280

38.021620  112.461016

38.021796  112.459724

38.021661  112.456910

38.022437  112.456560

38.023431  112.456503

38.023233  112.454653
 */
public final class PointConvert {

    public static double LAT_GAP = 0;
    public static double LNG_GAP = 0;

    private PointConvert() {
    }


    public static LatLng convert(double lat, double lng) {
        lat = 1.022 * lat - 0.8165;
        lng  = 1.022*lng -2.4606;
        if (BuildConfig.DEBUG) Log.d("PointConvert", "lat,lng:" + lat + " , " + lng);
        return new LatLng(lat, lng);
    }
}
