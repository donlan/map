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

package dong.lan.mapeye.model;

import dong.lan.mapeye.model.message.CMDMessage;
import io.realm.RealmObject;

/**
 * Created by 梁桂栋 on 16-12-6 ： 下午7:03.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: 事务封装类。主要是需要用户操作的事务，例如请求位置共享绑定。
 */

public class Affair extends RealmObject {
    public static final int HANDLE_AS_NOTICE = -1;
    public static final int HANDLE_NO = 0;
    public static final int HANDLE_DENY = 1;
    public static final int HANDLE_ACCEPT = 2;

    public static final int TYPE_MONITOR_BIND =0;
    public static final int TYPE_USER_INVITE =1;

    private String id;             //对应极光Api中的Message的id
    private long tag;         //保存特殊属性（此处保存Contact.createId(groupId,UserId)）
    private String fromUser;    //发送该事务的用户名
    private int type;            //事务的操作类型
    private String content;     //事务的内容
    private long createdTime;   //创建时间
    private String extras;      //附加消息
    private int handle;         //事务处理的状态

    /**
     *
     * @return 事务处理的状态对应的字符串
     */
    public String getStatusString(){
        if(handle == HANDLE_ACCEPT)
            return "已同意";
        else if(handle==HANDLE_DENY)
            return "已拒绝";
        return "未处理";
    }

    /**
     *
     * @return 事务的操作类型对应的字符串
     */
    public String getTypeString(){
        switch (type){
            case TYPE_MONITOR_BIND:
                return "绑定邀请";
            case TYPE_USER_INVITE:
                return "好友请求";
        }
        return "";
    }
    public long getTag() {
        return tag;
    }

    public void setTag(long tag) {
        this.tag = tag;
    }

    public int getHandle() {
        return handle;
    }

    public void setHandle(int handle) {
        this.handle = handle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

}
