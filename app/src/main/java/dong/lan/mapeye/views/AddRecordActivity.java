package dong.lan.mapeye.views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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

    private BaiduMap.OnMapLongClickListener mapLongClickListener = new BaiduMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(final LatLng latLng) {
            //presenter.drawAndAddPoint(baiduMap, latLng);
            new AlertDialog.Builder(AddRecordActivity.this)
                    .setTitle("是否在围栏内？")
                    .setPositiveButton("围栏内", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            presenter.test(baiduMap,latLng,true);
                        }
                    })
                    .setNegativeButton("不在围栏内", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            presenter.test(baiduMap,latLng,false);
                        }
                    }).show();
        }


    };

    private BaiduMap.OnMapClickListener mapClickListener = new BaiduMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            presenter.drawAndAddPoint(baiduMap, latLng);
        }

        @Override
        public boolean onMapPoiClick(MapPoi mapPoi) {
            return true;
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
        baiduMap.setOnMapLongClickListener(mapLongClickListener);
        baiduMap.setOnMapClickListener(mapClickListener);
        MapUtils.setLocation(baiduMap,new LatLng(38.323233,112.324234),BitmapDescriptorFactory.fromResource(R.drawable.location_48));
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
