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

import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import dong.lan.library.LabelTextView;
import dong.lan.mapeye.R;
import dong.lan.mapeye.common.Config;
import dong.lan.mapeye.common.MonitorManager;
import dong.lan.mapeye.model.MonitorTimer;
import dong.lan.mapeye.model.Record;
import dong.lan.mapeye.model.users.Contact;
import dong.lan.mapeye.utils.DateUtils;
import dong.lan.mapeye.views.BaseActivity;
import io.realm.Realm;

public class TimerHandlerActivity extends BaseActivity {

    @BindView(R.id.timer_start_info)
    LabelTextView startInfo;
    @BindView(R.id.timer_end_info)
    LabelTextView endInfo;
    @BindView(R.id.timer_other_info)
    TextView otherInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_handler);
        bindView(this);
        init();
    }

    private void init() {
        final boolean status = getIntent().getBooleanExtra(Config.TIMER_STATUS, false);
        final long createTime = getIntent().getLongExtra(Config.TIMER_TASK_ID, 0);
        final String recordId = getIntent().getStringExtra(Config.KEY_RECORD_ID);
        final String identifier = getIntent().getStringExtra(Config.KEY_IDENTIFIER);
        Logger.d(status + "," + createTime + "," + recordId + "," + identifier);
        if (createTime == 0) {
            finish();
            return;
        }

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator.hasVibrator())
            vibrator.cancel();
        vibrator.vibrate(2000);


        Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                MonitorTimer timer = realm.where(MonitorTimer.class)
                        .equalTo("createTime", createTime).findFirst();
                Contact contact = realm.where(Contact.class)
                        .equalTo("id", Contact.createId(recordId, identifier)).findFirst();
                Record record = realm.where(Record.class).equalTo("id", recordId).findFirst();
                if (timer != null && record != null && contact != null) {
                    MonitorTimer copyTimer = realm.copyFromRealm(timer);
                    Record copyRecord = realm.copyFromRealm(record);
                    Contact copyContact = realm.copyFromRealm(contact);
                    initView(copyTimer, copyRecord, copyContact);
                    if (status) {
                        contact.setStatus(Contact.STATUS_WAITING);
                        MonitorManager.instance().sendStartMonitorMsg(contact, record, realm);
                    } else {
                        if (contact.getStatus() == Contact.STATUS_MONITORING
                                || contact.getStatus() == Contact.STATUS_WAITING) {
                            contact.setStatus(Contact.STATUS_NONE);
                            MonitorManager.instance().sendStopMonitorMsg(contact, recordId, realm);
                        }
                    }
                    timer.setOpen(status);
                } else {
                    initView(null, null, null);
                }
            }
        });

    }

    private void initView(final MonitorTimer timer, final Record record, final Contact contact) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (timer == null) {
                    toast("定时任务失效");
                    finish();
                    return;
                }
                startInfo.setText(DateUtils.getTime(timer.getStartTime(), "开始时间: yyyy.MM.dd HH:mm"));
                endInfo.setText(DateUtils.getTime(timer.getEndTime(), "结束时间: yyyy.MM.dd HH:mm"));
                otherInfo.setText("此定时任务归属于:" + record.recordTittleInfo() + " \n 监听人是: " + contact.getUser().displayName());
            }
        });
    }
}
