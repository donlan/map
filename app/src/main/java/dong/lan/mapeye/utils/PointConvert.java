package dong.lan.mapeye.utils;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by 梁桂栋 on 2016/7/7.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */
public final class PointConvert {

    public static double LAT_GAP = 0;
    public static double LNG_GAP = 0;

    //百度坐标：38.021091287277706, 112.45592033224746
    //北斗坐标：38.002665,112.442631
    private PointConvert() {
    }

    public static void init() {
        LAT_GAP = 38.021091287277706 - 38.002665;
        LNG_GAP = 112.45592033224746 - 112.442631;
    }

    public static LatLng convert(double lat, double lng) {
        return new LatLng(lat + LAT_GAP, lng + LNG_GAP);
    }
}
