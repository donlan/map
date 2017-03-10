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

package com.tencent.qcloud.tlslibrary.helper;

import com.orhanobut.logger.Logger;

/**
 * Created by 梁桂栋 on 16-12-28 ： 下午12:43.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class JMHelper {
    public static String getJUsername(String identifier){
        String name = identifier.replace('-','_');
        Logger.d(name);
        return name;
    }

    public static String getTUsername(String identifier){
        String name = identifier.replace('_','-');
        Logger.d(name);
        return name;
    }

    public static String decodePassword(String identifier){
        String pwd = Secure.encode(identifier);
        Logger.d(pwd);
        return pwd;
    }
}
