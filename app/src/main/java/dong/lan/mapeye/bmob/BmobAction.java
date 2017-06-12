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

package dong.lan.mapeye.bmob;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import dong.lan.mapeye.bmob.bean.BContact;
import dong.lan.mapeye.bmob.bean.BPoint;
import dong.lan.mapeye.bmob.bean.BRecord;
import dong.lan.mapeye.bmob.bean.BUser;
import dong.lan.mapeye.events.RecordNetEvent;
import dong.lan.mapeye.model.Point;
import dong.lan.mapeye.model.Record;
import dong.lan.mapeye.model.users.Contact;
import dong.lan.mapeye.model.users.Group;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by 梁桂栋 on 17-3-18 ： 下午8:20.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class BmobAction {


    private static final String TAG = BmobAction.class.getSimpleName();

    public static void saveRecord(Record record, Group group) {
        new BRecord(record, group).save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                Log.d(TAG, "done: " + s + "," + e);
            }
        });
    }


    public static void getAllRecord() {
        if(BmobUser.getCurrentUser(BUser.class) == null)
            return;
        BmobQuery<BRecord> query = new BmobQuery<>();
        query.addWhereEqualTo("own", BmobUser.getCurrentUser(BUser.class));
        query.include("own");
        query.findObjects(new FindListener<BRecord>() {
            @Override
            public void done(List<BRecord> list, BmobException e) {
                if (e == null && list!=null) {
                    save(list, BmobUser.getCurrentUser(BUser.class));
                }
            }
        });
    }

    private static void save(final List<BRecord> bRecords, final BUser ower) {
        if (bRecords != null) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (BRecord record : bRecords) {
                        List<BContact> bcs = record.getContacts();
                        Group group = new Group();
                        group.setGroupId(record.getId());
                        group.setDescription(record.getInfo());
                        group.setMembers(new RealmList<Contact>());
                        group.setOwner(ower.toUser());
                        for (BContact c : bcs) {
                            Contact contact = c.toContact();
                            group.getMembers().add(contact);
                        }
                        realm.copyToRealmOrUpdate(group);
                        Record r = new Record();
                        r.setId(record.getId());
                        r.setCreateTime(record.getCreateTime());
                        r.setInfo(record.getInfo());
                        r.setLabel(record.getLabel());
                        r.setOwn(ower.toUser());
                        r.setRadius(record.getRadius());
                        r.setType(record.getType());
                        r.setPoints(new RealmList<Point>());

                        for (BPoint p : record.getPoints()) {
                            r.getPoints().add(new Point(p.lat, p.lng));
                        }
                        realm.copyToRealmOrUpdate(r);
                        EventBus.getDefault().post(new RecordNetEvent());
                    }
                }
            });
        }
    }

    public static void addContact(Group group) {
        final List<Contact> copyContacts = group.getMembers();
        BmobQuery<BRecord> query = new BmobQuery<>();
        query.addWhereEqualTo("id", group.getGroupId());
        query.findObjects(new FindListener<BRecord>() {
            @Override
            public void done(List<BRecord> list, BmobException e) {
                if (e == null || (list != null && !list.isEmpty())) {
                    BRecord record = list.get(0);
                    record.addUnique("contacts", copyContacts);
                    record.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {

                        }
                    });
                }
            }
        });
    }

    public static void updateContactTag(String id, Contact c) {
        final BContact contact = new BContact(c);
        BmobQuery<BRecord> query = new BmobQuery<>();
        query.addWhereEqualTo("id", id);
        query.findObjects(new FindListener<BRecord>() {
            @Override
            public void done(List<BRecord> list, BmobException e) {
                if (e == null || (list != null && !list.isEmpty())) {
                    BRecord record = list.get(0);
                    if (record.getContacts() != null) {
                        for (int i = 0, s = list.size(); i < s; i++) {
                            if (contact.getId().equals(list.get(i).getId())) {
                                record.setValue("contacts." + i, contact);
                                record.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {

                                    }
                                });
                                break;
                            }
                        }
                    }

                }
            }
        });
    }

    public static void deleteRecord(String id) {
        BmobQuery<BRecord> query = new BmobQuery<>();
        query.addWhereEqualTo("id", id);
        query.findObjects(new FindListener<BRecord>() {
            @Override
            public void done(List<BRecord> list, BmobException e) {
                if (e == null || (list != null && !list.isEmpty())) {
                    BRecord record = list.get(0);
                    record.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {

                        }
                    });
                }
            }
        });
    }

    public static void removeRecordMember(String id, final Contact contact) {
        final BContact c = new BContact(contact);
        BmobQuery<BRecord> query = new BmobQuery<>();
        query.addWhereEqualTo("id", id);
        query.findObjects(new FindListener<BRecord>() {
            @Override
            public void done(List<BRecord> list, BmobException e) {
                if (e == null || (list != null && !list.isEmpty())) {
                    BRecord record = list.get(0);
                    record.removeAll("contacts", Arrays.asList(c));
                    record.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {

                        }
                    });
                }
            }
        });
    }

    public static void register(String username, String pwd) {
        BUser user = new BUser();
        user.setUsername(username);
        user.setPassword(pwd);
        user.signUp(new SaveListener<BUser>() {
            @Override
            public void done(BUser bUser, BmobException e) {

            }
        });
    }

    public static void login(String username, String pwd) {
        BUser user = new BUser();
        user.setUsername(username);
        user.setPassword(pwd);
        user.login(new SaveListener<BUser>() {
            @Override
            public void done(BUser bUser, BmobException e) {

            }
        });
    }
}
