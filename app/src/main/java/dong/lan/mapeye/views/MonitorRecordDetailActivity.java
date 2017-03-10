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

package dong.lan.mapeye.views;

import android.os.Bundle;
import android.telecom.VideoProfile;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceOverlay;
import com.baidu.mapapi.map.Marker;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import dong.lan.mapeye.R;
import dong.lan.mapeye.model.MonitorRecode;
import io.realm.Realm;

public class MonitorRecordDetailActivity extends BaseActivity implements TraceListener {

    public static final String KEY_MONITOR_RECORD_ID = "monitorRecordId";
    public static final String KEY_POSITION = "position";
    @BindView(R.id.mapView)
    MapView mapView;
    private long id = 0;
    private int position;

    @OnClick(R.id.bar_left)
    void back() {
        finish();
    }

    @BindView(R.id.bar_right)
    TextView barRight;

    @OnClick(R.id.bar_right)
    void deleteMonitorRecord() {
        if (id == 0)
            return;
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(MonitorRecode.class)
                .equalTo("id", id).findFirst().deleteFromRealm();

        realm.commitTransaction();
        realm.close();
        setResult(position);
        finish();
    }

    private AMap aMap;
    private ArrayList<TraceLocation> traceLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_record_detail);
        bindView(this);
        mapView.onCreate(savedInstanceState);
        checkIntent();
    }

    private void checkIntent() {
        aMap = mapView.getMap();
        barRight.setText("删除");
        if (getIntent().hasExtra(KEY_MONITOR_RECORD_ID)) {
            id = getIntent().getLongExtra(KEY_MONITOR_RECORD_ID, 0);
            position = getIntent().getIntExtra(KEY_POSITION, -1);
            if (id != 0) {
                Realm realm = Realm.getDefaultInstance();
                MonitorRecode monitorRecode = realm.where(MonitorRecode.class)
                        .equalTo("id", id).findFirst();
                if (monitorRecode != null) {
                    if (monitorRecode.getLocations() == null || monitorRecode.getLocations().isEmpty()) {
                        toast("此记录没有监控记录点");
                    } else {
                        LBSTraceClient traceClient = LBSTraceClient.getInstance(getApplicationContext());
                        traceLocations = new ArrayList<>();
                        for (dong.lan.mapeye.model.TraceLocation traceLocation : monitorRecode.getLocations()) {
                            Logger.d(traceLocation);
                            traceLocations.add(new TraceLocation(traceLocation.getLatitude(),
                                    traceLocation.getLongitude(),
                                    traceLocation.getSpeed(),
                                    traceLocation.getBearing(),
                                    traceLocation.getCreateTime()));
                        }

                        traceClient.queryProcessedTrace((int) monitorRecode.getId(),
                                traceLocations,
                                LBSTraceClient.TYPE_BAIDU,
                                this);
                    }
                }

            }
        }
    }

    @Override
    public void onRequestFailed(int i, String s) {
        toast(s);
        for (TraceLocation traceLocation : traceLocations) {
            aMap.addMarker(new MarkerOptions().position(new LatLng(traceLocation.getLatitude(), traceLocation.getLongitude())));
        }
    }

    @Override
    public void onTraceProcessing(int i, int i1, List<LatLng> list) {
    }

    @Override
    public void onFinished(int lineId, List<LatLng> list, int distance, int waitTime) {
        LatLng l = list.get(0);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(l));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        TraceOverlay traceOverlay = new TraceOverlay(aMap, list);
        traceOverlay.setDistance(distance);
        traceOverlay.setWaitTime(waitTime);
        traceOverlay.setTraceStatus(TraceOverlay.TRACE_STATUS_FINISH);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null)
            mapView.onDestroy();
        if (aMap != null)
            aMap.clear();
        if (traceLocations != null)
            traceLocations.clear();
        traceLocations = null;
        aMap = null;
        mapView = null;
    }
}
