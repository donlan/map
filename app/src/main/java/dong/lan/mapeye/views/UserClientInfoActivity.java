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

package dong.lan.mapeye.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import butterknife.BindView;
import dong.lan.mapeye.R;
import dong.lan.mapeye.common.MessageHelper;
import dong.lan.mapeye.model.ClientInfo;

/**
 * Created by 梁桂栋 on 17-1-4 ： 下午5:03.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class UserClientInfoActivity extends BaseActivity {

    @BindView(R.id.info)
    TextView info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_info);
        checkIntent();
    }

    private void checkIntent() {

        String recordId = getIntent().getStringExtra("recordId");
        String identifier = getIntent().getStringExtra("identifier");
        String clientInfoJson = getIntent().getStringExtra("clientInfo");

        if (TextUtils.isEmpty(recordId) || TextUtils.isEmpty(clientInfoJson)) {
            toast("数据已经失效");
            return;
        }

        ClientInfo clientInfo = MessageHelper.getInstance().toTarget(clientInfoJson, ClientInfo.class);
        info.setText(clientInfo.toString());


    }
}
