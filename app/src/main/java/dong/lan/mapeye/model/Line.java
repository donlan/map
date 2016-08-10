package dong.lan.mapeye.model;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import dong.lan.mapeye.BuildConfig;
import io.realm.RealmObject;

/**
 * Created by 梁桂栋 on 2016/7/9.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */
public class Line extends RealmObject {
    public Point start;
    public Point end;
    public double A;
    public double B;
    public double C;


    public void init(Point start, Point end) {
        this.start = start;
        this.end = end;

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
        if (BuildConfig.DEBUG) Log.d("Line", "A,B,C:" + A + "," + B + "," + C);
    }


    public double getYuanxinjiao(double k) {
        return 2 *Math.toDegrees( Math.asin(k / (2 * 6371004)));
    }

    public double getPointDis(double lat,double lng){
        if(lat == start.lat)
            return Math.abs(lng - start.lng);
        else if(lng == start.lng)
            return  Math.abs(lat - start.lat);
        else{
            return Math.sqrt((lat - start.lat)*(lat - start.lat) + (lng - start.lng)*(lng - start.lng));
        }
    }

    public double getDistance(double lat, double lng) {
        double d = Math.abs(A * lat + B * lng + C) / Math.sqrt(A * A + B * B);
        d = d * DistanceUtil.getDistance(new LatLng(start.lat,start.lng),new LatLng(lat,lng))/getPointDis(lat,lng);
        if (BuildConfig.DEBUG) Log.d("Line", "d:" + d);
        return d;
    }

    public boolean isOnline(double lat, double lng) {

        if (lat * A + lng * B + C == lng)
            return true;
        else if (DistanceUtil.getDistance(new LatLng(lat, lng), new LatLng(start.lat, start.lng)) < Route.DIS)
            return true;
        else if (getDistance(lat, lng) < Route.DIS)
            return true;
        return false;
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    public double getA() {
        return A;
    }

    public void setA(double a) {
        A = a;
    }

    public double getB() {
        return B;
    }

    public void setB(double b) {
        B = b;
    }

    public double getC() {
        return C;
    }

    public void setC(double c) {
        C = c;
    }
}
