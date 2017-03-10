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

/**
 * Created by 梁桂栋 on 16-12-25 ： 下午3:32.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public interface CMDMessage {

    int TYPE_CUSTOM = 0;
    int TYPE_CMD_AND_TEXT = 1;
    int TYPE_CMD_TEXT_LOCATION = 2;

    int CMD_ERROR = -10000;
    int CMD_CHAT_TEXT = -1;
    int CMD_EXCEPTION = -2;
    int CMD_SEND_MONITOR_INVITE = 1;
    int CMD_MONITOR_INVITE_OK = 2;
    int CMD_MONITOR_INVITE_DENY = 3;
    int CMD_MONITORING = 4;
    int CMD_MONITOR_LOCATING = 5;
    int CMD_MONITOR_START = 6;
    int CMD_MONITOR_STOP = 7;
    int CMD_MONITOR_INVITE_RECEIVE = 8;
    int CMD_SET_LOCATION_SPEED = 9;
    int CMD_CLIENT_INFO = 10;
    int CMD_CLIENT_INFO_REPLY = 11;
}
