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

import dong.lan.mapeye.model.Record;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by 梁桂栋 on 16-11-21 ： 上午12:11.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: 一个{@link Record}对应一个Group,包含该记录下所有实现位置共享绑定{@link Contact}
 */

public class Group extends RealmObject {

    @PrimaryKey
    private String groupId;               //对应极光Api的groupId
    private String description;         //描述信息
    private User owner;                 //所属与的用户
    private RealmList<Contact> members; //该记录下所有实现位置共享绑定的Contact

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public RealmList<Contact> getMembers() {
        return members;
    }

    public void setMembers(RealmList<Contact> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId=" + groupId +
                ", description='" + description + '\'' +
                ", owner=" + owner +
                ", members=" + members +
                '}';
    }
}
