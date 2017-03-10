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
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

import dong.lan.mapeye.common.UserManager;
import dong.lan.mapeye.contracts.ContactsContract;
import dong.lan.mapeye.model.users.Contacts;
import dong.lan.mapeye.model.users.User;
import dong.lan.mapeye.views.ContactsFragment;
import io.realm.Realm;

/**
 * Created by 梁桂栋 on 16-11-15 ： 下午2:01.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class ContactsPresenter implements ContactsContract.Presenter {

    private ContactsFragment view;
    private Realm realm;
    private Contacts contacts;

    public ContactsPresenter(ContactsFragment view) {
        this.view = view;
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void loadAllContacts() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                String id = UserManager.instance().myIdentifier();
                Contacts c = realm.where(Contacts.class).equalTo("owner.identifier", id).findFirst();
                if (c != null)
                    contacts = realm.copyFromRealm(c);
                UserManager.instance().setContacts(c);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (contacts != null && !contacts.getContacts().isEmpty())
                    view.initAdapter();
                else {

                    TIMFriendshipManager.getInstance().getFriendList(new TIMValueCallBack<List<TIMUserProfile>>() {
                        @Override
                        public void onError(int i, String s) {
                            view.toast(s);
                            Logger.d(i+","+s);
                        }

                        @Override
                        public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                            if (timUserProfiles == null || timUserProfiles.isEmpty()) {
                                view.show("你目前没有联系人");
                            } else {
                                List<TIMUserProfile> userProfiles = new ArrayList<>();
                                for(TIMUserProfile profile:timUserProfiles){
                                    if(profile.getIdentifier().equals(UserManager.instance().myIdentifier()))
                                        continue;
                                    userProfiles.add(profile);
                                }
                                Realm realm = Realm.getDefaultInstance();
                                contacts = UserManager.instance().saveContacts(userProfiles, realm);
                                view.initAdapter();
                            }
                        }
                    });
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
                view.show(error.getMessage());
            }
        });

    }

    @Override
    public int getContactCount() {
        return (contacts == null || contacts.getContacts() == null) ? 0 : contacts.getContacts().size();
    }

    @Override
    public User getContact(int position) {
        return contacts.getContacts().get(position);
    }


}
