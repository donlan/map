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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dong.lan.library.LabelTextView;
import dong.lan.mapeye.R;
import dong.lan.mapeye.adapter.BaseHolder;
import dong.lan.mapeye.adapter.BaseRealmAdapter;
import dong.lan.mapeye.common.Config;
import dong.lan.mapeye.common.MonitorManager;
import dong.lan.mapeye.model.MonitorTimer;
import dong.lan.mapeye.utils.DateUtils;
import dong.lan.mapeye.views.customsView.RecycleViewDivider;
import dong.lan.mapeye.views.customsView.ToggleButton;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MonitorTimerTaskActivity extends BaseActivity {

    @BindView(R.id.add_new_timer_task)
    FloatingActionButton addTaskfab;
    @BindView(R.id.timer_task_list)
    RecyclerView timerTaskList;

    @BindView(R.id.bar_right)
    TextView refresh;
    @BindView(R.id.bar_center)
    TextView tittle;
    private RealmResults<MonitorTimer> timers;
    private Adapter adapter;
    private Realm realm;
    private String recordId;
    private String identifier;

    @OnClick(R.id.bar_left)
    void back() {
        finish();
    }

    @OnClick(R.id.bar_right)
    void refreshTimers() {
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.add_new_timer_task)
    void addNewTimerTask() {
        Intent intent = new Intent(this, AddTimerTaskActivity.class);
        intent.putExtra(Config.KEY_IDENTIFIER, identifier);
        intent.putExtra(Config.KEY_RECORD_ID, recordId);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_timer_task);
        bindView(this);
        checkIntent();
    }

    private void checkIntent() {
        refresh.setText("刷新");
        recordId = getIntent().getStringExtra(Config.KEY_RECORD_ID);
        identifier = getIntent().getStringExtra(Config.KEY_IDENTIFIER);
        tittle.setText(identifier);
        if (TextUtils.isEmpty(recordId)) {
            addTaskfab.setVisibility(View.GONE);
        }
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        timers = realm.where(MonitorTimer.class)
                .equalTo("user.identifier", identifier)
                .findAllSorted("createTime", Sort.DESCENDING);
        realm.commitTransaction();
        if (timers.isEmpty())
            toast("无定时任务");
        else {
            tittle.setText(timers.get(0).getUser().displayName());
        }
        timerTaskList.setLayoutManager(new GridLayoutManager(this, 1));
        timerTaskList.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL, 10.0f));
        adapter = new Adapter(timers);
        timerTaskList.setAdapter(adapter);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        realm = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == 200) {
            adapter.notifyDataSetChanged();
        }
    }

    private class Adapter extends BaseRealmAdapter<MonitorTimer> {

        Adapter(RealmResults<MonitorTimer> data) {
            super(data);
        }

        @Override
        public BaseHolder bindHolder(ViewGroup parent, int type) {
            View view = LayoutInflater.from(MonitorTimerTaskActivity.this)
                    .inflate(R.layout.item_timer_task, null);
            return new ViewHolder(view);
        }

        @Override
        public void bindData(MonitorTimer value, BaseHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.timerRecordInfo.setText(value.getRecord().getLabel());
            viewHolder.timerRepeat.setText(value.tagString());

            if (value.getEndTime() < System.currentTimeMillis()) {
                viewHolder.timerSwitcher.setChecked(false);
                viewHolder.timerSwitcher.setVisibility(View.GONE);
            }
            viewHolder.info.setText(value.getDesc());
            viewHolder.timeInfo.setText(DateUtils.getTime(value.getCreateTime(), "创建时间: yyyy.MM.dd HH:mm"));
            viewHolder.timerSwitcher.setChecked(value.isOpen());
            viewHolder.timerStartTime.setText(DateUtils.getTime(value.getStartTime(), "MM月dd日 HH:mm"));
            viewHolder.timerEndTime.setText(DateUtils.getTime(value.getEndTime(), "MM月dd日 HH:mm"));
        }
    }

    class ViewHolder extends BaseHolder {

        @BindView(R.id.timer_info)
        TextView info;
        @BindView(R.id.timer_time_info)
        TextView timeInfo;
        @BindView(R.id.timer_record_info)
        TextView timerRecordInfo;
        @BindView(R.id.timer_repeat)
        LabelTextView timerRepeat;
        @BindView(R.id.timer_start_time)
        TextView timerStartTime;
        @BindView(R.id.timer_end_time)
        TextView timerEndTime;
        @BindView(R.id.timer_switcher)
        ToggleButton timerSwitcher;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            timerSwitcher.setOnCheckChangeListener(new ToggleButton.OnCheckChangeListener() {
                @Override
                public void onChanged(boolean isChecked) {
                    MonitorManager.instance().handleTimerStatus(MonitorTimerTaskActivity.this,
                            timers.get(getLayoutPosition()), isChecked, realm);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(v.getContext())
                            .setMessage("确定删除此定时任务?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MonitorManager.instance()
                                            .deleteMonitor(timers.get(getLayoutPosition()), realm);
                                    adapter.notifyDataSetChanged();
                                }
                            }).setNegativeButton("取消",null)
                            .show();
                    return true;
                }
            });
        }

        @Override
        protected void bindView(View view) {

        }
    }


}
