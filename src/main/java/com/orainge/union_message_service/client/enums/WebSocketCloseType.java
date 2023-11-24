package com.orainge.union_message_service.client.enums;

public enum WebSocketCloseType {
    DISCONNECT(9000, "已断开连接", "用户断开连接"),
    DUPLICATE_CLIENT(9001, "已有相同的 ID 的客户端连接到服务器", "已有相同的 ID 的客户端连接到服务器"),
    ERROR(9002, "断开连接", "异常情况断开连接"),
    AUTHORIZATION_ERROR(1002, "连接失败，客户端 ID/密钥/连接协议/服务器地址不正确", "客户端 ID/密钥/连接协议/服务器地址不正确");

    private final int code;
    private final String message;
    private final String description;

    WebSocketCloseType(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
