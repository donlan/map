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

package dong.lan.mapeye.common;

import android.content.Context;
import android.util.Log;


import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by 梁桂栋 on 17-3-15 ： 下午3:31.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class SMSServer {

    private static final String TAG = SMSServer.class.getSimpleName();
    public static final int ACTION_VERIFICATION_SUCCESS = 3;
    public static int ACTION_SUBMIT_CODE = 1;
    public static int ACTION_QUERY_CODE = 2;
    public static int ACTION_EXCEPTION = 0;

    private Context appContext;
    private SmsCallback smsCallback;

    public SMSServer(Context appContext, final SmsCallback callback) {
        this.smsCallback = callback;
        this.appContext = appContext;
        SMSSDK.initSDK(appContext, "1c1a0cd7d6a29", "1fdfb9e07797e1c1a2c6943290c61adc");
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Log.d(TAG, "afterEvent: "+data.getClass().getSimpleName());
                if (result == SMSSDK.RESULT_COMPLETE) {
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        smsCallback.onSmsCallback(ACTION_SUBMIT_CODE, "提交验证码成功");
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        boolean smart = (Boolean)data;
                        if(smart) {
                            callback.onSmsCallback(ACTION_VERIFICATION_SUCCESS,"智能验证通过");
                        } else {
                            smsCallback.onSmsCallback(ACTION_QUERY_CODE,"获取验证码成功");
                            //依然走短信验证
                        }
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }
                } else {
                    smsCallback.onSmsCallback(ACTION_EXCEPTION,((Throwable) data).getMessage());
                    ((Throwable) data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
    }


    public void destroy(){
        SMSSDK.unregisterAllEventHandler();
    }

    public void getSMSCode(String country, String phone) {
        Log.d(TAG, "getSMSCode: "+country+","+phone);
        SMSSDK.getVerificationCode(country, phone);
    }


    public void submitSMSCode(String country, String phone, String code) {
        Log.d(TAG, "getSMSCode: "+country+","+phone+","+code);
        SMSSDK.submitVerificationCode(country, phone, code);
    }


    public interface SmsCallback {
        void onSmsCallback(int action, String data);
    }
}
