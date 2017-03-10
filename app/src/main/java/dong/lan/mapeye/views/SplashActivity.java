package dong.lan.mapeye.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.Callable;

import dong.lan.mapeye.contracts.LoginAndSignContract;
import dong.lan.mapeye.utils.SPHelper;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.Single;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {

                SPHelper.init(SplashActivity.this.getApplicationContext());
                return SPHelper.getBoolean(LoginAndSignContract.loginAndSignView.KEY_IS_LOGIN);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Boolean o) {
                        Log.d(TAG, "onNext: "+o);
                        if(o){
                            startActivity(new Intent(SplashActivity.this,MainActivity.class));
                        }else{
                            startActivity(new Intent(SplashActivity.this,LoginAndSignActivity.class));
                        }
                    }
                });

    }
}
