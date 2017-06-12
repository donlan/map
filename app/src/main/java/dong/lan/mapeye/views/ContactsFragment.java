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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dong.lan.library.LabelTextView;
import dong.lan.mapeye.R;
import dong.lan.mapeye.common.UserManager;
import dong.lan.mapeye.contracts.ContactsContract;
import dong.lan.mapeye.contracts.UserCenterContract;
import dong.lan.mapeye.model.users.User;
import dong.lan.mapeye.presenter.ContactsPresenter;

/**
 * Created by 梁桂栋 on 16-11-15 ： 下午2:02.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class ContactsFragment extends BaseFragment implements ContactsContract.View {

    ContactsPresenter presenter;
    private View cacheView;

    @BindView(R.id.contactsList)
    RecyclerView contactsList;

    public static ContactsFragment newInstance(String tittle) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TITTLE_TAG, tittle);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (cacheView == null) {
            cacheView = inflater.inflate(R.layout.framgment_contacts, container, false);
            presenter = new ContactsPresenter(this);
            ButterKnife.bind(this, cacheView);
            contactsList.setLayoutManager(new LinearLayoutManager(getContext()));
            presenter.loadAllContacts();
        }
        return cacheView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void initAdapter() {
        if (contactsList.getAdapter() == null)
            contactsList.setAdapter(new Adapter());
    }

    @Override
    public void refreshAdapter(int position) {
        if (position <= 1) {
            contactsList.getAdapter().notifyDataSetChanged();
        } else {
            contactsList.getAdapter().notifyItemChanged(position);
        }
    }


    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_contact, null);
            final ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.count.setVisibility(View.GONE);
                    Intent intent = new Intent(getActivity(), UserCenterActivity.class);
                    intent.putExtra(UserCenterContract.View.KEY_USERNAME,
                            presenter.getContact(viewHolder.getLayoutPosition()).identifier());
                    startActivity(intent);
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            User contact = presenter.getContact(holder.getLayoutPosition());
            holder.username.setText(UserManager.instance().getUserDisplayName(contact));
        }

        @Override
        public int getItemCount() {
            return presenter.getContactCount();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemContactHead)
        ImageView head;
        @BindView(R.id.itemContactName)
        TextView username;
        @BindView(R.id.itemContactMsg)
        TextView msg;
        @BindView(R.id.itemContactCount)
        LabelTextView count;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
