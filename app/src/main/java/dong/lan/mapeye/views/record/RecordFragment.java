/*
 *
 *   Copyright (C) 2017 author : 梁桂栋
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

package dong.lan.mapeye.views.record;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dong.lan.library.LabelTextView;
import dong.lan.mapeye.R;
import dong.lan.mapeye.contracts.RecordContract;
import dong.lan.mapeye.events.MainEvent;
import dong.lan.mapeye.events.RecordNetEvent;
import dong.lan.mapeye.model.Record;
import dong.lan.mapeye.presenter.RecordPresenter;
import dong.lan.mapeye.utils.StringHelper;
import dong.lan.mapeye.views.AddRecordActivity;
import dong.lan.mapeye.views.BaseFragment;
import dong.lan.mapeye.views.customsView.RecycleViewDivider;

import static dong.lan.mapeye.contracts.RecordDetailContract.RecordDetailView.KEY_POSITION;
import static dong.lan.mapeye.contracts.RecordDetailContract.RecordDetailView.KEY_RECORD;

/**
 * Created by 梁桂栋 on 16-11-6 ： 下午4:09.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class RecordFragment extends BaseFragment implements RecordContract.RecordView {

    private static final String TAG = "RecordFragment";
    private RecordPresenter presenter;
    private View view;

    public static RecordFragment newInstance(String tittle) {
        RecordFragment fragment = new RecordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TITTLE_TAG, tittle);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new RecordPresenter(this);
    }

    View emptyView;
    Unbinder unbinder;

    @BindView(R.id.record_list)
    RecyclerView recyclerView;
    @BindView(R.id.emptyViewStub)
    ViewStub viewStub;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_record, container, false);
            unbinder = ButterKnife.bind(this, view);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
            recyclerView.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.VERTICAL, 10.0f));
            EventBus.getDefault().register(this);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void recordNetEvent(RecordNetEvent event){
        recyclerView.getAdapter().notifyDataSetChanged();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MainEvent event) {
        int code = event.getCode();
        Logger.d(event.toString());
        switch (code) {
            case MainEvent.CODE_ADDED_RECORD:
                if (event.getData() instanceof Record) {
                    Record record = (Record) event.getData();
                    setAdapter();
                    presenter.addAndRefresh(record);
                }
                break;
            case MainEvent.CODE_DELETE_RECORD:
                if (event.getData() instanceof Integer) {
                    presenter.removeRecord((Integer) event.getData());
                }
                break;
            case MainEvent.CODE_REFRESH:
                if (event.getData() instanceof Integer) {
                    refresh((Integer) event.getData());
                }
                break;
        }
    }

    @Override
    public void loadData() {
        presenter.load();
    }

    @Override
    public void refresh(int index) {
        recyclerView.getAdapter().notifyItemChanged(index);
    }

    @Override
    public void delete(int index) {
        recyclerView.getAdapter().notifyItemRemoved(index);
    }

    @Override
    public void jump(int index) {

    }

    @Override
    public void insertRecord(int position) {
        if (recyclerView.getAdapter() == null)
            recyclerView.setAdapter(new Adapter());
        recyclerView.getAdapter().notifyItemInserted(position);
    }

    @Override
    public void setAdapter() {
        if (recyclerView.getAdapter() == null)
            recyclerView.setAdapter(new Adapter());
        else
            recyclerView.getAdapter().notifyDataSetChanged();
        if (emptyView != null)
            emptyView.setVisibility(View.GONE);
    }

    @Override
    public void setUpEmptyView() {
        if (emptyView == null) {
            emptyView = viewStub.inflate();
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), AddRecordActivity.class));
                }
            });
        }
    }


    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_record, null);
            final ViewHolder holder = new ViewHolder(view);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jump(holder.getLayoutPosition());
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), RecordDetailActivity.class);
                    intent.putExtra(KEY_RECORD,
                            presenter.getRecord(holder.getLayoutPosition()).getId());
                    intent.putExtra(KEY_POSITION, holder.getLayoutPosition());
                    startActivity(intent);
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Record record = presenter.getRecord(position);
            if (record != null) {
                holder.type.setText(Record.getRecordTypeStr(record.getType()));
                holder.label.setText(record.getLabel());
                holder.desc.setText(StringHelper.getRecordDesc(record));
            }
        }

        @Override
        public int getItemCount() {
            return presenter.getRecordSize();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.record_type)
        LabelTextView type;
        @BindView(R.id.record_info)
        LabelTextView label;
        @BindView(R.id.record_desc)
        TextView desc;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
