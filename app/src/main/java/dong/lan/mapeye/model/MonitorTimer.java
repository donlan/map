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

package dong.lan.mapeye.model;

import dong.lan.mapeye.model.users.User;
import io.realm.RealmObject;

/**
 * Created by 梁桂栋 on 17-1-5 ： 下午5:06.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class MonitorTimer extends RealmObject {


    private static final int DEFAULT_DELAY_TIME = 2000;
    private Record record;
    private User user;
    private long createTime;
    private long startTime;
    private long endTime;
    private boolean isRepeat;
    private boolean isContinue;
    private boolean isOpen;
    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getTriggerTimeOfStart() {
        long curTime = System.currentTimeMillis();
        if (curTime < startTime)
            return System.currentTimeMillis() + startTime - curTime + DEFAULT_DELAY_TIME;
        return System.currentTimeMillis() + DEFAULT_DELAY_TIME;
    }

    public long getTriggerTimeOfEnd() {
        long curTime = System.currentTimeMillis();
        if (curTime < endTime)
            return System.currentTimeMillis() + endTime - curTime + DEFAULT_DELAY_TIME;
        return System.currentTimeMillis() + DEFAULT_DELAY_TIME;
    }


    public String tagString() {
        return isRepeat ? "重复" : "单次" + (isContinue ? "_不连续" : "_连续");
    }

    public boolean isContinue() {
        return isContinue;
    }

    public void setContinue(boolean aContinue) {
        isContinue = aContinue;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }
}
