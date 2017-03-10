package com.tencent.qcloud.presentation.presenter;

import android.support.annotation.Nullable;
import android.util.Log;

import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMElem;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageDraft;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.presentation.event.MessageEvent;
import com.tencent.qcloud.presentation.event.RefreshEvent;
import com.tencent.qcloud.presentation.viewfeatures.ChatView;

import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 聊天界面逻辑
 */
public class ChatPresenter implements Observer {

    private ChatView view;
    private TIMConversation conversation;
    private boolean isGetingMessage = false;
    private final int LAST_MESSAGE_NUM = 20;
    private final static String TAG = "ChatPresenter";

    public ChatPresenter(ChatView view,String identify,TIMConversationType type){
        this.view = view;
        conversation = TIMManager.getInstance().getConversation(type,identify);
    }


    /**
     * 加载页面逻辑
     */
    public void start() {
        //注册消息监听
        MessageEvent.getInstance().addObserver(this);
        RefreshEvent.getInstance().addObserver(this);
        getMessage(null);
        if (conversation.hasDraft()){
            view.showDraft(conversation.getDraft());
        }
    }


    /**
     * 中止页面逻辑
     */
    public void stop() {
        //注销消息监听
        MessageEvent.getInstance().deleteObserver(this);
        RefreshEvent.getInstance().deleteObserver(this);
    }

    /**
     * 获取聊天TIM会话
     */
    public TIMConversation getConversation(){
        return conversation;
    }

    /**
     * 发送消息
     *
     * @param message 发送的消息
     */
    public void sendMessage(final TIMMessage message) {
        conversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表
                view.onSendMessageFail(code, desc, message);
            }

            @Override
            public void onSuccess(TIMMessage msg) {
                //发送消息成功,消息状态已在sdk中修改，此时只需更新界面
                MessageEvent.getInstance().onNewMessage(null);

            }
        });
        //message对象为发送中状态
        MessageEvent.getInstance().onNewMessage(message);
    }


    /**
     * 发送在线消息
     *
     * @param message 发送的消息
     */
    public void sendOnlineMessage(final TIMMessage message){
        conversation.sendOnlineMessage(message, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
                view.onSendMessageFail(i, s, message);
            }

            @Override
            public void onSuccess(TIMMessage message) {

            }
        });
    }



    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable the {@link Observable} object.
     * @param data       the data passed to {@link Observable#notifyObservers(Object)}.
     */
    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof MessageEvent){
            TIMMessage msg = (TIMMessage) data;
            if (msg==null||msg.getConversation().getPeer().equals(conversation.getPeer())&&msg.getConversation().getType()==conversation.getType()){
                view.showMessage(msg);
                //当前聊天界面已读上报，用于多终端登录时未读消息数同步
                readMessages();
            }
        }else if (observable instanceof RefreshEvent){
            view.clearAllMessage();
            getMessage(null);
        }
    }


    /**
     * 获取消息
     *
     * @param message 最后一条消息
     */
    public void getMessage(@Nullable TIMMessage message){
        if (!isGetingMessage){
            isGetingMessage = true;
            conversation.getMessage(LAST_MESSAGE_NUM, message, new TIMValueCallBack<List<TIMMessage>>() {
                @Override
                public void onError(int i, String s) {
                    isGetingMessage = false;
                    Log.e(TAG,"get message error"+s);
                }

                @Override
                public void onSuccess(List<TIMMessage> timMessages) {
                    isGetingMessage = false;
                    view.showMessage(timMessages);
                }
            });
        }

    }

    /**
     * 设置会话为已读
     *
     */
    public void readMessages(){
        conversation.setReadMessage();
    }


    /**
     * 保存草稿
     *
     * @param message 消息数据
     */
    public void saveDraft(TIMMessage message){
        conversation.setDraft(null);
        if (message != null && message.getElementCount() > 0){
            TIMMessageDraft draft = new TIMMessageDraft();
            for (int i = 0; i < message.getElementCount(); ++i){
                draft.addElem(message.getElement(i));
            }
            conversation.setDraft(draft);
        }

    }





}
