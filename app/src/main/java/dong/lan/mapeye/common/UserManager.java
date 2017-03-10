package dong.lan.mapeye.common;

import com.orhanobut.logger.Logger;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.presentation.business.LoginBusiness;
import com.tencent.qcloud.tlslibrary.service.TlsBusiness;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import dong.lan.mapeye.contracts.RecordDetailContract;
import dong.lan.mapeye.model.users.Contact;
import dong.lan.mapeye.model.users.Contacts;
import dong.lan.mapeye.model.users.Group;
import dong.lan.mapeye.model.users.User;
import dong.lan.mapeye.model.users.UserInfo;
import dong.lan.mapeye.model.message.IMessage;
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


    private static UserManager manager;
    private User user;
    private Realm realm;
    private Contacts contacts;

    private TIMUserProfile userProfile;


    private UserManager() {
        realm = Realm.getDefaultInstance();
    }


    public String myIdentifier() {
        return UserInfo.getInstance().getId();
    }

    /**
     * 检查默认的realm实例是否已经关闭，如果关闭则新获取一个
     */
    private void checkRealm() {
        if (realm.isClosed())
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

    public TIMUserProfile me() {

        if (userProfile == null) {
            userProfile = MessageHelper.getInstance().
                    toTarget(SPHelper.get("USER"), TIMUserProfile.class);
        }
        return userProfile;
    }

    /**
     * @return 返回登陆过后本地保存的用户对象，没有登陆或者退出登陆则返回null
     */
    public User getCurrentUser() {
        if (user == null || !user.isManaged()) {
            if (!isLogin())
                return null;
            try {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                user = realm.where(User.class).equalTo("identifier", userProfile.getIdentifier()).findFirst();
                realm.commitTransaction();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    public void updateUserAvatar(String path) {
        // // TODO: 16-12-20 用户头像
    }

    /**
     * 保存联系人信息到Realm中
     *
     * @param userInfos 所有的联系人
     */
//    public void saveContacts(List<UserInfo> userInfos) {
//        Contacts c = getContacts();
//        if (c == null) {
//            checkRealm();
//            realm.beginTransaction();
//            contacts = realm.createObject(Contacts.class);
//            realm.commitTransaction();
//        }
//        for (UserInfo userInfo : userInfos) {
//            addContact(userInfo);
//        }
//    }

//    /**
//     * 保存联系人信息到Realm中
//     *
//     * @param userInfos 所有的联系人
//     * @param realm     提供的realm实例
//     * @return 返回保存所有联系人后的Contacts
//     */
//    public Contacts saveContacts(List<UserInfo> userInfos, Realm realm) {
//        long id = JMessageClient.getMyInfo().getUserID();
//        realm.beginTransaction();
//        contacts = realm.where(Contacts.class).equalTo("owner.id", id).findFirst();
//        if (contacts == null) {
//            contacts = realm.createObject(Contacts.class);
//            contacts.setOwner(user);
//        }
//        for (UserInfo userInfo : userInfos) {
//            User user = realm.createObject(User.class);
//            user.setIdentifier(userInfo.getUserID());
//            user.setUsername(userInfo.getUserName());
//            user.setNickname(userInfo.getNickname());
//            user.setRemark(userInfo.getNotename());
//            if (contacts.getContacts() == null) {
//                RealmList<User> users = new RealmList<>();
//                users.add(user);
//                contacts.setContacts(users);
//            } else {
//                contacts.getContacts().add(user);
//            }
//        }
//        realm.commitTransaction();
//        return contacts;
//    }


    /**
     * 保存联系人信息到Realm中
     *
     * @param userInfos 所有的联系人
     * @param realm     提供的realm实例
     * @return 返回保存所有联系人后的Contacts
     */
    public Contacts saveContacts(List<TIMUserProfile> userInfos, Realm realm) {
        String identifier = UserInfo.getInstance().getId();
        realm.beginTransaction();
        contacts = realm.where(Contacts.class).equalTo("owner.identifier", identifier).findFirst();
        if (contacts == null) {
            contacts = realm.createObject(Contacts.class);
            contacts.setOwner(user);
        }
        for (TIMUserProfile userInfo : userInfos) {
            User user = realm.createObject(User.class);
            user.setIdentifier(userInfo.getIdentifier());
            user.setRemark(userInfo.getRemark());
            user.setHeadAvatar(userInfo.getFaceUrl());
            user.setNickname(userInfo.getNickName());
            user.setSex((int) userInfo.getGender().getValue());

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
     * 缓存查到到联系人到内存
     *
     * @param contacts 需要设置的联系人信息
     */
    public void setContacts(Contacts contacts) {
        this.contacts = contacts;
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
            contacts = realm.where(Contacts.class).equalTo("owner.id", user.getIdentifier()).findFirst();
            realm.commitTransaction();
        }
        return contacts;
    }

    /**
     * 使用提供的realm查找当前用户的所有联系人
     *
     * @param realm 提供的额realm实例
     * @return 所有联系人
     */
    public Contacts getContacts(Realm realm) {
        if (contacts == null) {
            realm.beginTransaction();
            contacts = realm.where(Contacts.class).equalTo("owner.id", user.getIdentifier()).findFirst();
            realm.commitTransaction();
        }
        return contacts;
    }


//    /**
//     * 保存当前登录用户的信息
//     */
//    public void saveUser() {
//        UserInfo userInfo = JMessageClient.getMyInfo();
//        checkRealm();
//        realm.beginTransaction();
//        if (realm.where(User.class).equalTo("id", userInfo.getUserID()).findAll().isEmpty()) {
//            user = realm.createObject(User.class);
//            user.setIdentifier(userInfo.getUserID());
//            user.setUsername(userInfo.getUserName());
//            user.setNickname(userInfo.getNickname());
//            user.setRemark(userInfo.getNotename());
//            Contacts contact = realm.createObject(Contacts.class);
//            contact.setOwner(this.user);
//        }
//        realm.commitTransaction();
//    }

    /**
     * 使用默认的realm添加一个联系人
     *
     * @param userInfo 联系人信息
     */
//    public void addContact(UserInfo userInfo) {
//        getContacts();
//        if (contacts == null)
//            return;
//        checkRealm();
//        addContact(userInfo, realm);
//    }

    /**
     * 使用提供的realm，添加一个联系人
     *
     * @param userInfo 联系人信息
     * @param realm    提供的realm操作实例
     */

//    public void addContact(UserInfo userInfo, Realm realm) {
//        if (contacts == null)
//            return;
//        realm.beginTransaction();
//        User user = realm.createObject(User.class);
//        user.setIdentifier(userInfo.getUserID());
//        user.setUsername(userInfo.getUserName());
//        user.setNickname(userInfo.getNickname());
//        user.setRemark(userInfo.getNotename());
//        if (contacts.getContacts() == null) {
//            RealmList<User> users = new RealmList<>();
//            users.add(user);
//            contacts.setContacts(users);
//        } else {
//            contacts.getContacts().add(user);
//        }
//        realm.commitTransaction();
//    }


    /**
     * @return 已登陆则返回true，否则返回false
     */
    public boolean isLogin() {
        return SPHelper.getBoolean(KEY_IS_LOGIN);
    }

    /**
     * 退出登录
     *
     * @return
     */
    public boolean logout() {
        LoginBusiness.logout(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Logger.d(i + "," + s);
            }

            @Override
            public void onSuccess() {
                Logger.d("logout success");
            }
        });
        TlsBusiness.logout(myIdentifier());
        JMessageClient.logout();
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
    public Group initGroupInfo(List<TIMGroupMemberInfo> userInfos, String groupId, String label, Realm realm) {
        Group group = null;
        String id = UserManager.instance().myIdentifier();
        realm.beginTransaction();
        group = realm.createObject(Group.class);
        group.setGroupId(groupId);
        group.setOwner(realm.where(User.class).equalTo("identifier", id).findFirst());
        group.setDescription(label);
        if (group.getMembers() == null)
            group.setMembers(new RealmList<Contact>());
        for (TIMGroupMemberInfo user :
                userInfos) {
            if (user.getUser().equals(id) || group.getMembers().contains(Contact.createId(groupId, user.getUser())))
                continue;
            Contact contact = new Contact();
            contact.setId(Contact.createId(groupId, user.getUser()));
            contact.setUser(realm.where(User.class).equalTo("identifier", id).findFirst());
            contact.setTag(Contact.TAG_ADDING);
            contact.setStatus(Contact.STATUS_NONE);
            contact.setRepeatMonitor(false);
            group.getMembers().add(contact);
        }
        realm.commitTransaction();
        return group;
    }

    /**
     * 添加新的用户到指定群组中
     *
     * @param groupId 群组的id
     * @param users   新添加的用户
     */
//    public void addGroupMembers(long groupId, List<TIMUserProfile> users, Realm realm) {
//        Group group = null;
//        realm.beginTransaction();
//        group = realm.where(Group.class).equalTo("groupId", groupId).findFirst();
//        if (group != null) {
//            for (TIMUserProfile user :
//                    users) {
//                Contact contact = new Contact();
//                contact.setUser(realm.where(User.class).equalTo("identifier", user.getIdentifier()).findFirst());
//                contact.setUserInfo(user);
//                contact.setTag(Contact.TAG_ADDING);
//                contact.setStatus(Contact.STATUS_WAITING);
//                contact.setId(Contact.createId(groupId, user.getIdentifier()));
//                contact.setRepeatMonitor(false);
//                group.getMembers().add(contact);
//            }
//        }
//        realm.commitTransaction();
//    }

    /**
     * 添加新的用户到指定群组中
     *
     * @param groupId 群组的id
     * @param users   新添加的用户
     */
    public void addGroupMembers(String groupId, List<TIMUserProfile> users, Realm realm) {
        Group group = null;
        realm.beginTransaction();
        group = realm.where(Group.class).equalTo("groupId", groupId).findFirst();
        if (group != null) {
            for (TIMUserProfile user :
                    users) {
                if (user.getIdentifier().equals(myIdentifier()) ||
                        group.getMembers().contains(Contact.createId(groupId, userProfile.getIdentifier())))
                    continue;
                Contact contact = new Contact();
                contact.setUser(realm.where(User.class).equalTo("identifier", user.getIdentifier()).findFirst());
                contact.setTag(Contact.TAG_ADDING);
                contact.setStatus(Contact.STATUS_WAITING);
                contact.setId(Contact.createId(groupId, user.getIdentifier()));
                contact.setRepeatMonitor(false);
                group.getMembers().add(contact);
            }
        }
        Logger.d("" + group);
        realm.commitTransaction();
    }

//    public void addGroupMembers(long groupId, UserInfo user, Realm realm) {
//        Group group = null;
//        if (realm.isInTransaction())
//            realm.cancelTransaction();
//        realm.beginTransaction();
//        group = realm.where(Group.class).equalTo("groupId", groupId).findFirst();
//        if (group != null) {
//            RealmResults<Contact> contacts = realm.where(Contact.class)
//                    .equalTo("id", Contact.createId(groupId, user.getUserID())).findAll();
//            if (contacts.size() >= 1) {
//                Contact contact = contacts.get(0);
//                contact.setTag(Contact.TAG_ADDING);
//                contact.setStatus(Contact.STATUS_WAITING);
//                group.getMembers().add(contact);
//                Logger.d("readd:" + contact.getUser().getJUsername());
//            } else {
//                Contact contact = new Contact();
//                contact.setUser(realm.where(User.class).equalTo("id", user.getUserID()).findFirst());
//                contact.setUserInfo(user);
//                contact.setTag(Contact.TAG_ADDING);
//                contact.setStatus(Contact.STATUS_WAITING);
//                contact.setId(Contact.createId(groupId, user.getUserID()));
//                contact.setRepeatMonitor(false);
//                group.getMembers().add(contact);
//                Logger.d("add:" + contact.getUser().getJUsername());
//            }
//        }
//        realm.commitTransaction();
//    }


    public void updateContactTag(IMessage msg, final int tag) {
        final String recordId = msg.getStringExtra(JMCenter.EXTRAS_RECORD_ID);
        String identifier = msg.getStringExtra(JMCenter.EXTRAS_IDENTIFIER);
        final long id = Contact.createId(recordId, identifier);
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Group group = realm.where(Group.class).equalTo("groupId", recordId).findFirst();
                RealmList<Contact> contacts = group.getMembers();
                for (Contact c : contacts) {
                    if (c.getId() == id) {
                        c.setTag(tag);
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


    public String getUserDisplayName(User user) {
        String identifier = user.getIdentifier().substring(3);
        if (user.equals(this.user)) {
            if (user.getNickname() != null && !user.getNickname().equals(""))
                return identifier + "(" + user.getNickname() + ")";
        } else {
            if (user.getRemark() != null && !user.getRemark().equals("")) {
                return identifier + "(" + user.getRemark() + ")";
            }
        }
        return identifier;
    }

    public String getUserDisplayName(cn.jpush.im.android.api.model.UserInfo user) {
        String identifier = user.getUserName().substring(3);
        if (this.user != null && this.user.getIdentifier().contains(identifier)) {
            if (user.getNickname() != null && !user.getNickname().equals(""))
                return identifier + "(" + user.getNickname() + ")";
        } else {
            if (user.getNotename() != null && !user.getNotename().equals("")) {
                return identifier + "(" + user.getNotename() + ")";
            }
        }
        return identifier;
    }

    void updateContactStatus(long id, final int status) {
        Realm realm = Realm.getDefaultInstance();
        Logger.d("updateContactStatus");
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

    public void addContact(TIMUserProfile userProfile) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        User user = realm.createObject(User.class);
        user.setIdentifier(userProfile.getIdentifier());
        user.setUsername(User.getUserDescriber(userProfile));
        user.setNickname(userProfile.getNickName());
        user.setRemark(userProfile.getRemark());
        user.setHeadAvatar(userProfile.getFaceUrl());
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
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                Logger.d(i + ":" + s);
            }

            @Override
            public void onSuccess(TIMUserProfile user) {
                Logger.d(user.toString());
                userProfile = user;
                SPHelper.put("USER", MessageHelper.getInstance().toJson(userProfile));
            }
        });
    }
}
