package dong.lan.mapeye.model;

import java.util.List;
import java.util.concurrent.Callable;

import dong.lan.mapeye.bmob.BmobAction;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 梁桂栋 on 16-11-1 ： 下午8:31.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class RecordModel {
    public void loadAllRecord(final SingleSubscriber<List<Record>> subscriber) {
        Single.fromCallable(new Callable<List<Record>>() {
            @Override
            public List<Record> call() throws Exception {
                List<Record> res = null;
                try {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    RealmResults<Record> records = realm.where(Record.class).findAll();
                    realm.commitTransaction();
                    res = realm.copyFromRealm(records);
                    realm.close();
                    if (res == null || res.isEmpty())
                        loadAllFromServer();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                return res;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }


    /**
     * 本地没有数据时,尝试从服务器加载数据到本地
     */
    private void loadAllFromServer() {
        BmobAction.getAllRecord();
    }

}
