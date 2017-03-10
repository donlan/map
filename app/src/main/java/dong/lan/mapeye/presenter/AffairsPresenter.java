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

import com.orhanobut.logger.Logger;

import dong.lan.mapeye.contracts.AffairsContract;
import dong.lan.mapeye.model.Affair;
import dong.lan.mapeye.views.AffairsListFragment;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by 梁桂栋 on 16-12-8 ： 下午11:37.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class AffairsPresenter implements AffairsContract.Presenter {

    private AffairsListFragment view;
    private Realm realm;
    private RealmResults<Affair> affairs;

    public AffairsPresenter(AffairsListFragment view) {
        this.view = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (realm != null)
            realm.close();
        realm = null;
    }

    @Override
    public void loadAllAffairsAsync() {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
            realm.addChangeListener(new RealmChangeListener<Realm>() {
                @Override
                public void onChange(Realm element) {
                    view.notifyAffairsChanged();
                }
            });
        }
        if (realm.isInTransaction())
            return;
        realm.beginTransaction();
        affairs = realm.where(Affair.class)
                .findAllSorted("createdTime", Sort.DESCENDING);
        realm.commitTransaction();
        view.setupAdapter(affairs);
        Logger.d(affairs);
    }
}
