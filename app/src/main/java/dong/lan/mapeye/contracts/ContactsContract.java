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

import dong.lan.mapeye.model.users.User;

/**
 * Created by 梁桂栋 on 16-11-15 ： 下午2:00.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: 联系人契约类
 */

public final class ContactsContract {

    private ContactsContract(){}

    public interface View {

        //初始化联系人的适配器
        void initAdapter();

        //刷新
        void refreshAdapter(int position);
    }

    public interface Presenter {

        //加载用户的所有联系人
        void loadAllContacts();

        //联系人总数
        int getContactCount();

        //获取联系人列表指定位置出的Contact
        User getContact(int position);


    }
}
