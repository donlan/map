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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dong.lan.mapeye.R;
import dong.lan.mapeye.model.message.IMessage;
import dong.lan.mapeye.utils.DateUtils;

/**
 * Created by 梁桂栋 on 16-11-22 ： 下午3:27.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private static final int TYPE_FROM = 1;
    private static final int TYPE_SEND = 2;
    private List<IMessage> messages;

    public ChatAdapter(List<IMessage> messages) {
        this.messages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_FROM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_from, null);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_send, null);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        IMessage message = messages.get(position);
        holder.text.setText(message.content());
        holder.chatTime.setText(DateUtils.getTimestampString(message.timestamp()));
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isSelf() ? TYPE_SEND : TYPE_FROM;
    }

    public void appendMessage(IMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size());
    }

    public void appendToFirst(List<IMessage> messages) {
        this.messages.addAll(0, messages);
        notifyItemRangeInserted(0, messages.size());
    }


    @Override
    public int getItemCount() {
        return messages == null ? 0 : messages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chat_head)
        ImageView head;
        @BindView(R.id.chat_text)
        TextView text;
        @BindView(R.id.chat_time)
        TextView chatTime;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
