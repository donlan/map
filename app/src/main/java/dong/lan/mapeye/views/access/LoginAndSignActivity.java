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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import dong.lan.mapeye.R;
import dong.lan.mapeye.bmob.BmobAction;
import dong.lan.mapeye.common.SMSServer;
import dong.lan.mapeye.common.Secure;
import dong.lan.mapeye.common.UserManager;
import dong.lan.mapeye.utils.InputUtils;
import dong.lan.mapeye.views.BaseActivity;
import dong.lan.mapeye.views.MainActivity;
import dong.lan.mapeye.views.customsView.EditTextWithClearButton;
import dong.lan.permission.CallBack;
import dong.lan.permission.Permission;

/**
 * Created by 梁桂栋 on 16-11-8 ： 下午3:28.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class LoginAndSignActivity extends BaseActivity {

    private static final String TAG = "LoginAndSignActivity";

    @BindView(R.id.phoneNumber)
    EditTextWithClearButton phoneNumInput;
    @BindView(R.id.requireCheckCode)
    Button reqCheckCodeBtn;
    @BindView(R.id.checkCode_login)
    EditText checkCodeInput;
    @BindView(R.id.btn_login)
    Button loginBtn;
    @BindView(R.id.btn_register)
    Button registerBtn;
    @BindView(R.id.tittle)
    TextView tittleTv;
    private SMSServer smsServer;
    private boolean loginFlag = true;
    private boolean isRegister = false;
    private MyHandler handler;
    private int timeCount = -1;
    private BasicCallback basicCallback = new BasicCallback() {
        @Override
        public void gotResult(int i, String s) {
            if (i == 0) {
                if (loginFlag) {
                    String phone = phoneNumInput.getText().toString();
                    BmobAction.login(phone,Secure.encode(phone));
                    timeCount = -1;
                    Intent intent = new Intent(LoginAndSignActivity.this, MainActivity.class);
                    intent.putExtra("loginFlag", loginFlag);
                    intent.putExtra("isRegister", isRegister);
                    startActivity(intent);
                    finish();
                } else {
                    loginFlag = true;
                    handlerLoginAction(loginFlag);
                }
            } else {
                toast(s);
            }

        }
    };

    @OnClick(R.id.requireCheckCode)
    void requireSmsCode() {
        String phoneNumber = phoneNumInput.getText().toString();
        if (phoneNumber.length() != 11) {
            toast("非法手机号码");
            return;
        }
        if (timeCount > 0) {
            return;
        }
        InputUtils.hideInputKeyboard(phoneNumInput);
        timeCount = 60;
        if (handler == null)
            handler = new MyHandler(reqCheckCodeBtn);
        handler.resetTimeCount();
        handler.sendEmptyMessageDelayed(0, 1000);
        reqCheckCodeBtn.setEnabled(false);
        smsServer.getSMSCode("+86", phoneNumber);
    }


    @OnClick(R.id.btn_account)
    void accountLogin() {
        startActivityForResult(new Intent(this, LoginActivity.class), 1);
    }

    @OnClick(R.id.btn_login)
    void login() {
        isRegister = false;
        loginFlag = true;
        tittleTv.setText("手机号登录");
        submitAction();
    }

    @OnClick(R.id.btn_register)
    void register() {
        isRegister = true;
        loginFlag = false;
        tittleTv.setText("手机号注册");
        submitAction();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_register);
        bindView(this);

        smsServer = new SMSServer(getApplicationContext(), new SMSServer.SmsCallback() {
            @Override
            public void onSmsCallback(final int action, final String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toast(data);
                        reqCheckCodeBtn.setEnabled(true);
                        if (action == SMSServer.ACTION_SUBMIT_CODE || action == SMSServer.ACTION_VERIFICATION_SUCCESS) {
                            handlerLoginAction(loginFlag);
                        }
                    }
                });
            }

        });

        List<String> pers = new ArrayList<>();
        pers.add(Manifest.permission.READ_SMS);
        pers.add(Manifest.permission.READ_PHONE_STATE);
        pers.add(Manifest.permission.READ_CONTACTS);
        pers.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        pers.add(Manifest.permission.RECEIVE_SMS);
        Permission.instance().check(new CallBack<List<String>>() {
            @Override
            public void onResult(List<String> result) {

            }
        }, this, pers);

    }

    private void handlerLoginAction(boolean loginFlag) {
        String phone = phoneNumInput.getText().toString();
        if (loginFlag) {
            UserManager.instance().login(phone, basicCallback);
        } else {
            JMessageClient.register(phone, Secure.encode(phone), basicCallback);
            BmobAction.register(phone,Secure.encode(phone));
        }
    }

    private void submitAction() {
        String phoneNumber = phoneNumInput.getText().toString();
        String smsCode = checkCodeInput.getText().toString();
        if (TextUtils.isEmpty(smsCode)) {
            toast("没有输入验证码");
            return;
        }
        smsServer.submitSMSCode("+86", phoneNumber, smsCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        smsServer.destroy();
        smsServer = null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 100)
            finish();
    }

    private static class MyHandler extends Handler {
        SoftReference<Button> reqCheckCodeBtnSR;
        int timeCount = 60;

        MyHandler(Button button) {
            super();
            reqCheckCodeBtnSR = new SoftReference<Button>(button);
        }

        void resetTimeCount() {
            timeCount = 60;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                timeCount--;
                if (reqCheckCodeBtnSR.get() != null) {
                    if (timeCount > 0) {
                        sendEmptyMessageDelayed(0, 1000);
                        reqCheckCodeBtnSR.get().setText("请等待 " + timeCount + " 秒");
                    } else {
                        reqCheckCodeBtnSR.get().setText("获取验证码");
                    }
                }
            }
        }
    }
}
