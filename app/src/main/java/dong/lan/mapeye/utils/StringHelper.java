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

package dong.lan.mapeye.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

import dong.lan.mapeye.model.users.Contact;
import dong.lan.mapeye.model.Record;

/**
 * Created by 梁桂栋 on 16-12-1 ： 下午3:29.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public final class StringHelper {
    private StringHelper() {
    }

    public static String MonitorInfo(Contact contact) {
        if (contact == null)
            return "";
        String text = "";
        if (contact.getStartMonitorTime() == 0) {
            text = "没有设置监听时间";
        } else {
            boolean isRepeat = contact.isRepeatMonitor();
            SimpleDateFormat sdf = null;
            if (isRepeat)
                sdf = new SimpleDateFormat("a HH:mm", Locale.CHINA);
            else
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            text += "开始时间： " + sdf.format(contact.getStartMonitorTime());
            if (contact.getEndMonitorTime() != 0)
                text += "  结束时间： " + sdf.format(contact.getEndMonitorTime());
        }
        return text;
    }

    public static String getContactTag(Contact contact) {
        if (contact == null)
            return "";
        int tag = contact.getTag();
        switch (tag) {
            case Contact.TAG_ADDING:
                return "待对方同意";
            case Contact.TAG_AGREE:
                return "已同意";
            case Contact.TAG_DENY:
                return "已拒绝";
            default:
                return "未知";
        }
    }

    public static String getContactStatus(Contact contact) {
        if (contact == null)
            return null;
        int status = contact.getStatus();
        switch (status) {
            case Contact.STATUS_MONITORING:
                return "监听中";
            case Contact.STATUS_NO_RESPONSE:
                return "无反馈";
            case Contact.STATUS_WAITING:
                return "等待监听回复";
            case Contact.STATUS_OFFLINE:
                return "离线";
            case Contact.STATUS_WARNING:
                return "警告";
            case Contact.STATUS_NONE:
                return "无监听任务";
            default:
                return "未知";
        }
    }

    public static String getRecordDesc(Record record){
        return "创建于:" +
                DateUtils.format(record.getCreateTime(), "yyyy年M月d日 HH:mm") +
                " 共由 " +
                record.getPoints().size() +
                " 个节点构成";
    }
}
