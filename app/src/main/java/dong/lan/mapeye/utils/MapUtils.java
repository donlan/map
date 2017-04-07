/*
 *
 *   Copyright (C) 2016 author : 梁桂栋
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

package dong.lan.mapeye.utils;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import dong.lan.mapeye.model.Record;
import dong.lan.mapeye.model.TraceLocation;

/**
 * Created by 梁桂栋 on 16-11-10 ： 下午9:50.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class MapUtils {
    private MapUtils() {
    }

    /**
     * 将定位得到的信息设置到地图上
     * @param baiduMap 将要被设置的地图
     * @param bdLocation 定位信息
     * @param locBmp 位置显示的图标
     */
    public static void setLocation(BaiduMap baiduMap, BDLocation bdLocation, BitmapDescriptor locBmp) {
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                .latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude()).build();
        baiduMap.setMyLocationData(locData);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(
                new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, locBmp));
        baiduMap.animateMapStatus(u);
    }

    /**
     * 将地图的试图定位到某一点
     * @param baiduMap 当前显示的地图
     * @param point 指定的显示位置点
     * @param locBmp 位置显示的图标
     */
    public static void setLocation(BaiduMap baiduMap, LatLng point, BitmapDescriptor locBmp) {
        MyLocationData locData = new MyLocationData.Builder()
                .latitude(point.latitude)
                .longitude(point.longitude).build();
        baiduMap.setMyLocationData(locData);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(point);
        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, locBmp));
        baiduMap.animateMapStatus(u);
    }

    /**
     * 将地图的试图定位到某一点
     * @param baiduMap 当前显示的地图
     * @param points 指定的显示位置点
     * @param locBmp 位置显示的图标
     */
    public static void setLocation(BaiduMap baiduMap, List<LatLng> points, BitmapDescriptor locBmp) {
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        for (LatLng p : points
                ) {
            bounds.include(p);
        }
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds.build());
        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, locBmp));
        baiduMap.animateMapStatus(u);
    }

    /**
     * 根据坐标点绘制地图Marker标记
     * @param baiduMap 绘制标记的地图
     * @param points 绘制标记的位置左边点集合
     * @param bitmap Marker的显示图标
     * @return 绘制得到的Marker集合
     */
    public static List<Marker> drawMarker(BaiduMap baiduMap, List<LatLng> points, BitmapDescriptor bitmap) {
        List<Marker> markers = new ArrayList<>();
        for (int i = 0, size = points.size(); i < size; i++) {
            OverlayOptions option = new MarkerOptions()
                    .position(points.get(i))
                    .icon(bitmap);
            Marker marker = (Marker) baiduMap.addOverlay(option);
            marker.setDraggable(true);
            markers.add(marker);
        }
        return markers;
    }

    /**
     * 根据坐标点绘制地图Marker标记
     * @param baiduMap 绘制标记的地图
     * @param points 绘制标记的位置左边点集合
     * @param bitmap Marker的显示图标
     */
    public static void onlyDrawMarker(BaiduMap baiduMap, List<LatLng> points, BitmapDescriptor bitmap) {
        for (int i = 0, size = points.size(); i < size; i++) {
            OverlayOptions option = new MarkerOptions()
                    .position(points.get(i))
                    .icon(bitmap);
            baiduMap.addOverlay(option);
        }
    }

    /**
     * 在地图上绘制一个圆
     * @param baiduMap
     * @param point 圆心位置坐标点
     * @param radius 圆半径
     */
    public static void drawCircle(BaiduMap baiduMap, LatLng point, int radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(point)
                .radius(radius)
                .stroke(new Stroke(5, 0xAA00FF00))
                .fillColor(0xe33FF00);
        baiduMap.addOverlay(circleOptions);

    }

    /**
     * 在地图上画一个Marker
     * @param baiduMap
     * @param point 绘制Marker的位置坐标点
     * @param bitmap 绘制Marker的图标
     * @return 绘制得到的Marker
     */
    public static Marker drawMarker(BaiduMap baiduMap, LatLng point, BitmapDescriptor bitmap) {
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .draggable(true)
                .perspective(true)
                .zIndex(3)
                .icon(bitmap);
        return (Marker) baiduMap.addOverlay(option);
    }

    /**
     * 在地图上画一个Marker
     * @param baiduMap
     * @param point 绘制Marker的位置坐标点
     * @param bitmap 绘制Marker的图标
     * @return 绘制得到的Marker
     */
    public static Marker drawMarker(BaiduMap baiduMap, LatLng point, BitmapDescriptor bitmap, float achorX, float achorY) {
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .draggable(true)
                .perspective(true)
                .anchor(achorX, achorY)
                .icon(bitmap);
        return (Marker) baiduMap.addOverlay(option);
    }

    /**
     * 绘制多边形围栏
     * @param baiduMap
     * @param points 构成多边形围栏的坐标点
     */
    public static void drawFence(BaiduMap baiduMap, List<LatLng> points) {
        if (points == null || points.isEmpty())
            return;
        if (points.size() > 2) {
            OverlayOptions ooPolygon = new PolygonOptions().points(points)
                    .stroke(new Stroke(5, 0xAA00FF00)).fillColor(0xe33FF00);
            baiduMap.addOverlay(ooPolygon);
        } else {
            drawRoute(baiduMap, points);
        }
    }


    /**
     * 根据记录类型绘制不同的图形
     * @param baiduMap
     * @param points 构成图形的左边点集合
     * @param type 记录类型
     */
    public static void drawRecord(BaiduMap baiduMap, List<LatLng> points, int type) {
        if (type == Record.TYPE_FENCE) {
            drawFence(baiduMap, points);
        } else if (type == Record.TYPE_ROUTE) {
            drawRoute(baiduMap, points);
        } else if (type == Record.TYPE_CIRCLE) {
            drawCircle(baiduMap, points.get(0), 10);
        }
    }

    /**
     * 根据记录类型绘制不同的图形
     * @param baiduMap
     * @param points 构成图形的左边点集合
     * @param type 记录类型
     * @param radius 圆形围栏的半径
     */
    public static void drawRecord(BaiduMap baiduMap, List<LatLng> points, int type,int radius) {
        if(points==null || points.isEmpty())
            return;
        if (type == Record.TYPE_FENCE) {
            drawFence(baiduMap, points);
        } else if (type == Record.TYPE_ROUTE) {
            drawRoute(baiduMap, points);
        } else if (type == Record.TYPE_CIRCLE) {
            drawCircle(baiduMap, points.get(0), radius);
        }
    }

    /**
     * 在地图上根据提供的坐标点绘制折线段
     * @param baiduMap
     * @param points 绘制折线段的额坐标点
     */
    public static void drawRoute(BaiduMap baiduMap, List<LatLng> points) {
        if (points == null || points.size() <= 1)
            return;
        PolylineOptions polylineOptions = new PolylineOptions().points(points)
                .width(10).color(0xee33FF00).keepScale(true);
        baiduMap.addOverlay(polylineOptions);
    }

    public static void drawTrace(BaiduMap baiduMap, List<TraceLocation> locations,BitmapDescriptor bitmap){
        if (locations == null || locations.size() <= 1)
            return;
        List<LatLng> points = new ArrayList<>(locations.size());
        for(int i = 0,s = locations.size();i<s;i++) {
            TraceLocation location = locations.get(i);
            LatLng p = new LatLng(location.getLatitude(),location.getLongitude());
            points.add(p);
            if(bitmap!=null){
                Marker marker = drawMarker(baiduMap,p,bitmap);
            }
            if(i == 0){
                setLocation(baiduMap,p,bitmap);
            }
        }

        PolylineOptions polylineOptions = new PolylineOptions().points(points)
                .width(10).color(0xee33FF00).keepScale(true);
        baiduMap.addOverlay(polylineOptions);
    }
}
