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

import dong.lan.mapeye.model.users.User;
import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by 梁桂栋 on 16-12-23 ： 下午6:40.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class MonitorRecode extends RealmObject {

    private long id;
    private Record record;
    private long createTime;
    private long endTime;
    private User monitoredUser;
    private RealmList<TraceLocation> locations;


    public static long createId(long contactId, long time) {
        return contactId ^ time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public User getMonitoredUser() {
        return monitoredUser;
    }

    public void setMonitoredUser(User monitoredUser) {
        this.monitoredUser = monitoredUser;
    }

    public RealmList<TraceLocation> getLocations() {
        return locations;
    }

    public void setLocations(RealmList<TraceLocation> locations) {
        this.locations = locations;
    }
}
