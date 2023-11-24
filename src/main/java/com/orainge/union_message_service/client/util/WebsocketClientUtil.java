package com.orainge.union_message_service.client.util;

import com.orainge.union_message_service.client.processor.OnMessageProcessor;
import com.orainge.union_message_service.client.websocket.WebsocketServerContainer;
import com.orainge.websocket_forward.util.JSONUtil;
import com.orainge.websocket_forward.util.uuid.UUIDUtil;
import com.orainge.websocket_forward.vo.ExchangeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Component
@Slf4j
public class WebsocketClientUtil {
    @Resource
    private OnMessageProcessor onMessageProcessor;

    @Resource
    private WebsocketServerContainer websocketServerContainer;

    /**
     * 收到消息时的操作
     *
     * @param exchangeMessage 收到的消息
     */
    public void onMessage(ExchangeMessage<?> exchangeMessage) {
        onMessageProcessor.process(exchangeMessage);
    }

    /**
     * 回复消息
     *
     * @param receivedMessage 收到的消息
     * @param replyMessage    回复消息
     */
    public void replyMessage(ExchangeMessage<?> receivedMessage, ExchangeMessage<?> replyMessage) {
        if (replyMessage != null) {
            replyMessage.setResponseId(receivedMessage.getRequestId());
            sendMessage(replyMessage);
        }
    }

    /**
     * 发送
     *
     * @param message 要发送的消息
     */
    public void sendMessage(ExchangeMessage<?> message) {
        if (message != null) {
            if (StringUtils.isEmpty(message.getRequestId())) {
                message.setRequestId(UUIDUtil.uuid());
            }
            websocketServerContainer.send(JSONUtil.toJSONString(message));
        }
    }
}
