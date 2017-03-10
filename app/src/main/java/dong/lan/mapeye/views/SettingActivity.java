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

package dong.lan.mapeye.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import dong.lan.mapeye.R;
import dong.lan.mapeye.common.Config;
import dong.lan.mapeye.utils.SPHelper;
import dong.lan.mapeye.views.customsView.ToggleButton;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.setting_start_boot)
    ToggleButton bootStart;
    @OnClick(R.id.bar_left) void back(){
        finish();
    }

    @OnClick(R.id.setting_alert_sound)
    void pickAlertSoundAction() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择提示声音"), 1);
        } catch (android.content.ActivityNotFoundException e) {
            show("请安装文件管理器（File Manager）");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        bindView(this);
        initView();
    }

    private void initView() {
        bootStart.setChecked(SPHelper.getBoolean(Config.SP_KEY_AUTOSTART));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            SPHelper.put(Config.SP_KEY_ALERT_SOUND, uri.getPath());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (SPHelper.getBoolean(Config.SP_KEY_AUTOSTART) != bootStart.isChecked()) {
            SPHelper.putBoolean(Config.SP_KEY_AUTOSTART, bootStart.isChecked());
        }
        super.onDestroy();
    }
}
