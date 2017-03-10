package dong.lan.mapeye.views;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewStub;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import dong.lan.mapeye.R;
import dong.lan.mapeye.contracts.LoginAndSignContract;
import dong.lan.mapeye.presenter.LoginRegisterPresenter;
import dong.lan.mapeye.views.customsView.CircleTextView;
import dong.lan.mapeye.views.customsView.LabelTextView;
import dong.lan.mapeye.views.customsView.LoadingTextView;

/**
 * Created by 梁桂栋 on 16-11-8 ： 下午3:28.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class LoginAndSignActivity extends BaseActivity implements LoginAndSignContract.loginAndSignView {

    private static final String TAG = "LoginAndSignActivity";
    @BindView(R.id.switcher)
    CircleTextView switcher;

    @OnFocusChange(R.id.login_pwd)
    public void loginUsernameFocus(){
        clearErrorText(loginpwdInputLayout,loginUNLayout);
    }
    @OnFocusChange(R.id.login_username)
    public void loginPwdFocus(){
        clearErrorText(loginpwdInputLayout,loginUNLayout);
    }


    @OnClick(R.id.switcher)
    public void switcherFunc() {
        if (isLogin) {
            setTitle("注册");
            switcher.setBgColorFromRes(R.color.md_orange_500);
            switcher.setText("登录");
            if (registerView == null) {
                registerView = registerStub.inflate();
                register = ButterKnife.findById(registerView, R.id.register);
                regPwdLayout = ButterKnife.findById(registerView, R.id.regPwdLayout);
                regPwd = ButterKnife.findById(registerView, R.id.regPwd);
                regUsernameLayout = ButterKnife.findById(registerView, R.id.regUsernameLayout);
                regUsername = ButterKnife.findById(registerView, R.id.regUsername);
                register.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkAndRegister();
                    }
                });
                regUsername.setOnFocusChangeListener(listener);
                regPwd.setOnFocusChangeListener(listener);
            }
            loginView.setVisibility(View.GONE);
            registerView.setVisibility(View.VISIBLE);
            isLogin = false;
            switcherAnim(loginView, registerView);

        } else {
            setTitle("登录");
            switcher.setBgColorFromRes(R.color.colorAccent);
            switcher.setText("注册");
            registerView.setVisibility(View.GONE);
            loginView.setVisibility(View.VISIBLE);
            isLogin = true;
            switcherAnim(registerView, loginView);
        }
    }

    private View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            clearErrorText(regUsernameLayout,regPwdLayout);
        }
    };


    @BindView(R.id.cardView)
    CardView loginView;
    @BindView(R.id.registerStub)
    ViewStub registerStub;
    @BindView(R.id.login_username_layout)
    TextInputLayout loginUNLayout;
    @BindView(R.id.login_pwd_layout)
    TextInputLayout loginpwdInputLayout;
    @BindView(R.id.login_pwd)
    TextInputEditText loginPwdInput;
    @BindView(R.id.login_username)
    TextInputEditText loginUsername;
    @BindView(R.id.login)
    LoadingTextView loginTV;
    @OnClick(R.id.login)
    public void login() {
        String username = loginUsername.getText().toString();
        if (username.equals("")) {
            setTextLayout(loginUNLayout, "用户名不能为空");
            return;
        }
        String password = loginPwdInput.getText().toString();
        if (password.length() < 6) {
            setTextLayout(loginpwdInputLayout, "密码不能少于6个字符");
            return;
        }
        loginTV.startLoading("登录中...");
        presenter.login(username, password);
    }

    private boolean isLogin = true;
    private View registerView;
    private TextInputEditText regUsername;
    private TextInputEditText regPwd;
    private TextInputLayout regPwdLayout;
    private LoadingTextView register;
    private TextInputLayout regUsernameLayout;
    private LoginRegisterPresenter presenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign);

        bindView(this);

        presenter = new LoginRegisterPresenter(this);

        init();
    }


    private void init(){
    }
    public void checkAndRegister() {
        String username = regUsername.getText().toString();
        if (username.equals("")) {
            setTextLayout(regUsernameLayout, "用户名不能为空");
            return;
        }
        String password = regPwd.getText().toString();
        if (password.length() < 6) {
            setTextLayout(regPwdLayout, "密码不能少于6个字符");
            return;
        }
        register.startLoading("注册中...");
        presenter.register(username, password);
    }

    public void setTextLayout(TextInputLayout textLayout, String text) {
        textLayout.setErrorEnabled(true);
        textLayout.setError(text);
    }
    public void clearErrorText(TextInputLayout curThis,TextInputLayout other){
        curThis.setErrorEnabled(false);
        other.setErrorEnabled(false);
    }

    @Override
    public void stopLogin() {
        loginTV.stopLoading();
    }

    @Override
    public void stopRegister() {
        register.stopLoading();
    }

    public void switcherAnim(View gone, View visible) {
        gone.animate().alpha(0f).setDuration(300).start();
        visible.animate().alpha(1f).setDuration(300).start();
        ObjectAnimator.ofFloat(visible, "scaleX", 0.7f, 1f).setDuration(300).start();
        ObjectAnimator.ofFloat(visible, "scaleY", 0.7f, 1f).setDuration(300).start();
    }


}
