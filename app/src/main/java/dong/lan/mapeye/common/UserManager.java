package dong.lan.mapeye.common;

import android.util.Log;

import com.orhanobut.logger.Logger;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import dong.lan.mapeye.bmob.BmobAction;
import dong.lan.mapeye.contracts.RecordDetailContract;
import dong.lan.mapeye.model.message.IMessage;
import dong.lan.mapeye.model.users.Contact;
import dong.lan.mapeye.model.users.Contacts;
import dong.lan.mapeye.model.users.Group;
import dong.lan.mapeye.model.users.IUserInfo;
import dong.lan.mapeye.model.users.User;
import dong.lan.mapeye.utils.SPHelper;
import io.realm.Realm;
import io.realm.RealmList;

import static dong.lan.mapeye.contracts.LoginAndSignContract.loginAndSignView.KEY_IS_LOGIN;


/**
 * Created by 梁桂栋 on 16-11-8 ： 下午2:47.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class UserManager {


    private static final String TAG = UserManager.class.getSimpleName();
    private static UserManager manager;
    private IUserInfo userInfo;
    private Realm realm;
    private Contacts contacts;
    private User user;


    private UserManager() {
        realm = Realm.getDefaultInstance();
    }

    /**
     * 返回单例对象
     *
     * @return 用户管理对象
     */
    public static UserManager instance() {
        if (manager == null) {
            synchronized (UserManager.class) {
                if (manager == null) {
                    manager = new UserManager();
                }
            }
        }
        return manager;
    }

    public String myIdentifier() {
        return userInfo.identifier();
    }

    /**
     * 检查默认的realm实例是否已经关闭，如果关闭则新获取一个
     */
    private void checkRealm() {
        if (realm.isClosed())
            realm = Realm.getDefaultInstance();
    }

    public IUserInfo me() {

        if (userInfo == null) {
            userInfo = MessageHelper.getInstance().
                    toTarget(SPHelper.get("USER"), User.class);
        }
        return userInfo;
    }


    public void updateUserAvatar(String path) {
        // // TODO: 16-12-20 用户头像
    }


    /**
     * 保存联系人信息到Realm中
     *
     * @param userInfos 所有的联系人
     * @param realm     提供的realm实例
     * @return 返回保存所有联系人后的Contacts
     */
    public Contacts saveContacts(List<UserInfo> userInfos, Realm realm) {
        String identifier = userInfo.identifier();
        realm.beginTransaction();
        contacts = realm.where(Contacts.class).equalTo("owner.identifier", identifier).findFirst();
        if (contacts == null) {
            contacts = realm.createObject(Contacts.class);
            if (user.isManaged()) {
                contacts.setOwner(user);
            } else {
                contacts.setOwner(realm.where(User.class).equalTo("identifier", user.identifier()).findFirst());
            }
        }
        for (UserInfo userInfo : userInfos) {
            User user = realm.createObject(User.class);
            user.setUsername(userInfo.getUserName());
            user.setIdentifier(userInfo.getUserName());
            user.setRemark(userInfo.getNotename());
            user.setHeadAvatar(userInfo.getAvatar());
            user.setNickname(userInfo.getNickname());
            user.setSex(userInfo.getGender() == UserInfo.Gender.male ? 1 : 0);

            if (contacts.getContacts() == null) {
                RealmList<User> users = new RealmList<>();
                users.add(user);
                contacts.setContacts(users);
            } else {
                contacts.getContacts().add(user);
            }
        }
        realm.commitTransaction();
        return contacts;
    }

    /**
     * 使用默认的realm查找当前用户的所有联系人
     *
     * @return 所有联系人
     */
    public Contacts getContacts() {
        if (contacts == null) {
            checkRealm();
            realm.beginTransaction();
            contacts = realm.where(Contacts.class).equalTo("owner.identifier", userInfo.identifier()).findFirst();
            realm.commitTransaction();
        }
        return contacts;
    }

    /**
     * 缓存查到到联系人到内存
     *
     * @param contacts 需要设置的联系人信息
     */
    public void setContacts(Contacts contacts) {
        this.contacts = contacts;
    }

    /**
     * 使用提供的realm查找当前用户的所有联系人
     *
     * @param realm 提供的额realm实例
     * @return 所有联系人
     */
    public Contacts getContacts(Realm realm) {
        Contacts contacts = null;
        contacts = Realm.getDefaultInstance().where(Contacts.class)
                .equalTo("owner.identifier", userInfo.identifier()).findFirstAsync();
        return contacts;
    }


    /**
     * @return 已登陆则返回true，否则返回false
     */
    public boolean isLogin() {
        return SPHelper.getBoolean(KEY_IS_LOGIN) && JMessageClient.getMyInfo() != null;
    }


    public void login(String phone, BasicCallback callback) {
        Log.d(TAG, "login: " + phone);
        JMessageClient.login(phone, Secure.encode(phone), callback);
    }


    /**
     * 退出登录
     *
     * @return
     */
    public boolean logout() {
        JMessageClient.logout();
        SPHelper.put("USER", "");
        SPHelper.putBoolean(KEY_IS_LOGIN, false);
        return true;
    }


    /**
     * 删除一个联系人
     *
     * @param id
     */
    public void deleteContact(String id) {
        try {
            checkRealm();
            realm.beginTransaction();
            realm.where(Contact.class).equalTo("user.identifier", id).findFirst().deleteFromRealm();
            realm.where(User.class).equalTo("identifier", id).findFirst().deleteFromRealm();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存一个群组信息到realm中
     *
     * @param userInfos 该群组中的所有你用户
     * @param groupId   群组id
     * @param label     群组描述
     * @param realm     提供的realm
     * @return 保存后的群组
     */
    public Group initGroupInfo(List<UserInfo> userInfos, String groupId, String label, Realm realm) {
        Group group = null;
        String id = UserManager.instance().myIdentifier();
        realm.beginTransaction();
        group = new Group();
        group.setGroupId(groupId);
        group.setOwner(realm.where(User.class).equalTo("identifier", id).findFirst());
        group.setDescription(label);
        if (group.getMembers() == null)
            group.setMembers(new RealmList<Contact>());
        for (UserInfo user :
                userInfos) {
            if (user.getUserName().equals(id))
                continue;
            Contact contact = new Contact();
            contact.setId(Contact.createId(groupId, user.getUserName()));
            contact.setUser(realm.where(User.class).equalTo("identifier", id).findFirst());
            contact.setTag(Contact.TAG_ADDING);
            contact.setStatus(Contact.STATUS_NONE);
            contact.setRepeatMonitor(false);
            group.getMembers().add(contact);


        }
        realm.copyToRealmOrUpdate(group);
        realm.commitTransaction();
        return group;
    }


    /**
     * 添加新的用户到指定群组中
     *
     * @param groupId 群组的id
     * @param users   新添加的用户
     */
    public void addGroupMembers(String groupId, List<User> users, Realm realm) {
        Group group = null;
        realm.beginTransaction();
        group = realm.where(Group.class).equalTo("groupId", groupId).findFirst();
        if (group != null) {
            for (User user :
                    users) {
                if (user.identifier().equals(myIdentifier()))
                    continue;
                RealmList<Contact> contacts = group.getMembers();
                boolean canAdd = true; //防止重复添加
                for (Contact contact : contacts) {
                    if (contact.getId().equals(Contact.createId(groupId, user.identifier()))) {
                        canAdd = false;
                        break;
                    }
                }
                if (canAdd) {
                    Contact contact = new Contact();
                    contact.setUser(realm.where(User.class).equalTo("identifier", user.identifier()).findFirst());
                    contact.setTag(Contact.TAG_ADDING);
                    contact.setStatus(Contact.STATUS_WAITING);
                    contact.setId(Contact.createId(groupId, user.identifier()));
                    contact.setRepeatMonitor(false);
                    group.getMembers().add(contact);

                    BmobAction.addContact(group);
                }
            }
        }
        Logger.d("" + group);
        realm.commitTransaction();
    }


    public void updateContactTag(IMessage msg, final int tag) {
        final String recordId = msg.getStringExtra(JMCenter.EXTRAS_RECORD_ID);
        String identifier = msg.getStringExtra(JMCenter.EXTRAS_IDENTIFIER);
        final String id = Contact.createId(recordId, identifier);
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Group group = realm.where(Group.class).equalTo("groupId", recordId).findFirst();
                RealmList<Contact> contacts = group.getMembers();
                for (Contact c : contacts) {
                    if (c.getId().equals(id)) {
                        c.setTag(tag);
                        BmobAction.updateContactTag(recordId, c);
                        break;
                    }
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                MonitorManager.instance().post(RecordDetailContract.RecordDetailView.REFRESH);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Logger.d(error.getMessage());
            }
        });
        realm.close();

    }


    public String getUserDisplayName(IUserInfo user) {
        String identifier = user.username();
        if (user.equals(userInfo)) {
            if (user.nickname() != null && !user.nickname().equals(""))
                return identifier + "(" + user.nickname() + ")";
        } else {
            if (user.remark() != null && !user.remark().equals("")) {
                return identifier + "(" + user.remark() + ")";
            }
        }
        return identifier;
    }

    public String getUserDisplayName(UserInfo user) {
        String identifier = user.getUserName();
        if (userInfo != null && userInfo.username().contains(identifier)) {
            if (user.getNickname() != null && !user.getNickname().equals(""))
                return identifier + "(" + user.getNickname() + ")";
        } else {
            if (user.getNotename() != null && !user.getNotename().equals("")) {
                return identifier + "(" + user.getNotename() + ")";
            }
        }
        return identifier;
    }

    void updateContactStatus(String id, final int status) {
        Realm realm = Realm.getDefaultInstance();
        if (realm.isInTransaction())
            realm.cancelTransaction();
        realm.beginTransaction();
        Contact contact = realm.where(Contact.class).equalTo("id", id).findFirst();
        if (contact != null) {
            contact.setStatus(status);
        }
        realm.commitTransaction();
        realm.close();
        MonitorManager.instance().post(RecordDetailContract.RecordDetailView.REFRESH);
    }

    public void addContact(UserInfo userInfo) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        User user = realm.createObject(User.class);
        user.setIdentifier(String.valueOf(userInfo.getUserID()));
        user.setUsername(User.getUserDescriber(userInfo));
        user.setNickname(userInfo.getNickname());
        user.setRemark(userInfo.getNotename());
        user.setHeadAvatar(userInfo.getAvatar());
        if (contacts.getContacts() == null) {
            RealmList<User> users = new RealmList<>();
            users.add(user);
            contacts.setContacts(users);
        } else {
            contacts.getContacts().add(user);
        }
        realm.commitTransaction();
    }

    public void initMe() {
        final UserInfo userInfo = JMessageClient.getMyInfo();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                user = realm.where(User.class).equalTo("identifier", userInfo.getUserName()).findFirst();
                if (user == null) {
                    user = new User(userInfo);
                    realm.copyToRealm(user);
                }
                if (user.isManaged())
                    UserManager.this.userInfo = realm.copyFromRealm(user);
                else {
                    UserManager.this.userInfo = user;
                }
                SPHelper.put("USER", MessageHelper.getInstance().toJson(userInfo));

                Logger.d(userInfo);
            }
        });

    }

    public void setUserBmobObjId(final String s) {
        if (user.isManaged()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    user.setBmobObjId(s);
                }
            });
        } else {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    User user = realm.where(User.class).equalTo("identifier", myIdentifier()).findFirst();
                    if (user != null) {
                        user.setBmobObjId(s);
                    }
                }
            });
        }
    }
}
