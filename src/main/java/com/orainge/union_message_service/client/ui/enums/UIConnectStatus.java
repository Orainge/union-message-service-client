package com.orainge.union_message_service.client.ui.enums;

/**
 * UI 上连接的状态
 */
public enum UIConnectStatus {
    CONNECTING("0", "连接中", "断开连接", "正在连接..."),
    CONNECTED("1", "已连接", "断开连接", "已连接"),
    DISCONNECTED("2", "断开连接", "连接服务器", "已断开");

    /**
     * 状态 Code
     */
    private final String code;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 当前状态下【连接】按钮显示的文本
     */
    private final String connectButtonText;

    /**
     * 【显示状态】菜单的文本
     */
    private final String menuText;

    UIConnectStatus(String code, String description, String connectButtonText, String menuText) {
        this.code = code;
        this.description = description;
        this.connectButtonText = connectButtonText;
        this.menuText = menuText;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getConnectButtonText() {
        return connectButtonText;
    }

    public String getMenuText() {
        return menuText;
    }
}