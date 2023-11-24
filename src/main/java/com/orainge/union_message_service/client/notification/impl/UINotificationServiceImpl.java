package com.orainge.union_message_service.client.notification.impl;

import com.orainge.union_message_service.client.ui.bean.NotificationMessage;
import com.orainge.union_message_service.client.notification.NotificationService;
import com.orainge.union_message_service.client.ui.UIManager;
import lombok.extern.slf4j.Slf4j;

/**
 * 使用 UI 通知服务
 */
@Slf4j
public class UINotificationServiceImpl implements NotificationService {
    /**
     * UI 管理器
     */
    private UIManager uiManager;

    /**
     * 通过 UI 发送通知
     */
    public void notify(NotificationMessage systemMsg) {
        // 发送 UI 通知
        uiManager.notifyToast(systemMsg);
    }

    /**
     * 设置 UiManager bean,解决循环依赖问题
     */
    public void setUiManager(UIManager uiManager) {
        this.uiManager = uiManager;
    }
}
