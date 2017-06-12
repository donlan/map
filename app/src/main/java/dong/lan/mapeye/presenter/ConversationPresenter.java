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


import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import dong.lan.mapeye.contracts.ConversationContract;
import dong.lan.mapeye.model.conversation.IConversation;
import dong.lan.mapeye.model.conversation.JConversation;
import dong.lan.mapeye.views.ConversationFragment;

/**
 * Created by 梁桂栋 on 16-11-18 ： 下午6:52.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class ConversationPresenter implements ConversationContract.Presenter {

    private ConversationFragment view;
    private List<IConversation> conversations;

    public ConversationPresenter(ConversationFragment view) {
        this.view = view;
    }


    @Override
    public void loadAllConversations() {

        List<Conversation> conversationList = JMessageClient.getConversationList();
        conversations = new ArrayList<>();
        if (conversationList == null || conversationList.isEmpty()) {
            view.toast("没有会话记录");
        } else {
            for (Conversation c :
                    conversationList) {
                if (c.getType() == ConversationType.single)
                    conversations.add(new JConversation(c));
            }
            view.setConversionAdapter(conversations);
        }
    }

    @Override
    public void deleteConversation(int layoutPosition) {
        IConversation c = conversations.get(layoutPosition);
        JMessageClient.deleteSingleConversation(c.chatPeer());
        conversations.remove(layoutPosition);
        view.removeConversation(layoutPosition);
    }

}
