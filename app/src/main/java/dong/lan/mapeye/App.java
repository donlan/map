package dong.lan.mapeye;

import android.app.Application;
import android.content.Context;

import com.amap.api.maps.AMap;
import com.baidu.mapapi.SDKInitializer;
import com.orhanobut.logger.Logger;
import com.tencent.TIMGroupReceiveMessageOpt;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMOfflinePushListener;
import com.tencent.TIMOfflinePushNotification;
import com.tencent.TIMTextElem;
import com.tencent.qalsdk.sdk.MsfSdkUtils;

import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Message;
import dong.lan.mapeye.common.JMCenter;
import dong.lan.mapeye.common.MonitorManager;
import dong.lan.mapeye.utils.SPHelper;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * 项目：  MapEye
 * 作者：  梁桂栋
 * 日期：  7/2/2016  15:19.
 * Email: 760625325@qq.com
 */
public class App extends Application {


    private static App context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;


        SPHelper.init(this);
        Logger.init("MapEye");
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                        if (oldVersion == 3) {
                            RealmSchema schema = realm.getSchema();
                            schema.get("Record").addField("radius", int.class);
                            schema.remove("Location");
                        }
                    }
                })
                .schemaVersion(4)
                .name("mapeye")
                .build();

        Realm.setDefaultConfiguration(configuration);
        MonitorManager.instance().init(this);
        SDKInitializer.initialize(this);

        if (MsfSdkUtils.isMainProcess(this)) {
            TIMManager.getInstance().setOfflinePushListener(new TIMOfflinePushListener() {
                @Override
                public void handleNotification(TIMOfflinePushNotification notification) {
                    if (notification.getGroupReceiveMsgOpt() == TIMGroupReceiveMessageOpt.ReceiveAndNotify) {
                        //消息被设置为需要提醒
                        notification.doNotify(getApplicationContext(), R.drawable.logo);
                    }
                }
            });
        }

        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {
            @Override
            public boolean onNewMessages(List<TIMMessage> list) {
                for (TIMMessage message : list) {
                    MonitorManager.instance().dispatchMessage(message);
                }
                return false;
            }
        });

        JMessageClient.init(this);
        JPushInterface.setDebugMode(true);


        JMessageClient.registerEventReceiver(this);

    }


    public void onEventMainThread(MessageEvent event) {
        Message msg = event.getMessage();
        JMCenter.instance().dispatchMessage(msg);

    }


}
