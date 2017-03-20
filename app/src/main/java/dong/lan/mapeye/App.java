package dong.lan.mapeye;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Locale;

import cn.bmob.v3.Bmob;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.ConversationRefreshEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
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


    private static final String TAG = "App";
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

        Bmob.initialize(this, "03d2a14db425e979034b43b5d661af19");

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
                        }else if(oldVersion == 4){
                            RealmSchema schema = realm.getSchema();
                            schema.get("Contact").removeField("startMonitorTime")
                                    .removeField("endMonitorTime")
                                    .addField("startMonitorTime",long.class)
                                    .addField("endMonitorTime",long.class);
                        }
                    }
                })
                .schemaVersion(5)
                .name("mapeye")
                .build();
        Realm.setDefaultConfiguration(configuration);
        MonitorManager.instance().init(this);
        SDKInitializer.initialize(this);

        JMessageClient.setDebugMode(true);
        JMessageClient.init(this, true);
        JMessageClient.registerEventReceiver(this);

    }


    public void onEventMainThread(MessageEvent event) {
        Message msg = event.getMessage();
        JMCenter.instance().dispatchMessage(msg);
    }

    public void onEventMainThread(OfflineMessageEvent event) {
        //获取事件发生的会话对象
        Conversation conversation = event.getConversation();
        List<Message> newMessageList = event.getOfflineMessageList();//获取此次离线期间会话收到的新消息列表
        for (Message message : newMessageList)
            JMCenter.instance().dispatchMessage(message);
        System.out.println(String.format(Locale.SIMPLIFIED_CHINESE, "收到%d条来自%s的离线消息。\n", newMessageList.size(), conversation.getTargetId()));
    }


    /**
     * 如果在JMessageClient.init时启用了消息漫游功能，则每当一个会话的漫游消息同步完成时
     * sdk会发送此事件通知上层。
     **/
    public void onEventMainThread(ConversationRefreshEvent event) {
        //获取事件发生的会话对象
        Conversation conversation = event.getConversation();
        //获取事件发生的原因，对于漫游完成触发的事件，此处的reason应该是
        //MSG_ROAMING_COMPLETE
        ConversationRefreshEvent.Reason reason = event.getReason();
        System.out.println(String.format(Locale.SIMPLIFIED_CHINESE, "收到ConversationRefreshEvent事件,待刷新的会话是%s.\n", conversation.getTargetId()));
        System.out.println("事件发生的原因 : " + reason);
        MonitorManager.instance().post(conversation.getLatestMessage());
    }


}
