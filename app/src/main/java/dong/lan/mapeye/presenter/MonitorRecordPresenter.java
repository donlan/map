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

package dong.lan.mapeye.presenter;

import dong.lan.mapeye.R;
import dong.lan.mapeye.contracts.MonitorRecordContact;
import dong.lan.mapeye.model.MonitorRecode;
import dong.lan.mapeye.views.MonitorRecordActivity;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by 梁桂栋 on 16-12-23 ： 下午11:53.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class MonitorRecordPresenter implements MonitorRecordContact.Presenter {
    private MonitorRecordActivity view;
    private Realm realm;
    private RealmResults<MonitorRecode> results;

    public MonitorRecordPresenter(MonitorRecordActivity view) {
        this.view = view;
    }


    @Override
    public void initList(String identifier) {
        realm = Realm.getDefaultInstance();
        results = realm.where(MonitorRecode.class)
                .equalTo("monitoredUser.identifier", identifier)
                .findAllSorted("createTime", Sort.DESCENDING);
        if (results.size() > 0) {
            view.initAdapter(results);
        }
    }
}
