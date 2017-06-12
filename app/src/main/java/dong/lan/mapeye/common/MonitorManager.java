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
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import com.baidu.mapapi.utils.DistanceUtil;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.LinkedList;

import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import dong.lan.mapeye.App;
import dong.lan.mapeye.R;
import dong.lan.mapeye.TimerTaskReceiver;
import dong.lan.mapeye.model.Affair;
import dong.lan.mapeye.model.ClientInfo;
import dong.lan.mapeye.model.MonitorRecode;
import dong.lan.mapeye.model.MonitorTimer;
import dong.lan.mapeye.model.Record;
import dong.lan.mapeye.model.TraceLocation;
import dong.lan.mapeye.model.message.CMDMessage;
import dong.lan.mapeye.model.message.IMessage;
import dong.lan.mapeye.model.users.Contact;
import dong.lan.mapeye.model.users.User;
import dong.lan.mapeye.task.MonitorStatusTask;
import dong.lan.mapeye.utils.NetUtils;
import dong.lan.mapeye.utils.PolygonHelper;
import dong.lan.mapeye.utils.SPHelper;
import dong.lan.mapeye.utils.Utils;
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
    private static MonitorManager manager;
    private LocationService locationService;
    private HashMap<String, Record> monitor;
    private HashMap<String, MonitorRecode> monitorRecodes;
    private SerializedSubject<Object, Object> bus;
    private Context context;
    private Uri alertSound = null;
    private Realm defaultRealm;
    private NotificationManager notificationManager;
    private LinkedList<LocationEntity> locationList;

    private MonitorManager() {
        monitor = new HashMap<>();
        monitorRecodes = new HashMap<>();
        //基于Rx的用于Message的二次订阅分发使用
        bus = new SerializedSubject<>(PublishSubject.create());
        locationList = new LinkedList<>();
    }

    public static MonitorManager instance() {
        if (manager == null)
            manager = new MonitorManager();
        return manager;
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

    /**
     * 添加一个监听
     *
     * @param id     用户id  groupId ^ userId
     * @param record 监听的记录实体
     */
    public void addMonitor(String id, Record record, MonitorRecode monitorRecode) {
        monitor.put(id, record);
        monitorRecodes.put(id, monitorRecode);
    }

    /**
     * 根据id移除一个用户的位置监听
     *
     * @param id 用户id  groupId ^ userId
     */
    public void removeMonitor(String id) {
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
        String contactId = Contact.createId(recordId, identifier);
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
                monitorRecode.getLocations().add(traceLocation);
                if (monitorRecode.getCreateTime() == 0)
                    monitorRecode.setCreateTime(System.currentTimeMillis());
                defaultRealm.commitTransaction();
            }
        }
    }


    /***
     * 平滑策略代码实现方法，主要通过对新定位和历史定位结果进行速度评分，
     * 来判断新定位结果的抖动幅度，如果超过经验值，则判定为过大抖动，进行平滑处理,若速度过快，
     * 则推测有可能是由于运动速度本身造成的，则不进行低速平滑处理 ╭(●｀∀´●)╯
     *
     */
    private LocationEntity Algorithm(BDLocation location) {
        double curSpeed = 0;
        LocationEntity newLocation = new LocationEntity();
        if (locationList.isEmpty() || locationList.size() < 2) {
            newLocation.iscalculate = false;
            newLocation.location = location;
            newLocation.time = System.currentTimeMillis();
            locationList.add(newLocation);
        } else {
            if (locationList.size() > 5)
                locationList.removeFirst();
            double score = 0;
            for (int i = 0; i < locationList.size(); ++i) {
                LatLng lastPoint = new LatLng(locationList.get(i).location.getLatitude(),
                        locationList.get(i).location.getLongitude());
                LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
                double distance = DistanceUtil.getDistance(lastPoint, curPoint);
                curSpeed = distance / (System.currentTimeMillis() - locationList.get(i).time) / 1000;
                score += curSpeed * Utils.EARTH_WEIGHT[i];
            }
            if (score > 0.00000999 && score < 0.00005) { // 经验值,开发者可根据业务自行调整，也可以不使用这种算法
                location.setLongitude(
                        (locationList.get(locationList.size() - 1).location.getLongitude() + location.getLongitude())
                                / 2);
                location.setLatitude(
                        (locationList.get(locationList.size() - 1).location.getLatitude() + location.getLatitude())
                                / 2);
                newLocation.iscalculate = true;
            } else {
                newLocation.iscalculate = false;
            }
            newLocation.location = location;
            newLocation.time = System.currentTimeMillis();
            locationList.add(newLocation);
        }
        return newLocation;
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
            in = PolygonHelper.isPointInPolygon(record.getLatLngPoints(),
                    new LatLng(traceLocation.getLatitude(),
                            traceLocation.getLongitude()));
        } else if (type == Record.TYPE_ROUTE) {
            in = PolygonHelper.isOnRoute(record.getLatLngPoints(),
                    traceLocation.getLatitude(),
                    traceLocation.getLongitude(),
                    (int) traceLocation.getRadius());
        } else if (type == Record.TYPE_CIRCLE) {
            in = PolygonHelper.isInCircleFence(record.getPoints().get(0),
                    new LatLng(traceLocation.getLatitude(), traceLocation.getLongitude()),
                    (int) traceLocation.getRadius(),
                    record.getRadius());
        }
        Logger.d(in);
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
    public void doMonitorInvite(final String text, final IMessage message) {
        //为了确定是发送的位置检测绑定邀请消息(通过群发的消息)
        final String extras = message.getStringExtra(JMCenter.EXTRAS_RECORD_ID) +
                "," +
                message.getStringExtra(JMCenter.EXTRAS_IDENTIFIER);
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Affair affair = realm.createObject(Affair.class);
                affair.setOwner(UserManager.instance().myIdentifier());
                affair.setType(Affair.TYPE_MONITOR_BIND);
                affair.setContent(text);
                affair.setCreatedTime(message.timestamp());
                affair.setFromUser(message.sender());
                affair.setHandle(Affair.HANDLE_NO);
                affair.setId(message.id());
                affair.setExtras(extras);
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
        });

    }

    /**
     * 停止定位服务
     */
    public void stopMonitorLocation(String recordId, String identifier) {
        if (locationService != null)
            locationService.stop();
        locationList.clear();
        TraceManager.get().stop(recordId + "_" + identifier);
    }

    private boolean isCmdValid(long timestamp) {
        return System.currentTimeMillis() - timestamp < VALID_TIME_GAP;
    }

    /**
     * 被检测开始发送监听的位置信息结果
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
                    beginLocating(recordId, identifier, toUserId);
                }
            }
        });

        TraceManager.get().start(App.getContext(), recordId + "_" + identifier);
    }

    private void beginLocating(final String recordId, final String identifier, final String toUserId) {
        if (locationService == null) {
            locationService = new LocationService(context);
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
            locationService.registerListener(new BDLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    if (bdLocation != null) {
                        LocationEntity locationEntity = Algorithm(bdLocation);
                        JMCenter.sendLocation(CMDMessage.CMD_MONITOR_LOCATING,
                                recordId, identifier, toUserId, locationEntity.location);
                    }
                }

                @Override
                public void onConnectHotSpotMessage(String s, int i) {

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
            float pct = level;
            clientInfo.setBattery(pct);
            NetUtils.NetworkType networkType = NetUtils.getNetworkType(context);
            int netType = ClientInfo.getNetType(networkType);
            clientInfo.setNetStatus(netType);
            JMCenter.replyMobileInfo(recordId, identifier, clientInfo);
        }
        Logger.d("" + batteryInfo);
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
                .setContentTitle("客户端设备信息")
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
     * 删除定时监听任务记录
     *
     * @param timer
     * @param realm
     */

    public void deleteMonitor(final MonitorTimer timer, Realm realm) {
        if (timer.isManaged()) {
            realm.beginTransaction();
            timer.deleteFromRealm();
            realm.commitTransaction();
        } else {
            final long createTime = timer.getCreateTime();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.where(MonitorTimer.class).equalTo("createTime", createTime)
                            .findAll().deleteAllFromRealm();
                }
            });
        }
    }

    /**
     * 处理监听任务 开启/关闭
     *
     * @param timer
     * @param isChecked
     * @param realm
     */
    public void handleTimerStatus(Context context, MonitorTimer timer, boolean isChecked, Realm realm) {

        //目前默认是单次执行
        if (timer.getEndTime() < System.currentTimeMillis())
            return;
        realm.beginTransaction();
        timer.setOpen(isChecked);
        realm.commitTransaction();

        AlarmManager startAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        AlarmManager endAlarmManagere = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent timerIntent = new Intent(context, TimerTaskReceiver.class);
        timerIntent.putExtra(Config.TIMER_STATUS, true);
        timerIntent.putExtra(Config.TIMER_TASK_ID, timer.getCreateTime());
        timerIntent.putExtra(Config.KEY_RECORD_ID, timer.getRecord().getId());
        timerIntent.putExtra(Config.KEY_IDENTIFIER, timer.getUser().identifier());

        PendingIntent startPending = PendingIntent.getBroadcast(
                context, timer.hashCode(), timerIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent endTimerIntent = new Intent(context, TimerTaskReceiver.class);
        endTimerIntent.putExtra(Config.TIMER_STATUS, false);
        endTimerIntent.putExtra(Config.TIMER_TASK_ID, timer.getCreateTime());
        endTimerIntent.putExtra(Config.KEY_RECORD_ID, timer.getRecord().getId());
        endTimerIntent.putExtra(Config.KEY_IDENTIFIER, timer.getUser().identifier());

        PendingIntent endPending = PendingIntent.getBroadcast(
                context, timer.hashCode() + 1, endTimerIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (isChecked) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                startAlarmManager.set(AlarmManager.RTC_WAKEUP, timer.getTriggerTimeOfStart(), startPending);
                endAlarmManagere.set(AlarmManager.RTC_WAKEUP, timer.getTriggerTimeOfEnd(), endPending);
            } else {
                startAlarmManager.setExact(AlarmManager.RTC_WAKEUP, timer.getTriggerTimeOfStart(), startPending);
                endAlarmManagere.setExact(AlarmManager.RTC_WAKEUP, timer.getTriggerTimeOfEnd(), endPending);
            }
        } else {
            startAlarmManager.cancel(startPending);
            endAlarmManagere.cancel(endPending);
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
            toast(contact.getUser().username() + " 没有启动位置监听");
            return;
        }

        User user = contact.getUser();
        final String id = contact.getId();
        toast("开始发送定位结束指令给 " + user.displayName());
        Message jmesage = new JMCenter.JMessage(CMDMessage.CMD_MONITOR_STOP,
                user.username(), "停止监听")
                .appendStringExtra(JMCenter.EXTRAS_IDENTIFIER, user.identifier())
                .appendStringExtra(JMCenter.EXTRAS_RECORD_ID, recordId).build();
        JMCenter.sendMessage(jmesage, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                Logger.d(i + "," + s);
                if (i == 0) {
                    toast("发送定位结束指令成功");
                    Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Contact c = realm.where(Contact.class)
                                    .equalTo("id",id).findFirst();
                            if(c!=null)
                                c.setStatus(Contact.STATUS_NONE);
                        }
                    });
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
        if (Thread.currentThread().getName().contains("main"))
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
            toast(contact.getUser().username() + " 没有与你进行位置共享绑定");
            return;
        }
        if (contact.getStatus() == Contact.STATUS_MONITORING) {
            toast("当前用户正在位置监听");
            return;
        }
        final String recordId = record.getId();
        final String label = record.getLabel();
        User user = contact.getUser();
        final String id = contact.getId();
        final long mrId = MonitorRecode.createId(id, System.currentTimeMillis());
        toast("开始发送定位指令给 " + user.displayName());


        Message jmesage = new JMCenter.JMessage(CMDMessage.CMD_MONITOR_START,
                user.username(), "开始监听")
                .appendStringExtra(JMCenter.EXTRAS_IDENTIFIER, user.identifier())
                .appendStringExtra(JMCenter.EXTRAS_RECORD_ID, record.getId()).build();
        JMCenter.sendMessage(jmesage, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Contact c = realm.where(Contact.class)
                                    .equalTo("id",id).findFirst();
                            Record r = realm.where(Record.class)
                                    .equalTo("id",recordId)
                                    .findFirst();
                            if(c!=null) {
                                c.setStatus(Contact.STATUS_WAITING);
                                MonitorRecode monitorRecode = realm.createObject(MonitorRecode.class);
                                monitorRecode.setId(mrId);
                                monitorRecode.setCreateTime(0);
                                monitorRecode.setMonitoredUser(c.getUser());
                                monitorRecode.setRecord(r);
                                monitorRecode.setLocations(new RealmList<TraceLocation>());
                                addMonitor(id, r, monitorRecode);
                            }
                        }
                    });

                    Intent monitorTask = new Intent(context, MonitorStatusTask.class);
                    monitorTask.putExtra(MonitorStatusTask.KEY_RECORD_ID, recordId);
                    monitorTask.putExtra(MonitorStatusTask.KEY_MONITOR_NAME, label);
                    monitorTask.putExtra(MonitorStatusTask.KEY_MONITOR_ID, id);
                    context.startService(monitorTask);
                    toast("发送定位指令成功");
                }
            }
        });
    }


    /**
     * 封装定位结果和时间的实体类
     *
     * @author baidu
     */
    public static class LocationEntity {
        BDLocation location;
        long time;
        boolean iscalculate;
    }
}
