package dong.lan.mapeye.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.util.concurrent.Callable;

import dong.lan.mapeye.common.SMSServer;
import dong.lan.mapeye.common.Secure;
import dong.lan.mapeye.common.UserManager;
import dong.lan.mapeye.contracts.LoginAndSignContract;
import dong.lan.mapeye.utils.SPHelper;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.Single;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static dong.lan.mapeye.contracts.LoginAndSignContract.loginAndSignView.KEY_IS_LOGIN;

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

        if (UserManager.instance().isLogin()) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            startActivity(new Intent(SplashActivity.this, LoginAndSignActivity.class));
        }
        finish();
    }

}
