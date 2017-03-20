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

package dong.lan.mapeye.bmob.bean;

import cn.bmob.v3.BmobObject;
import dong.lan.mapeye.model.users.IUserInfo;

/**
 * Created by 梁桂栋 on 17-3-18 ： 下午7:36.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class BUser extends BmobObject {
    private String identifier;
    private String username;
    private int sex;
    private String headAvatar;
    private String nickname;
    private String remark;

    public BUser() {
    }

    public BUser(IUserInfo userInfo) {
        identifier = userInfo.identifier();
        username = userInfo.username();
        sex = userInfo.sex();
        headAvatar = userInfo.avatar();
        nickname = userInfo.nickname();
        remark = userInfo.remark();
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

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

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

    @Override
    public String toString() {
        return "BUser{" +
                "identifier='" + identifier + '\'' +
                ", username='" + username + '\'' +
                ", sex=" + sex +
                ", headAvatar='" + headAvatar + '\'' +
                ", nickname='" + nickname + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
