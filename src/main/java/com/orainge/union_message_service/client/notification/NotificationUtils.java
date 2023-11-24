package com.orainge.union_message_service.client.notification;

import com.orainge.union_message_service.client.config.opeartion_system.OperationSystemConfig;
import com.orainge.union_message_service.client.notification.impl.MacOSNotificationServiceImpl;
import com.orainge.union_message_service.client.notification.impl.UINotificationServiceImpl;
import com.orainge.union_message_service.client.ui.UIManager;
import com.orainge.union_message_service.client.ui.bean.NotificationMessage;
import com.orainge.union_message_service.client.util.os.OSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationUtils {
    private final NotificationService notificationService;

    public NotificationUtils(OperationSystemConfig operationSystemConfig) {
        if (OSUtils.isMacOS() || OSUtils.isMacOSX()) {
            // MacOS 系统
            this.notificationService = new MacOSNotificationServiceImpl(
                    operationSystemConfig.getMacos().getTerminalNotifier()
            );
        } else if (OSUtils.isWindows()) {
            // Windows 系统
            this.notificationService = new UINotificationServiceImpl();
        } else if (OSUtils.isLinux()) {
            // Linux 系统
            this.notificationService = new UINotificationServiceImpl();
        } else {
            // 其他情况
            this.notificationService = null;
        }
    }

    public void notify(NotificationMessage notificationMessage) {
        notificationService.notify(notificationMessage);
    }

    /**
     * 设置 UiManager bean,解决循环依赖问题
     */
    public void addUiManagerToService(UIManager uiManager) {
        notificationService.setUiManager(uiManager);
    }
}
