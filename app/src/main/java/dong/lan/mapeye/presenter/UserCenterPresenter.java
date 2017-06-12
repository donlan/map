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
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import dong.lan.mapeye.common.Config;
import dong.lan.mapeye.common.UserManager;
import dong.lan.mapeye.contracts.UserCenterContract;
import dong.lan.mapeye.model.users.IUserInfo;
import dong.lan.mapeye.model.users.User;
import dong.lan.mapeye.utils.PhotoUtil;
import dong.lan.mapeye.views.ChatActivity;
import dong.lan.mapeye.views.UserCenterActivity;
import io.realm.Realm;


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
    private IUserInfo userInfo;
    private UserInfo myUserInfo;

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
                        File file = new File(dir, userInfo.identifier() + "_head_" +
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
                        Config.PICTURE_PATH, userInfo.identifier() + "head.jpg", bitmap, true);
                view.refreshAvatar(BitmapFactory.decodeFile(headFile.getPath()));
                JMessageClient.updateUserAvatar(headFile, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            view.toast(s+":"+myUserInfo.getAvatar());
                        } else {
                            view.toast(s);
                        }
                    }
                });
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
    public void saveUserMarkerName(final String name) {
        if (name == null || name.equals("")) {
            view.toast("名字不能为空");
            return;
        }
        if (isUserSelf) {
            if (name.equals(userInfo.nickname()))
                return;
            myUserInfo.setNickname(name);
            JMessageClient.updateMyInfo(UserInfo.Field.nickname, myUserInfo, new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        updateLocalUserInfo();
                        view.toast("昵称设置成功");
                    } else {
                        view.toast(s);
                    }
                }
            });
        } else {
            if (name.equals(userInfo.remark()))
                return;
            if (myUserInfo != null) {
                myUserInfo.setNotename(name);
                myUserInfo.updateNoteName(name, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            updateLocalUserInfo();
                            view.toast("更新备注成功");
                        } else {
                            view.toast(s);
                        }
                    }
                });
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        User user = realm.where(User.class).equalTo("identifier", myUserInfo.getUserName())
                                .findFirst();
                        if (user != null)
                            user.setRemark(name);
                    }
                });
            }
        }
    }

    @Override
    public void saveUserPhone(String phoneNumber) {
        if (isUserSelf && (phoneNumber == null || phoneNumber.length() != 11)) {
            view.toast("手机号码不正确");
            return;
        }
        myUserInfo.setSignature(phoneNumber);
        JMessageClient.updateMyInfo(UserInfo.Field.signature, myUserInfo, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    updateLocalUserInfo();
                    view.toast("设置手机号成功");
                } else {
                    view.toast(s);
                }
            }
        });

    }


    @Override
    public void checkIntentAndInit(final boolean isUserSelf) {
        this.isUserSelf = isUserSelf;


        final String username = isUserSelf ? UserManager.instance().myIdentifier() :
                view.getIntent().getStringExtra(UserCenterContract.View.KEY_USERNAME);

        JMessageClient.getUserInfo(username, new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                if (i == 0 && userInfo != null) {
                    UserCenterPresenter.this.userInfo = new User(userInfo);
                    myUserInfo = userInfo;
                    myUserInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                        @Override
                        public void gotResult(int i, String s, Bitmap bitmap) {
                            if(i == 0)
                            {
                                view.refreshAvatar(bitmap);
                            }
                        }
                    });
                    view.initView(UserCenterPresenter.this.userInfo, isUserSelf);
                } else {
                    view.toast("获取用户信息失败:" + s);
                }
            }
        });

    }

    private void updateLocalUserInfo() {
        if (myUserInfo != null) {
            Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    User user = realm.where(User.class).equalTo("identifier", myUserInfo.getUserName())
                            .findFirst();
                    if (!myUserInfo.getAvatar().equals(user.avatar()))
                        user.setHeadAvatar(myUserInfo.getAvatar());
                    if (!myUserInfo.getNickname().equals(user.nickname())) {
                        user.setNickname(myUserInfo.getNickname());
                    }
                    if (!myUserInfo.getSignature().equals(user.phone())) {
                        user.setPhoneNumber(myUserInfo.getSignature());
                    }
                    if (!myUserInfo.getNotename().equals(user.remark())) {
                        user.setRemark(myUserInfo.getNotename());
                    }
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    error.printStackTrace();
                }
            });
        }
    }

    @Override
    public void toChatActivity() {
        Intent intent = new Intent(view, ChatActivity.class);
        intent.putExtra(ChatActivity.CHAT_TYPE, 0);
        intent.putExtra(ChatActivity.CHAT_TITTLE, userInfo.identifier());
        intent.putExtra(ChatActivity.CHAT_PEER, userInfo.identifier());
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
