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

package dong.lan.mapeye.views;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import dong.lan.mapeye.R;
import dong.lan.mapeye.common.Config;
import dong.lan.mapeye.model.MonitorTimer;
import dong.lan.mapeye.model.Record;
import dong.lan.mapeye.model.users.User;
import dong.lan.mapeye.utils.DateUtils;
import dong.lan.mapeye.views.customsView.DateTimePicker;
import io.realm.Realm;

public class AddTimerTaskActivity extends BaseActivity {


    @BindView(R.id.bar_center)
    TextView tittle;
    @BindView(R.id.timer_desc)
    EditText descInput;
    private long startTime;
    private long endTime;
    private boolean isRepeat;
    private boolean isContinue;
    private String recordId;
    private String identifier;

    @OnClick(R.id.bar_left)
    void back() {
        finish();
    }

    @OnCheckedChanged(R.id.timer_repeat_cb)
    void repeatCheck(CheckBox checkBox, boolean isCheck) {
        isRepeat = isCheck;
    }

    @OnCheckedChanged(R.id.timer_continue_cb)
    void continueCheck(CheckBox checkBox, boolean isCheck) {
        isContinue = isCheck;
    }

    @OnClick(R.id.timer_task_save_lvt)
    void done() {


        //不能小于一分钟
        if (endTime - startTime < 60000) {
            toast("间隔不能小于一分钟");
            return;
        }

        if (TextUtils.isEmpty(descInput.getText().toString())) {
            toast("定时任务描述不能为空");
            return;
        }

        alert("添加中...");
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        MonitorTimer task = realm.createObject(MonitorTimer.class);
        task.setUser(realm.where(User.class).equalTo("identifier", identifier).findFirst());
        task.setCreateTime(System.currentTimeMillis());
        task.setContinue(isContinue);
        task.setEndTime(endTime);
        task.setOpen(false);
        task.setDesc(descInput.getText().toString());
        task.setRepeat(isRepeat);
        task.setStartTime(startTime);
        task.setRecord(realm.where(Record.class).equalTo("id", recordId).findFirst());
        realm.commitTransaction();
        dismiss();
        toast("添加完成");
        setResult(200);
        finish();

    }

    @OnClick(R.id.timer_start_time)
    void setTimerStartTime(final TextView textView) {
        new DateTimePicker(this, new DateTimePicker.CallBack() {
            @Override
            public void onClose(long time) {
                startTime = time;
                textView.setText("开始：" + DateUtils.getTime(time, "yyyy-MM-dd HH:mm"));
            }
        }).show();

    }

    @OnClick(R.id.timer_end_time)
    void setTimerEndTime(final TextView textView) {
        new DateTimePicker(this, new DateTimePicker.CallBack() {
            @Override
            public void onClose(long time) {
                endTime = time;
                textView.setText("结束：" + DateUtils.getTime(time, "yyyy-MM-dd HH:mm"));
            }
        }).show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_timer_task);
        bindView(this);
        checkIntent();
    }

    private void checkIntent() {
        recordId = getIntent().getStringExtra(Config.KEY_RECORD_ID);
        identifier = getIntent().getStringExtra(Config.KEY_IDENTIFIER);

        if (TextUtils.isEmpty(recordId)) {
            toast("缺少记录id");
            finish();
        }
    }

}
