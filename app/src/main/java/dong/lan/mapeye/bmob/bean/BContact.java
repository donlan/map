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
import dong.lan.mapeye.model.users.Contact;
import dong.lan.mapeye.model.users.User;

/**
 * Created by 梁桂栋 on 17-3-18 ： 下午7:43.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class BContact extends BmobObject {

    private String id;                    //对应域UserInfo.getUserId()
    private Long startMonitorTime;      //定时监听位置变化的开始时间
    private Long endMonitorTime;        //定时监听位置变化的结束时间
    private Boolean isRepeatMonitor;    //是否重复监听（单次定时监听还是重复监听）
    private Integer status;                 //被监听用户的状态
    private Integer tag;                    //被监听用户的绑定标记
    private BUser user;                  //所对应的User用户


    public BContact() {
    }

    public BContact(Contact contact) {
        id= contact.getId();
        startMonitorTime = contact.getStartMonitorTime();
        endMonitorTime = contact.getEndMonitorTime();
        isRepeatMonitor = contact.isRepeatMonitor();
        status = contact.getStatus();
        tag = contact.getTag();
        user = new BUser(contact.getUser());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getStartMonitorTime() {
        return startMonitorTime;
    }

    public void setStartMonitorTime(Long startMonitorTime) {
        this.startMonitorTime = startMonitorTime;
    }

    public Long getEndMonitorTime() {
        return endMonitorTime;
    }

    public void setEndMonitorTime(Long endMonitorTime) {
        this.endMonitorTime = endMonitorTime;
    }

    public Boolean getRepeatMonitor() {
        return isRepeatMonitor;
    }

    public void setRepeatMonitor(Boolean repeatMonitor) {
        isRepeatMonitor = repeatMonitor;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public BUser getUser() {
        return user;
    }

    public void setUser(BUser user) {
        this.user = user;
    }


    public Contact toContact() {
        User u = new User();
        u.setRemark(user.getRemark());
        u.setSex(user.getSex());
        u.setHeadAvatar(user.getHeadAvatar());
        u.setNickname(user.getNickname());
        u.setIdentifier(user.getIdentifier());

        Contact contact = new Contact();
        contact.setId(id);
        contact.setEndMonitorTime(endMonitorTime);
        contact.setRepeatMonitor(isRepeatMonitor);
        contact.setStatus(status);
        contact.setTag(tag);
        contact.setStartMonitorTime(startMonitorTime);
        contact.setUser(u);

        return contact;
    }

    @Override
    public String toString() {
        return "BContact{" +
                "id='" + id + '\'' +
                ", startMonitorTime=" + startMonitorTime +
                ", endMonitorTime=" + endMonitorTime +
                ", isRepeatMonitor=" + isRepeatMonitor +
                ", status=" + status +
                ", tag=" + tag +
                ", user=" + user +
                '}';
    }
}
