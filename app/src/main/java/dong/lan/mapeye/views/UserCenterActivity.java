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

package dong.lan.mapeye.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.tencent.TIMUserProfile;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import dong.lan.mapeye.R;
import dong.lan.mapeye.common.Config;
import dong.lan.mapeye.common.UserManager;
import dong.lan.mapeye.contracts.UserCenterContract;
import dong.lan.mapeye.model.users.User;
import dong.lan.mapeye.presenter.UserCenterPresenter;
import dong.lan.mapeye.views.customsView.LabelTextView;

/**
 * Created by 梁桂栋 on 16-12-12 ： 下午2:42.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class UserCenterActivity extends BaseActivity implements UserCenterContract.View {


    @BindView(R.id.user_center_head)
    ImageView userHead;
    @BindView(R.id.user_center_rename_et)
    EditText renameEt;
    @BindView(R.id.user_center_rename_done)
    ImageButton renameDoneIb;
    @BindView(R.id.user_center_phone_et)
    EditText phoneEt;
    @BindView(R.id.user_center_phone_done)
    ImageButton phoneDoneIb;
    @BindView(R.id.user_center_tag)
    LabelTextView tagLtv;
    @BindView(R.id.user_center_look_for_monitor)
    LabelTextView lookForMonitorLtv;
    @BindView(R.id.user_center_upload_head)
    LabelTextView uploadAvatarLtv;

    @BindView(R.id.userTimerTask)
    LabelTextView userTimerTaskLtv;

    @OnClick(R.id.userTimerTask)
    void toUserTimerCenter() {
        Intent intent = new Intent(this, MonitorTimerTaskActivity.class);
        intent.putExtra(Config.KEY_IDENTIFIER, identifier);
        startActivity(intent);
    }

    @OnClick(R.id.bar_left)
    void back() {
        finish();
    }

    @OnClick(R.id.user_center_upload_head)
    void chatOrUploadAvatar() {
        presenter.chatOrUploadAvatar();
    }


    @OnClick(R.id.user_center_phone_done)
    void setPhoneDone() {
        presenter.saveUserPhone(phoneEt.getText().toString());
        phoneDoneIb.setVisibility(View.GONE);
    }

    @OnClick(R.id.user_center_rename_done)
    void renameDone() {
        presenter.saveUserMarkerName(renameEt.getText().toString());
        renameDoneIb.setVisibility(View.GONE);
    }

    @OnClick(R.id.user_center_head)
    void selectAvatar() {
        presenter.pickAvatarImage();
    }

    @OnTextChanged(R.id.user_center_rename_et)
    void renameTextChange(Editable editable) {
        if (editable.length() > 0 || renameDoneIb.getVisibility() == View.GONE)
            renameDoneIb.setVisibility(View.VISIBLE);
    }

    @OnTextChanged(R.id.user_center_phone_et)
    void setPhoneChange(Editable editable) {
        if (editable.length() > 0 || phoneDoneIb.getVisibility() == View.GONE)
            phoneDoneIb.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.bar_right)
    void toChat() {
        presenter.toChatActivity();
    }

    @OnClick(R.id.user_center_look_for_monitor)
    void toMonitorRecordAction() {
        if (identifier == null || identifier.equals(UserManager.instance().myIdentifier()))
            return;
        Intent intent = new Intent(this, MonitorRecordActivity.class);
        intent.putExtra(MonitorRecordActivity.IDENTIFIER, identifier);
        startActivity(intent);
    }

    @BindView(R.id.bar_center)
    TextView tittleTv;
    @BindView(R.id.bar_right)
    TextView barRightTv;
    @BindView(R.id.userIdentifier)
    LabelTextView identifyLtv;


    private UserCenterPresenter presenter;
    private String identifier;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        bindView(this);
        presenter = new UserCenterPresenter(this);
        presenter.checkIntentAndInit(!getIntent().hasExtra(UserCenterContract.View.KEY_USERNAME));
    }


    @Override
    public void refreshAvatar(Bitmap bitmap) {
        userHead.setImageBitmap(bitmap);
    }

    @Override
    public void initView(TIMUserProfile userInfo, boolean isUserSelf) {
        identifier = userInfo.getIdentifier();
        tittleTv.setText(User.getUserDescriber(userInfo));
        identifyLtv.setText(userInfo.getIdentifier());
        String phone = userInfo.getIdentifier();
        if (phone == null || phone.equals("")) {
            phoneEt.setHint("未绑定手机");
        }
        phoneEt.setText(phone);
        phoneEt.setFocusable(false);
        phoneEt.setEnabled(false);
        tagLtv.setVisibility(View.GONE);
        String nickname;
        if (isUserSelf) {
            userTimerTaskLtv.setVisibility(View.GONE);
            nickname = userInfo.getNickName();
            renameEt.setHint(nickname == null || nickname.equals("") ? "设置昵称" : nickname);
        } else {
            nickname = userInfo.getRemark();
            renameEt.setHint(nickname == null || nickname.equals("") ? "设置备注" : nickname);
            barRightTv.setText("聊天");
            phoneEt.setFocusable(false);
            phoneEt.setEnabled(false);
            uploadAvatarLtv.setText("发消息");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Config.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
                presenter.handlerCameraUploadAvatar(requestCode);
                break;
            case Config.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地修改头像
                presenter.handlerGalleryUploadAvatar(resultCode, data);
                break;
            case Config.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
                presenter.resetAndUploadAvatar(data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
