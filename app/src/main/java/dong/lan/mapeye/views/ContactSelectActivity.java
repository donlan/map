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

package dong.lan.mapeye.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import dong.lan.mapeye.R;
import dong.lan.mapeye.common.JMCenter;
import dong.lan.mapeye.common.UserManager;
import dong.lan.mapeye.model.Record;
import dong.lan.mapeye.model.message.CMDMessage;
import dong.lan.mapeye.model.users.Contacts;
import dong.lan.mapeye.model.users.User;
import dong.lan.mapeye.utils.NetUtils;
import dong.lan.mapeye.views.customsView.RecycleViewDivider;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;

/**
 * Created by 梁桂栋 on 16-11-16 ： 下午11:31.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class ContactSelectActivity extends BaseActivity {

    private static final String TAG = ContactSelectActivity.class.getSimpleName();
    @BindView(R.id.usersList)
    RecyclerView userList;
    @BindView(R.id.done)
    TextView done;
    private boolean isDoing = false;
    private Adapter adapter;
    private String groupId;
    private Realm realm;
    private Contacts contacts;

    @OnClick(R.id.back)
    public void back() {
        finish();
    }

    @OnClick(R.id.done)
    public void doneAction() {
        if (!NetUtils.isAvailableByPing(this)) {
            show("网络不可用");
            return;
        }
        if (isDoing) {
            show("发送请求中...");
            return;
        }
        isDoing = true;
        show("开始发送请求...");
        if (adapter == null || adapter.getUserInfos() == null || adapter.getUserInfos().isEmpty()) {
            show("你没有联系人 T ^ T");
            isDoing = false;
            return;
        }
        final List<User> userInfos = adapter.getUserInfos();
        for (final User u :
                userInfos) {

            Message jmesage = new JMCenter.JMessage(CMDMessage.CMD_SEND_MONITOR_INVITE,
                    u.identifier(), "正在请求对你进行位置监控")
                    .appendStringExtra(JMCenter.EXTRAS_IDENTIFIER, u.identifier())
                    .appendStringExtra(JMCenter.EXTRAS_RECORD_ID, groupId).build();
            JMCenter.sendMessage(jmesage, new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    Logger.d(i + "," + s);
                    if (i == 0) {
                        Realm realm = Realm.getDefaultInstance();
                        UserManager.instance().addGroupMembers(groupId, userInfos, realm);
                        realm.close();
                        toast("发送成功");
                        setResult(200);
                        finish();
                    } else {
                        toast(s);
                    }
                }
            });
        }
        isDoing = false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        bindView(this);
        realm = Realm.getDefaultInstance();
        if (getIntent().hasExtra(Record.GROUP_ID)) {
            groupId = getIntent().getStringExtra(Record.GROUP_ID);
            if (TextUtils.isEmpty(groupId)) {
                return;
            }
            userList.setLayoutManager(new GridLayoutManager(this, 1));
            userList.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL, 5.0f));
            contacts = UserManager.instance().getContacts(realm);
            if (contacts != null) {
                contacts.addChangeListener(new RealmChangeListener<RealmModel>() {
                    @Override
                    public void onChange(RealmModel element) {
                        if (adapter == null) {
                            adapter = new Adapter();
                            userList.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }

        } else {
            show("没有此记录相关的详细人配置");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        if (adapter != null)
            adapter.destroy();
        adapter = null;
        realm = null;
    }

    class Adapter extends RecyclerView.Adapter<SelectHolder> {
        private HashSet<Integer> set = new HashSet<>();
        private LayoutInflater inflater;


        Adapter() {
            inflater = LayoutInflater.from(ContactSelectActivity.this);
        }

        @Override
        public SelectHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = inflater.inflate(R.layout.item_select_user, null);
            final SelectHolder holder = new SelectHolder(v);
            holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        set.add(holder.getLayoutPosition());
                    } else if (set.contains(holder.getLayoutPosition())) {
                        set.remove(holder.getLayoutPosition());
                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(SelectHolder holder, final int i) {
            User user = contacts.getContacts().get(i);
            if (user.identifier().equals(UserManager.instance().myIdentifier())) {
                holder.check.setVisibility(View.GONE);
            } else {
                holder.check.setVisibility(View.VISIBLE);
                holder.check.setChecked(set.contains(i));
            }
            holder.name.setText(user.displayName());
        }

        @Override
        public int getItemCount() {
            return contacts == null || contacts.getContacts() == null ? 0 : contacts.getContacts().size();
        }

        public HashSet<Integer> getSet() {
            return set;
        }

        List<User> getUserInfos() {
            List<User> t = new ArrayList<>();
            for (Integer aSet : set) {
                t.add(contacts.getContacts().get(aSet));
            }
            return t;
        }

        public void destroy() {
            if (contacts != null)
                contacts.removeChangeListeners();
            contacts = null;
        }
    }

    class SelectHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.itemSelectCheck)
        CheckBox check;
        @BindView(R.id.itemSelectHead)
        ImageView head;
        @BindView(R.id.itemSelectName)
        TextView name;

        SelectHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}
