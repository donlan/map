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

package dong.lan.mapeye.presenter;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupMembersCallback;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import dong.lan.mapeye.R;
import dong.lan.mapeye.bmob.BmobAction;
import dong.lan.mapeye.common.Config;
import dong.lan.mapeye.common.JMCenter;
import dong.lan.mapeye.common.MonitorManager;
import dong.lan.mapeye.common.UserManager;
import dong.lan.mapeye.contracts.RecordDetailContract;
import dong.lan.mapeye.events.MainEvent;
import dong.lan.mapeye.model.Point;
import dong.lan.mapeye.model.Record;
import dong.lan.mapeye.model.TraceLocation;
import dong.lan.mapeye.model.message.CMDMessage;
import dong.lan.mapeye.model.users.Contact;
import dong.lan.mapeye.model.users.Group;
import dong.lan.mapeye.utils.MapUtils;
import dong.lan.mapeye.utils.TransitionUtil;
import dong.lan.mapeye.views.MonitorTimerTaskActivity;
import dong.lan.mapeye.views.customsView.Dialog;
import dong.lan.mapeye.views.customsView.MapPinNumView;
import dong.lan.mapeye.views.record.RecordDetailActivity;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;

/**
 * Created by 梁桂栋 on 16-11-10 ： 下午8:16.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class RecordDetailPresenter implements RecordDetailContract.Presenter {

    private RecordDetailActivity view;
    private Record record = null;
    private int position = -1;
    private List<LatLng> points;
    private boolean isEdit = false;
    private Group group = null;
    private Realm realm = null;
    private HashMap<String, Marker> markersMap;
    private Stack<LatLng> backup;

    public RecordDetailPresenter(RecordDetailActivity view) {
        this.view = view;
        backup = new Stack<>();
        realm = Realm.getDefaultInstance();
    }

    @Override
    public int getMonitorUsersSize() {
        return (group == null) ? 0 : group.getMembers().size();
    }

    @Override
    public List<Contact> getMonitors() {
        return group == null ? null : group.getMembers();
    }

    @Override
    public Contact getContact(int position) {
        return (group == null || group.getMembers() == null) ? null : group.getMembers().get(position);
    }

    @Override
    public void initByRecord(final String id) {
        if (realm.isInTransaction()) {
            realm.cancelTransaction();
        }
        realm.executeTransaction(
                new Realm.Transaction() {
                    @Override
                    public void execute(final Realm realm) {
                        record = realm.where(Record.class).equalTo("id", id).findFirst();
                        if (record == null) {
                            view.show("找不到记录");
                        } else {
                            view.setTitle(record.getLabel());
                            view.init();
                            points = new ArrayList<>();
                            points.addAll(TransitionUtil.Point2Latlng(record.getPoints()));
                            MapUtils.drawMarker(view.getMap(), points, view.getMarkerIcon());
                            if (!points.isEmpty()) {
                                view.setRecordLocation(points);
                                view.drawRecord(points, record.getType(), record.getRadius());
                            }
                            final String id = record.getId();
                            group = realm.where(Group.class).equalTo("groupId", id).findFirst();
                            if (group == null || group.getMembers().isEmpty()) {

                                JMessageClient.getGroupMembers(Long.parseLong(id), new GetGroupMembersCallback() {
                                    @Override
                                    public void gotResult(int i, String s, List<UserInfo> list) {
                                        group = UserManager.instance().
                                                initGroupInfo(list, id, record.getLabel(), realm);
                                        initAdapter(group);
                                    }
                                });
                            } else {
                                initAdapter(group);
                            }
                            Logger.d("" + group);

                        }
                    }
                }
        );
    }

    private void initAdapter(Group group) {
        view.setAdapter();
        group.addChangeListener(new RealmChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel element) {

                RecordDetailPresenter.this.view.refreshList();
            }
        });
    }

    @Override
    public void deleteRecord() {
        if (record != null) {
            try {
                JMessageClient.exitGroup(Long.parseLong(record.getId()), new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if(i == 0){
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            record.deleteFromRealm();
                            RealmList<Contact> contacts = group.getMembers();
                            for (Contact c :
                                    contacts) {
                                c.deleteFromRealm();
                            }
                            contacts.clear();
                            group.deleteFromRealm();
                            realm.commitTransaction();
                            BmobAction.deleteRecord(record.getId());
                            view.show("删除成功");
                            EventBus.getDefault().post(new MainEvent(MainEvent.CODE_DELETE_RECORD, position));
                            view.finish();
                        }else{
                            view.toast(s);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                view.toast(e.getMessage());
            }
        }
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public Record initByRecord() {
        return record;
    }

    @Override
    public void reeditRecord() {
        isEdit = true;
        view.beginEditAction();
        MapUtils.drawMarker(view.getMap(),
                points,
                view.getMarkerIcon());
    }

    @Override
    public void addPoint(LatLng point) {
        if (!isEdit)
            return;
        if (record.getType() == Record.TYPE_CIRCLE && points.size() == 1) {
            view.toast("半径围栏只能有一个标记点");
            return;
        }
        Marker marker = MapUtils.drawMarker(view.getMap(), point, view.getMarkerIcon());
        marker.setDraggable(true);
        points.add(point);
        view.getMap().clear();
        MapUtils.onlyDrawMarker(view.getMap(), points, view.getMarkerIcon());
        MapUtils.drawRecord(view.getMap(), points, record.getType(), record.getRadius());
    }

    @Override
    public void dragMarker(Marker marker) {
        if (!isEdit) {
            isEdit = true;
            view.beginEditAction();
        }
        BaiduMap baiduMap = view.getMap();
        baiduMap.clear();
        MapUtils.onlyDrawMarker(baiduMap, points, view.getMarkerIcon());
        MapUtils.drawRecord(baiduMap, points, record.getType(), record.getRadius());
    }

    @Override
    public void checkEdit() {
        view.endEditAction();
        isEdit = false;
        realm.beginTransaction();
        record.getPoints().clear();
        for (LatLng latLng : points) {
            record.getPoints().add(new Point(latLng.latitude, latLng.longitude));
        }
        realm.commitTransaction();
        EventBus.getDefault().post(new MainEvent(MainEvent.CODE_REFRESH, position));
    }

    @Override
    public void cancelEdit() {
        isEdit = false;
        view.endEditAction();
    }

    @Override
    public void startMonitor(int position) {
        final Contact contact = getContact(position);
        MonitorManager.instance().sendStartMonitorMsg(contact, record, realm);
    }

    @Override
    public void stopMonitor(int position) {
        final Contact contact = getContact(position);
        MonitorManager.instance().sendStopMonitorMsg(contact, record.getId(), realm);

    }

    @Override
    public void removeMonitorUser(int position) {
        final Contact contact = getContact(position);
        if (contact == null)
            return;
        if (contact.getStatus() == Contact.STATUS_MONITORING) {
            view.show("请先停止当前用户的位置监听");
            return;
        }
        JMessageClient.removeGroupMembers(Long.parseLong(record.getId()), Collections.singletonList(contact.getUser().identifier()), new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if(i == 0){
                    realm.beginTransaction();
                    List<Contact> contacts = group.getMembers();
                    contacts.remove(contact);
                    contact.deleteFromRealm();
                    realm.commitTransaction();
                    BmobAction.removeRecordMember(record.getId(), contact);
                    view.refreshList();
                }else{
                    view.toast(s);
                }
            }
        });

    }

    @Override
    public void handlerLocationMessage(TraceLocation traceLocation) {
        if (markersMap == null)
            markersMap = new HashMap<>();
        String id = String.valueOf(traceLocation.getMonitorId());
        LatLng point = new LatLng(traceLocation.getLatitude(), traceLocation.getLongitude());
        if (markersMap.containsKey(id)) {
            Marker marker = markersMap.get(id);
            marker.setPosition(point);
        } else {
            int pos = 0;
            List<Contact> contacts = group.getMembers();
            for (int i = 0; i < contacts.size(); i++) {
                if (contacts.get(i).getId().equals(id)) {
                    pos = i + 1;
                    break;
                }
            }
            BitmapDescriptor icon = BitmapDescriptorFactory.fromView(
                    new MapPinNumView(view, String.valueOf(pos), Color.YELLOW, 18, Color.DKGRAY));
            Marker marker = MapUtils.drawMarker(view.getMap(), point, icon, 0.5f, 1f);
            markersMap.put(id, marker);
        }
    }

    @Override
    public void undoAction() {
        if (points == null || points.isEmpty()) {
            view.show("没有可恢复的点");
            return;
        }
        LatLng point = points.remove(points.size() - 1);
        backup.push(point);
        view.getMap().clear();
        MapUtils.onlyDrawMarker(view.getMap(), points, view.getMarkerIcon());
        MapUtils.drawRecord(view.getMap(), points, record.getType(), record.getRadius());
    }

    @Override
    public void redoAction() {
        if (backup.isEmpty()) {
            view.show("没有恢复记录了");
            return;
        }
        LatLng point = backup.pop();
        points.add(point);
        view.getMap().clear();
        MapUtils.onlyDrawMarker(view.getMap(), points, view.getMarkerIcon());
        MapUtils.drawRecord(view.getMap(), points, record.getType(), record.getRadius());
    }

    @Override
    public void setMonitorLocationSpeed(int layoutPosition) {
        final Contact contact = getContact(layoutPosition);
        if (contact == null)
            return;
        if (contact.getStatus() != Contact.STATUS_MONITORING) {
            view.toast(contact.getUser().username() + " : 没有开始监听");
            return;
        }
        final Dialog dialog = new Dialog(view).setupView(R.layout.dialog_location_speed);
        dialog.bindClick(R.id.location_speed_done, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = dialog.getText(R.id.location_speed_input);
                try {
                    int time = Integer.parseInt(input);
                    if (time < 1 || time > 1800) {
                        view.toast("时间间隔不符合要求");
                    } else {
                        Message m = new JMCenter.JMessage(CMDMessage.CMD_SET_LOCATION_SPEED,
                                contact.getUser().identifier(),
                                "定位频率调整为： " + time + " 秒 1 次")
                                .appendNumberExtra(JMCenter.EXTRAS_LOCATION_SPEED, time)
                                .build();
                        JMCenter.sendMessage(m, new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i == 0)
                                    view.toast("发送指定成功");
                            }
                        });

                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    view.toast("输入必须是不含空格的正整数");
                }
            }
        }).show();
    }

    @Override
    public void renameRecord() {
        final Dialog dialog = new Dialog(view).setupView(R.layout.dialog_simple_one_input);
        dialog.bindText(R.id.simpleDialogTittle, "设置新的标签");
        dialog.bindClick(R.id.simpleDialogDone, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = dialog.getText(R.id.simpleDialogInput, EditText.class);
                if (TextUtils.isEmpty(text)) {
                    view.toast("不能为空");
                    return;
                }
                realm.beginTransaction();
                record.setLabel(text);
                realm.commitTransaction();
                view.setTitle(text);
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void sendClientInfoReq(int position) {
        final Contact contact = getContact(position);

        JMCenter.sendClientInfoMessage(contact.getUser().identifier(),
                record.getId(), new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                Logger.d(i+","+s);
                if (i == 0) {
                    view.toast("发送请求成功，请等待数据回传");
                }else{
                    view.toast(i+","+s);
                }
            }
        });
    }

    @Override
    public void toMonitorTimerTask(int position) {
        final Contact contact = getContact(position);
        Intent intent = new Intent(view, MonitorTimerTaskActivity.class);
        intent.putExtra(Config.KEY_IDENTIFIER, contact.getUser().identifier());
        intent.putExtra(Config.KEY_RECORD_ID, record.getId());
        view.startActivity(intent);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (group != null)
            group.removeChangeListeners();
        if (realm != null) {
            realm.removeAllChangeListeners();
            realm.close();
        }
        if (markersMap != null)
            markersMap.clear();
        markersMap = null;
    }
}
