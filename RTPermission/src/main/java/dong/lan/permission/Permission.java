package dong.lan.permission;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 梁桂栋 on 17-2-16 ： 下午5:07.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: SmartTrip
 */

public class Permission {
    private Permission() {
    }

    private static Permission instance;
    private CallBack<List<String>> callback;

    public static Permission instance() {
        if (instance == null) {
            instance = new Permission();
        }
        return instance;
    }


    public void onDestroy() {
        callback = null;
    }


    public void check(CallBack<List<String>> callback, Activity context, List<String> permissions) {

        if (callback != this.callback) {
            this.callback = null;
            this.callback = callback;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<String> denies = new ArrayList<>();
            for (String perm :
                    permissions) {
                if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                    denies.add(perm);
                }
            }
            requestPermission(context, denies);
        } else {
            callback.onResult(null);
        }
    }

    private void requestPermission(Activity activity, List<String> reqList) {
        if (!reqList.isEmpty()) {
            String req[] = new String[reqList.size()];
            reqList.toArray(req);
            ActivityCompat.requestPermissions(activity, req, 1);
        } else {
            callback.onResult(null);
        }
    }

    public void handleRequestResult(final Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        List<String> denies = null;
        for (int i = 0, s = grantResults.length; i < s; i++) {
            if (PackageManager.PERMISSION_GRANTED != grantResults[i]) {
                if (denies == null)
                    denies = new ArrayList<>();
                denies.add(permissions[i]);
            }
        }
        if (denies == null || denies.isEmpty()) {
            callback.onResult(null);
        } else {
            if (activity != null) {
                final List<String> finalDenies = denies;
                new AlertDialog.Builder(activity)
                        .setMessage("如需正常使用软件功能，需要申请权限。是否重新申请权限？")
                        .setPositiveButton("重新申请", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermission(activity, finalDenies);
                            }
                        }).setNegativeButton("取消", null)
                        .show();
            }
        }
    }


}
