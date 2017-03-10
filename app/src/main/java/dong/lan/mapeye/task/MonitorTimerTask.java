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

package dong.lan.mapeye.task;

import android.app.IntentService;
import android.content.Intent;

import dong.lan.mapeye.common.Config;
import dong.lan.mapeye.common.MonitorManager;
import dong.lan.mapeye.model.MonitorTimer;
import dong.lan.mapeye.model.Record;
import dong.lan.mapeye.model.users.Contact;
import io.realm.Realm;

/**
 * Created by 梁桂栋 on 17-1-8 ： 下午8:09.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */
public class MonitorTimerTask extends IntentService {

    Realm realm;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MonitorTimerTask(String name) {
        super(name);
        realm = Realm.getDefaultInstance();
    }

    public MonitorTimerTask() {
        super("MonitorTimerTask");
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //开始true，结束false
        boolean isStart = intent.getBooleanExtra(Config.TIMER_STATUS, false);
        long timerId = intent.getLongExtra(Config.TIMER_TASK_ID, 0);
        if (timerId == 0) {
            stopSelf();
            return;
        }
        String recordId = intent.getStringExtra(Config.KEY_RECORD_ID);
        String identifier = intent.getStringExtra(Config.KEY_IDENTIFIER);
        if (isStart) {
            Record record = realm.where(Record.class).equalTo("id",recordId).findFirst();
            Contact contact = realm.where(Contact.class)
                    .equalTo("id", Contact.createId(recordId, identifier)).findFirst();
            contact.setStatus(Contact.STATUS_WAITING);
            MonitorManager.instance().sendStartMonitorMsg(contact,record,realm);
        } else {
            if (realm.isInTransaction())
                realm.cancelTransaction();
            realm.beginTransaction();
            MonitorTimer timer = realm.where(MonitorTimer.class)
                    .equalTo("createTime", timerId).findFirst();
            Contact contact = realm.where(Contact.class)
                    .equalTo("id", Contact.createId(recordId, identifier)).findFirst();
            if (contact.getStatus() == Contact.STATUS_MONITORING
                    || contact.getStatus() == Contact.STATUS_WAITING) {
                contact.setStatus(Contact.STATUS_NONE);
                MonitorManager.instance().sendStopMonitorMsg(contact,recordId,realm);
            }
            timer.setOpen(false);
            realm.commitTransaction();
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null)
            realm.close();
        realm = null;
    }
}
