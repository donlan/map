package dong.lan.mapeye.views;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Unbinder;
import dong.lan.mapeye.R;

/**
 * Created by 梁桂栋 on 2016/7/9.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */
public class CollectPointsActivity extends BaseActivity implements BDLocationListener {

    @BindView(R.id.collect_content_tv)
    TextView conllectContent;
    @BindView(R.id.collect_lat_tv)
    EditText latEt;
    @BindView(R.id.collect_lng_tv)
    EditText lngEt;
    @BindView(R.id.collect_save)
    TextView save;
    Unbinder unbinder;
    private LocationClient locationClient;
    private ClipboardManager clipboardManager = null;
    private boolean getPosition = false;

    @OnClick(R.id.collect_save)
    public void save() {
        if (getPosition){
            toast("正在获取百度定位信息");
            return;
        }
        getPosition = true;
        locationClient.start();
    }

    @OnLongClick(R.id.collect_content_tv)
    public boolean getContent() {
        if(clipboardManager==null){
            clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        }
        clipboardManager.setText(conllectContent.getText().toString());
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_points);
        unbinder = ButterKnife.bind(this);
        locationClient = new LocationClient(this);
        locationClient.registerLocationListener(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setTimeOut(5000);
        option.setScanSpan(5000);
        locationClient.setLocOption(option);
    }


    public void setContent(double lat, double lng) {
        conllectContent.append("北斗经纬度："+latEt.getText().toString()+","+lngEt.getText().toString()+"\n");
        conllectContent.append("百度经纬度："+lat+","+lng+"\n");
        conllectContent.append("------------\n");
        locationClient.stop();
        getPosition = false;
        toast("保存成功");
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if(bdLocation==null)
            return;
        setContent(bdLocation.getLatitude(),bdLocation.getLongitude());
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        locationClient.unRegisterLocationListener(this);
    }
}
