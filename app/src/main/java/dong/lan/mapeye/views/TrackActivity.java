package dong.lan.mapeye.views;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.ArcOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dong.lan.mapeye.R;
import dong.lan.mapeye.common.LocationService;
import dong.lan.mapeye.views.customsView.CircleTextView;

/**
 * Created by 梁桂栋 on 16-10-31 ： 下午11:38.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class TrackActivity extends BaseActivity {
    private static final double DIS_SLOPE = 0.001;
    private static final double DIS_MAX =100;

    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.track_start)
    CircleTextView trackStartTV;
    @OnClick(R.id.track_start)
    public void trackStart() {
        locationService.start();
    }

    @OnClick(R.id.track_save)
    public void trackSave() {
        locationService.stop();
        startActivity(new Intent(TrackActivity.this,TrackShowDemo.class));
    }

    @OnClick(R.id.track_stop)
    public void trackStop() {
        locationService.stop();
    }

    @BindView(R.id.mapView)
    MapView mapView;


    Unbinder unbinder;
    LocationService locationService;

    BitmapDescriptor curLocationBD;
    BaiduMap map;
    List<LatLng> points = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        unbinder = ButterKnife.bind(this);
        init();
    }


    private void init(){
        locationService = new LocationService(getApplicationContext());
        locationService.registerListener(locationListener);
        curLocationBD = BitmapDescriptorFactory.fromResource(R.drawable.location_marker);
        map = mapView.getMap();
        map.setMyLocationEnabled(true);
        map.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING,true,curLocationBD));
    }


    BDLocationListener locationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //text.setText(LocationHelper.getLocationInfo(bdLocation));
            if(bdLocation!=null && mapView!=null){
                MyLocationData mld = new MyLocationData.Builder()
                        .latitude(bdLocation.getLatitude())
                        .longitude(bdLocation.getLongitude())
                        .direction(bdLocation.getDirection())
                        .speed(bdLocation.getSpeed())
                        .build();
                map.setMyLocationData(mld);
                addPoint(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()));

            }
        }
    };
    int count = 0;
    private void addPoint(LatLng point){
        if(points.isEmpty()) {
            points.add(point);
            count++;
        }
        else{
            double dis = DistanceUtil.getDistance(points.get(points.size()-1),point);
            if(dis<DIS_SLOPE ||dis>100)
                return;
            points.add(point);
            count++;
        }
        drawPoly();
    }
    private void drawPoly(){
        if(points.size()<2 || count != 3)
            return;
        int end =points.size()-1;
        ArcOptions arcOptions = new ArcOptions().color(Color.GREEN).width(10)
                .points(points.get(end--),points.get(end--),points.get(end));
        PolylineOptions polylineOptions = new PolylineOptions().points(points)
                .width(10).color(Color.RED).keepScale(true);
        map.addOverlay(polylineOptions);
        map.addOverlay(arcOptions);
        count=1;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        unbinder=null;
        if(mapView!=null)
            mapView.onDestroy();
        if(map!=null) {
            map.setMyLocationEnabled(false);
        }
        locationService.stop();
        locationService.unregisterListener(locationListener);
        map =null;
        mapView = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }


}
