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

package dong.lan.mapeye;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

import dong.lan.mapeye.common.Config;
import dong.lan.mapeye.task.TimerHandlerActivity;

public class TimerTaskReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent timerIntent = new Intent(context, TimerHandlerActivity.class);
        boolean status = intent.getBooleanExtra(Config.TIMER_STATUS, false);
        long createTime = intent.getLongExtra(Config.TIMER_TASK_ID, 0);
        String recordId = intent.getStringExtra(Config.KEY_RECORD_ID);
        String identifier = intent.getStringExtra(Config.KEY_IDENTIFIER);
        Logger.d(status + "," + createTime + "," + recordId + "," + identifier);
        timerIntent.putExtra(Config.TIMER_STATUS, status);
        timerIntent.putExtra(Config.TIMER_TASK_ID, createTime);
        timerIntent.putExtra(Config.KEY_RECORD_ID, recordId);
        timerIntent.putExtra(Config.KEY_IDENTIFIER, identifier);
        timerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(timerIntent);
    }
}
