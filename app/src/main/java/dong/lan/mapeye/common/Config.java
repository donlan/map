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

import android.os.Environment;
import android.support.v4.os.EnvironmentCompat;

import java.io.File;

/**
 * Created by 梁桂栋 on 16-11-16 ： 上午10:42.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public final class Config {

    public static final String PICTURE_PATH = Environment.getExternalStorageDirectory().getPath()
            + "/mapeye/images/";
    public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 100;
    public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 101;
    public static final int REQUESTCODE_UPLOADAVATAR_CROP = 102;

    public static final int REQ_CODE_START_TIMER = 1001;
    public static final int REQ_CODE_END_TIMER = 1002;

    public static final String SP_KEY_ALERT_SOUND = "alertSound";

    public static final String SP_KEY_AUTOSTART = "autostart";
    public static final String KEY_RECORD_ID = "recordId";
    public static final String KEY_IDENTIFIER = "identifier";
    public static final String TIMER_STATUS = "timerStatus";
    public static final String TIMER_TASK_ID = "timerId";

    private Config() {
    }

}
