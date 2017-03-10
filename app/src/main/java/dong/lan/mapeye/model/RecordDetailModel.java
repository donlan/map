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

package dong.lan.mapeye.model;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.Callable;

import io.realm.Realm;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 梁桂栋 on 16-11-10 ： 下午8:10.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class RecordDetailModel {


    private static final String TAG = "RecordDetailModel";

    private Realm realm;


    public void destroy() {
        realm.close();
    }

    public void getRecord(final String id, final SingleSubscriber<Record> subscriber) {
        Single.fromCallable(new Callable<Record>() {
            @Override
            public Record call() throws Exception {
                Record record = null;
                try {
                    if (realm == null) {
                        realm = Realm.getDefaultInstance();
                    }
                    realm.beginTransaction();
                    record = realm.where(Record.class).equalTo("id", id).findFirst();
                    realm.commitTransaction();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
                return record;
            }
        })
                .subscribe(subscriber);
    }

    public void init(Context applicationContext) {
        if (realm == null) {
            Realm.init(applicationContext);
            realm = Realm.getDefaultInstance();
        }
    }
}
