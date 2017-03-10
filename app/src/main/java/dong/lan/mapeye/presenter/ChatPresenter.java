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

package dong.lan.mapeye.presenter;

import com.tencent.TIMManager;
import com.tencent.TIMMessage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import dong.lan.mapeye.contracts.ChatContract;
import dong.lan.mapeye.model.conversation.IConversation;
import dong.lan.mapeye.model.conversation.JConversation;
import dong.lan.mapeye.model.message.IMessage;
import dong.lan.mapeye.views.ChatActivity;

/**
 * Created by 梁桂栋 on 16-11-18 ： 下午8:24.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class ChatPresenter implements ChatContract.Presenter {
    private ChatActivity view;
    private int offset = 0;
    private IConversation conversation;

    public ChatPresenter(ChatActivity view) {
        this.view = view;
    }


    @Override
    public void initChatList(String identifier) {
        conversation = new JConversation(JMessageClient.getSingleConversation(identifier));
        if (conversation == null) {
            view.show("没有初始化好会话配置");
        } else {
            List<IMessage> messages = conversation.loadMessage(offset);
            offset = messages.isEmpty() ? -1 : offset + messages.size();
            view.setUpAdapter(messages);
        }
    }

    @Override
    public void loadMoreMessage() {

        if (offset == -1) {
            view.toast("没有更多消息了");
            view.stopRefresh();
        } else {
            List<IMessage> messages = conversation.loadMessage(offset);
            offset = messages.isEmpty() ? -1 : offset + messages.size();
            view.appendMessageToFirst(messages);
        }
    }

    @Override
    public void sendMessage(String messageText) {

        final IMessage message = conversation.createTextMessage(messageText);
        conversation.sendMessage(message, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    view.appendMessage(message);
                } else {
                    view.toast(i + "," + s);
                }
            }
        });
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }
}
