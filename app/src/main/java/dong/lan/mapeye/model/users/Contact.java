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


import com.tencent.TIMUserProfile;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by 梁桂栋 on 16-11-15 ： 下午4:31.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: 一个记录Record里绑定位置共享关系的联系人信息
 */

public class Contact extends RealmObject {
    public static final int TAG_ADDING = 0;
    public static final int TAG_DENY = 1;
    public static final int TAG_AGREE = 2;

    public static final int STATUS_WAITING = 0;
    public static final int STATUS_MONITORING = 1;
    public static final int STATUS_WARNING = 2;
    public static final int STATUS_NO_RESPONSE = 3;
    public static final int STATUS_OFFLINE = 4;
    public static final int STATUS_NONE = 5;


    private long id;                    //对应域UserInfo.getUserId()
    private Date startMonitorTime;      //定时监听位置变化的开始时间
    private Date endMonitorTime;        //定时监听位置变化的结束时间
    private boolean isRepeatMonitor;    //是否重复监听（单次定时监听还是重复监听）
    private int status;                 //被监听用户的状态
    private int tag;                    //被监听用户的绑定标记
    private User user;                  //所对应的User用户
    @Ignore
    private TIMUserProfile userInfo;          //不存入Realm的UserInfo

    public static long createId(String groupId, String userId) {
        return groupId.hashCode() ^ userId.hashCode();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getStartMonitorTime() {
        return startMonitorTime;
    }

    public void setStartMonitorTime(Date startMonitorTime) {
        this.startMonitorTime = startMonitorTime;
    }

    public Date getEndMonitorTime() {
        return endMonitorTime;
    }

    public void setEndMonitorTime(Date endMonitorTime) {
        this.endMonitorTime = endMonitorTime;
    }

    public boolean isRepeatMonitor() {
        return isRepeatMonitor;
    }

    public void setRepeatMonitor(boolean repeatMonitor) {
        isRepeatMonitor = repeatMonitor;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TIMUserProfile getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(TIMUserProfile userInfo) {
        this.userInfo = userInfo;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        else if (obj instanceof Contact)
            return id == ((Contact) obj).getId();
        else if(obj instanceof Long)
            return id == (Long) obj;
        return false;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }
}
