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

package dong.lan.mapeye.model.users;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by 梁桂栋 on 16-11-21 ： 上午12:15.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class Contacts extends RealmObject {

    private User owner;
    private RealmList<User> contacts;

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public RealmList<User> getContacts() {
        return contacts;
    }

    public void setContacts(RealmList<User> contacts) {
        this.contacts = contacts;
    }
}
