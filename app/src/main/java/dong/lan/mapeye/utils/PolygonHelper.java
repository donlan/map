package dong.lan.mapeye.utils;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import dong.lan.mapeye.model.Point;
import dong.lan.mapeye.model.Route;
import io.realm.RealmList;

/**
 * 项目：  MapEye
 * 作者：  梁桂栋
 * 日期：  7/2/2016  20:25.
 * Email: 760625325@qq.com
 */
public final class PolygonHelper {
    private PolygonHelper() {
    }

    /*
      判断一点是否在多边形中
      @param points 构成多边形的顶点
      #param p 需要判断的点
     */
    public static boolean isPointInPolygon(List<LatLng> points, LatLng p) {

        double[] x = new double[points.size()];
        double[] y = new double[points.size()];
        double px = p.latitude;
        double py = p.longitude;
        int size = points.size();
        for (int i = 0; i < size; i++) {
            x[i] = points.get(i).latitude;
            y[i] = points.get(i).longitude;
        }
        int crossing = 0;
        for (int i = 0; i < size; i++) {
            int j = i + 1;
            if (j == size) {
                j = 0;
            }
            double slope = (y[j] - y[i]) / (x[j] - x[i]);
            boolean cond1 = (x[i] <= px) && (px < x[j]);
            boolean cond2 = (x[j] <= px) && (px < x[i]);
            boolean above = (py < slope * (px - x[i]) + y[i]);
            if ((cond1 || cond2) && above)
                crossing++;
        }
        return crossing % 2 != 0;
    }


    public static void createFence(BaiduMap baiduMap, List<LatLng> points) {
        if (points.size() > 0) {
            OverlayOptions ooPolygon = new PolygonOptions().points(points)
                    .stroke(new Stroke(5, 0xAA00FF00)).fillColor(0x88FFFF00);
            baiduMap.addOverlay(ooPolygon);
        }
    }

    public static void createRoute(BaiduMap baiduMap, Route route, List<LatLng> points) {
        if (points.size() > 0) {
            OverlayOptions ooPolyLine = new PolylineOptions().points(points)
                    .width(10);
            baiduMap.addOverlay(ooPolyLine);
            route.points.clear();
            for (int i = 0, s = points.size(); i < s; i++)
                route.points.add(new Point(points.get(i).latitude, points.get(i).longitude));
        }
    }

    public static List<LatLng> makeRoute(BaiduMap baiduMap, Route route) {
        List<LatLng> points = new ArrayList<>();
        for(int i=0,s=route.points.size();i<s;i++)
            points.add(new LatLng(route.points.get(i).lat,route.points.get(i).lng));
        if (points.size() > 0) {
            OverlayOptions ooPolyLine = new PolylineOptions().points(points)
                    .width(10);
            baiduMap.addOverlay(ooPolyLine);
        }
        return points;
    }

    public static List<LatLng> createFence(BaiduMap baiduMap, RealmList<Point> points) {
        List<LatLng> ps = new ArrayList<>();
        for (Point p : points) {
            ps.add(new LatLng(p.getLat(), p.getLng()));
        }
        if (points.size() > 0) {
            OverlayOptions ooPolygon = new PolygonOptions().points(ps)
                    .stroke(new Stroke(5, 0xAA00FF00)).fillColor(0x88FFFF00);
            baiduMap.addOverlay(ooPolygon);
        }
        return ps;
    }

}
