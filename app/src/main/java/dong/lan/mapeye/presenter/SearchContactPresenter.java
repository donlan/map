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
import android.view.View;
import android.widget.EditText;

import com.orhanobut.logger.Logger;

import java.util.Collections;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import dong.lan.mapeye.R;
import dong.lan.mapeye.common.JMCenter;
import dong.lan.mapeye.common.UserManager;
import dong.lan.mapeye.contracts.SearchContact;
import dong.lan.mapeye.views.SearchContactActivity;
import dong.lan.mapeye.views.customsView.Dialog;

/**
 * Created by 梁桂栋 on 16-11-16 ： 上午10:56.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class SearchContactPresenter implements SearchContact.Presenter {
    private static final String TAG = "SearchContactPresenter";
    private SearchContactActivity view;

    public SearchContactPresenter(SearchContactActivity view) {
        this.view = view;
    }

    @Override
    public void queryContact(final String username) {
        Logger.d(username);
        JMessageClient.getUserInfo(username, new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                if(i == 0){
                    if (userInfo != null) {
                        view.setContactsAdapter(Collections.singletonList(userInfo));
                    } else {
                        view.toast("无匹配用户");
                    }
                }else{
                    view.toast(s);
                }
            }
        });
    }

    @Override
    public void sendInvite(final UserInfo user) {
        if (user.getUserName().equals(UserManager.instance().myIdentifier())) {
            view.toast("不能添加自己为好友");
            return;
        }
        final Dialog dialog = new Dialog(view);
        dialog.setupView(R.layout.dialog_add_friend_reason)
                .bindClick(R.id.addFriendReqDone, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String reason = dialog.getText(R.id.addFriendReason, EditText.class);
                        if (TextUtils.isEmpty(reason)) {
                            view.toast("请求理由不能为空");
                            return;
                        }
                        JMCenter.friendInvite(user.getUserName(), reason, new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                Logger.d(i+","+s);
                                if(i == 0){
                                    view.show("发送请求成功");
                                }else{
                                    view.toast(s);
                                }
                            }
                        });

                        dialog.dismiss();
                    }
                }).show();


    }
}
