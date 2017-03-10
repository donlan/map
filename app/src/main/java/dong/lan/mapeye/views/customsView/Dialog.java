/*
 *
 *   Copyright (C) 2016 author : 梁桂栋
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

package dong.lan.mapeye.views.customsView;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.WeakHashMap;

/**
 * Created by 梁桂栋 on 16-11-16 ： 下午9:57.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class Dialog {
    private AlertDialog.Builder builder;
    private View view;
    private WeakHashMap<Integer, View> viewMap;
    private AlertDialog dialog;

    public Dialog(Context context) {
        builder = new AlertDialog.Builder(context);
        viewMap = new WeakHashMap<>();
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                viewMap.clear();
                view = null;
                viewMap = null;
                builder = null;

            }
        });
    }



    public Dialog setupView(int layoutId) {
        view = LayoutInflater.from(builder.getContext()).inflate(layoutId, null);
        builder.setView(view);
        return this;
    }

    public AlertDialog.Builder builder() {
        return builder;
    }

    public Dialog show(){
         dialog = builder.show();
        return this;
    }

    public void dismiss(){
        if(dialog!=null)
            dialog.dismiss();
    }

    public Dialog bindView(int id) {
        View v = view.findViewById(id);
        viewMap.put(id, v);
        return this;
    }

    public Dialog bindText(int viewId, String text) {
        View view = getView(viewId);
        if (view instanceof TextView)
            ((TextView) view).setText(text);
        return this;
    }

    public <T extends TextView> String getText(int viewId, Class<T> tClass) {
        View v = getView(viewId);
        return ((T) v).getText().toString();
    }
    public <T extends TextView> String getText(int viewId) {
        View v = getView(viewId);
        return ((T) v).getText().toString();
    }

    public Dialog bindClick(int viewId, View.OnClickListener listener) {
        View v = getView(viewId);
        v.setOnClickListener(listener);
        return this;
    }

    private View getView(int viewId) {
        View v = viewMap.get(viewId);
        if (v == null) {
            v = view.findViewById(viewId);
            viewMap.put(viewId, v);
        }
        return v;
    }

}
