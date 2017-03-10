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
 * Created by 梁桂栋 on 16-12-28 ： 下午11:00.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public interface IMessage{

    /**
     *
     * @return 具体实现类包装的消息实体
     */
    Object realMessage();

    /**
     *
     * @return 消息发送时的时间
     */
    long timestamp();

    /**
     *
     * @return 发送者，默认用户名
     */
    String sender();

    /**
     * @return 消息内容
     */
    String content();

    /**
     *
     * @return 消息id
     */
    String id();


    /**
     * 是否是用户自己发送的消息
     * @return true则是自己发送，否则则是发送给自己的消息
     */
    boolean isSelf();

    /**
     * 获取消息中附加的数字字段
     * @param key 字段key
     * @return 数字内容
     */
    Number getNumberExtra(String key);

    /**
     * 获取消息中附加的字符串字段
     * @param key 字段key
     * @return 字符串内容
     */
    String getStringExtra(String key);
}
