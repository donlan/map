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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import dong.lan.mapeye.R;
import dong.lan.mapeye.bmob.BmobAction;
import dong.lan.mapeye.views.BaseActivity;
import dong.lan.mapeye.views.MainActivity;
import dong.lan.mapeye.views.customsView.EditTextWithClearButton;

/**
 * Created by 梁桂栋 on 2017/6/11 ： 10:21.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map:
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.login_user)
    EditTextWithClearButton loginUser;
    @BindView(R.id.login_pwd)
    EditText loginPwd;
    private boolean isRegister;
    private boolean loginFlag = true;

    @OnClick(R.id.btn_login)
    void loginClick(){
        final String username = loginUser.getText().toString();
        final String pwd = loginPwd.getText().toString();

        login(username,pwd);
    }

    private void login(String username, String pwd) {
        if(TextUtils.isEmpty(username) || username.length()<3 || username.length()>10){
            toast("用户名不合格");
            return;
        }

        if(TextUtils.isEmpty(pwd) || pwd.length()<6 || pwd.length()>16){
            toast("密码长度为6-16位字母数字组合");
            return;
        }
        BmobAction.login(username,pwd);
        JMessageClient.login(username, pwd, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if(i == 0){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("loginFlag", loginFlag);
                    intent.putExtra("isRegister", isRegister);
                    startActivity(intent);
                    setResult(100);
                    finish();
                }else{
                    toast("登录失败："+s);
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        setResult(100);
    }

    @OnClick(R.id.btn_register)
    void toRegister(){
        startActivityForResult(new Intent(this,RegisterActivity.class),1);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        bindView(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode==1){
            String username = data.getStringExtra("username");
            String pwd = data.getStringExtra("pwd");
            isRegister = true;
            login(username,pwd);

        }
    }
}
