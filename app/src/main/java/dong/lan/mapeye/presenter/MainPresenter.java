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

import android.text.TextUtils;

import com.tencent.TIMSNSChangeInfo;
import com.tencent.TIMUserProfile;

import java.util.List;

import dong.lan.mapeye.common.UserManager;
import dong.lan.mapeye.model.Affair;
import dong.lan.mapeye.views.MainActivity;
import io.realm.Realm;

/**
 * Created by 梁桂栋 on 16-11-21 ： 下午5:09.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class MainPresenter {
    private static final String TAG = "MainPresenter";
    private MainActivity view;

    public MainPresenter(MainActivity view) {
        this.view = view;
    }

    public void handlerReceivedInvite(List<TIMSNSChangeInfo> list) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (int i = 0; i < list.size(); i++) {
            TIMSNSChangeInfo info = list.get(0);
            if (info.getIdentifier().equals(UserManager.instance().myIdentifier()))
                continue;
            Affair affair = realm.createObject(Affair.class);
            affair.setContent("好友请求理由：" + info.getWording());
            affair.setCreatedTime(System.currentTimeMillis());
            affair.setExtras("");
            affair.setType(Affair.TYPE_USER_INVITE);
            affair.setFromUser(TextUtils.isEmpty(info.getRemark())
                    ? (TextUtils.isEmpty(info.getNickName())
                    ? info.getIdentifier() : info.getNickName()) : info.getRemark());
        }
        realm.commitTransaction();
        realm.close();
        realm = null;
    }

    public void handlerInviteAccepted(List<TIMUserProfile> userProfiles) {
        for (int i = 0; i < userProfiles.size(); i++) {
            if (userProfiles.get(i).getIdentifier().equals(UserManager.instance().myIdentifier()))
                continue;
            UserManager.instance().addContact(userProfiles.get(i));
        }
    }

    public void handlerContactDeleted(List<String> userIds) {

        for (int i = 0; i < userIds.size(); i++) {
            UserManager.instance().deleteContact(userIds.get(i));
        }
    }


}
