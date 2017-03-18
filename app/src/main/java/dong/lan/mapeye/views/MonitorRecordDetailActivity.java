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
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import dong.lan.mapeye.R;
import dong.lan.mapeye.model.MonitorRecode;
import dong.lan.mapeye.model.TraceLocation;
import dong.lan.mapeye.utils.MapUtils;
import io.realm.Realm;

public class MonitorRecordDetailActivity extends BaseActivity  {

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

    private BaiduMap baiduMap;
    private ArrayList<TraceLocation> traceLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_record_detail);
        bindView(this);
        checkIntent();
    }

    private void checkIntent() {
        baiduMap = mapView.getMap();
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
                        MapUtils.drawTrace(baiduMap,monitorRecode.getLocations(), BitmapDescriptorFactory.fromResource(R.drawable.dot_32_red));
                    }
                }

            }
        }
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
