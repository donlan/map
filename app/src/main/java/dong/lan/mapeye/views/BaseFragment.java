package dong.lan.mapeye.views;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by 梁桂栋 on 16-11-1 ： 下午7:50.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class BaseFragment extends Fragment {
    public static final String ARG_TITTLE_TAG ="fragment_tittle";
    protected Unbinder unbinder;
    protected View content;
    protected boolean isInit = false;
    public void toast(String text){
        Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
    }

    public void show(String text){
        Snackbar.make(getView(),text,Snackbar.LENGTH_SHORT).show();
    }

    protected void bindView(Fragment fragment){
       unbinder = ButterKnife.bind(fragment,content);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(unbinder!=null)
            unbinder.unbind();
    }
}
