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
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dong.lan.mapeye.R;
import dong.lan.mapeye.model.Affair;
import dong.lan.mapeye.utils.DateUtils;
import dong.lan.mapeye.views.customsView.LabelTextView;
import io.realm.RealmResults;

/**
 * Created by 梁桂栋 on 16-12-9 ： 上午12:13.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class AffairsAdapter extends BaseRealmAdapter<Affair> {
    public AffairsAdapter(RealmResults<Affair> data) {
        super(data);
    }

    @Override
    public BaseHolder bindHolder(ViewGroup parent, int type) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_affair, null);
        return new Holder(view);
    }

    @Override
    public void bindData(Affair value, BaseHolder baseHolder, int position) {
        Holder holder = (Holder) baseHolder;
        holder.type.setText(value.getTypeString());
        holder.status.setText(value.getStatusString());
        holder.fromUser.setText(value.getFromUser());
        holder.extras.setText(value.getExtras());
        holder.content.setText(value.getContent());
        holder.time.setText(DateUtils.getChatTime(value.getCreatedTime()));
    }

     class Holder extends BaseHolder {

        @BindView(R.id.item_affair_type)
        LabelTextView type;
        @BindView(R.id.item_affair_extras)
        TextView extras;
        @BindView(R.id.item_affair_content)
        TextView content;
        @BindView(R.id.item_affair_fromUser)
        TextView fromUser;
        @BindView(R.id.item_affair_status)
        LabelTextView status;
        @BindView(R.id.item_affair_time)
        TextView time;

        public Holder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindView(View view) {
            ButterKnife.bind(this, view);
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AffairsAdapter.this.onClick(
                            OnClickListener.TYPE_NORMAL,
                            getLayoutPosition(),
                            AffairsAdapter.this.getValue(getLayoutPosition()));
                }
            });
        }
    }
}
