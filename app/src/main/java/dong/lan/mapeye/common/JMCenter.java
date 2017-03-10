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

package dong.lan.mapeye.common;

import android.content.Intent;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.orhanobut.logger.Logger;
import com.tencent.qcloud.sdk.Constant;
import com.tencent.qcloud.tlslibrary.helper.JMHelper;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.LocationContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import dong.lan.mapeye.model.ClientInfo;
import dong.lan.mapeye.model.users.Contact;
import dong.lan.mapeye.model.TraceLocation;
import dong.lan.mapeye.model.message.CMDMessage;
import dong.lan.mapeye.model.message.JGMessage;

/**
 * Created by 梁桂栋 on 16-12-28 ： 下午12:43.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class JMCenter {

    public static final String EXTRAS_LOCATION_SPEED = "locationSpeed";
    public static final String EXTRAS_CMD = "cmd";
    public static final String EXTRAS_RECORD_ID = "recordId";
    public static final String EXTRAS_IDENTIFIER = "contactId";
    public static final String EXTRAS_DIRECTION = "direction";
    public static final String EXTRAS_SPEED = "speed";
    private static final String EXTRAS_CLIENT_INFO = "clientInfo";


    private static JMCenter jmCenter;

    private JMCenter() {

    }

    public static JMCenter instance() {
        if (jmCenter == null)
            jmCenter = new JMCenter();
        return jmCenter;
    }

    public static Message createMessage(int cmd, String toUser, String text) {
        Message message = JMessageClient.createSingleTextMessage(toUser, Constant.JM_KEY, text);
        message.getContent().setNumberExtra(EXTRAS_CMD, cmd);
        return message;
    }


    public static void sendMessage(Message message, BasicCallback callback) {
        if (callback != null)
            message.setOnSendCompleteCallback(callback);
        JMessageClient.sendMessage(message);
    }


    public static void friendInvite(String toUser, String reason, BasicCallback callback) {
        ContactManager.sendInvitationRequest(toUser, Constant.JM_KEY, reason, callback);
    }

    public static void acceptInvite(String toUser, BasicCallback callback) {
        ContactManager.acceptInvitation(toUser, Constant.JM_KEY, callback);
    }

    public static void declineInvite(String toUser, String reason, BasicCallback callback) {
        ContactManager.declineInvitation(toUser, Constant.JM_KEY, reason, callback);
    }

    public void dispatchMessage(Message msg) {
        MonitorManager.instance().post(msg);
        switch (msg.getContentType()) {
            case text:
                textAction(msg);
                break;
            case location:
                LocationContent content = (LocationContent) msg.getContent();
                int cmd = content.getNumberExtra(JMCenter.EXTRAS_CMD).intValue();
                String recordId = getStringExtras(EXTRAS_RECORD_ID, msg);
                String identifier = getStringExtras(EXTRAS_IDENTIFIER, msg);
                if (cmd == CMDMessage.CMD_MONITOR_LOCATING) {
                    TraceLocation traceLocation = new TraceLocation();
                    traceLocation.setLatitude(content.getLatitude().doubleValue());
                    traceLocation.setLongitude(content.getLongitude().doubleValue());
                    traceLocation.setBearing(getNumberExtras(EXTRAS_DIRECTION, msg).floatValue());
                    traceLocation.setSpeed(getNumberExtras(EXTRAS_SPEED, msg).floatValue());
                    traceLocation.setCreateTime(msg.getCreateTime());
                    traceLocation.setRadius(content.getScale().floatValue());
                    MonitorManager.instance().handlerMonitorLocation(recordId, identifier, traceLocation);
                }
                break;
            case eventNotification:
                //处理事件提醒消息
                EventNotificationContent eventNotificationContent = (EventNotificationContent) msg.getContent();
                switch (eventNotificationContent.getEventNotificationType()) {
                    case group_member_added:
                        //群成员加群事件
                        break;
                    case group_member_removed:
                        //群成员被踢事件
                        break;
                    case group_member_exit:
                        //群成员退群事件
                        break;
                }
                break;

        }
    }

    private void textAction(Message msg) {
        int cmd = getNumberExtras(EXTRAS_CMD, msg).intValue();
        String recordId = getStringExtras(EXTRAS_RECORD_ID, msg);
        String identifier = getStringExtras(EXTRAS_IDENTIFIER, msg);
        switch (cmd) {
            case CMDMessage.CMD_CLIENT_INFO_REPLY:
                MonitorManager.instance().notifyClientInfo(recordId,identifier,getStringExtras(EXTRAS_CLIENT_INFO,msg));
                break;
            case CMDMessage.CMD_CLIENT_INFO:
                MonitorManager.instance().gatherMobileInfo(recordId, identifier);
                break;
            case CMDMessage.CMD_SEND_MONITOR_INVITE:
                TextContent content = (TextContent) msg.getContent();
                MonitorManager.instance().doMonitorInvite(content.getText(), new JGMessage(msg));
                break;
            case CMDMessage.CMD_MONITOR_INVITE_DENY:
                MonitorManager.instance().doMonitorDeny(new JGMessage(msg));
                break;
            case CMDMessage.CMD_MONITOR_INVITE_OK:
                MonitorManager.instance().doMonitorAccept(new JGMessage(msg));
                break;

            case CMDMessage.CMD_MONITOR_START:
                MonitorManager.instance().startMonitorLocation(
                        recordId,
                        identifier,
                        msg.getFromUser().getUserName(),
                        msg.getCreateTime());
                break;
            case CMDMessage.CMD_MONITOR_STOP:
                MonitorManager.instance().stopMonitorLocation();
                break;
            case CMDMessage.CMD_MONITORING:
                long id = Contact.createId(getStringExtras(EXTRAS_RECORD_ID, msg), getStringExtras(EXTRAS_IDENTIFIER, msg));
                UserManager.instance().updateContactStatus(id, Contact.STATUS_MONITORING);
                break;
            case CMDMessage.CMD_SET_LOCATION_SPEED:
                int time = getNumberExtras(EXTRAS_LOCATION_SPEED, msg).intValue();
                LocationService locationService = MonitorManager.instance().currentLocationService();
                if (locationService.isRunning()) {
                    LocationClientOption option = locationService.getDefaultLocationClientOption();
                    option.setScanSpan(time * 1000);
                    if (time == 1) {
                        option.setLocationNotify(true);
                    } else {
                        option.setLocationNotify(false);
                    }
                    locationService.setLocationOption(option);
                    locationService.restart();
                }
                break;
        }
    }


    public static Number getNumberExtras(String key, Message message) {
        if (message == null)
            return 0;
        return message.getContent().getNumberExtra(key);
    }


    public static String getStringExtras(String key, Message message) {
        if (message == null)
            return "";
        return message.getContent().getStringExtra(key);
    }

    public static void sendLocation(int cmd, String recordId, String identifier, String toUserId, BDLocation bdLocation) {
        Logger.d(recordId + "," + identifier);
        Message message = JMessageClient.createSingleLocationMessage(
                toUserId,
                Constant.JM_KEY,
                bdLocation.getLatitude(),
                bdLocation.getLongitude(),
                (int) bdLocation.getRadius(),
                bdLocation.getAddrStr());
        float speed = bdLocation.getSpeed();
        float direction = bdLocation.getDirection();
        message.getContent().setStringExtra(EXTRAS_RECORD_ID, recordId);
        message.getContent().setStringExtra(EXTRAS_IDENTIFIER, identifier);
        message.getContent().setNumberExtra(EXTRAS_CMD, cmd);
        message.getContent().setNumberExtra(EXTRAS_DIRECTION, direction);
        message.getContent().setNumberExtra(EXTRAS_SPEED, speed);
        JMessageClient.sendMessage(message);
    }

    public static void sendClientInfoMessage(String identifier, String recordId, BasicCallback callback) {
        Message message = JMessageClient.createSingleTextMessage(JMHelper.getJUsername(identifier),
                "获取手机信息");
        message.getContent().setNumberExtra(EXTRAS_CMD, CMDMessage.CMD_CLIENT_INFO);
        message.getContent().setStringExtra(EXTRAS_RECORD_ID, recordId);
        message.getContent().setStringExtra(EXTRAS_IDENTIFIER, identifier);
        if (callback != null)
            message.setOnSendCompleteCallback(callback);
        JMessageClient.sendMessage(message);
    }

    public static void replyMobileInfo(String recordId, String identifier, ClientInfo clientInfo) {
        Message message = JMessageClient.createSingleTextMessage(JMHelper.getJUsername(identifier), clientInfo.toString());
        message.getContent().setNumberExtra(EXTRAS_CMD, CMDMessage.CMD_CLIENT_INFO_REPLY);
        message.getContent().setStringExtra(EXTRAS_IDENTIFIER, identifier);
        message.getContent().setStringExtra(EXTRAS_RECORD_ID, recordId);
        message.getContent().setStringExtra(EXTRAS_CLIENT_INFO, clientInfo.toJson());
        JMessageClient.sendMessage(message);
    }

    public static class JMessage {
        Message message;

        public JMessage(int cmd, String identifier, String text) {
            message = JMessageClient.createSingleTextMessage(JMHelper.getJUsername(identifier), text);
            message.getContent().setNumberExtra(EXTRAS_CMD, cmd);
        }

        public JMessage appendNumberExtra(String key, Number number) {
            message.getContent().setNumberExtra(key, number);
            return this;
        }

        public JMessage appendStringExtra(String key, String val) {
            message.getContent().setStringExtra(key, val);
            return this;
        }

        public Message build() {
            return message;
        }
    }
}

