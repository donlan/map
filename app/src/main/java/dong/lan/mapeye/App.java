package dong.lan.mapeye;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * 项目：  MapEye
 * 作者：  梁桂栋
 * 日期：  7/2/2016  15:19.
 * Email: 760625325@qq.com
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}
