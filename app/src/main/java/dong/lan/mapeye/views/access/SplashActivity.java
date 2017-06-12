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

package dong.lan.mapeye.views.access;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import dong.lan.mapeye.App;
import dong.lan.mapeye.R;
import dong.lan.mapeye.common.UserManager;
import dong.lan.mapeye.views.MainActivity;
import dong.lan.permission.CallBack;
import dong.lan.permission.Permission;

/**
 * Created by 梁桂栋 on 16-11-6 ： 下午10:44.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        List<String> per = new ArrayList<>(5);
        per.add(Manifest.permission.READ_PHONE_STATE);
        per.add(Manifest.permission.ACCESS_FINE_LOCATION);
        per.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        per.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        per.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Permission.instance().check(new CallBack<List<String>>() {
            @Override
            public void onResult(List<String> result) {
                if (result == null) {
                    new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            App.getContext().start("");
                            if (UserManager.instance().isLogin()) {
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            } else {
                                startActivity(new Intent(SplashActivity.this, LoginAndSignActivity.class));
                            }
                            finish();
                        }
                    }.sendEmptyMessageDelayed(0,1500);
                }
            }
        }, this, per);
    }

}
