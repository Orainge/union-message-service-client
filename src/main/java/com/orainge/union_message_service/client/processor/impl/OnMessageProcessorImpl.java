package com.orainge.union_message_service.client.processor.impl;

import com.alibaba.fastjson.JSON;
import com.orainge.union_message_service.client.bean.MessageBean;
import com.orainge.union_message_service.client.notification.NotificationUtils;
import com.orainge.union_message_service.client.ui.UIManager;
import com.orainge.union_message_service.client.ui.bean.NotificationMessage;
import com.orainge.union_message_service.client.processor.OnMessageProcessor;
import com.orainge.union_message_service.client.util.settings.NotificationHistoryUtils;
import com.orainge.websocket_forward.vo.ExchangeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 处理服务端发送过来的消息
 */
@Component
@Slf4j
public class OnMessageProcessorImpl implements OnMessageProcessor {
    @Resource
    private NotificationUtils notificationUtils;

    @Resource
    private NotificationHistoryUtils notificationHistoryUtils;

    @Resource
    private UIManager uiManager;

    @Override
    public void process(ExchangeMessage<?> exchangeMessage) {
        Object body = exchangeMessage.getBody();
        log.debug("[统一推送通知服务客户端] 收到服务端消息: " + JSON.toJSONString(body));

        // {
        //	"isArchive": "1",
        //	"level": "active",
        //	"icon": "http://xxx.com/xxx.png",
        //	"title": "标题",
        //	"body": "通知内容"
        //}

        if (body != null) {
            // 转换信息 Bean
            MessageBean message = JSON.parseObject(JSON.toJSONString(body), MessageBean.class);

            // 添加通知时间
            message.setNotifyDate(new Date());

            // 拼装通知
            NotificationMessage notificationMessage = new NotificationMessage();
            notificationMessage.setTitle(message.getTitle());
            notificationMessage.setMessage(message.getBody());
            notificationMessage.setAppIcon(message.getIcon());
            notificationMessage.setNotifyDate(new Date()); // 添加通知时间

            // 用新线程执行
            new Thread(() -> {
                // 保存到历史记录中
                notificationHistoryUtils.saveNotification(message);
                // 调用回调
                uiManager.onReceiveNotification(notificationMessage);
            }).start();

            // 发送通知
            notificationUtils.notify(notificationMessage);
        }
    }
}
