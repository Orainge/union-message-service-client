package com.orainge.union_message_service.client.ui.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orainge.union_message_service.client.ui.enums.UIMessageLevel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 系统通知信息 Bean
 */
@Data
@Accessors(chain = true)
public class NotificationMessage {
    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知副标题
     */
    private String subtitle;

    /**
     * 通知内容
     */
    private String message;

    /**
     * 通知等级
     */
    private UIMessageLevel messageLevel;

    /**
     * [Macos] 通知 ICON
     */
    private String appIcon;

    /**
     * [Macos] 点击通知后打开的 URL
     */
    private String url;

    /**
     * [Macos] 点击 "显示" 按钮打开的包名
     */
    private String activate;

    /**
     * [Macos] 点击通知后执行的 CMD 命令
     */
    private String executeCommand;

    /**
     * 扩展字段: 通知时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date notifyDate;

    /**
     * 格式化后的notifyDate
     */
    @JsonIgnore
    private String notifyDateString;

    /**
     * 获取格式化后的notifyDate
     */
    @JsonIgnore
    public String getNotifyDateString() {
        if (notifyDateString == null) {
            notifyDateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(notifyDate);
        }
        return notifyDateString;
    }

    /**
     * 获取托盘图标通知类型
     */
    public TrayIcon.MessageType getMessageType() {
        if (messageLevel == null) {
            // 如果未设置，则返回默认值 INFO
            return TrayIcon.MessageType.INFO;
        } else if (messageLevel.equals(UIMessageLevel.ERROR)) {
            return TrayIcon.MessageType.ERROR;
        } else if (messageLevel.equals(UIMessageLevel.WARNING)) {
            return TrayIcon.MessageType.WARNING;
        }
        if (messageLevel.equals(UIMessageLevel.INFO)) {
            return TrayIcon.MessageType.INFO;
        }
        if (messageLevel.equals(UIMessageLevel.DEBUG)) {
            return TrayIcon.MessageType.NONE;
        } else {
            return null;
        }
    }
}
