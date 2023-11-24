package com.orainge.union_message_service.client.websocket;

import com.orainge.union_message_service.client.enums.WebSocketCloseType;
import com.orainge.union_message_service.client.ui.UIManager;
import com.orainge.union_message_service.client.ui.enums.UIConnectStatus;
import com.orainge.union_message_service.client.util.settings.ClientSettingUtils;
import com.orainge.union_message_service.client.util.NotificationLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Websocket 客户端钩子函数
 */
@Slf4j
@Component
public class WebSocketServerHook {
    @Resource
    private NotificationLogger nLog;

    @Resource
    private ClientSettingUtils clientSettingUtils;

    @Resource
    private UIManager uiManager;

    /**
     * 钩子函数：发起连接后
     */
    public void afterConnect() {
        // 变更状态
        clientSettingUtils.setUiConnectStatus(UIConnectStatus.CONNECTING);
        uiManager.updateUiConnectStatus();
    }

    /**
     * 钩子函数：连接打开时
     */
    public void afterOpen(String id) {
        log.info("[统一推送通知服务客户端] 连接成功 [ID: " + id + "]");
//        nLog.info("服务器连接成功");

        // 变更状态
        clientSettingUtils.setUiConnectStatus(UIConnectStatus.CONNECTED);
        uiManager.updateUiConnectStatus();
    }

    /**
     * 钩子函数：当客户端重复登录时
     */
    public void afterDuplicateClient() {
        log.error("[统一推送通知服务客户端] 连接错误: 已有相同的 ID 的客户端连接到服务器");
        nLog.error("已有相同的 ID 的客户端连接到服务器");

        // 变更状态
        clientSettingUtils.setUiConnectStatus(UIConnectStatus.DISCONNECTED);
        uiManager.updateUiConnectStatus();
    }

    public void afterClose(int code, String reason, boolean remote,
                           boolean connectSuccessfully, boolean showConnectErrorNotification) {
        if (code == WebSocketCloseType.DUPLICATE_CLIENT.getCode()) {
            // 客户端 ID 重复连接
            log.error("[统一推送通知服务客户端] " + WebSocketCloseType.DUPLICATE_CLIENT.getDescription());
            nLog.error(WebSocketCloseType.DUPLICATE_CLIENT.getMessage());
        } else if (code == WebSocketCloseType.DISCONNECT.getCode()) {
            // 用户主动断开连接
            log.info("[统一推送通知服务客户端] " + WebSocketCloseType.DISCONNECT.getDescription());
//            nLog.info(WebSocketCloseType.DISCONNECT.getMessage());
        } else if (code == WebSocketCloseType.AUTHORIZATION_ERROR.getCode()) {
            // 授权错误
            log.error("[统一推送通知服务客户端] " + WebSocketCloseType.AUTHORIZATION_ERROR.getDescription());
            nLog.error(WebSocketCloseType.AUTHORIZATION_ERROR.getMessage());
        } else {
            // 其他原因断开
            log.error(remote ? "[统一推送通知服务客户端] 连接关闭 - 服务器断开连接" : "[统一推送通知服务客户端] 连接关闭 - 客户端断开连接");
            if (showConnectErrorNotification) {
                nLog.error(connectSuccessfully ? "与服务器失去连接，请检查网络设置" : "连接失败，请检查网络设置");
            }
        }

        // 变更状态
        clientSettingUtils.setUiConnectStatus(UIConnectStatus.DISCONNECTED);
        uiManager.updateUiConnectStatus();
    }

    public void onError(Exception e, boolean connectSuccessfully, boolean showConnectErrorNotification) {
        log.error("[统一推送通知服务客户端] 连接错误", e);
        if (showConnectErrorNotification) {
            nLog.error(connectSuccessfully ? "与服务器失去连接，请检查网络设置" : "连接失败，请检查网络设置");
        }

        // 变更状态
        clientSettingUtils.setUiConnectStatus(UIConnectStatus.DISCONNECTED);
        uiManager.updateUiConnectStatus();
    }
}
