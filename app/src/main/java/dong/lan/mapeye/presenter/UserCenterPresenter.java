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

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;

import com.orhanobut.logger.Logger;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.tlslibrary.helper.JMHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dong.lan.mapeye.common.Config;
import dong.lan.mapeye.common.UserManager;
import dong.lan.mapeye.contracts.UserCenterContract;
import dong.lan.mapeye.utils.PhotoUtil;
import dong.lan.mapeye.views.ChatActivity;
import dong.lan.mapeye.views.UserCenterActivity;


/**
 * Created by 梁桂栋 on 16-12-17 ： 下午8:19.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class UserCenterPresenter implements UserCenterContract.Presenter {

    private UserCenterActivity view;
    private String avatarPath;
    private boolean isUserSelf;
    private TIMUserProfile userInfo;

    public UserCenterPresenter(UserCenterActivity view) {
        this.view = view;
    }

    @Override
    public void pickAvatarImage() {
        new AlertDialog.Builder(view)
                .setTitle("上传新头像")
                .setPositiveButton("相机", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File dir = new File(Config.PICTURE_PATH);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        // 原图
                        File file = new File(dir, userInfo.getIdentifier() + "_head_" +
                                new SimpleDateFormat("head_yyyyMMdd_HHmm", Locale.CHINA).format(new Date()));
                        avatarPath = file.getAbsolutePath();// 获取相片的保存路径
                        Uri imageUri = Uri.fromFile(file);

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        view.startActivityForResult(intent,
                                Config.REQUESTCODE_UPLOADAVATAR_CAMERA);
                    }
                })
                .setNegativeButton("相册", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        view.startActivityForResult(intent,
                                Config.REQUESTCODE_UPLOADAVATAR_LOCATION);
                    }
                }).show();
    }

    @Override
    public void cropImageAction(Uri uri, int width, int height, int requestCode, boolean isCrop) {
        Intent intent;
        if (isCrop) {
            intent = new Intent("com.android.camera.action.CROP");
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        view.startActivityForResult(intent, requestCode);

    }

    @Override
    public void resetAndUploadAvatar(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            if (bitmap != null) {
                view.refreshAvatar(bitmap);
                File headFile = PhotoUtil.saveBitmapAndReturn(
                        Config.PICTURE_PATH, userInfo.getIdentifier() + "head.jpg", bitmap, true);

            }
        }
    }

    @Override
    public void handlerCameraUploadAvatar(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                view.show("SD不可用");
                return;
            }
            File file = new File(avatarPath);
            cropImageAction(Uri.fromFile(file), 200, 200,
                    Config.REQUESTCODE_UPLOADAVATAR_CROP, true);
        }
    }

    @Override
    public void handlerGalleryUploadAvatar(int resultCode, Intent data) {
        Uri uri;
        if (data == null) {
            return;
        }
        if (resultCode == Activity.RESULT_OK) {
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                view.show("SD不可用");
                return;
            }
            uri = data.getData();
            avatarPath = uri.getPath();
            cropImageAction(uri, 200, 200,
                    Config.REQUESTCODE_UPLOADAVATAR_CROP, true);
        } else {
            view.show("照片获取失败");
        }

    }

    @Override
    public void saveUserMarkerName(String name) {
        if (name == null || name.equals("")) {
            view.toast("名字不能为空");
            return;
        }
        if (isUserSelf) {
            if (name.equals(userInfo.getNickName()))
                return;
            TIMFriendshipManager.getInstance().setNickName(name, new TIMCallBack() {
                @Override
                public void onError(int i, String s) {
                    view.toast(s);
                }

                @Override
                public void onSuccess() {
                    view.toast("昵称设置成功");
                }
            });
        } else {
            if (name.equals(userInfo.getRemark()))
                return;
            TIMFriendshipManager.getInstance().setFriendRemark(userInfo.getIdentifier(), name,
                    new TIMCallBack() {
                        @Override
                        public void onError(int i, String s) {
                            view.toast(s);
                        }

                        @Override
                        public void onSuccess() {
                            view.toast("设置备注成功");
                        }
                    });
        }
    }

    @Override
    public void saveUserPhone(String phoneNumber) {
        if (isUserSelf && (phoneNumber == null || phoneNumber.length() != 11)) {
            view.toast("手机号码不正确");
            return;
        }
        TIMFriendshipManager.getInstance().setCustomInfo("phoneNumber", phoneNumber.getBytes(),
                new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        view.toast(s);
                    }

                    @Override
                    public void onSuccess() {
                    }
                });
    }


    @Override
    public void checkIntentAndInit(final boolean isUserSelf) {
        this.isUserSelf = isUserSelf;


        final String username = isUserSelf ? UserManager.instance().myIdentifier() :
                view.getIntent().getStringExtra(UserCenterContract.View.KEY_USERNAME);
        TIMFriendshipManager.getInstance().getFriendsProfile(Collections.singletonList(username), new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                view.toast(s);
            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                if (timUserProfiles != null && timUserProfiles.size() > 0) {
                    userInfo = timUserProfiles.get(0);
                    view.initView(timUserProfiles.get(0), isUserSelf);
                }
            }
        });

    }

    @Override
    public void toChatActivity() {
        Intent intent = new Intent(view, ChatActivity.class);
        intent.putExtra(ChatActivity.CHAT_TYPE, 0);
        intent.putExtra(ChatActivity.CHAT_TITTLE,userInfo.getIdentifier());
        intent.putExtra(ChatActivity.CHAT_PEER, JMHelper.getJUsername(userInfo.getIdentifier()));
        view.startActivity(intent);
    }

    @Override
    public void chatOrUploadAvatar() {
        if (isUserSelf)
            pickAvatarImage();
        else
            toChatActivity();
    }


}
