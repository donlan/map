package dong.lan.mapeye.presenter;

import java.util.ArrayList;
import java.util.List;

import dong.lan.mapeye.contracts.RecordContract;
import dong.lan.mapeye.model.Record;
import dong.lan.mapeye.model.RecordModel;
import dong.lan.mapeye.views.record.RecordFragment;
import rx.SingleSubscriber;

/**
 * Created by 梁桂栋 on 16-11-1 ： 下午8:22.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class RecordPresenter implements RecordContract.Presenter {
    private RecordFragment view;
    private RecordModel model;
    private List<Record> records;

    public RecordPresenter(RecordFragment view) {
        this.view = view;
        model = new RecordModel();
    }

    @Override
    public void load() {
        model.loadAllRecord(new SingleSubscriber<List<Record>>() {
            @Override
            public void onSuccess(List<Record> value) {
                if (value == null || value.isEmpty()) {
                    view.setUpEmptyView();
                } else {
                    records = value;
                    view.setAdapter();
                }
            }

            @Override
            public void onError(Throwable error) {
                view.show(error.getMessage());
            }
        });
    }

    @Override
    public int getRecordSize() {
        return records == null ? 0 : records.size();
    }

    @Override
    public Record getRecord(int position) {
        return records.get(position);
    }

    @Override
    public void addAndRefresh(Record record) {
        if(record!=null){
            if(records==null)
                records = new ArrayList<>();
            records.add(record);
            view.refresh(records.size());
        }
    }

    @Override
    public void removeRecord(int index) {
        records.remove(index);
        view.delete(index);
    }
}
