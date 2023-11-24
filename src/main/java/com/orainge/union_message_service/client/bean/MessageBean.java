package com.orainge.union_message_service.client.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class MessageBean {
    private String isArchive;
    private String level;
    private String icon;
    private String title;
    private String body;

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
}
