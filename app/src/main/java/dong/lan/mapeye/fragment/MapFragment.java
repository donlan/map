package dong.lan.mapeye.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dong.lan.mapeye.BuildConfig;
import dong.lan.mapeye.R;
import dong.lan.mapeye.adapter.FenceAdapter;
import dong.lan.mapeye.model.Fence;
import dong.lan.mapeye.model.Point;
import dong.lan.mapeye.model.Route;
import dong.lan.mapeye.utils.PolygonHelper;
import dong.lan.mapeye.utils.SoundHelper;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * 项目：  MapEye
 * 作者：  梁桂栋
 * 日期：  7/2/2016  15:36.
 * Email: 760625325@qq.com
 */
public class MapFragment extends Fragment implements BDLocationListener, BaiduMap.OnMapClickListener,
        BaiduMap.OnMapLongClickListener, FenceAdapter.OnFenceItemClickListener {

    public static final int TAG_START = 0;
    public static final int TAG_GENERATE = 1;
    public static final int TAG_SAVE = 2;
    public static final int TAG_RESET = 3;
    public static final int TAG_LOOK = 4;
    public static final int TAG_MY_LOOK = 5;
    public static final int TAG_ROUTE = 6;
    public static final int TAG_SAVE_ROUTE = 7;

    public static final int FLAG_FENCE = 11;
    public static final int FLAG_ROUTE = 12;
    @Bind(R.id.mapView)
    MapView mapView;
    @Bind(R.id.fence_list)
    RecyclerView fenceListView;
    @Bind(R.id.map_drawer)
    DrawerLayout mapDraw;
    private BitmapDescriptor bitmap;
    private boolean CLEAR = true;

    @OnClick(R.id.start_record)
    public void startRecord() {
        TAG = TAG_START;
        Toast("开始记录地图点击点");
    }

    @OnClick(R.id.generate_record)
    public void generateRecord() {
        TAG = TAG_GENERATE;
        FLAG = FLAG_FENCE;
        PolygonHelper.createFence(baiduMap, points);
    }

    @OnClick(R.id.generate_route_record)
    public void generateRoute() {
        TAG = TAG_ROUTE;
        FLAG = FLAG_ROUTE;
        PolygonHelper.createRoute(baiduMap, route, points);
    }

    @OnClick(R.id.save_record)
    public void saveRecord() {
        TAG = TAG_SAVE;
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.alert_label_text, null);
        final EditText editText = (EditText) view.findViewById(R.id.fence_label_et);
        new AlertDialog.Builder(getActivity())
                .setTitle("输入围栏名称")
                .setView(view)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().length() < 1) {
                            Toast("不能是空标题");
                            return;
                        }
                        realm.beginTransaction();
                        Fence fence = realm.createObject(Fence.class);
                        fence.setCreateTime(new Date());
                        fence.setTag(0);
                        fence.setLabel(editText.getText().toString());
                        for (int i = 0, s = points.size(); i < s; i++) {
                            Point point = new Point(points.get(i));
                            fence.points.add(point);
                        }
                        realm.commitTransaction();
                        Toast("保存成功");
                    }
                }).show();

    }

    @OnClick(R.id.save_route)
    public void saveRoute() {
        TAG = TAG_SAVE;
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.alert_label_text, null);
        final EditText editText = (EditText) view.findViewById(R.id.fence_label_et);
        new AlertDialog.Builder(getActivity())
                .setTitle("输入路径名称")
                .setView(view)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().length() < 1) {
                            Toast("不能是空标题");
                            return;
                        }
                        realm.beginTransaction();
                        Route route = realm.createObject(Route.class);
                        route.time = new Date();
                        route.tag = 0;
                        route.label = editText.getText().toString();
                        for (int i = 0, s = points.size(); i < s; i++) {
                            Point point = new Point(points.get(i));
                            route.points.add(point);
                        }
                        route.createLinesByPoints();
                        realm.commitTransaction();
                        Toast("保存成功");
                    }
                }).show();
    }

    @OnClick(R.id.query_fence)
    public void queryFence() {
        mapDraw.openDrawer(GravityCompat.START);
    }

    @OnClick(R.id.reset_record)
    public void resetRecord() {
        points.clear();
        baiduMap.clear();
        CLEAR =true;
        TAG = TAG_RESET;
        Toast("重置围栏成功");
    }

    @OnClick(R.id.start_look)
    public void startLook() {
        if (points.size() < 2) {
            Toast("请先生成一个围栏或者路径");
            return;
        }
        TAG = TAG_LOOK;

        Toast("开始监听远程回传位置的变化");
    }

    @OnClick(R.id.start_my_look)
    public void startMyLook() {
        if (points.size() < 2) {
            Toast("请先生成一个围栏");
            return;
        }
        TAG = TAG_MY_LOOK;
        Toast("开始监听当前定位位置变化");
    }

    private SoundHelper soundHelper;
    private FenceAdapter adapter;
    private Route route;
    private Vibrator vibrator;
    private BaiduMap baiduMap;
    private Marker eyeMarker;
    private Realm realm;
    private int TAG = -1;
    private int FLAG = -1;
    private boolean isFirstLoc = true;
    private LocationClient locationClient;
    private List<LatLng> points = new ArrayList<>();
    private BitmapDescriptor locBitmap = BitmapDescriptorFactory.fromResource(R.drawable.tip);
    private MyLocationConfiguration.LocationMode locationMode = MyLocationConfiguration.LocationMode.NORMAL;


    public MapFragment() {
        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.nav);
        route = new Route();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder(getActivity());
        builder.schemaVersion(1);
        builder.deleteRealmIfMigrationNeeded();
        realm = Realm.getInstance(builder.build());
        soundHelper = new SoundHelper().init(2);
        soundHelper.loadAll(getActivity(),new int[]{R.raw.alert});
        if (Build.VERSION.SDK_INT >= 23
                && ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        }

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode, true, null));
        locationClient = new LocationClient(getActivity());
        locationClient.registerLocationListener(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setTimeOut(5000);
        option.setScanSpan(10000);
        locationClient.setLocOption(option);
        locationClient.start();


        baiduMap.setOnMapClickListener(this);
        baiduMap.setOnMapLongClickListener(this);
        fenceListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        RealmResults<Fence> results = realm.where(Fence.class).findAll();
        RealmResults<Route> routes = realm.where(Route.class).findAll();
        adapter = new FenceAdapter(getActivity(), results, routes);
        adapter.setFenceItemClickListener(this);
        fenceListView.setAdapter(adapter);
    }

    public void setMapType(int type) {
        baiduMap.setMapType(type);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (locationClient != null) {
            locationClient.unRegisterLocationListener(this);
            locationClient.stop();
            locationClient = null;
        }
        if (mapView != null) {
            mapView.onDestroy();
            mapView = null;
        }
        if (baiduMap != null) {
            baiduMap.setMyLocationEnabled(false);
        }

        if (soundHelper != null) {
            soundHelper.release();
            soundHelper = null;
        }

        locBitmap.recycle();
        bitmap.recycle();
        route = null;
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation == null || mapView == null)
            return;
        if (BuildConfig.DEBUG)
            Log.d("MapFragment", bdLocation.getLatitude() + "," + bdLocation.getLongitude() + "_" + bdLocation.getLocType());

        LatLng loc = new LatLng(bdLocation.getLatitude(),
                bdLocation.getLongitude());
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                .latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude()).build();
        baiduMap.setMyLocationData(locData);
        if (isFirstLoc) {
            isFirstLoc = false;
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(loc);
            baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(locationMode, true, locBitmap));
            baiduMap.animateMapStatus(u);
        }
        if (BuildConfig.DEBUG)
            Log.d("MapFragment", "points.size():" + points.size() + "TAG:" + TAG);
        if (TAG == TAG_MY_LOOK) {
            onLocationReceived(loc);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (BuildConfig.DEBUG) Log.d("MapFragment", latLng.toString());
        if (TAG == TAG_START) {
            OverlayOptions option = new MarkerOptions()
                    .position(latLng)
                    .icon(bitmap);
            baiduMap.addOverlay(option);
            points.add(latLng);
        }
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (latLng != null) {
            onLocationReceived(latLng);
        }
    }

    private void Toast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }


    public void onLocationReceived(LatLng latLng) {
        if (points == null || points.size() < 2) {
            Toast("未设置围栏");
            return;
        }
        boolean in = true;
        if (FLAG_FENCE == FLAG)
            in = PolygonHelper.isPointInPolygon(points, latLng);
        if (FLAG == FLAG_ROUTE)
            in = route.isOnRoute(latLng.latitude, latLng.longitude);
        if (!in) {
            vibrator.vibrate(1000);
            soundHelper.play(getActivity(), R.raw.alert, 0);
        }
        if (eyeMarker == null || CLEAR) {
            OverlayOptions option = new MarkerOptions()
                    .position(latLng)
                    .draggable(false)
                    .zIndex(9)
                    .icon(bitmap);
            eyeMarker = (Marker) baiduMap.addOverlay(option);
            CLEAR = false;
        } else {
            eyeMarker.setPosition(latLng);
        }
            eyeMarker.setVisible(true);
        if (FLAG == FLAG_FENCE)
            Toast("围栏中: " + in);
        if (FLAG == FLAG_ROUTE)
            Toast("路径中: " + in);
        //if (BuildConfig.DEBUG) Log.d("MapFragment", "in:" + in);
    }

    @Override
    public void onFenceItemClick(Fence fence) {
        FLAG = FLAG_FENCE;
        baiduMap.clear();
        CLEAR = true;
        points.clear();
        points.addAll(PolygonHelper.createFence(baiduMap, fence.getPoints()));
        mapDraw.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onRouteItemClick(Route route) {
        FLAG = FLAG_ROUTE;
        baiduMap.clear();
        CLEAR = true;
        points.clear();
        this.route.copyTo(route);
        points.addAll(PolygonHelper.makeRoute(baiduMap, route));
        mapDraw.closeDrawer(GravityCompat.START);
    }
}
