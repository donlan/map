package dong.lan.mapeye.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

import butterknife.BindView;
import butterknife.OnClick;
import dong.lan.mapeye.R;
import dong.lan.mapeye.common.LocationService;
import dong.lan.mapeye.contracts.AddRecordContract;
import dong.lan.mapeye.presenter.AddRecordPresenter;
import dong.lan.mapeye.utils.MapUtils;

/**
 * Created by 梁桂栋 on 16-11-7 ： 下午10:56.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class AddRecordActivity extends BaseActivity implements AddRecordContract.AddRecordView {

    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.bar_center)
    TextView tittleTv;
    @BindView(R.id.bar_right)
    TextView saveTv;
    private AddRecordContract.Presenter presenter;
    private BaiduMap baiduMap;
    private BaiduMap.OnMapClickListener mapLongClickListener = new BaiduMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            presenter.drawAndAddPoint(baiduMap, latLng);
        }

        @Override
        public boolean onMapPoiClick(MapPoi mapPoi) {
            return false;
        }
    };
    private BitmapDescriptor locBitmap = BitmapDescriptorFactory.fromResource(R.drawable.tip);
    private LocationService locationService;
    private BDLocationListener locationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null)
                return;
            MapUtils.setLocation(baiduMap, bdLocation, locBitmap);
            locationService.stop();
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    };

    @OnClick(R.id.reset_record)
    void resetRecord() {
        presenter.resetRecord();
    }

    @OnClick(R.id.build_fence)
    void buildFence() {
        presenter.drawFence(baiduMap);
    }

    @OnClick(R.id.build_route)
    void buildRoute() {
        presenter.drawRoute(baiduMap);
    }

    @OnClick(R.id.build_circle)
    void buildCircleFence(){
        presenter.drawCircleFence(baiduMap);
    }

    @OnClick(R.id.bar_right)
    void saveRecord() {
        presenter.saveRecord();
    }

    @OnClick(R.id.bar_left)
    void back() {
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);
        init();
    }


    private void init() {
        bindView(this);
        tittleTv.setText("添加记录");
        saveTv.setText("保存");
        presenter = new AddRecordPresenter(this);
        baiduMap = mapView.getMap();
        baiduMap.setOnMapClickListener(mapLongClickListener);

        locationService = new LocationService(this);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.registerListener(locationListener);
        locationService.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null)
            mapView.onDestroy();
        if (baiduMap != null)
            baiduMap = null;
        if (locBitmap != null)
            locBitmap.recycle();
        locBitmap = null;
        if (locationService != null)
            locationService.unregisterListener(locationListener);
        locationService = null;
    }

    @Override
    public void clearMap() {
        baiduMap.clear();
    }
}
