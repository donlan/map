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
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.model.Message;
import dong.lan.mapeye.R;
import dong.lan.mapeye.adapter.BaseAdapter;
import dong.lan.mapeye.adapter.BaseHolder;
import dong.lan.mapeye.common.MonitorManager;
import dong.lan.mapeye.contracts.ConversationContract;
import dong.lan.mapeye.model.conversation.IConversation;
import dong.lan.mapeye.presenter.ConversationPresenter;
import dong.lan.mapeye.utils.DateUtils;
import dong.lan.mapeye.views.customsView.LabelTextView;
import dong.lan.mapeye.views.customsView.RecycleViewDivider;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by 梁桂栋 on 16-11-18 ： 下午6:43.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class ConversationFragment extends BaseFragment implements ConversationContract.View {

    private static final String TAG = "ConversationFragment";
    private Subscription subscription;

    public static ConversationFragment newInstance(String tittle) {
        ConversationFragment fragment = new ConversationFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TITTLE_TAG, tittle);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.conversationList)
    RecyclerView conversationList;


    private View cacheView;
    private Adapter adapter;
    private ConversationPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (cacheView == null) {
            cacheView = inflater.inflate(R.layout.fragment_conversation, container, false);
            ButterKnife.bind(this, cacheView);
            conversationList.setLayoutManager(new LinearLayoutManager(getContext()));
            conversationList.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.VERTICAL, 4.0f));
            presenter = new ConversationPresenter(this);
            presenter.loadAllConversations();

            subscription = MonitorManager.instance()
                    .subscriber(Message.class)
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Message>() {
                        @Override
                        public void call(Message message) {
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
        return cacheView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null)
            subscription.unsubscribe();
    }

    @Override
    public void setConversionAdapter(List<IConversation> conversations) {
        if (adapter == null) {
            adapter = new Adapter(conversations);
            conversationList.setAdapter(adapter);
        }
    }

    @Override
    public void refreshConversation(int position) {
        if (position >= 0 && position < adapter.getItemCount())
            adapter.notifyItemChanged(position);
    }

    @Override
    public void removeConversation(int layoutPosition) {
        if (adapter != null)
            adapter.notifyItemRemoved(layoutPosition);
    }


    class Adapter extends BaseAdapter<IConversation> {

        Adapter(List<IConversation> data) {
            super(data);
        }

        @Override
        public BaseHolder bindHolder(ViewGroup parent, int type) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_contact, null);
            return new ViewHolder(view);
        }

        @Override
        public void bindData(IConversation value, BaseHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            if (value.unreadCount() > 0) {
                viewHolder.count.setVisibility(View.VISIBLE);
                viewHolder.count.setText(String.valueOf(value.unreadCount()));
            } else {
                viewHolder.count.setVisibility(View.GONE);
            }
            viewHolder.msg.setText(value.displayContent());
            viewHolder.username.setText(value.displayName());
            int gender = value.targetUserGender();
            if (gender != 1) {
                viewHolder.head.setImageResource(R.drawable.girl);
            } else {
                viewHolder.head.setImageResource(R.drawable.boy);
            }
            viewHolder.time.setText(DateUtils.getDescriptionTimeFromTimestamp(value.timestamp()));
        }
    }

    class ViewHolder extends BaseHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

        @BindView(R.id.itemContactHead)
        ImageView head;
        @BindView(R.id.itemContactName)
        TextView username;
        @BindView(R.id.itemContactMsg)
        TextView msg;
        @BindView(R.id.itemContactCount)
        LabelTextView count;
        @BindView(R.id.time)
        TextView time;

        @Override
        protected void bindView(View view) {
            ButterKnife.bind(this, view);
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IConversation conversation = adapter.getValue(getLayoutPosition());
                    conversation.resetReadCount();
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra(ChatActivity.CHAT_PEER, conversation.chatPeer());
                    intent.putExtra(ChatActivity.CHAT_TITTLE,conversation.displayName());
                    intent.putExtra(ChatActivity.CHAT_POSITION, getLayoutPosition());
                    intent.putExtra(ChatActivity.CHAT_TYPE, 0);
                    startActivityForResult(intent, 100);
                }
            });
            this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    presenter.deleteConversation(getLayoutPosition());
                    return false;
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode != -1) {
            adapter.notifyItemChanged(resultCode);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}
