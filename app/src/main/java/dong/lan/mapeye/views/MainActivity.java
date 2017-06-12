package dong.lan.mapeye.views;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.model.UserInfo;
import dong.lan.mapeye.R;
import dong.lan.mapeye.common.UserManager;
import dong.lan.mapeye.model.Affair;
import dong.lan.mapeye.model.users.Contact;
import dong.lan.mapeye.model.users.Friend;
import dong.lan.mapeye.model.users.User;
import dong.lan.mapeye.utils.SPHelper;
import dong.lan.mapeye.views.access.LoginAndSignActivity;
import dong.lan.mapeye.views.record.RecordFragment;
import dong.lan.permission.Permission;
import io.realm.Realm;

import static dong.lan.mapeye.contracts.LoginAndSignContract.loginAndSignView.KEY_IS_LOGIN;

/**
 * Created by 梁桂栋 on 16-11-6 ： 下午8:31.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.container)
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    Fragment[] tabs;

    @OnClick(R.id.bar_add)
    public void toAddRecordActivity() {
        startActivity(new Intent(this, AddRecordActivity.class));
    }

    @OnClick(R.id.bar_add_user)
    public void toQueryUserActivity() {
        startActivity(new Intent(this, SearchContactActivity.class));
    }

    @OnClick(R.id.user_center)
    public void toUserCenterActivity() {
        Intent intent = new Intent(this, UserCenterActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.setting)
    public void toSettingActivity() {
        startActivity(new Intent(this, SettingActivity.class));
    }

    @OnClick({R.id.drawer_logout, R.id.bar_logout})
    public void logout() {
        UserManager.instance().logout();
        startActivity(new Intent(this, LoginAndSignActivity.class));
        finish();
    }

    @OnClick(R.id.offline_map_download)
    void toDownloadOfflineMap() {
        startActivity(new Intent(this, OfflineMapActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_of_main);
        bindView(this);
        init();
    }


    private void init() {

        if (getIntent().getBooleanExtra("loginFlag", false)) {
            SPHelper.putBoolean(KEY_IS_LOGIN, true);
        }


        UserManager.instance().initMe();


        setSupportActionBar(toolbar);
        JMessageClient.registerEventReceiver(this);
        tabs = new Fragment[4];
        tabs[0] = RecordFragment.newInstance("记录");
        tabs[1] = ContactsFragment.newInstance("通讯录");
        tabs[2] = ConversationFragment.newInstance("消息");
        tabs[3] = AffairsListFragment.newInstance("事务");
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(Color.WHITE, Color.WHITE);
        tabLayout.setSelectedTabIndicatorColor(Color.LTGRAY);

    }

    public void onEvent(LoginStateChangeEvent event) {
        LoginStateChangeEvent.Reason reason = event.getReason();//获取变更的原因
        UserInfo myInfo = event.getMyInfo();//获取当前被登出账号的信息
        switch (reason) {
            case user_password_change:
                //用户密码在服务器端被修改
                break;
            case user_logout:
                toast("当前账号在其他设备登录,请重新登录");
                UserManager.instance().logout();
                startActivity(new Intent(this, LoginAndSignActivity.class));
                finish();
                break;
            case user_deleted:
                //用户被删除
                break;
        }
    }



    public void onEvent(ContactNotifyEvent event) {
        final String reason = event.getReason();
        final String fromUsername = event.getFromUsername();

        Realm realm = Realm.getDefaultInstance();
        switch (event.getType()) {
            case invite_received://收到好友邀请
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Affair affair = realm.createObject(Affair.class);
                        affair.setOwner(UserManager.instance().myIdentifier());
                        affair.setId(String.valueOf(System.currentTimeMillis()));
                        affair.setContent("好友请求理由：" + reason);
                        affair.setCreatedTime(System.currentTimeMillis());
                        affair.setExtras("");
                        affair.setType(Affair.TYPE_USER_INVITE);
                        affair.setHandle(Affair.HANDLE_NO);
                        affair.setFromUser(fromUsername);
                    }
                });

                break;
            case invite_accepted://对方接收了你的好友邀请
                JMessageClient.getUserInfo(fromUsername, new GetUserInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, final UserInfo userInfo) {
                        if(i == 0){
                            Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    Affair affair = realm.createObject(Affair.class);
                                    affair.setOwner(UserManager.instance().myIdentifier());
                                    affair.setId(String.valueOf(System.currentTimeMillis()));
                                    affair.setContent(fromUsername + "同意了你的好友请求");
                                    affair.setCreatedTime(System.currentTimeMillis());
                                    affair.setExtras("");
                                    affair.setType(Affair.TYPE_NOTICE);
                                    affair.setHandle(Affair.HANDLE_AS_NOTICE);
                                    affair.setFromUser(fromUsername);
                                    Friend friend = new Friend();
                                    friend.setOwnerId(UserManager.instance().myIdentifier());
                                    friend.setFriendId(userInfo.getUserName());
                                    User user = new User(userInfo);
                                    realm.copyToRealmOrUpdate(user);
                                    friend.setUser(user);
                                    realm.copyToRealmOrUpdate(friend);
                                }
                            });
                        }
                    }
                });

                break;
            case invite_declined://对方拒绝了你的好友邀请
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Affair affair = realm.createObject(Affair.class);
                        affair.setOwner(UserManager.instance().myIdentifier());
                        affair.setId(String.valueOf(System.currentTimeMillis()));
                        affair.setContent(fromUsername + " 拒绝了你的好友请求:" + reason);
                        affair.setCreatedTime(System.currentTimeMillis());
                        affair.setExtras("");
                        affair.setType(Affair.TYPE_NOTICE);
                        affair.setHandle(Affair.HANDLE_AS_NOTICE);
                        affair.setFromUser(fromUsername);
                    }
                });
                break;
            case contact_deleted://对方将你从好友中删除
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Affair affair = realm.createObject(Affair.class);
                        affair.setOwner(UserManager.instance().myIdentifier());
                        affair.setId(String.valueOf(System.currentTimeMillis()));
                        affair.setContent(fromUsername + "将你从好友中删除");
                        affair.setCreatedTime(System.currentTimeMillis());
                        affair.setExtras("");
                        affair.setHandle(Affair.HANDLE_AS_NOTICE);
                        affair.setType(Affair.TYPE_NOTICE);
                        affair.setFromUser(fromUsername);

                        realm.where(Contact.class).equalTo("user.identifier", fromUsername).findAll().deleteAllFromRealm();
                    }
                });
                break;
            default:
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permission.instance().handleRequestResult(this,requestCode,permissions,grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
    }


    private class PagerAdapter extends FragmentPagerAdapter {

        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return tabs[position];
        }

        @Override
        public int getCount() {
            return tabs.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position].getArguments().getString(BaseFragment.ARG_TITTLE_TAG);
        }
    }
}
