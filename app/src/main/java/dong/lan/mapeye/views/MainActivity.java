package dong.lan.mapeye.views;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;

import com.orhanobut.logger.Logger;
import com.tencent.TIMFriendGroup;
import com.tencent.TIMFriendshipProxyListener;
import com.tencent.TIMFriendshipProxyStatus;
import com.tencent.TIMManager;
import com.tencent.TIMSNSChangeInfo;
import com.tencent.TIMUserProfile;
import com.tencent.qcloud.tlslibrary.activity.HostLoginActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import dong.lan.mapeye.R;
import dong.lan.mapeye.common.UserManager;
import dong.lan.mapeye.model.Affair;
import dong.lan.mapeye.presenter.MainPresenter;
import dong.lan.mapeye.views.record.RecordFragment;
import io.realm.Realm;

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
        startActivity(new Intent(this, HostLoginActivity.class));
        finish();
    }

    @OnClick(R.id.offline_map_download)
    void toDownloadOfflineMap() {
        startActivity(new Intent(this, OfflineMapActivity.class));
    }

    Fragment[] tabs;
    private MainPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_of_main);
        bindView(this);
        init();
    }


    private void init() {

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
        presenter = new MainPresenter(this);

        UserManager.instance().initMe();

        TIMManager.getInstance().setFriendshipProxyListener(new TIMFriendshipProxyListener() {
            @Override
            public void OnProxyStatusChange(TIMFriendshipProxyStatus timFriendshipProxyStatus) {
                Logger.d(timFriendshipProxyStatus.getStatus());
            }

            @Override
            public void OnAddFriends(List<TIMUserProfile> list) {
                presenter.handlerInviteAccepted(list);
                Logger.d(list);
            }

            @Override
            public void OnDelFriends(List<String> list) {
                presenter.handlerContactDeleted(list);
                Logger.d(list);
            }

            @Override
            public void OnFriendProfileUpdate(List<TIMUserProfile> list) {
                Logger.d(list);
            }

            @Override
            public void OnAddFriendReqs(List<TIMSNSChangeInfo> list) {
                presenter.handlerReceivedInvite(list);
                Logger.d(list);
            }

            @Override
            public void OnAddFriendGroups(List<TIMFriendGroup> list) {
                Logger.d(list);
            }

            @Override
            public void OnDelFriendGroups(List<String> list) {
                Logger.d(list);
            }

            @Override
            public void OnFriendGroupUpdate(List<TIMFriendGroup> list) {
                Logger.d(list);
            }
        });
    }

    public void onEvent(ContactNotifyEvent event) {
        String reason = event.getReason();
        String fromUsername = event.getFromUsername();
        String appkey = event.getfromUserAppKey();

        switch (event.getType()) {
            case invite_received://收到好友邀请
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                Affair affair = realm.createObject(Affair.class);
                affair.setContent("好友请求理由：" + reason);
                affair.setCreatedTime(System.currentTimeMillis());
                affair.setExtras("");
                affair.setType(Affair.TYPE_USER_INVITE);
                affair.setFromUser(fromUsername);
                realm.commitTransaction();
                realm.close();
                break;
            case invite_accepted://对方接收了你的好友邀请
                //...
                break;
            case invite_declined://对方拒绝了你的好友邀请
                //...
                break;
            case contact_deleted://对方将你从好友中删除
                //...
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
    }


    class PagerAdapter extends FragmentPagerAdapter {

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
