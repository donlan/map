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

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.tencent.TIMFriendAddResponse;
import com.tencent.TIMFriendResponseType;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMMessage;
import com.tencent.TIMValueCallBack;

import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import dong.lan.mapeye.R;
import dong.lan.mapeye.common.JMCenter;
import dong.lan.mapeye.common.MessageHelper;
import dong.lan.mapeye.model.Affair;
import dong.lan.mapeye.model.message.CMDMessage;
import dong.lan.mapeye.model.message.MessageCreator;
import dong.lan.mapeye.utils.DateUtils;
import dong.lan.mapeye.views.customsView.LabelTextView;
import io.realm.Realm;

public class AffairHandleActivity extends BaseActivity {

    public static final String KEY_AFFAIR_ID = "affairId";
    public static final String KEY_AFFAIR_POSITION = "position";

    @BindView(R.id.head)
    ImageView head;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.extras)
    TextView extras;
    @BindView(R.id.deny)
    LabelTextView deny;
    @BindView(R.id.accept)
    LabelTextView accept;
    @BindView(R.id.bar_center)
    TextView tittle;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.bar_right)
    TextView deleteTv;
    @BindView(R.id.affairType)
    LabelTextView affairType;

    @OnClick(R.id.bar_right)
    void delete() {
        if (realm == null || affair == null) {
            show("没有事务可以删除");
            return;
        }
        try {
            realm.beginTransaction();
            affair.deleteFromRealm();
            realm.commitTransaction();
            isDelete = true;
            finish();
        } catch (Exception e) {
            Logger.d(e.getMessage());
            toast(e.getMessage());
        }
    }

    @OnClick(R.id.bar_left)
    void back() {
        finish();
    }

    @OnClick(R.id.deny)
    void deny(final View view) {
        toast("开始发送同意请求的回执消息");
        view.setEnabled(false);

        if (affair.getType() == Affair.TYPE_MONITOR_BIND) {
            handleMonitorInvite(CMDMessage.CMD_MONITOR_INVITE_DENY,"拒绝位置共享绑定",Affair.HANDLE_DENY);
//            TIMMessage message = MessageCreator.createNormalCmdMessage(CMDMessage.CMD_MONITOR_INVITE_DENY,
//                    "拒绝位置共享绑定",
//                    "");
//            message.setCustomStr(affair.getExtras());
//            MessageHelper.sendTIMMessage(affair.getFromUser(), message, new TIMValueCallBack<TIMMessage>() {
//                @Override
//                public void onError(int i, String s) {
//                    toast("发送回执消息失败： " + s);
//                    view.setEnabled(true);
//                }
//
//                @Override
//                public void onSuccess(TIMMessage message) {
//                    view.setEnabled(true);
//                    if (realm == null)
//                        realm = Realm.getDefaultInstance();
//                    realm.beginTransaction();
//                    affair.setHandle(Affair.HANDLE_DENY);
//                    realm.commitTransaction();
//                    finish();
//                    toast("发送回执消息成功");
//                }
//            });
        } else if (affair.getType() == Affair.TYPE_USER_INVITE) {
            handleFriendInvite(true, affair.getFromUser());
        }
    }

    @OnClick(R.id.accept)
    void accept(final View view) {
        toast("开始发送同意请求的回执消息");
        view.setEnabled(false);
        if (affair.getType() == Affair.TYPE_MONITOR_BIND) {

            handleMonitorInvite(CMDMessage.CMD_MONITOR_INVITE_OK,"已同意位置共享绑定",Affair.HANDLE_ACCEPT);
//            TIMMessage message = MessageCreator.createNormalCmdMessage(CMDMessage.CMD_MONITOR_INVITE_DENY,
//                    "已同意位置共享绑定",
//                    "");
//            message.setCustomStr(affair.getExtras());
//            MessageHelper.sendTIMMessage(affair.getFromUser(), message, new TIMValueCallBack<TIMMessage>() {
//                @Override
//                public void onError(int i, String s) {
//                    view.setEnabled(true);
//                    toast("发送回执消息失败： " + s);
//                }
//
//                @Override
//                public void onSuccess(TIMMessage message) {
//
//                }
//            });
        } else if (affair.getType() == Affair.TYPE_USER_INVITE) {
            handleFriendInvite(true, affair.getFromUser());
        }
        view.setEnabled(true);
    }

    private Realm realm;
    private Affair affair;
    private int position = -1;
    private boolean isDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affair_handle);
        bindView(this);
        deleteTv.setText("删除");
        checkIntent();
    }

    private void handleMonitorInvite(int cmd, String reply, final int handle){
        String str[] = affair.getExtras().split(",");
        Message m = new JMCenter.JMessage(cmd,
                affair.getFromUser(),reply)
                .appendStringExtra(JMCenter.EXTRAS_RECORD_ID,str[0])
                .appendStringExtra(JMCenter.EXTRAS_IDENTIFIER,str[1])
                .build();
        JMCenter.sendMessage(m, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if(i==0){
                    if (realm == null)
                        realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    affair.setHandle(handle);
                    realm.commitTransaction();
                    finish();
                    toast("发送回执消息成功");
                }else
                    toast(s);
            }
        });
    }

    private void handleFriendInvite(final boolean isAccept, String remark) {
        if(isAccept){
            JMCenter.acceptInvite(affair.getFromUser(), new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    Logger.d(i+","+s);
                }
            });
        }else{
            JMCenter.declineInvite(affair.getFromUser(), "", new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    Logger.d(i+","+s);
                }
            });
        }

        TIMFriendAddResponse response = new TIMFriendAddResponse();
        response.setIdentifier(affair.getFromUser());
        response.setRemark(remark);
        response.setType(isAccept ? TIMFriendResponseType.AgreeAndAdd : TIMFriendResponseType.Reject);
        TIMFriendshipManager.getInstance().addFriendResponse(response, new TIMValueCallBack<TIMFriendResult>() {
            @Override
            public void onError(int i, String s) {
                toast(s);
                Logger.d(i + "," + s);
            }

            @Override
            public void onSuccess(TIMFriendResult timFriendResult) {
                Logger.d(timFriendResult.getStatus());
                if (realm == null)
                    realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                affair.setHandle(isAccept ? Affair.HANDLE_ACCEPT : Affair.HANDLE_DENY);
                realm.commitTransaction();
                finish();
            }
        });

    }

    private void checkIntent() {
        if (getIntent().hasExtra(KEY_AFFAIR_ID)) {
            String id = getIntent().getStringExtra(KEY_AFFAIR_ID);
            position = getIntent().getIntExtra(KEY_AFFAIR_POSITION, -1);
            if (TextUtils.isEmpty(id)) {
                toast("无事务需要处理");
                finish();
            } else {
                realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                affair = realm.where(Affair.class).equalTo("id", id).findFirst();
                realm.commitTransaction();
                if (affair != null) {
                    tittle.setText(affair.getFromUser());
                    content.setText(affair.getContent());
                    extras.setText(affair.getExtras());
                    affairType.setText(affair.getTypeString());
                    time.setText(DateUtils.format(new Date(affair.getCreatedTime()), "事务时间： YYYY-MM-dd HH:mm"));
                    if (affair.getHandle() == Affair.HANDLE_AS_NOTICE) {
                        deny.setVisibility(View.GONE);
                        accept.setVisibility(View.GONE);
                    } else if (affair.getHandle() == Affair.HANDLE_ACCEPT) {
                        accept.setText("已处理");
                        deny.setEnabled(false);
                        accept.setEnabled(false);
                    } else if (affair.getHandle() == Affair.HANDLE_DENY) {
                        deny.setText("已拒绝");
                        deny.setEnabled(false);
                        accept.setEnabled(false);
                    }
                }
            }
        } else {
            toast("无事务需要处理");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null)
            realm.close();
        realm = null;
        affair = null;
        if (position != -1) {
            setResult(isDelete ? -position : position);
        }
    }
}
