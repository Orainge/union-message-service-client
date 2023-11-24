package com.orainge.union_message_service.client.util.settings.bean;

import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * UI 界面上的配置
 */
@Data
public class UIClientConfig {
    /**
     * 客户端 ID
     */
    private String clientId;

    /**
     * 客户端密钥
     */
    private String clientSecurityKey;

    /**
     * 页面上显示的服务端地址
     */
    private String uiServerUrl;

    /**
     * 页面上显示的【是否启用SSL】
     */
    private boolean uiServerSSL;

    /**
     * 重连重试时间（秒）
     */
    private Integer reconnectWait;

    /**
     * 是否在运行程序时自动连接服务器
     */
    private boolean connectOnStart;

    /**
     * 是否在断线时自动重新连接
     */
    private boolean autoReconnectWhenDisconnect;

    public String getUiServerUrl() {
        return uiServerUrl == null ? "" : uiServerUrl;
    }

    /**
     * 获取完整服务器接口 URL
     */
    public String getServerUrl() {
        if (StringUtils.isEmpty(uiServerUrl)) {
            return null;
        }
        return (uiServerSSL ? "wss://" : "ws://") + uiServerUrl;
    }

    /**
     * 设置服务器 URL 地址
     */
    public void setServerUrl(String serverUrl) {
        if (serverUrl == null || "".equals(serverUrl)) {
            return;
        }

        if (serverUrl.startsWith("ws://")) {
            uiServerSSL = false;
            this.uiServerUrl = serverUrl.substring(5);
        } else if (serverUrl.startsWith("wss://")) {
            uiServerSSL = true;
            this.uiServerUrl = serverUrl.substring(6);
        } else {
            throw new RuntimeException("服务器地址协议不正确");
        }
    }
}
