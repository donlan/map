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

package dong.lan.mapeye.model.message;


import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.model.Message;

/**
 * Created by 梁桂栋 on 16-12-28 ： 下午11:04.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class JGMessage implements IMessage {

    private Message message;

    public JGMessage(Message message) {
        this.message = message;
    }


    @Override
    public Message realMessage() {
        return message;
    }

    @Override
    public long timestamp() {
        return message.getCreateTime();
    }

    @Override
    public String sender() {
        return message.getFromUser().getUserName();
    }

    @Override
    public String content() {
        ContentType type = message.getContentType();
        if (ContentType.text.equals(type)) {
            TextContent textContent = (TextContent) message.getContent();
            return textContent.getText();
        } else if (ContentType.location.equals(type))
            return "[位置信息]";
        else if (ContentType.custom.equals(type))
            return "[自定义消息]";
        else if (ContentType.unknown.equals(type))
            return "[未知消息]";
        return null;
    }

    @Override
    public String id() {
        return String.valueOf(message.getId());
    }

    @Override
    public boolean isSelf() {
        return MessageDirect.send.equals(message.getDirect());
    }

    @Override
    public Number getNumberExtra(String key) {
        return message.getContent().getNumberExtra(key);
    }

    @Override
    public String getStringExtra(String key) {
        return message.getContent().getStringExtra(key);
    }
}
