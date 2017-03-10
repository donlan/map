package dong.lan.mapeye.presenter;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.tencent.TIMGroupManager;
import com.tencent.TIMValueCallBack;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import dong.lan.mapeye.R;
import dong.lan.mapeye.common.UserManager;
import dong.lan.mapeye.contracts.AddRecordContract;
import dong.lan.mapeye.events.MainEvent;
import dong.lan.mapeye.model.users.Contact;
import dong.lan.mapeye.model.users.Group;
import dong.lan.mapeye.model.Point;
import dong.lan.mapeye.model.Record;
import dong.lan.mapeye.model.users.User;
import dong.lan.mapeye.utils.MapUtils;
import dong.lan.mapeye.views.AddRecordActivity;
import dong.lan.mapeye.views.customsView.Dialog;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by 梁桂栋 on 16-11-7 ： 下午11:32.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class AddRecordPresenter implements AddRecordContract.Presenter {

    private AddRecordActivity view;
    private int type = Record.TYPE_CIRCLE;
    private BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.nav);
    private BitmapDescriptor locBitmap = BitmapDescriptorFactory.fromResource(R.drawable.tip);
    private List<LatLng> points = new ArrayList<>();
    private ProgressDialog progress;
    private int radius;

    public AddRecordPresenter(AddRecordActivity view) {
        this.view = view;
    }

    @Override
    public void resetRecord() {
        points.clear();
        view.clearMap();
    }

    @Override
    public void addPoint(Point point) {

    }

    @Override
    public void saveRecord() {
        if (type == Record.TYPE_CIRCLE && points.size() > 1) {
            view.show("请先点击屏幕下方的 半径 按钮");
            return;
        }
        if (type == Record.TYPE_FENCE && points.size() < 3) {
            view.show("请先点击屏幕下方的 围栏 按钮");
            return;
        }
        if (type == Record.TYPE_ROUTE && points.size() < 2) {
            view.show("请先点击屏幕下方的 路径 按钮");
            return;
        }

        if (progress == null) {
            progress = new ProgressDialog(view);
            progress.setCanceledOnTouchOutside(false);
        }
        View dialogView = LayoutInflater.from(view).inflate(R.layout.alert_label_text, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.fence_label_et);
        new AlertDialog.Builder(view)
                .setTitle("输入记录名称")
                .setView(dialogView)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().length() < 1) {
                            view.toast("不能是空标题");
                            return;
                        }

                        TIMGroupManager.getInstance().createGroup("Public",
                                Collections.singletonList(UserManager.instance().myIdentifier()),
                                editText.getText().toString(),
                                new TIMValueCallBack<String>() {
                                    @Override
                                    public void onError(int i, String s) {
                                        view.toast(s);
                                        dismissProgress();
                                    }

                                    @Override
                                    public void onSuccess(String groupId) {
                                        try {
                                            progress.setMessage("生成" + Record.getRecordTypeStr(type) + " 中...");
                                            progress.show();
                                            final String label = editText.getText().toString();
                                            Realm realm = Realm.getDefaultInstance();
                                            realm.beginTransaction();
                                            User user = realm.where(User.class).equalTo("identifier",UserManager.instance().myIdentifier()).findFirst();
                                            Record record = realm.createObject(Record.class);
                                            record.setId(groupId);
                                            record.setCreateTime(new Date());
                                            record.setOwn(user);
                                            record.setLabel(label);
                                            record.setInfo(label);
                                            record.setType(type);
                                            record.setRadius(radius);
                                            record.setPoints(new RealmList<Point>());
                                            for (int i = 0, s = points.size(); i < s; i++) {
                                                Point point = new Point(points.get(i));
                                                record.getPoints().add(point);
                                            }
                                            Group group = realm.createObject(Group.class);
                                            group.setOwner(user);
                                            group.setDescription(label);
                                            group.setGroupId(groupId);
                                            group.setMembers(new RealmList<Contact>());
                                            realm.commitTransaction();
                                            Record r = realm.copyFromRealm(record);
                                            realm.close();
                                            EventBus.getDefault().post(new MainEvent(MainEvent.CODE_ADDED_RECORD, r));
                                            view.toast("保存成功");
                                            dismissProgress();

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            view.toast(e.getMessage());
                                        } finally {
                                            dismissProgress();
                                        }
                                    }
                                });
                    }
                }).show();
    }

    private void dismissProgress() {
        if (progress != null && progress.isShowing())
            progress.dismiss();
    }

    @Override
    public void drawFence(BaiduMap baiduMap) {
        if (points.size() < 3) {
            view.toast("一个围栏至少需要三个标志点");
            return;
        }
        type = Record.TYPE_FENCE;
        baiduMap.clear();
        MapUtils.drawRecord(baiduMap, points, Record.TYPE_FENCE);
        MapUtils.onlyDrawMarker(baiduMap, points, bitmap);
    }

    @Override
    public void drawRoute(BaiduMap baiduMap) {
        if (points.size() < 2) {
            view.toast("一个路径至少需要两个标志点");
            return;
        }
        type = Record.TYPE_CIRCLE;
        baiduMap.clear();
        MapUtils.drawRecord(baiduMap, points, Record.TYPE_ROUTE);
        MapUtils.onlyDrawMarker(baiduMap, points, bitmap);
    }

    @Override
    public void drawAndAddPoint(BaiduMap baiduMap, LatLng latLng) {
        OverlayOptions option = new MarkerOptions()
                .position(latLng)
                .icon(bitmap);
        baiduMap.addOverlay(option);
        points.add(latLng);
        if (points.size() >= 2) {
            int size = points.size();
            PolylineOptions polylineOptions = new PolylineOptions().points(points.subList(size - 2, size))
                    .width(10).color(0xee33FF00).keepScale(true);
            baiduMap.addOverlay(polylineOptions);
        }
    }

    @Override
    public void drawCircleFence(final BaiduMap baiduMap) {
        if (points.size() > 1) {
            view.toast("圆形围栏只能有一个标记点");
            return;
        }
        type = Record.TYPE_CIRCLE;
        final Dialog dialog = new Dialog(view)
                .setupView(R.layout.normal_editable_dialog);
        dialog.bindText(R.id.dialog_tittle, "设定整数围栏半径（单位：米）")
                .bindClick(R.id.dialog_ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            radius = Integer.parseInt(dialog.getText(R.id.dialog_et));
                            MapUtils.drawCircle(baiduMap, points.get(0), radius);
                            dialog.dismiss();
                        } catch (Exception e) {
                            view.toast("半径只能是正整数");
                        }

                    }
                }).show();

    }

    @Override
    public void onStart() {

    }

    @Override
    public void location(BaiduMap baiduMap, LatLng latLng) {
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, locBitmap));
        baiduMap.animateMapStatus(u);
    }

    @Override
    public void onDestroy() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        if (locBitmap != null) {
            locBitmap.recycle();
            locBitmap = null;
        }
    }
}
