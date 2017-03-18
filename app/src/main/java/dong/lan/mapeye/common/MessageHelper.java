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


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;


/**
 * Created by 梁桂栋 on 16-11-21 ： 下午6:25.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class MessageHelper {



    private static MessageHelper factory;
    private Gson gson;

    private MessageHelper() {
        gson = new GsonBuilder().serializeNulls().create();
    }

    public static MessageHelper getInstance() {
        if (factory == null)
            factory = new MessageHelper();
        return factory;
    }

    public static String createRecordContactExtras(String recordId, String contactId) {
        return recordId + "," + contactId;
    }

    public static String[] parseRecordContactId(String fromMessageExtras) {
        return fromMessageExtras.split(",");
    }

//    public String getMessageText(Message message) {
//        Logger.d(message.getContent().toJson());
//        try {
//            GsonMessage msg = toTarget(message.getContent().toJson(), GsonMessage.class);
//            return msg.getText();
//        } catch (Exception e) {
//
//            Logger.d(e.getMessage());
//        }
//        return "";
//    }
//
//
//    public static void sendText(int cmd, String toUser, String text, String key[], Number val[], BasicCallback callback) {
//        Message message = JMessageClient.createSingleTextMessage(toUser, text);
//        message.getContent().setNumberExtra(KEY_EXTRAS_CMD, cmd);
//        if (key != null && val != null) {
//            for (int i = 0; i < key.length; i++) {
//                message.getContent().setNumberExtra(key[i], val[i]);
//            }
//        }
//        if (callback != null)
//            message.setOnSendCompleteCallback(callback);
//        JMessageClient.sendMessage(message);
//    }
//
//    static int getMessageCMD(Message message) {
//        int res = CMD_ERROR;
//        try {
//            res = message.getContent().getNumberExtra(KEY_EXTRAS_CMD).intValue();
//        } catch (Exception e) {
//            Logger.d(e.getMessage());
//        }
//        return res;
//    }
//
//    static int getIntMessageExtras(String key, Message message) {
//        int res = CMD_ERROR;
//        try {
//            res = message.getContent().getNumberExtra(key).intValue();
//        } catch (Exception e) {
//            Logger.d(e.getMessage());
//        }
//        return res;
//    }
//
//    public static long getLongMessageExtras(String key, Message message) {
//        long res = CMD_ERROR;
//        try {
//            res = message.getContent().getNumberExtra(key).longValue();
//        } catch (Exception e) {
//            Logger.d(e.getMessage());
//        }
//        return res;
//    }



    /**
     * 将指定消息字符串通过Gson解析成自定的目标实体
     *
     * @param msg    json格式的字符串
     * @param tClass 目标实体
     * @param <T>    目标实体泛型
     * @return 目标实体
     */
    public <T> T toTarget(String msg, Class<T> tClass) {
        return gson.fromJson(msg, tClass);
    }

    public String toJson(Object o) {
        return gson.toJson(o);
    }
}
