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

package dong.lan.mapeye.contracts;


import java.util.List;

import dong.lan.mapeye.model.conversation.IConversation;


/**
 * Created by 梁桂栋 on 16-11-18 ： 下午6:49.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public final class ConversationContract {

    private ConversationContract() {
    }


    public interface View {
        /**
         * 将所有会话设置到列表中
         *
         * @param conversations 所有会话
         */
        void setConversionAdapter(List<IConversation> conversations);

        /**
         * 刷新列表指定位置处的会话
         *
         * @param position 列表的位置索引
         */
        void refreshConversation(int position);

        void removeConversation(int layoutPosition);
    }

    public interface Presenter {
        /**
         * 从本地加载所有会话信息
         */
        void loadAllConversations();

        void deleteConversation(int layoutPosition);
    }
}
