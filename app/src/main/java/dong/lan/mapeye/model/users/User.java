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

import android.text.TextUtils;

import cn.jpush.im.android.api.model.UserInfo;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by 梁桂栋 on 16-11-6 ： 下午4:00.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class User extends RealmObject implements IUserInfo{

    @PrimaryKey
    private String identifier;
    private String username;
    private int sex;
    private String headAvatar;
    private String nickname;
    private String remark;
    private String phone;
    private String bmobObjId;

    public User() {
    }

    public User(UserInfo userInfo) {
        identifier = userInfo.getUserName();
        username = userInfo.getUserName();
        sex = userInfo.getGender() == UserInfo.Gender.male ? 1 :0;
        headAvatar = userInfo.getAvatar();
        nickname = userInfo.getNickname();
        remark = userInfo.getNotename();
        phone = userInfo.getSignature();
    }

    public static String getUserDescriber(UserInfo userProfile) {
        if (userProfile == null) {
            return "未知";
        }
        String identifier = userProfile.getUserName();
        if (userProfile.getNickname() != null && !userProfile.getNickname().equals(""))
            return identifier + "(" + userProfile.getNickname() + ")";

        if (userProfile.getNotename() != null && !userProfile.getNotename().equals(""))
            return identifier + "(" + userProfile.getNotename() + ")";

        return identifier;
    }

    public static String getUserDescriber(IUserInfo userProfile) {
        if (userProfile == null) {
            return "未知";
        }
        String identifier = userProfile.username();
        if (userProfile.nickname() != null && !userProfile.nickname().equals(""))
            return identifier + "(" + userProfile.nickname() + ")";

        if (userProfile.remark() != null && !userProfile.remark().equals(""))
            return identifier + "(" + userProfile.remark() + ")";

        return identifier;
    }

    public String getBmobObjId() {
        return bmobObjId;
    }

    public void setBmobObjId(String bmobObjId) {
        this.bmobObjId = bmobObjId;
    }

    public String avatar() {
        return headAvatar;
    }

    public void setHeadAvatar(String headAvatar) {
        this.headAvatar = headAvatar;
    }

    public String nickname() {
        return nickname;
    }

    @Override
    public boolean isMe() {
        return false;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String remark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int sex() {
        return sex;
    }

    @Override
    public String phone() {
        return phone;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String identifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String username() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String displayName(){
        if(!TextUtils.isEmpty(remark))
            return remark;
        if(!TextUtils.isEmpty(nickname))
            return nickname;
        if(!TextUtils.isEmpty(username))
            return username;
        return identifier;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phone = phoneNumber;
    }
}
