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

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.model.UserInfo;
import dong.lan.library.LabelTextView;
import dong.lan.mapeye.R;
import dong.lan.mapeye.contracts.SearchContact;
import dong.lan.mapeye.model.users.User;
import dong.lan.mapeye.presenter.SearchContactPresenter;

/**
 * Created by 梁桂栋 on 16-11-16 ： 上午10:56.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class SearchContactActivity extends BaseActivity implements SearchContact.View {

    private static final String TAG = "SearchContactActivity";
    private String[] phoneCode = new String[]{"+86","+852","+853","+886","+81","+1","+44","+61"};
    private String curPhoneCode = "86-";

    @BindView(R.id.query_et)
    EditText queryText;
    @BindView(R.id.searchContactsList)
    RecyclerView contactList;

    @OnClick(R.id.phoneCodeTv)
    void setPhoneCode(final View view) {
        new AlertDialog.Builder(this)
                .setTitle("设置手机区位码")
                .setSingleChoiceItems(phoneCode, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((TextView) view).setText(phoneCode[which]);
                        curPhoneCode = phoneCode[which].substring(1) + "-";
                        dialog.dismiss();
                    }
                }).show();
    }

    @OnClick(R.id.query_ib)
    public void startQueryContact() {
        if (TextUtils.isEmpty(queryText.getText().toString()))
            return;
        presenter.queryContact(queryText.getText().toString());
    }

    private SearchContactPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contact);
        ButterKnife.bind(this);
        presenter = new SearchContactPresenter(this);
    }


    Adapter adapter;
    List<UserInfo> users;

    @Override
    public void setContactsAdapter(List<UserInfo> users) {
        if (adapter == null) {
            this.users = new ArrayList<>();
            this.users.addAll(users);
            adapter = new Adapter();
            contactList.setLayoutManager(new GridLayoutManager(this, 1));
            contactList.setAdapter(adapter);
        } else {
            this.users.clear();
            this.users.addAll(users);
            adapter.notifyDataSetChanged();
        }
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SearchContactActivity.this).inflate(R.layout.item_search_contact, null);
            final ViewHolder holder = new ViewHolder(view);
            holder.addAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.sendInvite(users.get(holder.getLayoutPosition()));
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            UserInfo u = users.get(position);
            holder.name.setText(User.getUserDescriber(u));
        }

        @Override
        public int getItemCount() {
            return users == null ? 0 : users.size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.itemSearchAdd)
        LabelTextView addAction;
        @BindView(R.id.itemSearchHead)
        ImageView head;
        @BindView(R.id.itemSearchName)
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}


