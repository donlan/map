package dong.lan.mapeye.activities;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * 项目：  MapEye
 * 作者：  梁桂栋
 * 日期：  7/3/2016  23:38.
 * Email: 760625325@qq.com
 */
public class BaseActivity  extends AppCompatActivity {


    public void Toast(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }
}
