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

package dong.lan.mapeye.task;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import dong.lan.mapeye.common.JMCenter;
import dong.lan.mapeye.common.MonitorManager;
import dong.lan.mapeye.events.MonitorTaskEvent;
import dong.lan.mapeye.model.message.CMDMessage;
import dong.lan.mapeye.model.users.Contact;
import io.realm.Realm;

/**
 * Created by 梁桂栋 on 16-12-20 ： 下午10:25.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class MonitorStatusTask extends IntentService {

    public static final String KEY_MONITOR_ID = "monitor_id";
    public static final String KEY_MONITOR_NAME = "monitor_name";
    public static final String KEY_RECORD_ID = "record_id";
    private static final int LIMIT = 10;
    private int tryCount = 0;


    public MonitorStatusTask() {
        super("monitorTask");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String id = intent.getStringExtra(KEY_MONITOR_ID);
        final String recordId = intent.getStringExtra(KEY_RECORD_ID);
        Logger.d(recordId);
        if (!TextUtils.isEmpty(id)) {
            long time = System.currentTimeMillis();
            Realm realm = Realm.getDefaultInstance();
            while (tryCount < LIMIT) {
                if (System.currentTimeMillis() - time >= 6000) {
                    time = System.currentTimeMillis();
                    realm.beginTransaction();
                    Contact contact = realm.where(Contact.class).equalTo("id", id).findFirst();
                    if (contact != null) {
                        tryCount++;
                        if (contact.getStatus() == Contact.STATUS_MONITORING
                                || contact.getStatus() == Contact.STATUS_NONE) {
                            break;
                        } else if (tryCount == LIMIT) {
                            contact.setStatus(Contact.STATUS_OFFLINE);
                            MonitorManager.instance().post(new MonitorTaskEvent(
                                    contact.getId(),
                                    recordId,
                                    MonitorTaskEvent.TYPE_UPDATE_STATE
                            ));

                            Message message = JMCenter.createMessage(CMDMessage.CMD_MONITOR_STOP,
                                    contact.getUser().identifier(),
                                    "停止定位");
                            JMCenter.sendMessage(message, new BasicCallback() {
                                @Override
                                public void gotResult(int i, String s) {
                                    Logger.d(i+","+s);
                                }
                            });
                            realm.close();
                            break;
                        }
                    } else {
                        realm.close();
                        break;
                    }
                    realm.commitTransaction();
                }
            }
            if (realm != null && !realm.isClosed())
                realm.close();
            realm = null;
        }
    }


}
