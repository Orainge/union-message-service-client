package com.orainge.union_message_service.client.processor;

import com.orainge.websocket_forward.vo.ExchangeMessage;

/**
 * 处理服务端发送过来的消息
 */
public interface OnMessageProcessor {
    void process(ExchangeMessage<?> exchangeMessage);
}
