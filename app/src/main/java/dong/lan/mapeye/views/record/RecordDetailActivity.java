/*
 *
 *   Copyright (C) 2017 author : 梁桂栋
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

package dong.lan.mapeye.views.record;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dong.lan.library.LabelTextView;
import dong.lan.mapeye.R;
import dong.lan.mapeye.common.MonitorManager;
import dong.lan.mapeye.common.UserManager;
import dong.lan.mapeye.contracts.RecordDetailContract;
import dong.lan.mapeye.events.MonitorTaskEvent;
import dong.lan.mapeye.model.Record;
import dong.lan.mapeye.model.TraceLocation;
import dong.lan.mapeye.model.users.Contact;
import dong.lan.mapeye.model.users.User;
import dong.lan.mapeye.presenter.RecordDetailPresenter;
import dong.lan.mapeye.utils.MapUtils;
import dong.lan.mapeye.utils.StringHelper;
import dong.lan.mapeye.views.BaseActivity;
import dong.lan.mapeye.views.ChatActivity;
import dong.lan.mapeye.views.ContactSelectActivity;
import dong.lan.mapeye.views.customsView.MapPinNumView;
import dong.lan.mapeye.views.customsView.PinView;
import dong.lan.mapeye.views.customsView.ToggleCheckBox;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by 梁桂栋 on 16-11-10 ： 下午6:42.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class RecordDetailActivity extends BaseActivity implements RecordDetailContract.RecordDetailView {

    public static final int PICK_CONTACT = 1001;
    private static final String TAG = "RecordDetailActivity";
    @BindView(R.id.monitorMapView)
    MapView mapView;
    @BindView(R.id.monitorToolbar)
    Toolbar toolbar;
    @BindView(R.id.monitorUsersList)
    RecyclerView usersList;
    @BindView(R.id.reeditUndo)
    ImageButton undo;
    @BindView(R.id.reeditRedo)
    ImageButton redo;
    @BindView(R.id.reeditChecked)
    ImageButton checked;
    @BindView(R.id.reeditRecord)
    ImageButton reedit;
    @BindView(R.id.reeditCancel)
    ImageButton cancel;
    double lat = 38.023233;
    double lng = 112.454653;
    Marker marker;
    int count = 1000;
    private Subscription locSubscriber;
    private Subscription refreshSub;
    private Subscription monitorSubscription;
    private BaiduMap baiduMap;
    private RecordDetailPresenter presenter;
    private Adapter adapter;
    private BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.nav);
    private BitmapDescriptor locBitmap = BitmapDescriptorFactory.fromResource(R.drawable.tip);

    @OnClick(R.id.reeditCancel)
    public void cancelEdit() {
        presenter.cancelEdit();
    }

    @OnClick(R.id.reeditUndo)
    public void undoAction() {
        presenter.undoAction();
    }

    @OnClick(R.id.reeditRedo)
    public void redoAction() {
        presenter.redoAction();
    }

    @OnClick(R.id.reeditChecked)
    public void checkedAction() {
        presenter.checkEdit();
    }

    @OnClick(R.id.addMonitorUser)
    public void addMonitorUser() {
        Intent intent = new Intent(this, ContactSelectActivity.class);
        intent.putExtra(Record.GROUP_ID, presenter.initByRecord().getId());
        startActivityForResult(intent, PICK_CONTACT);
    }

    @OnClick(R.id.reeditRecord)
    public void reeditRecord() {
        presenter.reeditRecord();
    }

    @OnClick(R.id.deleteRecord)
    public void deleteRecord() {
        presenter.deleteRecord();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);
        bindView(this);
        setSupportActionBar(toolbar);
        checkIntent();
    }

    @Override
    public void init() {
        baiduMap = mapView.getMap();
        usersList.setLayoutManager(new GridLayoutManager(this, 1));
        baiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                presenter.addPoint(latLng);
            }
        });

    }

    @Override
    public void refreshList() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.map_satellite:
                setMapDisplayType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.map_normal:
                setMapDisplayType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.test:
                test();
                break;
            case R.id.rename:
                presenter.renameRecord();
                break;
        }
        return true;
    }

    private void setMapDisplayType(int mapType) {
        if (baiduMap != null)
            baiduMap.setMapType(mapType);
    }

    @Override
    public void checkIntent() {
        endEditAction();
        if (getIntent().hasExtra(KEY_RECORD)) {
            String recordId = getIntent().getStringExtra(KEY_RECORD);
            int position = getIntent().getIntExtra(KEY_POSITION, -1);
            presenter = new RecordDetailPresenter(this);
            presenter.setPosition(position);
            presenter.initByRecord(recordId);
        } else {
            show("must pass a Record by Intent");
        }
        locSubscriber = MonitorManager.instance().subscriber(TraceLocation.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<TraceLocation>() {
                    @Override
                    public void call(TraceLocation traceLocation) {
                        presenter.handlerLocationMessage(traceLocation);
                    }
                });
        refreshSub = MonitorManager.instance().subscriber(int.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        refreshList();
                    }
                });
        monitorSubscription = MonitorManager.instance().subscriber(MonitorTaskEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<MonitorTaskEvent>() {
                    @Override
                    public void call(MonitorTaskEvent monitorTaskEvent) {
                        refreshList();
                    }
                });

    }

    private void test(){
        marker = MapUtils.drawMarker(baiduMap, new LatLng(lat,lng),
                BitmapDescriptorFactory.fromView(new PinView(this,0,Color.YELLOW,"godood")));
        new H().sendEmptyMessage(100);
    }

    @Override
    public void setAdapter() {
        if (adapter == null) {
            adapter = new Adapter();
            usersList.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setRecordLocation(List<LatLng> point) {
        MapUtils.setLocation(baiduMap, point, locBitmap);

    }

    @Override
    public void drawRecord(List<LatLng> points, int type, int radius) {
        baiduMap.clear();
        MapUtils.drawMarker(baiduMap, points, bitmap);
        MapUtils.drawRecord(baiduMap, points, type, radius);
    }

    @Override
    public BaiduMap getMap() {
        return baiduMap;
    }

    @Override
    public BitmapDescriptor getMarkerIcon() {
        return bitmap;
    }

    @Override
    public void beginEditAction() {
        reedit.setVisibility(View.GONE);
        cancel.setVisibility(View.VISIBLE);
        undo.setVisibility(View.VISIBLE);
        redo.setVisibility(View.VISIBLE);
        checked.setVisibility(View.VISIBLE);
    }

    @Override
    public void endEditAction() {
        reedit.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.GONE);
        undo.setVisibility(View.GONE);
        redo.setVisibility(View.GONE);
        checked.setVisibility(View.GONE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (baiduMap != null)
            baiduMap.clear();
        baiduMap = null;
        if (mapView != null)
            mapView.onDestroy();
        mapView = null;
        if (locBitmap != null)
            locBitmap.recycle();
        locBitmap = null;
        if (bitmap != null)
            bitmap.recycle();
        bitmap = null;
        presenter.onDestroy();
        if (locSubscriber != null)
            locSubscriber.unsubscribe();
        if (refreshSub != null)
            refreshSub.unsubscribe();
        if (monitorSubscription != null)
            monitorSubscription.unsubscribe();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200) {
            if (adapter == null) {
                adapter = new Adapter();
                usersList.setAdapter(adapter);
            } else
                refreshList();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private class H extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 100) {
                count--;
                if (count > 0) {
                    lat += 0.001;
                    lng += 0.001;
                    marker.setPosition(new LatLng(lat, lng));
                    sendEmptyMessageDelayed(100, 500);
                }
            }
        }
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(RecordDetailActivity.this)
                    .inflate(R.layout.item_monitor_user, null);
            final ViewHolder holder = new ViewHolder(view);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Contact contact = presenter.getContact(holder.getLayoutPosition());
                    Intent intent = new Intent(RecordDetailActivity.this, ChatActivity.class);
                    intent.putExtra(ChatActivity.CHAT_TITTLE,contact.getUser().displayName());
                    intent.putExtra(ChatActivity.CHAT_PEER, contact.getUser().identifier());
                    startActivity(intent);
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Contact contact = presenter.getContact(position);
            User user = contact.getUser();
            holder.name.setText(UserManager.instance().getUserDisplayName(user));
            holder.info.setText(StringHelper.MonitorInfo(contact));
            holder.tag.setText(StringHelper.getContactTag(contact));
            holder.status.setText(StringHelper.getContactStatus(contact));
            holder.numView.setNum(String.valueOf(position + 1));
        }

        @Override
        public int getItemCount() {
            return presenter.getMonitorUsersSize();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.itemMonitorInfo)
        TextView info;
        @BindView(R.id.itemMonitorName)
        TextView name;
        @BindView(R.id.itemMonitorUserHead)
        ImageView head;
        @BindView(R.id.itemMonitorStatus)
        LabelTextView status;
        @BindView(R.id.itemMonitorTag)
        LabelTextView tag;
        @BindView(R.id.monitorHandleLayout)
        LinearLayout handlerLayout;
        @BindView(R.id.itemMonitorNum)
        MapPinNumView numView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.toggleSwitcher)
        void click(View v) {
            if (((ToggleCheckBox) v).isChecked()) {
                handlerLayout.setVisibility(View.VISIBLE);
            } else {
                handlerLayout.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.sendClientInfo)
        void sendClientInfoReq(){
            presenter.sendClientInfoReq(getLayoutPosition());
        }

        @OnClick(R.id.startMonitor)
        void startMonitor() {
            presenter.startMonitor(getLayoutPosition());
        }

        @OnClick(R.id.stopMonitor)
        void stopMonitor() {
            presenter.stopMonitor(getLayoutPosition());
        }

        @OnClick(R.id.removeMonitor)
        void removeMonitor() {
            presenter.removeMonitorUser(getLayoutPosition());
        }

        @OnClick(R.id.setLocationSpeed)
        void setMonitorLocationSpeed() {
            presenter.setMonitorLocationSpeed(getLayoutPosition());
        }

        @OnClick(R.id.timerTask)
        void monitorTimerTask(){
            presenter.toMonitorTimerTask(getLayoutPosition());
        }
    }
}
