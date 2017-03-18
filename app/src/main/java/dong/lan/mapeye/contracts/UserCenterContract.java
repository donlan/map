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

package dong.lan.mapeye.contracts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;


import cn.jpush.im.android.api.model.UserInfo;
import dong.lan.mapeye.model.users.IUserInfo;


/**
 * Created by 梁桂栋 on 16-12-12 ： 下午3:17.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public final class UserCenterContract {
    private UserCenterContract() {
    }

    public interface View {
        String KEY_USERNAME = "username";


        /**
         * 将从Intent解析出来的头像图片设置到页面中
         *
         * @param bitmap 从Intent解析出来的头像图片位图
         */
        void refreshAvatar(Bitmap bitmap);

        /**
         * 更具用户信息初始化页面
         *
         * @param userInfo   用户信息
         * @param isUserSelf 是否是用户自己
         */
        void initView(IUserInfo userInfo, boolean isUserSelf);
    }

    public interface Presenter {
        //从相机或者相册获取头像图片
        void pickAvatarImage();

        //通过Intent跳转到图片裁剪
        void cropImageAction(Uri uri, int width, int height, int requestCode, boolean isCrop);

        //重新设置页面的头像并上传到用户中心
        void resetAndUploadAvatar(Intent data);

        //处理通过相机拍照返回的图片
        void handlerCameraUploadAvatar(int resultCode);

        /**
         * 处理通过图库选出的头像图片
         *
         * @param resultCode 结果码
         * @param data       Intent返回的数据
         */
        void handlerGalleryUploadAvatar(int resultCode, Intent data);

        /**
         * 如果时用户自己则保存昵称
         * 否则保存为备注名
         *
         * @param name 名字
         */
        void saveUserMarkerName(String name);

        /**
         * 保存用户的手机号码
         *
         * @param phoneNumber 手机号码
         */
        void saveUserPhone(String phoneNumber);

        /**
         * 根据Intent的携带参数初始化
         *
         * @param isUserSelf 是否时用户自己 true:用户自己 false:好友
         */
        void checkIntentAndInit(boolean isUserSelf);

        /**
         * 跳转到聊天页面
         */
        void toChatActivity();

        void chatOrUploadAvatar();
    }
}
