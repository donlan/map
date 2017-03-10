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

package dong.lan.mapeye.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dong.lan.mapeye.R;
import dong.lan.mapeye.common.UserManager;
import dong.lan.mapeye.model.MonitorRecode;
import dong.lan.mapeye.model.Record;
import dong.lan.mapeye.utils.DateUtils;
import io.realm.RealmResults;

/**
 * Created by 梁桂栋 on 16-12-24 ： 上午12:06.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class MonitorRecordAdapter extends BaseRealmAdapter<MonitorRecode> {
    public MonitorRecordAdapter(RealmResults<MonitorRecode> data) {
        super(data);
    }

    @Override
    public BaseHolder bindHolder(ViewGroup parent, int type) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_monitor_record, null);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(MonitorRecode value, BaseHolder holder, int position) {
        if (value != null) {
            Record record = value.getRecord();
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.recordTittle.setText(record.getLabel());
            viewHolder.username.setText(UserManager.instance().getUserDisplayName(value.getMonitoredUser()));
            viewHolder.monitorInfo.setText("");
            viewHolder.monitorInfo.append("监听位置数：");
            viewHolder.monitorInfo.append(value.getLocations()==null?"0":value.getLocations().size()+"");
            viewHolder.monitorInfo.append("\n");
            viewHolder.monitorInfo.append("创建时间：");
            viewHolder.monitorInfo.append(DateUtils.getTime(value.getCreateTime(), "yyyy年MM月dd日 HH:mm:ss"));
            viewHolder.monitorInfo.append("\n");
            viewHolder.monitorInfo.append("结束时间：");
            viewHolder.monitorInfo.append(DateUtils.getTime(value.getEndTime(), "yyyy年MM月dd日 HH:mm:ss"));
            viewHolder.monitorInfo.append("\n");
        }
    }

     static class ViewHolder extends BaseHolder {

        @BindView(R.id.userAvatar)
        ImageView avatar;
        @BindView(R.id.recordTittle)
        TextView recordTittle;
        @BindView(R.id.username)
        TextView username;
        @BindView(R.id.monitorInfo)
        TextView monitorInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void bindView(View view) {
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
