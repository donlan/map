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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.jpush.im.android.api.JMessageClient;
import dong.lan.mapeye.R;
import dong.lan.mapeye.adapter.ChatAdapter;
import dong.lan.mapeye.contracts.ChatContract;
import dong.lan.mapeye.model.message.IMessage;
import dong.lan.mapeye.presenter.ChatPresenter;

/**
 * Created by 梁桂栋 on 16-11-18 ： 下午8:25.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class ChatActivity extends BaseActivity implements ChatContract.View {

    private static final String TAG = "ChatActivity";
    public static final String CHAT_TYPE = "chatType";
    public static final String CHAT_TITTLE = "chat_tittle";
    @BindView(R.id.chatList)
    RecyclerView chatList;
    @BindView(R.id.chatRefresher)
    SwipeRefreshLayout chatRefresher;
    @BindView(R.id.chat_input)
    EditText chatInput;

    @OnTextChanged(R.id.chat_input)
    void chatInputChanged() {
        if (chatInput.getText().length() <= 0 && chatSend.isEnabled())
            chatSend.setEnabled(false);
        else if (chatInput.getText().length() > 0 && !chatSend.isEnabled())
            chatSend.setEnabled(true);
    }

    @OnClick(R.id.chat_back)
    void back() {
        setResult(position);
        finish();
    }

    @BindView(R.id.chat_send)
    Button chatSend;
    @BindView(R.id.chat_tittle)
    TextView tittle;

    @OnClick(R.id.chat_send)
    void sendMessage() {
        presenter.sendMessage(chatInput.getText().toString());
    }

    private ChatPresenter presenter;

    private ChatAdapter adapter;
    private int position = -1;

    public static final String CHAT_PEER = "chatUsername";
    public static final String CHAT_POSITION = "chatPosition";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        bindView(this);
        presenter = new ChatPresenter(this);
        checkIntent();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(position);
        if (presenter != null)
            presenter.onDestroy();

        JMessageClient.exitConversation();
    }


    @Override
    public void checkIntent() {
        int type = getIntent().getIntExtra(CHAT_TYPE, 0);
        String chatTittle = getIntent().getStringExtra(CHAT_TITTLE);
        String identifier = getIntent().getStringExtra(CHAT_PEER);
        if (identifier.equals("")) {
            show("没有用户信息");
        } else {
            JMessageClient.enterSingleConversation(identifier);
            position = getIntent().getIntExtra(CHAT_POSITION, -1);
            tittle.setText(chatTittle);
            adapter = new ChatAdapter(new ArrayList<IMessage>());
            chatList.setLayoutManager(new GridLayoutManager(this, 1));
            chatList.setAdapter(adapter);
            presenter.initChatList(identifier);
            chatRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    presenter.loadMoreMessage();
                }
            });
        }
    }

    @Override
    public void stopRefresh() {
        chatRefresher.setRefreshing(false);
    }

    @Override
    public void setUpAdapter(List<IMessage> messages) {
        adapter.appendToFirst(messages);
        chatList.smoothScrollToPosition(adapter.getItemCount());
    }

    @Override
    public void appendMessageToFirst(IMessage message) {
        adapter.appendMessage(message);
        chatInput.setText("");
        chatList.smoothScrollToPosition(adapter.getItemCount());
    }

    @Override
    public void appendMessageToFirst(List<IMessage> messages) {
        chatRefresher.setRefreshing(false);
        if (messages == null || messages.isEmpty())
            show("没有更多历史消息啦");
        else
            adapter.appendToFirst(messages);
    }


    @Override
    public void appendMessage(List<IMessage> timMessages) {
        for (IMessage timMessage : timMessages)
            appendMessage(timMessage);
    }

    @Override
    public void appendMessage(IMessage timMessage) {
        adapter.appendMessage(timMessage);
        chatList.smoothScrollToPosition(adapter.getItemCount() - 1);
        chatInput.setText("");
    }
}
