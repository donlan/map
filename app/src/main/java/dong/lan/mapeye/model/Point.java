package dong.lan.mapeye.model;

import com.baidu.mapapi.model.LatLng;

import io.realm.RealmObject;

/**
 * 项目：  MapEye
 * 作者：  梁桂栋
 * 日期：  7/3/2016  14:29.
 * Email: 760625325@qq.com
 */
public class Point extends RealmObject {
    public double lat;
    public double lng;
    public Point(){}
    public Point(double lat,double lng){
       this.lat = lat;
        this.lng = lng;
    }
    public Point(LatLng latLng){
        this.lat = latLng.latitude;
        this.lng = latLng.longitude;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
