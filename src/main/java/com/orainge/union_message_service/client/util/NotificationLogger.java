package com.orainge.union_message_service.client.util;

import com.orainge.union_message_service.client.notification.NotificationUtils;
import com.orainge.union_message_service.client.ui.bean.NotificationMessage;
import com.orainge.union_message_service.client.ui.enums.UIMessageLevel;
import com.orainge.union_message_service.client.util.os.OSUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 输出到通知的 Logger
 */
@Component
public class NotificationLogger {
    @Resource
    private NotificationUtils notificationUtils;

    public void debug(String str) {
        notificationUtils.notify(new NotificationMessage()
                .setTitle("统一通知推送服务")
                .setSubtitle("调试")
                .setMessage(replaceChar(str))
                .setMessageLevel(UIMessageLevel.DEBUG)
        );
    }

    public void info(String str) {
        notificationUtils.notify(new NotificationMessage()
                .setTitle("统一通知推送服务")
                .setMessage(replaceChar(str))
                .setMessageLevel(UIMessageLevel.INFO)
        );
    }

    public void error(String str) {
        notificationUtils.notify(new NotificationMessage()
                .setTitle("统一通知推送服务")
                .setSubtitle("错误")
                .setMessage(replaceChar(str))
                .setMessageLevel(UIMessageLevel.ERROR)
        );
    }

    /**
     * 替换特殊字符串
     */
    private String replaceChar(String str) {
        String result = str;
        if (OSUtils.isMacOS() || OSUtils.isMacOSX()) {
            if (str.startsWith("[")) {
                result = "\\[" + result.substring(1);
            }
        }
        return result;
    }
}
