package dong.lan.mapeye.utils;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.mapapi.utils.SpatialRelationUtil;
import com.orhanobut.logger.Logger;

import java.util.List;

import dong.lan.mapeye.model.Point;

/**
 * 项目：  MapEye
 * 作者：  梁桂栋
 * 日期：  7/2/2016  20:25.
 * Email: 760625325@qq.com
 */
public final class PolygonHelper {

    public static int DISTANCE = 20;

    private PolygonHelper() {
    }

    /*
      判断一点是否在多边形中
      @param points 构成多边形的顶点
      #param p 需要判断的点
     */
    public static boolean isPointInPolygon(List<LatLng> points, LatLng p) {

        return SpatialRelationUtil.isPolygonContainsPoint(points,p);
    }



    public static boolean isPointInPolygon(List<Point> points, double latitude, double longitude) {

        int size = points.size();
        double[] x = new double[size];
        double[] y = new double[size];
        for (int i = 0; i < size; i++) {
            x[i] = points.get(i).getLat();
            y[i] = points.get(i).getLat();
        }
        int crossing = 0;
        for (int i = 0; i < size; i++) {
            int j = i + 1;
            if (j == size) {
                j = 0;
            }
            double slope = (y[j] - y[i]) / (x[j] - x[i]);
            boolean cond1 = (x[i] <= latitude) && (latitude < x[j]);
            boolean cond2 = (x[j] <= latitude) && (latitude < x[i]);
            boolean above = (longitude < slope * (latitude - x[i]) + y[i]);
            if ((cond1 || cond2) && above)
                crossing++;
        }
        return crossing % 2 != 0;
    }


    public static boolean isOnRoute(List<LatLng> points,double latitude, double longitude,int scale){
        LatLng target = new LatLng(latitude,latitude);
        LatLng p = SpatialRelationUtil.getNearestPointFromLine(points,target);
        double d = DistanceUtil.getDistance(p,target);
        return d < DISTANCE+scale;
    }

    public static boolean isOnRoute(List<Point> points, double latitude, double longitude) {
        if (points == null || points.isEmpty())
            return true;
        if (points.size() == 1) {
            double lat = points.get(0).getLat();
            double lng = points.get(0).getLng();
            return DistanceUtil.getDistance(new LatLng(lat, lng), new LatLng(lat, longitude)) < DISTANCE;
        }
        double A;
        double B;
        double C;
        for (int i = 0, s = points.size() - 1; i < s; i++) {
            Point start = points.get(i);
            Point end = points.get(i + 1);
            if (start.lat == end.lat) {
                A = 0;
                B = 1;
                C = start.lng;
            } else if (start.lng == end.lng) {
                A = 1;
                B = 0;
                C = start.lat;
            } else {
                A = end.lng - start.lng;
                B = start.lat - end.lat;
                C = end.lat * start.lng - start.lat * end.lng;
            }
            if (latitude * A + longitude * B + C == longitude)
                return true;
            else if (DistanceUtil.getDistance(new LatLng(latitude, longitude), new LatLng(start.lat, start.lng)) < DISTANCE)
                return true;
            else {
                double pd;
                if (latitude == start.lat)
                    pd = Math.abs(longitude - start.lng);
                else if (longitude == start.lng)
                    pd = Math.abs(latitude - start.lat);
                else {
                    pd = Math.sqrt((latitude - start.lat) * (latitude - start.lat) + (longitude - start.lng) * (longitude - start.lng));
                }

                double d = Math.abs(A * latitude + B * longitude + C) / Math.sqrt(A * A + B * B);
                d = d * DistanceUtil.getDistance(new LatLng(start.lat, start.lng), new LatLng(latitude, longitude)) / pd;
                Logger.d(d);
                return d < DISTANCE;
            }
        }
        return false;
    }

    public static boolean isInCircleFence(Point point, LatLng target, int scale, int radius) {
        LatLng p = new LatLng(point.getLat(), point.getLng());
        return SpatialRelationUtil.isCircleContainsPoint(p, radius + scale, target);
    }


}
