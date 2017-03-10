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

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by 梁桂栋 on 16-11-9 ： 下午1:51.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class Secure {

    private Secure(){}

    public static String encode(String origin) {
        if (origin == null)
            return "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] ori = origin.getBytes();
            digest.update(ori);
            byte[] enc = digest.digest();
            return Base64.encodeToString(enc, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
