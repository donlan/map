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

package dong.lan.mapeye.task;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import cn.jpush.im.api.BasicCallback;
import dong.lan.mapeye.common.JMCenter;
import dong.lan.mapeye.utils.ShowUtils;

/**
 * Created by 梁桂栋 on 17-1-3 ： 下午4:57.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class ClientInfoRequireTask extends IntentService {


    private final static long TASK_LIFE = 10000;
    public final static String KEY_IDENTIFIER = "identifier";
    public static final String KEY_RECORD_ID = "recordId";
    private String identifier;
    private String recordId;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ClientInfoRequireTask(String name) {
        super(name);
    }

    public ClientInfoRequireTask() {
        super("ClientInfoRequireTask");
    }


    @Override
    protected void onHandleIntent(final Intent intent) {
        identifier = intent.getStringExtra(KEY_IDENTIFIER);
        recordId = intent.getStringExtra(KEY_RECORD_ID);
        if(TextUtils.isEmpty(identifier)){
            ShowUtils.toast(getBaseContext(),"用户数据异常（用户标示为空）");
            return;
        }
        JMCenter.sendClientInfoMessage(identifier,recordId, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    ShowUtils.toast(getBaseContext(),"发送请求成功，请等待数据回传");
                }else{
                    ShowUtils.toast(getBaseContext(),i+","+s);
                }
            }
        });

        long time = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - time > TASK_LIFE) {
                this.stopSelf();
                break;
            }
        }
    }

}
