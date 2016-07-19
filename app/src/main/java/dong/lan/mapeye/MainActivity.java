package dong.lan.mapeye;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import dong.lan.mapeye.activities.BaseActivity;
import dong.lan.mapeye.activities.CollectPointsActivity;
import dong.lan.mapeye.activities.OfflineMapActivity;
import dong.lan.mapeye.fragment.MapFragment;
import dong.lan.mapeye.utils.PointConvert;
import dong.lan.mapeye.utils.SPHelper;

public class MainActivity extends BaseActivity {

    CoordinateConverter.CoordType CONVERT_TYPE;
    SMSReceiver smsReceiver;
    IntentFilter smsFilter;
    String phone;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, mapFragment, "MAP").show(mapFragment).commit();

        if (Build.VERSION.SDK_INT >= 23 && !Settings.System.canWrite(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 601);
        }
        if (Build.VERSION.SDK_INT >= 23
                && (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        }

        if (Build.VERSION.SDK_INT >= 23
                && (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS) !=
                PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_SMS,
                    Manifest.permission.BROADCAST_SMS,
                    Manifest.permission.RECEIVE_SMS
            }, 2);
        }

        SPHelper.init(this);
        phone = SPHelper.get("phone");
        String d = SPHelper.get("distance");
        if(!d.equals("")){
            mapFragment.setDistance(Integer.valueOf(d));
        }
        smsFilter = new IntentFilter();
        smsFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        smsReceiver = new SMSReceiver();
        registerReceiver(smsReceiver, smsFilter);

        try{
            String latStr = SPHelper.get("padding_lat");
            String lngStr = SPHelper.get("padding_lng");

            if(latStr.equals("") || lngStr.equals(""))
                return;
            double lat  = Double.valueOf(latStr);
            double lng  = Double.valueOf(lngStr);
            PointConvert.init(lat,lng);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            setMonitorPhone();
        } else if (id == R.id.action_offline_map) {
            startActivity(new Intent(MainActivity.this, OfflineMapActivity.class));
        } else if (id == R.id.action_test) {
            //receiveSms("1110", "B38.002665,112.442631");
            receiveSms("1110", "B38.002732,112.442571");
        }else if(id==R.id.convert_gps){
            CONVERT_TYPE = CoordinateConverter.CoordType.GPS;
        }else if(id==R.id.convert_common){
            CONVERT_TYPE = CoordinateConverter.CoordType.COMMON;
        }else if(id==R.id.convert_no){
            CONVERT_TYPE =null;
        }else if(id==R.id.map_nomon){
            mapFragment.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        }else if(id==R.id.map_earth){
            mapFragment.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        }else if(id==R.id.action_to_collect){
            startActivity(new Intent(MainActivity.this, CollectPointsActivity.class));
        }else if(id==R.id.action_set_distance){
            setAlertDistance();
        }else if(id==R.id.action_set_padding){
            setPaddingDistance();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setPaddingDistance(){
        View view = LayoutInflater.from(this).inflate(R.layout.base_2et_alert, null);
        final EditText et_lat = (EditText) view.findViewById(R.id.base_alert_et1);
        final EditText et_lng = (EditText) view.findViewById(R.id.base_alert_et2);
        et_lat.setHint("纬度偏移量");
        et_lng.setHint("经度偏移量");
        new AlertDialog.Builder(this)
                .setTitle("输入北斗与百度经纬度偏移量")
                .setMessage("负数直接在数字前加上-负号，此偏移量将会与百度经纬度直接相加")
                .setView(view)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String latStr = et_lat.getText().toString();
                        String lngStr = et_lng.getText().toString();
                        try{
                            double lat  = Double.valueOf(latStr);
                            double lng  = Double.valueOf(lngStr);
                            SPHelper.addOrUpdate("padding_lat",latStr);
                            SPHelper.addOrUpdate("padding_lng",lngStr);
                            PointConvert.init(lat,lng);
                            Toast("保存成功");
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast("请确保输入的经纬度是双精度整数");
                        }
                    }
                }).show();
    }

    private void setMonitorPhone(){
        View view = LayoutInflater.from(this).inflate(R.layout.alert_label_text, null);
        final EditText editText = (EditText) view.findViewById(R.id.fence_label_et);
        editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        editText.setHint("手机号码");
        new AlertDialog.Builder(this)
                .setTitle("输入需要监听收到短信的手机号码")
                .setView(view)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().length() < 1) {
                            Toast("手机号码不能是空的");
                            return;
                        }
                        phone = editText.getText().toString();
                        SPHelper.addOrUpdate("phone",phone);
                        Toast("保存成功");
                    }
                }).show();
    }

    private void setAlertDistance(){
        View view = LayoutInflater.from(this).inflate(R.layout.alert_label_text, null);
        final EditText editText = (EditText) view.findViewById(R.id.fence_label_et);
        editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        editText.setHint("整数的距离值");
        new AlertDialog.Builder(this)
                .setTitle("输入监听目标的提醒距离范围")
                .setView(view)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String disStr =editText.getText().toString();
                        try{
                            int d = Integer.valueOf(disStr);
                            SPHelper.addOrUpdate("distance",String.valueOf(d));
                            mapFragment.setDistance(d);
                            Toast("保存成功");
                        }catch (Exception e){
                            Toast("输入的距离值不符合规范");
                        }
                    }
                }).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public void receiveSms(String from, String content) {
        Toast("收到：" + from + "(设置监听的号码是：" + phone + ") 的短信：" + content);
        if(phone==null){
            Toast("未设置监听手机号码");
            return;
        }
        if (content.charAt(0) == 'B') {
            content = content.substring(1);
            double lat = 0;
            double lng = 0;
            String[] p = content.split(",");
            lat = Double.parseDouble(p[0]);
            lng = Double.parseDouble(p[1]);
            LatLng desLatLng = PointConvert.convert(lat,lng);
            System.out.println("OLD_SMS:" + lat + "," + lng);
            Toast("解析短信的经纬度为："+desLatLng.latitude + "," + desLatLng.longitude);
            mapFragment.onLocationReceived(desLatLng);
        }else{
            Toast("请确保短信内容格式为：Bxx.xxxx,xx.xxxx");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }

    class SMSReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle data = intent.getExtras();
            Object[] pdus = (Object[]) data.get("pdus");
            SmsMessage[] smsMessages = new SmsMessage[pdus.length];
            for (int i = 0, l = pdus.length; i < l; i++) {
                smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }

            String from = smsMessages[0].getOriginatingAddress();
            StringBuilder sb = new StringBuilder();
            for (SmsMessage message : smsMessages) {
                sb.append(message.getMessageBody());
            }
            receiveSms(from, sb.toString());
        }

    }
}
