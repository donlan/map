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

import com.tencent.TIMMessage;

import java.util.List;

import dong.lan.mapeye.model.message.IMessage;


/**
 * Created by 梁桂栋 on 16-11-18 ： 下午8:23.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public final class ChatContract {

    private ChatContract(){}

    public interface View{

        void checkIntent();

        void setUpAdapter(List<IMessage> messages);

        void appendMessageToFirst(IMessage message);

        void appendMessageToFirst(List<IMessage> messages);

        void appendMessage(List<IMessage> timMessages);

        void appendMessage(IMessage timMessage);

        void stopRefresh();
    }

    public interface Presenter extends BaseLifeCyclePresenter{

        void initChatList(String username);

        void loadMoreMessage();

        void sendMessage(String messageText);

    }
}
