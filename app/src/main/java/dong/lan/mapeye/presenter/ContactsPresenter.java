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


import java.util.List;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.UserInfo;
import dong.lan.mapeye.common.UserManager;
import dong.lan.mapeye.contracts.ContactsContract;
import dong.lan.mapeye.model.users.Friend;
import dong.lan.mapeye.model.users.User;
import dong.lan.mapeye.views.ContactsFragment;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by 梁桂栋 on 16-11-15 ： 下午2:01.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class ContactsPresenter implements ContactsContract.Presenter {

    private ContactsFragment view;
    private Realm realm;
    private RealmResults<Friend> friends;

    public ContactsPresenter(ContactsFragment view) {
        this.view = view;
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void loadAllContacts() {

        final String id = UserManager.instance().myIdentifier();
        friends = realm.where(Friend.class).equalTo("ownerId", id).findAllAsync();
        view.initAdapter();
        friends.addChangeListener(new RealmChangeListener<RealmResults<Friend>>() {
            @Override
            public void onChange(RealmResults<Friend> element) {
                if (element.size() == 0) {
                    ContactManager.getFriendList(new GetUserInfoListCallback() {
                        @Override
                        public void gotResult(int i, String s, final List<UserInfo> list) {
                            if (i == 0) {
                                if (list == null || list.isEmpty())
                                    view.show("你目前没有联系人");
                                else {
                                    Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            for (UserInfo u :
                                                    list) {

                                                Friend friend = new Friend();
                                                friend.setFriendId(u.getUserName());
                                                User user = new User(u);
                                                realm.copyToRealmOrUpdate(user);
                                                friend.setUser(user);
                                                friend.setOwnerId(UserManager.instance().myIdentifier());
                                                realm.copyToRealmOrUpdate(friend);
                                            }
                                        }
                                    });
                                }
                            } else {
                                view.toast(s);
                            }
                        }
                    });
                }
                view.refreshAdapter(-1);
            }
        });
    }

    @Override
    public int getContactCount() {
        return (friends == null) ? 0 : friends.size();
    }

    @Override
    public User getContact(int position) {
        return friends.get(position).getUser();
    }


}
