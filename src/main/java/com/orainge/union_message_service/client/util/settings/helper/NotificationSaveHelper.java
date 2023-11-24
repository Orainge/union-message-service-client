package com.orainge.union_message_service.client.util.settings.helper;

import com.orainge.union_message_service.client.bean.MessageBean;

import java.util.List;

/**
 * 通知历史记录保存 Helper
 */
public interface NotificationSaveHelper {
    /**
     * 获取所有通知
     */
    List<MessageBean> getAllNotification();

    /**
     * 保存通知信息
     */
    void saveNotification(MessageBean message);

    /**
     * 保存所有通知信息
     */
    void saveAllNotification(List<MessageBean> messageList);

    /**
     * 删除所有通知
     */
    void deleteAllNotification();
}
