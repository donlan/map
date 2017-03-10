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

import com.tencent.TIMUserProfile;

import io.realm.RealmObject;

/**
 * Created by 梁桂栋 on 16-11-6 ： 下午4:00.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class User extends RealmObject {

    private String identifier;
    private String username;
    private int sex;
    private String headAvatar;
    private String nickname;
    private String remark;

    public String getHeadAvatar() {
        return headAvatar;
    }

    public void setHeadAvatar(String headAvatar) {
        this.headAvatar = headAvatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getUsername() {
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

    public static String getUserDescriber(TIMUserProfile userProfile) {
        if(userProfile == null) {
            return "未知";
        }
        String identifier = userProfile.getIdentifier().substring(3);
        if (userProfile.getRemark() != null && !userProfile.getRemark().equals(""))
            return identifier+"("+userProfile.getRemark()+")";

        if (userProfile.getNickName() != null && !userProfile.getNickName().equals(""))
            return identifier +"("+userProfile.getNickName()+")";

        return identifier;
    }

}
