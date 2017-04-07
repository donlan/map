package dong.lan.mapeye.views;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Collections;
import java.util.List;

import dong.lan.mapeye.App;
import dong.lan.mapeye.common.UserManager;
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

        Permission.instance().check(new CallBack<List<String>>() {
            @Override
            public void onResult(List<String> result) {
                if (result == null) {
                    App.getContext().start("");
                    if (UserManager.instance().isLogin()) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginAndSignActivity.class));
                    }
                    finish();
                }
            }
        }, this, Collections.singletonList(Manifest.permission.READ_PHONE_STATE));
    }

}
