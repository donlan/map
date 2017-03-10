package com.tencent.qcloud.tlslibrary.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.orhanobut.logger.Logger;
import com.tencent.qcloud.tlslibrary.R;
import com.tencent.qcloud.tlslibrary.helper.MResource;
import com.tencent.qcloud.tlslibrary.helper.SmsContentObserver;
import com.tencent.qcloud.tlslibrary.helper.Util;
import com.tencent.qcloud.tlslibrary.service.Constants;
import com.tencent.qcloud.tlslibrary.service.TLSService;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSSmsLoginListener;
import tencent.tls.platform.TLSUserInfo;

public class HostLoginActivity extends AppCompatActivity {


    private TLSService tlsService;
    private SmsContentObserver smsContentObserver = null;
    final static int STR_ACCOUNT_LOGIN_REQUEST = 10000;
    final static int SMS_REG_REQUEST = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tencent_tls_ui_activity_host_login);

        tlsService = TLSService.getInstance();

        initSmsService();
        smsContentObserver = new SmsContentObserver(new Handler(),
                this,
                (EditText) findViewById(R.id.checkCode_hostLogin),
                Constants.SMS_LOGIN_SENDER);
        //注册短信变化监听
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsContentObserver);
        SharedPreferences settings = getSharedPreferences(Constants.TLS_SETTING, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.SETTING_LOGIN_WAY, Constants.SMS_LOGIN);
        editor.commit();
    }


    private void initSmsService() {
//        tlsService.initTlsSdk(HostLoginActivity.this);
        tlsService.initSmsLoginService(this,
                (EditText) findViewById(R.id.selectCountryCode_hostLogin),
                (EditText) findViewById(R.id.phoneNumber_hostLogin),
                (EditText) findViewById(R.id.checkCode_hostLogin),
                (Button) findViewById(R.id.btn_requireCheckCode_hostLogin),
                (Button) findViewById(R.id.btn_hostLogin)
        );

        initTLSLogin();

        // 设置点击"注册新用户"事件
        findViewById(R.id.hostRegisterNewUser)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HostLoginActivity.this, HostRegisterActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private void initTLSLogin() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TLSUserInfo userInfo = tlsService.getLastUserInfo();
                if (userInfo != null) {
                    EditText editText = (EditText) HostLoginActivity.this
                            .findViewById(MResource.getIdByName(getApplication(), "id", "phoneNumber_hostLogin"));
                    String phoneNumber = userInfo.identifier;
                    phoneNumber = phoneNumber.substring(phoneNumber.indexOf('-') + 1);
                    editText.setText(phoneNumber);
                }
            }
        });
    }

    //应用调用Andriod_SDK接口时，使能成功接收到回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (STR_ACCOUNT_LOGIN_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                setResult(RESULT_OK, data);
                finish();
            }
        } else if (SMS_REG_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (intent == null) return;


        // 判断是否是从注册界面返回的
        String countryCode = intent.getStringExtra(Constants.COUNTRY_CODE);
        final String phoneNumber = intent.getStringExtra(Constants.PHONE_NUMBER);

        if (countryCode != null && phoneNumber != null) {

            Logger.d("onResume" + countryCode + "-" + phoneNumber);

            tlsService.smsLogin(countryCode, phoneNumber, new TLSSmsLoginListener() {
                @Override
                public void OnSmsLoginAskCodeSuccess(int i, int i1) {
                }

                @Override
                public void OnSmsLoginReaskCodeSuccess(int i, int i1) {
                }

                @Override
                public void OnSmsLoginVerifyCodeSuccess() {
                }

                @Override
                public void OnSmsLoginSuccess(final TLSUserInfo tlsUserInfo) {
                    Util.showToast(HostLoginActivity.this, "欢迎你的回来");
                    TLSService.setLastErrno(0);
                    Intent intent = new Intent();
                    intent.putExtra(Constants.EXTRA_LOGIN_WAY, Constants.SMS_LOGIN);
                    intent.putExtra(Constants.EXTRA_SMS_LOGIN, Constants.SMS_LOGIN_SUCCESS);
                    intent.putExtra(Constants.EXTRA_FROM_REGISTER, true);
                    if (Constants.thirdappPackageNameSucc != null && Constants.thirdappClassNameSucc != null) {
                        intent.setClassName(Constants.thirdappPackageNameSucc, Constants.thirdappClassNameSucc);
                        startActivity(intent);
                    } else {
                        setResult(Activity.RESULT_OK, intent);
                    }
                    finish();
                }

                @Override
                public void OnSmsLoginFail(TLSErrInfo tlsErrInfo) {
                    TLSService.setLastErrno(-1);
                    Util.notOK(HostLoginActivity.this, tlsErrInfo);
                }

                @Override
                public void OnSmsLoginTimeout(TLSErrInfo tlsErrInfo) {
                    TLSService.setLastErrno(-1);
                    Util.notOK(HostLoginActivity.this, tlsErrInfo);
                }
            });
        }

    }

    protected void onDestroy() {
        super.onDestroy();
        if (smsContentObserver != null) {
            this.getContentResolver().unregisterContentObserver(smsContentObserver);
        }
    }
}
