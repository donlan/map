package dong.lan.mapeye.contracts;

import android.support.design.widget.TextInputLayout;
import android.view.View;

/**
 * Created by 梁桂栋 on 16-11-8 ： 下午3:25.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public final class LoginAndSignContract {
    private LoginAndSignContract() {
    }

    public interface loginAndSignView {
        String KEY_IS_LOGIN = "isLogin";

        /**
         * 通用方法，用来设定指定TextInputLayout的显示文字
         *
         * @param textLayout
         * @param text
         */
        void setTextLayout(TextInputLayout textLayout, String text);

        /**
         * 登录界面的切换动画
         *
         * @param gone    将要隐藏的view
         * @param visible 将要显示的view
         */
        void switcherAnim(View gone, View visible);

        /**
         * 检查用户输入并注册
         */
        void checkAndRegister();

        /**
         * 清楚指定的TextInputLayout的错误显示信息
         *
         * @param curThis
         * @param other
         */
        void clearErrorText(TextInputLayout curThis, TextInputLayout other);

        /**
         * 登录成功后停止加载动画
         */
        void stopLogin();

        /**
         * 注册成功后停止加载动画
         */
        void stopRegister();
    }

    public interface Presenter extends BaseLifeCyclePresenter {

        /**
         * 登录操作
         * @param username 用户名
         * @param password 密码
         */
        void register(String username, String password);

        /**
         * 登录操作
         * @param username 用户名
         * @param password 密码
         */
        void login(String username, String password);
    }
}
