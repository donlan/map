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
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.tlslibrary.helper.JMHelper;

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
import dong.lan.mapeye.model.users.User;
import dong.lan.mapeye.model.message.CMDMessage;
import dong.lan.mapeye.utils.NetUtils;
import dong.lan.mapeye.views.customsView.RecycleViewDivider;
import io.realm.Realm;

/**
 * Created by 梁桂栋 on 16-11-16 ： 下午11:31.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class ContactSelectActivity extends BaseActivity {

    private boolean isDoing = false;
    @BindView(R.id.usersList)
    RecyclerView userList;

    @OnClick(R.id.back)
    public void back() {
        finish();
    }

    @BindView(R.id.done)
    TextView done;

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
        final List<TIMUserProfile> userInfos = adapter.getUserInfos();
        for (final TIMUserProfile u :
                userInfos) {

            Message jmesage = new JMCenter.JMessage(CMDMessage.CMD_SEND_MONITOR_INVITE,
                    JMHelper.getJUsername(u.getIdentifier()), "正在请求对你进行位置监控")
                    .appendStringExtra(JMCenter.EXTRAS_IDENTIFIER, u.getIdentifier())
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
//            TIMMessage message = MessageCreator.crateNormalTextCmdMessage(
//                    CMDMessage.CMD_SEND_MONITOR_INVITE, "正在请求对你进行位置监控");
//            message.setCustomStr(MessageHelper.createRecordContactExtras(groupId, u.getIdentifier()));
//            MessageHelper.sendTIMMessage(u.getIdentifier(), message, new TIMValueCallBack<TIMMessage>() {
//                @Override
//                public void onError(int i, String s) {
//                    Logger.d(i + "," + s);
//                }
//
//                @Override
//                public void onSuccess(TIMMessage message) {
//                    Logger.d(message.getConversation().getPeer());
//                    toast("发送成功");
//                    MonitorManager.instance().post(message);
//                }
//            });
        }
        isDoing = false;
    }


    private Adapter adapter;
    private String groupId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        bindView(this);
        if (getIntent().hasExtra(Record.GROUP_ID)) {
            groupId = getIntent().getStringExtra(Record.GROUP_ID);
            if (TextUtils.isEmpty(groupId))
                return;
            userList.setLayoutManager(new GridLayoutManager(this, 1));
            userList.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL, 5.0f));

            TIMFriendshipManager.getInstance().getFriendList(new TIMValueCallBack<List<TIMUserProfile>>() {
                @Override
                public void onError(int i, String s) {
                    toast(s);
                }

                @Override
                public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                    if (timUserProfiles == null || timUserProfiles.isEmpty())
                        toast("没有任何联系人");
                    else {

                        adapter = new Adapter(timUserProfiles);
                        userList.setAdapter(adapter);
                    }
                }
            });
        } else {
            show("没有此记录相关的详细人配置");
        }
    }


    class Adapter extends RecyclerView.Adapter<SelectHolder> {
        private List<TIMUserProfile> userInfos;
        private HashSet<Integer> set = new HashSet<>();
        private LayoutInflater inflater;

        Adapter(List<TIMUserProfile> userInfos) {
            this.userInfos = userInfos;
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
            TIMUserProfile user = userInfos.get(i);
            if (user.getIdentifier().equals(UserManager.instance().myIdentifier())) {
                holder.check.setVisibility(View.GONE);
            } else {
                holder.check.setVisibility(View.VISIBLE);
                holder.check.setChecked(set.contains(i));
            }
            holder.name.setText(User.getUserDescriber(userInfos.get(i)));
        }

        @Override
        public int getItemCount() {
            return userInfos.size();
        }

        public HashSet<Integer> getSet() {
            return set;
        }

        List<TIMUserProfile> getUserInfos() {
            List<TIMUserProfile> t = new ArrayList<>();
            for (Integer aSet : set) {
                t.add(userInfos.get(aSet));
            }
            return t;
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
