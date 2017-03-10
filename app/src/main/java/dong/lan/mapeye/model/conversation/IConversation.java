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

import java.util.List;

import cn.jpush.im.api.BasicCallback;
import dong.lan.mapeye.model.message.IMessage;

/**
 * Created by 梁桂栋 on 17-1-3 ： 上午1:30.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public interface IConversation {

    int MAN = 0;
    int WOMAN = 1;
    int OTHER = 2;

    /**
     *
     * @return 会话显示的名称
     */
    String displayName();

    /**
     * 会话显示的内容
     * @return
     */
    String displayContent();

    /**
     * 会话的时间
     * @return
     */
    long timestamp();

    /**
     *
     * @return 未读消息计数
     */
    int unreadCount();

    /**
     * 重置未读消息计数
     */
    void resetReadCount();

    /**
     * 会发发送者的性别
     * @return
     */
    int targetUserGender();

    /**
     * 会话标示，默认是用户名，群聊会话则是群聊ID
     * @return
     */
    String chatPeer();

    /**
     *
     * @param text 文本消息
     * @return 消息实体，通过IMessage提供约束
     */
    IMessage createTextMessage(String text);

    /**
     * 发送消息
     * @param message IMessage 的具体实现类
     * @param callback 消息发送回调
     */
    void sendMessage(IMessage message, BasicCallback callback);

    /**
     * 获取本地消息
     * @param offset 加载索引
     * @return 消息列表 默认的加载列表大小时PAGE_SIZE
     */
    List<IMessage> loadMessage(int offset);
}
