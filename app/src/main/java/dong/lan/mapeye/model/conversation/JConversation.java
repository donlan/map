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

package dong.lan.mapeye.model.conversation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import dong.lan.mapeye.common.JMCenter;
import dong.lan.mapeye.common.UserManager;
import dong.lan.mapeye.model.message.CMDMessage;
import dong.lan.mapeye.model.message.IMessage;
import dong.lan.mapeye.model.message.JGMessage;

/**
 * Created by 梁桂栋 on 17-1-3 ： 上午1:32.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class JConversation implements IConversation {


    private Conversation conversation;

    public JConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    @Override
    public String displayName() {
        if (conversation.getType().equals(ConversationType.single)) {
            return UserManager.instance().getUserDisplayName((UserInfo) conversation.getTargetInfo());
        } else if (conversation.getType().equals(ConversationType.group)) {
            return ((GroupInfo) conversation.getTargetInfo()).getGroupName();
        }
        return "";
    }

    @Override
    public String displayContent() {
        if (conversation == null)
            return "";
        IMessage message = new JGMessage(conversation.getLatestMessage());
        return message.content();
    }

    @Override
    public long timestamp() {
        return conversation == null ? System.currentTimeMillis() :
                conversation.getLatestMessage().getCreateTime();

    }

    @Override
    public int unreadCount() {
        return conversation == null ? 0 : conversation.getUnReadMsgCnt();
    }

    @Override
    public void resetReadCount() {
        if (conversation != null) {
            conversation.resetUnreadCount();
        }
    }

    @Override
    public int targetUserGender() {
        if (conversation.getType().equals(ConversationType.single)) {
            UserInfo userInfo = (UserInfo) conversation.getTargetInfo();
            UserInfo.Gender gender = userInfo.getGender();
            if (gender == null || gender.equals(UserInfo.Gender.female))
                return WOMAN;
            else return MAN;
        } else if (conversation.getType().equals(ConversationType.group))
            return OTHER;
        return OTHER;
    }

    @Override
    public String chatPeer() {
        if (conversation.getType().equals(ConversationType.single)) {
            return ((UserInfo) conversation.getTargetInfo()).getUserName();
        } else if (conversation.getType().equals(ConversationType.group)) {
            return ((GroupInfo) conversation.getTargetInfo()).getGroupName();
        }
        return "";
    }

    @Override
    public IMessage createTextMessage(String text) {
        Message message = conversation.createSendTextMessage(text);
        message.getContent().setNumberExtra(JMCenter.EXTRAS_CMD, CMDMessage.CMD_CHAT_TEXT);
        return new JGMessage(message);
    }


    @Override
    public void sendMessage(IMessage message, BasicCallback callback) {
        JMCenter.sendMessage((Message) message.realMessage(), callback);
    }

    @Override
    public List<IMessage> loadMessage(int offset) {
        int PAGE_SIZE = 20;
        List<Message> messageList = conversation.getMessagesFromNewest(offset, PAGE_SIZE);
        Collections.sort(messageList, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return (int) (o1.getCreateTime() - o2.getCreateTime());
            }
        });
        List<IMessage> messages = new ArrayList<>(PAGE_SIZE);
        for (int i = 0, s = messageList.size(); i < s; i++)
            messages.add(new JGMessage(messageList.get(i)));
        return messages;
    }
}
