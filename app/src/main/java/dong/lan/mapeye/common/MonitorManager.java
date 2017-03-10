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

package dong.lan.mapeye.common;

import android.annotation.TargetApi;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.orhanobut.logger.Logger;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;
import com.tencent.qcloud.tlslibrary.helper.JMHelper;

import java.util.HashMap;

import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import dong.lan.mapeye.R;
import dong.lan.mapeye.model.Affair;
import dong.lan.mapeye.model.ClientInfo;
import dong.lan.mapeye.model.MonitorTimer;
import dong.lan.mapeye.model.users.Contact;
import dong.lan.mapeye.model.message.BaseMessage;
import dong.lan.mapeye.model.message.CMDMessage;
import dong.lan.mapeye.model.MonitorRecode;
import dong.lan.mapeye.model.Record;
import dong.lan.mapeye.model.TraceLocation;
import dong.lan.mapeye.model.message.IMessage;
import dong.lan.mapeye.model.users.User;
import dong.lan.mapeye.task.MonitorStatusTask;
import dong.lan.mapeye.task.MonitorTimerTask;
import dong.lan.mapeye.utils.NetUtils;
import dong.lan.mapeye.utils.PolygonHelper;
import dong.lan.mapeye.utils.SPHelper;
import dong.lan.mapeye.views.AffairHandleActivity;
import dong.lan.mapeye.views.UserClientInfoActivity;
import dong.lan.mapeye.views.record.RecordDetailActivity;
import io.realm.Realm;
import io.realm.RealmList;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

