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

package dong.lan.mapeye.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import dong.lan.mapeye.R;
import dong.lan.mapeye.adapter.MonitorRecordAdapter;
import dong.lan.mapeye.adapter.RecycleViewClickListener;
import dong.lan.mapeye.contracts.MonitorRecordContact;
import dong.lan.mapeye.model.MonitorRecode;
import dong.lan.mapeye.presenter.MonitorRecordPresenter;
import dong.lan.mapeye.views.customsView.RecycleViewDivider;
import io.realm.RealmResults;

public class MonitorRecordActivity extends BaseActivity implements MonitorRecordContact.View {


    public static final String IDENTIFIER = "identifier";

    @OnClick(R.id.bar_left)
    void back() {
        finish();
    }

    @BindView(R.id.bar_center)
    TextView tittle;
    @BindView(R.id.monitor_record_list)
    RecyclerView monitorRecordList;

    private MonitorRecordPresenter presenter;
    private MonitorRecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_record);
        bindView(this);
        checkIntent();
    }

    @Override
    public void checkIntent() {
        if (getIntent().hasExtra(IDENTIFIER)) {
            monitorRecordList.setLayoutManager(new GridLayoutManager(this, 1));
            monitorRecordList.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL, 10.0f));
            String username = getIntent().getStringExtra(IDENTIFIER);
            tittle.setText(username);
            presenter = new MonitorRecordPresenter(this);
            presenter.initList(username);
        } else {
            toast("没有用户数据");
        }
    }

    @Override
    public void initAdapter(RealmResults<MonitorRecode> results) {
        adapter = new MonitorRecordAdapter(results);
        monitorRecordList.setAdapter(adapter);
        monitorRecordList.addOnItemTouchListener(new RecycleViewClickListener(monitorRecordList, new RecycleViewClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                MonitorRecode monitorRecode = adapter.getValue(position);
                Intent intent = new Intent(MonitorRecordActivity.this, MonitorRecordDetailActivity.class);
                intent.putExtra(MonitorRecordDetailActivity.KEY_MONITOR_RECORD_ID, monitorRecode.getId());
                intent.putExtra(MonitorRecordDetailActivity.KEY_POSITION,position);
                startActivityForResult(intent,200);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != -1) {
            adapter.notifyItemRemoved(resultCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
