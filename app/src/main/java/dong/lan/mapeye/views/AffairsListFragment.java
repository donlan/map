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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import dong.lan.mapeye.R;
import dong.lan.mapeye.adapter.AffairsAdapter;
import dong.lan.mapeye.adapter.BaseRealmAdapter;
import dong.lan.mapeye.common.MonitorManager;
import dong.lan.mapeye.contracts.AffairsContract;
import dong.lan.mapeye.model.Affair;
import dong.lan.mapeye.presenter.AffairsPresenter;
import dong.lan.mapeye.views.customsView.RecycleViewDivider;
import io.realm.RealmResults;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by 梁桂栋 on 16-12-8 ： 下午11:16.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class AffairsListFragment extends BaseFragment implements AffairsContract.View {

    public static AffairsListFragment newInstance(String tittle) {
        AffairsListFragment fragment = new AffairsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TITTLE_TAG, tittle);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.affairsList)
    RecyclerView affairsList;

    private AffairsPresenter presenter;


    private Subscription subscription;
    private AffairsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (content == null) {
            content = inflater.inflate(R.layout.fragment_affairs_list, container, false);
            bindView(this);
            affairsList.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            affairsList.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL, 8.0f));
            presenter = new AffairsPresenter(this);
        }
        return content;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isInit) {
            presenter.loadAllAffairsAsync();
            isInit = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null)
            presenter.onDestroy();
        if (subscription != null)
            subscription.unsubscribe();
        isInit = false;
    }

    @Override
    public void notifyAffairsChanged() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void setupAdapter(RealmResults<Affair> affairs) {
        adapter = new AffairsAdapter(affairs);
        affairsList.setAdapter(adapter);
        adapter.setClickListener(new BaseRealmAdapter.OnClickListener<Affair>() {
            @Override
            public void onClick(int type, int position, Affair value) {
                if (type == BaseRealmAdapter.OnClickListener.TYPE_NORMAL) {
                    Intent intent = new Intent(getActivity(), AffairHandleActivity.class);
                    intent.putExtra(AffairHandleActivity.KEY_AFFAIR_ID, value.getId());
                    intent.putExtra(AffairHandleActivity.KEY_AFFAIR_POSITION, position);
                    startActivityForResult(intent, position);
                }
            }
        });
        subscription = MonitorManager.instance().subscriber(Affair.class).subscribe(new Action1<Affair>() {
            @Override
            public void call(Affair affair) {
                Logger.d(affair);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Math.abs(resultCode) < adapter.getItemCount()) {
            if (requestCode < 0)
                adapter.notifyItemRemoved(-resultCode);
            else
                adapter.notifyItemChanged(resultCode);
        }
    }
}
