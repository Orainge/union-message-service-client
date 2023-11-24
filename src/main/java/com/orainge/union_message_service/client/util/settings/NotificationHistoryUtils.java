package com.orainge.union_message_service.client.util.settings;

import com.orainge.union_message_service.client.bean.MessageBean;
import com.orainge.union_message_service.client.util.settings.helper.NotificationSaveHelper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 历史通知工具类
 */
@Component
public class NotificationHistoryUtils {
    /**
     * 配置文件读取 Helper
     */
    private final NotificationSaveHelper notificationSaveHelper;

    private List<MessageBean> messageList = null;

    public NotificationHistoryUtils(NotificationSaveHelper notificationSaveHelper) {
        this.notificationSaveHelper = notificationSaveHelper;
    }

    /**
     * 获取历史记录
     */
    public List<MessageBean> getAllNotification() {
        return getAllNotification(false);
    }

    /**
     * 获取历史记录
     *
     * @param reloadFromFile 是否从外部重载数据 true: 是; false: 否
     */
    public List<MessageBean> getAllNotification(boolean reloadFromFile) {
        if (messageList == null || reloadFromFile) {
            messageList = notificationSaveHelper.getAllNotification();
        }
        return messageList;
    }

    /**
     * 保存配置
     */
    public void saveNotification(MessageBean message) {
        if (messageList == null) {
            messageList = notificationSaveHelper.getAllNotification();
        }
        messageList.add(0, message);
        notificationSaveHelper.saveNotification(message);
    }

    /**
     * 删除单个
     *
     * @param index 要删除的列表索引
     */
    public void deleteNotification(int index) {
        // 调用此方法时，列表一定不为null，所以不需要做非 null 判断
        messageList.remove(index); // 从列表中某一项
        notificationSaveHelper.saveAllNotification(messageList); // 写入所有通知
    }

    /**
     * 删除所有通知
     */
    public void deleteAllNotification() {
        notificationSaveHelper.deleteAllNotification();
        messageList.clear();
    }
}