/**
 * Created by 梁桂栋 on 16-12-3 ： 下午8:44.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class MonitorManager {
    private static final String TAG = "MonitorManager";
    private static final long VALID_TIME_GAP = 1000 * 60 * 10;
    private LocationService locationService;
    private static MonitorManager manager;
    private HashMap<Long, Record> monitor;
    private HashMap<Long, MonitorRecode> monitorRecodes;
    private SerializedSubject<Object, Object> bus;
    private Context context;
    private Uri alertSound = null;
    private Realm defaultRealm;
    private NotificationManager notificationManager;

    private MonitorManager() {
        monitor = new HashMap<>();
        monitorRecodes = new HashMap<>();
        //基于Rx的用于Message的二次订阅分发使用
        bus = new SerializedSubject<>(PublishSubject.create());
    }

    /**
     * 初始化全局Context
     *
     * @param context 默认是在Application onCreate()中初始化
     */
    public void init(Context context) {
        this.context = context.getApplicationContext();
        defaultRealm = Realm.getDefaultInstance();
    }

    /**
     * @return 全局ApplicationContext
     */
    public Context getContext() {
        return context;
    }

    public static MonitorManager instance() {
        if (manager == null)
            manager = new MonitorManager();
        return manager;
    }

    /**
     * 添加一个监听
     *
     * @param id     用户id  groupId ^ userId
     * @param record 监听的记录实体
     */
    public void addMonitor(long id, Record record, MonitorRecode monitorRecode) {
        monitor.put(id, record);
        monitorRecodes.put(id, monitorRecode);
    }

    /**
     * 根据id移除一个用户的位置监听
     *
     * @param id 用户id  groupId ^ userId
     */
    public void removeMonitor(long id) {
        monitor.remove(id);
        MonitorRecode monitorRecode = monitorRecodes.remove(id);
        if (monitorRecode != null) {
            if (monitorRecode.isValid()) {
                defaultRealm.beginTransaction();
                monitorRecode = defaultRealm.where(MonitorRecode.class)
                        .equalTo("id", monitorRecode.getId()).findFirst();
                monitorRecode.setEndTime(System.currentTimeMillis());
                defaultRealm.commitTransaction();
            } else {
                monitorRecode.setEndTime(System.currentTimeMillis());
            }
        }
        Logger.d("remove Monitor Task:" + id);
    }

    /**
     * 处理发送过来的位置信息消息，主要是判断是否在围栏中或者路径之上并做相应提醒
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void handlerMonitorLocation(String recordId, String identifier, TraceLocation location) {
        long contactId = Contact.createId(recordId, identifier);
        Record record = monitor.get(contactId);
        if (record == null)
            return;
        PolygonHelper.DISTANCE = (int) location.getRadius();

        addMonitorRecord(monitorRecodes.get(contactId), location);

        location.setDisplayName(identifier);
        location.setMonitorId(contactId);
        bus.onNext(location);

        if (!judge(location, record)) {
            if (alertSound == null)
                alertSound = Uri.parse(SPHelper.get(Config.SP_KEY_ALERT_SOUND));

            Intent intent = new Intent(context, RecordDetailActivity.class);
            intent.putExtra(RecordDetailActivity.KEY_RECORD, record.getId());
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new Notification.Builder(context)
                    .setContentTitle(record.getLabel())
                    .setSmallIcon(R.drawable.logo)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setVibrate(new long[]{1000, 500, 1000})
                    .setSound(alertSound)
                    .setContentText(identifier + record.getNotInRecordStr())
                    .setContentIntent(pendingIntent)
                    .build();
            if (notificationManager == null)
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(identifier.hashCode(), notification);
        }
    }

    private void addMonitorRecord(MonitorRecode monitorRecode, TraceLocation traceLocation) {
        if (monitorRecode != null) {
            if (defaultRealm != null) {
                defaultRealm.beginTransaction();
                monitorRecode.getLocations().add(defaultRealm.copyToRealm(traceLocation));
                defaultRealm.commitTransaction();
            }
        }
    }

    /**
     * 判断是否在围栏中或者路径之上
     *
     * @param traceLocation 包含经纬度的信息
     * @param record        监听记录实体
     * @return 在围栏中或者路径之上返回true，否则false
     */
    private boolean judge(TraceLocation traceLocation, Record record) {
        int type = record.getType();
        boolean in = false;
        if (type == Record.TYPE_FENCE) {
            in = PolygonHelper.isPointInPolygon(record.getPoints(),
                    traceLocation.getLatitude(),
                    traceLocation.getLongitude());
        } else if (type == Record.TYPE_ROUTE) {
            in = PolygonHelper.isOnRoute(record.getPoints(),
                    traceLocation.getLatitude(),
                    traceLocation.getLongitude());
        } else if (type == Record.TYPE_CIRCLE) {
            in = PolygonHelper.isInCircleFence(record.getPoints().get(0),
                    new LatLng(traceLocation.getLatitude(), traceLocation.getLongitude()),
                    (int) traceLocation.getRadius(),
                    record.getRadius());
        }
        return in;
    }

    /**
     * 通过Rx发送订阅事件
     *
     * @param o 订阅事件
     */
    public void post(Object o) {
        bus.onNext(o);
    }

    /**
     * @param tClass 此处默认认为是Message.class
     * @param <T>    此处默认认为是Message
     * @return 一个位置信息类型消息的被订阅者
     */
    public <T> Observable<T> subscriber(Class<T> tClass) {
        return bus.ofType(tClass);
    }

    /**
     * 接收到从JMessage传递的消息，并做分发
     *
     * @param message
     */
    public void dispatchMessage(TIMMessage message) {
        for (int i = 0; i < message.getElementCount(); i++) {
            TIMElem elem = message.getElement(i);
            TIMElemType type = elem.getType();
            if (type.equals(TIMElemType.Custom)) {
                TIMCustomElem customElem = (TIMCustomElem) elem;
                String data = new String(customElem.getData());
                BaseMessage baseMessage = MessageHelper.getInstance()
                        .toTarget(data, BaseMessage.class);
                Logger.d(data + "+" + message.getCustomStr() + "+" + baseMessage.getCmd());
                handleCustomMessage(message, baseMessage);
            } else if (type.equals(TIMElemType.Text)) {
                TIMTextElem textElem = (TIMTextElem) elem;
                int cmd = message.getCustomInt();
                Logger.d(textElem.getText() + "," + message.getCustomStr() + "," + cmd);
            } else {
                Logger.d(message.getSender() + "," + type);
            }
        }
    }

    private void handleCustomMessage(TIMMessage message, BaseMessage baseMessage) {
        int cmd = baseMessage.getCmd();
        Logger.d("" + cmd);
        switch (cmd) {
//            case CMDMessage.CMD_MONITOR_START:
//                startMonitorLocation(message, baseMessage.getExtras());
//                break;
//            case CMDMessage.CMD_MONITOR_STOP:
//                stopMonitorLocation();
//                break;
//            case CMDMessage.CMD_MONITORING:
//                String[] ids = MessageHelper.parseRecordContactId(message.getCustomStr());
//                long id = Contact.createId(ids[0], ids[1]);
//                UserManager.instance().updateContactStatus(id, Contact.STATUS_MONITORING);
//                break;
//            case CMDMessage.CMD_SET_LOCATION_SPEED:
//                int time = baseMessage.getLocationSpeed();
//                if (locationService.isRunning()) {
//                    LocationClientOption option = locationService.getDefaultLocationClientOption();
//                    option.setScanSpan(time * 1000);
//                    if (time == 1) {
//                        option.setLocationNotify(true);
//                    } else {
//                        option.setLocationNotify(false);
//                    }
//                    locationService.setLocationOption(option);
//                    locationService.restart();
//                }
//                break;
//            case CMDMessage.CMD_MONITOR_LOCATING:
//                handlerMonitorLocation(message, baseMessage);
//                break;
        }
    }


    /**
     * 拒绝位置绑定
     *
     * @param msg 消息实体
     */
    public void doMonitorDeny(IMessage msg) {
        UserManager.instance().updateContactTag(msg, Contact.TAG_DENY);
    }


    public LocationService currentLocationService() {
        return locationService;
    }

    public void setLocationOption(LocationClientOption option) {
        locationService.setLocationOption(option);
        locationService.restart();
    }

    /**
     * 同意位置绑定
     *
     * @param msg 消息实体
     */
    public void doMonitorAccept(IMessage msg) {
        UserManager.instance().updateContactTag(msg, Contact.TAG_AGREE);
    }

    /**
     * 处理位置绑定的请求
     *
     * @param text
     * @param message
     */
    public void doMonitorInvite(String text, IMessage message) {
        //为了确定是发送的位置检测绑定邀请消息(通过群发的消息)
        String extras = message.getStringExtra(JMCenter.EXTRAS_RECORD_ID) +
                "," +
                message.getStringExtra(JMCenter.EXTRAS_IDENTIFIER);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Affair affair = realm.createObject(Affair.class);
        affair.setType(Affair.TYPE_MONITOR_BIND);
        affair.setContent(text);
        affair.setCreatedTime(message.timestamp());
        affair.setFromUser(message.sender());
        affair.setHandle(Affair.HANDLE_NO);
        affair.setId(message.id());
        affair.setExtras(extras);
        realm.commitTransaction();
        bus.onNext(affair);

        Intent toAffair = new Intent(context, AffairHandleActivity.class);
        toAffair.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        toAffair.putExtra(AffairHandleActivity.KEY_AFFAIR_ID, affair.getId());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, toAffair, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle("位置信息共享绑定请求")
                .setSubText("请确认请求人真实身份后再同意请求,请求人电话：" + message.sender())
                .setContentText(text)
                .setSmallIcon(R.drawable.logo)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setTicker(text);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) affair.getTag(), builder.build());
    }

    /**
     * 停止定位服务
     */
    public void stopMonitorLocation() {
        if (locationService != null)
            locationService.stop();
    }

    private boolean isCmdValid(long timestamp) {
        return System.currentTimeMillis() - timestamp < VALID_TIME_GAP;
    }

    /**
     * 开始发送监听的位置信息结果
     *
     * @param
     */
    public void startMonitorLocation(final String recordId, final String identifier, final String toUserId, long timestamp) {
        if (!isCmdValid(timestamp))
            return;
        Logger.d(recordId + "," + identifier);
        Message m = new JMCenter.JMessage(CMDMessage.CMD_MONITORING,
                toUserId,
                "开始发送位置信息")
                .appendStringExtra(JMCenter.EXTRAS_IDENTIFIER, identifier)
                .appendStringExtra(JMCenter.EXTRAS_RECORD_ID, recordId).build();
        JMCenter.sendMessage(m, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                Logger.d(i + "," + s);
                if (i == 0) {
                    Logger.d(recordId + "," + identifier);
                    beginLocating(recordId, identifier, toUserId);
                }
            }
        });
    }

    private void beginLocating(final String recordId, final String identifier, final String toUserId) {
        if (locationService == null) {
            locationService = new LocationService(context);
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
            locationService.registerListener(new BDLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    if (bdLocation != null) {
                        JMCenter.sendLocation(CMDMessage.CMD_MONITOR_LOCATING,
                                recordId, identifier, toUserId, bdLocation);
                    }
                }
            });
        }
        locationService.start();
    }


    /**
     * 收集手机的基本信息（电池，网络）
     *
     * @param recordId   来自那个监听记录
     * @param identifier 用户标识
     */
    public void gatherMobileInfo(String recordId, String identifier) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryInfo = context.registerReceiver(null, filter);
        if (batteryInfo != null) {
            ClientInfo clientInfo = new ClientInfo();
            int extra = batteryInfo.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            int charge = ClientInfo.CHARGE_NONE;
            if (extra == BatteryManager.BATTERY_PLUGGED_AC)
                charge = ClientInfo.CHARGE_AC;
            else if (extra == BatteryManager.BATTERY_PLUGGED_USB)
                charge = ClientInfo.CHARGE_USB;
            clientInfo.setChargeStatus(charge);
            int level = batteryInfo.getIntExtra(BatteryManager.EXTRA_LEVEL, 1);
            int max = batteryInfo.getIntExtra(BatteryManager.EXTRA_SCALE, 1);
            float pct = level / max;
            clientInfo.setBattery(pct);
            NetUtils.NetworkType networkType = NetUtils.getNetworkType(context);
            int netType = ClientInfo.getNetType(networkType);
            clientInfo.setNetStatus(netType);
            JMCenter.replyMobileInfo(recordId, identifier, clientInfo);
        }
    }

    public void notifyClientInfo(String recordId, String identifier, String clientInfoJson) {
        ClientInfo clientInfo = MessageHelper.getInstance().toTarget(clientInfoJson, ClientInfo.class);
        Intent toClientInfo = new Intent(context, UserClientInfoActivity.class);
        toClientInfo.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        toClientInfo.putExtra("recordId", recordId);
        toClientInfo.putExtra("identifier", identifier);
        toClientInfo.putExtra("clientInfo", clientInfoJson);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, toClientInfo, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle("客户端信息")
                .setSubText(identifier)
                .setContentText(clientInfo.toString())
                .setSmallIcon(R.drawable.logo)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setTicker(clientInfo.toString());

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(identifier.hashCode(), builder.build());
    }

    /**
     * 处理监听任务 开启/关闭
     *
     * @param timer
     * @param isChecked
     * @param realm
     */
    public void handleTimerStatus(MonitorTimer timer, boolean isChecked, Realm realm) {

        //目前默认是单次执行
        if (timer.getStartTime() < System.currentTimeMillis())
            return;

        realm.beginTransaction();
        timer.setOpen(isChecked);
        realm.commitTransaction();

        Intent timerIntent = new Intent(context, MonitorTimerTask.class);
        timerIntent.putExtra(Config.TIMER_STATUS, isChecked);
        timerIntent.putExtra(Config.TIMER_TASK_ID, timer.getCreateTime());
        timerIntent.putExtra(Config.KEY_RECORD_ID, timer.getRecord().getId());
        timerIntent.putExtra(Config.KEY_IDENTIFIER, timer.getUser().getIdentifier());


        PendingIntent startPending = PendingIntent.getService(
                context,
                Config.REQ_CODE_START_TIMER,
                timerIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timer.getTriggerTimeOfStart(), startPending);

        if (isChecked) {
            Intent endTimerIntent = new Intent(context, MonitorTimerTask.class);
            timerIntent.putExtra(Config.TIMER_STATUS, false);
            timerIntent.putExtra(Config.TIMER_TASK_ID, timer.getCreateTime());
            timerIntent.putExtra(Config.KEY_RECORD_ID, timer.getRecord().getId());
            timerIntent.putExtra(Config.KEY_IDENTIFIER, timer.getUser().getIdentifier());
            PendingIntent endPending = PendingIntent.getService(
                    context,
                    Config.REQ_CODE_END_TIMER,
                    endTimerIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, timer.getTriggerTimeOfEnd(), endPending);
        }
    }

    /**
     * 发送停止监听的控制消息
     *
     * @param contact
     * @param recordId
     * @param realm
     */
    public void sendStopMonitorMsg(final Contact contact, String recordId, final Realm realm) {
        if (contact == null) {
            return;
        }
        if (contact.getStatus() != Contact.STATUS_MONITORING
                && contact.getStatus() != Contact.STATUS_WAITING) {
            toast(contact.getUser().getUsername() + " 没有启动位置监听");
            return;
        }

        User user = contact.getUser();
        final long id = contact.getId();
        toast("开始发送定位结束指令给 " + user.displayName());
        Message jmesage = new JMCenter.JMessage(CMDMessage.CMD_MONITOR_STOP,
                JMHelper.getJUsername(user.getIdentifier()), "开始监听")
                .appendStringExtra(JMCenter.EXTRAS_IDENTIFIER, user.getIdentifier())
                .appendStringExtra(JMCenter.EXTRAS_RECORD_ID, recordId).build();
        JMCenter.sendMessage(jmesage, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                Logger.d(i + "," + s);
                if (i == 0) {
                    toast("发送定位结束指令成功");
                    if (realm != null && !realm.isClosed()) {
                        realm.beginTransaction();
                        contact.setStatus(Contact.STATUS_NONE);
                        realm.commitTransaction();
                    }
                    MonitorManager.instance().removeMonitor(id);
                }
            }
        });
    }


    /**
     * 可用于全局的toast
     *
     * @param text
     */
    public void toast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 发送开始监听的控制消息
     *
     * @param contact
     * @param record
     * @param realm
     */
    public void sendStartMonitorMsg(final Contact contact, final Record record, final Realm realm) {
        if (contact == null) {
            return;
        }
        if (contact.getTag() != Contact.TAG_AGREE) {
            toast(contact.getUser().getUsername() + " 没有与你进行位置共享绑定");
            return;
        }
        if (contact.getStatus() == Contact.STATUS_MONITORING) {
            toast("当前用户正在位置监听");
            return;
        }
        User user = contact.getUser();
        final long id = contact.getId();
        final long mrId = MonitorRecode.createId(id, System.currentTimeMillis());
        toast("开始发送定位指令给 " + user.displayName());

        Message jmesage = new JMCenter.JMessage(CMDMessage.CMD_MONITOR_START,
                JMHelper.getJUsername(user.getIdentifier()), "开始监听")
                .appendStringExtra(JMCenter.EXTRAS_IDENTIFIER, user.getIdentifier())
                .appendStringExtra(JMCenter.EXTRAS_RECORD_ID, record.getId()).build();
        JMCenter.sendMessage(jmesage, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                Logger.d(i + "," + s);
                if (i == 0) {
                    if (realm != null && !realm.isClosed()) {
                        realm.beginTransaction();
                        contact.setStatus(Contact.STATUS_WAITING);
                        MonitorRecode monitorRecode = realm.createObject(MonitorRecode.class);
                        monitorRecode.setId(mrId);
                        monitorRecode.setCreateTime(System.currentTimeMillis());
                        monitorRecode.setMonitoredUser(contact.getUser());
                        monitorRecode.setRecord(record);
                        monitorRecode.setLocations(new RealmList<TraceLocation>());
                        realm.commitTransaction();
                        MonitorManager.instance().addMonitor(id, record, monitorRecode);
                        Intent monitorTask = new Intent(context, MonitorStatusTask.class);
                        monitorTask.putExtra(MonitorStatusTask.KEY_RECORD_ID, record.getId());
                        monitorTask.putExtra(MonitorStatusTask.KEY_MONITOR_NAME, record.getLabel());
                        monitorTask.putExtra(MonitorStatusTask.KEY_MONITOR_ID, id);
                        context.startService(monitorTask);
                        toast("发送定位指令成功");
                    }
                }
            }
        });
    }
}
