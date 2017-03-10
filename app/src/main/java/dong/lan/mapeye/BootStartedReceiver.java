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

package dong.lan.mapeye;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import dong.lan.mapeye.common.Config;
import dong.lan.mapeye.utils.SPHelper;
import dong.lan.mapeye.views.BootInitActivity;

/**
 * Created by 梁桂栋 on 16-12-18 ： 下午3:27.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class BootStartedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean start = SPHelper.getBoolean(Config.SP_KEY_AUTOSTART);
        if (start) {
            Intent startIntent = new Intent(context, BootInitActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startIntent);
        }
    }
}
