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
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.TrackPoint;
import com.orhanobut.logger.Logger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import dong.lan.mapeye.App;
import dong.lan.mapeye.R;
import dong.lan.mapeye.model.MonitorRecode;
import dong.lan.mapeye.model.TraceLocation;
import dong.lan.mapeye.utils.MapUtils;
import dong.lan.trace.ITraceQueryAttr;
import dong.lan.trace.TraceClient;
import io.realm.Realm;

public class MonitorRecordDetailActivity extends BaseActivity {

    public static final String KEY_MONITOR_RECORD_ID = "monitorRecordId";
    public static final String KEY_POSITION = "position";
    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.bar_right)
    TextView barRight;

    @BindView(R.id.bar_center)
    TextView tittle;
    View dot;
    private long id = 0;
    private int position;
    private boolean hadDelete = false;
    private BaiduMap baiduMap;
    private ArrayList<TraceLocation> traceLocations;

    @OnClick(R.id.bar_left)
    void back() {
        finish();
    }

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
        hadDelete = true;
        finish();
    }

    @Override
    public void finish() {
        if (!hadDelete)
            position = -1;
        setResult(position);
        super.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_record_detail);
        bindView(this);
        checkIntent();
    }

    private void checkIntent() {
        barRight.setText("删除");

        baiduMap = mapView.getMap();
        if (getIntent().hasExtra(KEY_MONITOR_RECORD_ID)) {
            id = getIntent().getLongExtra(KEY_MONITOR_RECORD_ID, 0);
            position = getIntent().getIntExtra(KEY_POSITION, -1);
            if (id != 0) {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                MonitorRecode monitorRecode = realm.where(MonitorRecode.class)
                        .equalTo("id", id).findFirst();

                if (monitorRecode != null && monitorRecode.getLocations() != null)
                    drawTrace(realm.copyFromRealm(monitorRecode));
                realm.commitTransaction();
            }
        }


    }

    private void drawTrace(final MonitorRecode monitorRecode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (monitorRecode.getLocations() == null || monitorRecode.getLocations().isEmpty()) {
                    toast("此记录没有监控记录点");
                } else {
                    MapUtils.drawTrace(baiduMap, monitorRecode.getLocations(), BitmapDescriptorFactory.fromResource(R.drawable.pin_dot));
                }
            }
        });

        toast("计算轨迹中...");
        final TraceClient client = new TraceClient();

        client.queryTrace(App.getContext(), new ITraceQueryAttr() {
            @Override
            public String getEntry() {
                return monitorRecode.getRecord().getId() + "_" + monitorRecode.getMonitoredUser().identifier();
            }

            @Override
            public int getTag() {
                return 1;
            }

            @Override
            public long getStartTime() {
                return monitorRecode.getCreateTime() / 1000;
            }

            @Override
            public long getEndTime() {
                return monitorRecode.getEndTime() / 1000;
            }

            @Override
            public boolean needProcessed() {
                return true;
            }

            @Override
            public boolean needDenoise() {
                return true;
            }

            @Override
            public boolean needVacuate() {
                return true;
            }

            @Override
            public boolean needMapMatch() {
                return false;
            }

            @Override
            public int getRadiusThreshold() {
                return 10;
            }

            @Override
            public int getTransportMode() {
                return ITraceQueryAttr.MODE_WALKING;
            }
        }, new OnTrackListener() {
            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse response) {
                super.onHistoryTrackCallback(response);
                Logger.d(response.getTotal());
                Logger.d(response.getSize());
                if (response.getTotal() <= 2) {
                    toast("无轨迹数据");
                } else {

                    tittle.setText("轨迹里程："+new DecimalFormat("###").format(response.getDistance())+" 米");
                    com.baidu.trace.model.LatLng tsP = response.getStartPoint().getLocation();
                    LatLng sPoint = new LatLng(tsP.getLatitude(), tsP.getLongitude());
                    com.baidu.trace.model.LatLng tep = response.getEndPoint().getLocation();
                    LatLng ePoint = new LatLng(tep.getLatitude(), tep.getLongitude());
                    MapUtils.drawMarker(baiduMap, ePoint, BitmapDescriptorFactory.fromResource(R.drawable.dot));

                    List<LatLng> points = new ArrayList<LatLng>();
                    for (TrackPoint p : response.getTrackPoints()) {
                        points.add(new LatLng(p.getLocation().getLatitude(), p.getLocation().getLongitude()));
                    }
                    MapUtils.drawHistoryTrace(baiduMap, points);
                    MapUtils.setLocation(baiduMap, sPoint, BitmapDescriptorFactory.fromResource(R.drawable.location_marker));
                }
                client.stopAll();
            }
        });

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
        if (baiduMap != null)
            baiduMap.clear();
        if (traceLocations != null)
            traceLocations.clear();
        traceLocations = null;
        baiduMap = null;
        mapView = null;
    }


}
