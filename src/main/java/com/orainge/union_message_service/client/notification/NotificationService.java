package com.orainge.union_message_service.client.notification;

import com.orainge.union_message_service.client.ui.UIManager;
import com.orainge.union_message_service.client.ui.bean.NotificationMessage;

/**
 * 通知服务
 */
public interface NotificationService {
    /**
     * 发送通知
     */
    void notify(NotificationMessage notificationMessage);

    /**
     * 设置 UiManager bean,解决循环依赖问题
     */
    void setUiManager(UIManager uiManager);
}
