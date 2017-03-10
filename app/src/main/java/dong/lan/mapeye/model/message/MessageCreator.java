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

package dong.lan.mapeye.model.message;

import com.tencent.TIMCustomElem;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;

import dong.lan.mapeye.common.MessageHelper;

/**
 * Created by 梁桂栋 on 16-12-26 ： 下午2:21.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class MessageCreator {

    private MessageCreator(){}



    public static TIMMessage createNormalCmdMessage(int type, int cmd, String text) {
        TIMMessage timMessage = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();
        BaseMessage message = new BaseMessage();
        message.setType(type);
        message.setCmd(cmd);
        message.setCreateTime(System.currentTimeMillis());
        message.setText(text);
        message.setLatitude(0);
        message.setLocationSpeed(0);
        message.setLongitude(0);
        message.setExtras("");
        elem.setDesc(text);
        elem.setData(MessageHelper.getInstance().toJson(message).getBytes());
        timMessage.addElement(elem);
        timMessage.setTimestamp(System.currentTimeMillis());
        timMessage.setCustomInt(cmd);
        return timMessage;
    }

    public static TIMMessage createNormalCmdMessage(int cmd, String text,String extras) {
        TIMMessage timMessage = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();
        BaseMessage message = new BaseMessage();
        message.setType(CMDMessage.TYPE_CMD_AND_TEXT);
        message.setCmd(cmd);
        message.setCreateTime(System.currentTimeMillis());
        message.setText(text);
        message.setLatitude(0);
        message.setLocationSpeed(0);
        message.setLongitude(0);
        message.setExtras(extras);
        elem.setDesc(text);
        elem.setData(MessageHelper.getInstance().toJson(message).getBytes());
        timMessage.addElement(elem);
        timMessage.setTimestamp(System.currentTimeMillis());
        timMessage.setCustomInt(cmd);
        return timMessage;
    }

    public static TIMMessage createLocationSpeedMessage(int speed,String text){
        TIMMessage timMessage = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();
        BaseMessage message = new BaseMessage();
        message.setLatitude(0);
        message.setLongitude(0);
        message.setLocationSpeed(speed);
        message.setExtras("");
        message.setCreateTime(System.currentTimeMillis());
        message.setCmd(CMDMessage.CMD_SET_LOCATION_SPEED);
        message.setText(text);
        message.setType(CMDMessage.TYPE_CMD_TEXT_LOCATION);
        elem.setDesc(text);
        elem.setData(MessageHelper.getInstance().toJson(message).getBytes());
        timMessage.addElement(elem);
        timMessage.setTimestamp(System.currentTimeMillis());
        timMessage.setCustomInt(CMDMessage.CMD_SET_LOCATION_SPEED);
        return timMessage;
    }

    public static TIMMessage createLocatingMessage(double lat,double lng,float scale,String extras){
        TIMMessage timMessage = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();
        BaseMessage message = new BaseMessage();
        message.setLatitude(lat);
        message.setLongitude(lng);
        message.setLocationSpeed(0);
        message.setExtras(extras);
        message.setScale(scale);
        message.setCreateTime(System.currentTimeMillis());
        message.setCmd(CMDMessage.CMD_MONITOR_LOCATING);
        message.setText("");
        message.setType(CMDMessage.TYPE_CMD_TEXT_LOCATION);
        elem.setDesc("");
        elem.setData(MessageHelper.getInstance().toJson(message).getBytes());
        timMessage.addElement(elem);
        timMessage.setTimestamp(System.currentTimeMillis());
        timMessage.setCustomInt(CMDMessage.CMD_MONITOR_LOCATING);
        return timMessage;
    }

    public static TIMMessage crateNormalTextCmdMessage(int cmd,String text){
        TIMMessage timMessage = new TIMMessage();
        TIMTextElem textElem = new TIMTextElem();
        textElem.setText(text);
        timMessage.setCustomInt(cmd);
        timMessage.setTimestamp(System.currentTimeMillis());
        return timMessage;
    }
}
