package com.orainge.union_message_service.client.websocket;

import com.orainge.union_message_service.client.enums.WebSocketCloseType;
import com.orainge.union_message_service.client.util.settings.ClientSettingUtils;
import com.orainge.union_message_service.client.ui.UIDialogManager;
import com.orainge.union_message_service.client.util.WebsocketClientUtil;
import com.orainge.websocket_forward.consts.AuthHeader;
import com.orainge.websocket_forward.consts.MessageCode;
import com.orainge.websocket_forward.util.JSONUtil;
import com.orainge.websocket_forward.vo.ExchangeMessage;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Websocket 服务器对象容器
 */
@Component
@EnableScheduling
@Slf4j
public class WebsocketServerContainer {
    @Resource
    private ClientSettingUtils clientSettingUtils;

    @Resource
    private WebsocketClientUtil websocketClientUtil;

    @Resource
    private WebSocketServerHook webSocketServerHook;

    @Resource
    private UIDialogManager uiDialogManager;

    /**
     * 当前正在使用的 Websocket 客户端
     */
    private static WebSocketClient webSocketClient = null;

    /**
     * 是否在连接中
     */
    private boolean isConnecting = false;

    /**
     * 是否是由用户原因断开连接<br>
     * 用户主动断开连接/客户端ID填写错误
     */
    private boolean isDisconnectByUserReason = false;

    /**
     * 重试连接的线程
     */
    private Thread autoReconnectThread = null;

    /**
     * 是否显示连接断开的提示
     */
    private boolean showConnectErrorNotification = true;

    /**
     * 连接服务
     */
    public void connect() {
        showConnectErrorNotification = true; // 复位标识
        connect(false);
    }

    /**
     * 连接服务
     *
     * @param isReconnect 是否是重复连接
     */
    public void connect(boolean isReconnect) {
        if (isReconnect) {
            // 如果是重连
            showConnectErrorNotification = false; // 不显示连接错误的提示
        } else {
            // 如果不是重连
            // 如果存在重试线程,停止该线程
            if (autoReconnectThread != null && autoReconnectThread.isAlive()) {
                autoReconnectThread.interrupt();
                autoReconnectThread = null;
            }
        }

        if (isConnecting) {
            // 如果正在执行连接操作,返回
            return;
        }

        isConnecting = true;
        new Thread(() -> {
            final boolean[] connectSuccessfully = {false}; // 是否已经连接成功

            // 获取客户端信息
            String id = clientSettingUtils.getUiClientConfig().getClientId();
            String key = clientSettingUtils.getUiClientConfig().getClientSecurityKey();
            String url = clientSettingUtils.getUiClientConfig().getServerUrl();

            if (StringUtils.isEmpty(url)) {
                // 服务器URL不能为空
                uiDialogManager.showErrorDialog("请前往【设置】页面配置【服务器URL】");
                isConnecting = false;
                return;
            } else if (StringUtils.isEmpty(id)) {
                // 客户端 ID 不能为空
                uiDialogManager.showErrorDialog("请前往【设置】页面配置【客户端ID】");
                isConnecting = false;
                return;
            } else if (StringUtils.isEmpty(key)) {
                // 客户端密钥不能为空
                uiDialogManager.showErrorDialog("请前往【设置】页面配置【客户端密钥】");
                isConnecting = false;
                return;
            }

            // 添加请求头
            Map<String, String> headers = new HashMap<>();
            headers.put(AuthHeader.ID_HEADER_NAME, id); // 客户端 ID
            headers.put(AuthHeader.KEY_HEADER_NAME, key); // 客户端 KEY

            try {
                webSocketClient = new WebSocketClient(
                        new URI(url),
                        new Draft_6455(),
                        headers,
                        0
                ) {
                    @Override
                    public void onOpen(ServerHandshake serverHandshake) {
//                        // 发送心跳包
//                        String keepAliveTag = MessageCode.KEEP_ALIVE_TAG;
//                        WebsocketServerContainer.webSocketClient.send(keepAliveTag);

                        // 重置标签
                        connectSuccessfully[0] = true;
                        showConnectErrorNotification = true;
                        isConnecting = false;

                        // 执行钩子函数
                        webSocketServerHook.afterOpen(id);
                    }

                    @Override
                    public void onMessage(String message) {
                        // 检查是否为服务器发送的心跳检测包
                        if (MessageCode.KEEP_ALIVE_TAG.equals(message)) {
                            try {
                                WebsocketServerContainer.webSocketClient.send(MessageCode.KEEP_ALIVE_TAG_RESPONSE);
                                log.debug("[统一推送通知服务客户端] 响应服务器心跳 - 成功");
                            } catch (Exception e) {
                                log.debug("[统一推送通知服务客户端] 响应服务器心跳 - 失败", e);
                            }
                        } else if (MessageCode.KEEP_ALIVE_TAG_RESPONSE.equals(message)) {
                            // 检查是否为服务器发送的心跳检测响应包
                            // 收到心跳包，不做任何处理
                            log.debug("[统一推送通知服务客户端] 收到心跳响应");
                        } else if (MessageCode.DUPLICATE_CLIENT_TAG.equals(message)) {
                            // 检查是否为客户端重复登录检测响应
                            // 关闭连接
                            webSocketClient.closeConnection(
                                    WebSocketCloseType.DUPLICATE_CLIENT.getCode(),
                                    WebSocketCloseType.DUPLICATE_CLIENT.getMessage()
                            );
                        } else if (!StringUtils.isEmpty(message)) {
                            // 服务端发送来的消息
                            websocketClientUtil.onMessage(JSONUtil.parseObject(message, ExchangeMessage.class));
                        }
                    }

                    @Override
                    public void onClose(int code, String reason, boolean remote) {
                        onDisconnect();
                        if (code == WebSocketCloseType.DUPLICATE_CLIENT.getCode() ||
                                code == WebSocketCloseType.DISCONNECT.getCode() ||
                                code == WebSocketCloseType.AUTHORIZATION_ERROR.getCode()
                        ) {
                            // 异常关闭情况：重复客户端 / 用户选择断开连接 / 客户端 ID 或密钥不正确
                            isDisconnectByUserReason = true; // 更改标识符
                            webSocketServerHook.afterClose(code, reason, remote,
                                    connectSuccessfully[0], showConnectErrorNotification); // 执行钩子函数
                        }

                        if (remote) {
                            // 如果是远程断开连接(包括本机中途断网)
                            webSocketServerHook.afterClose(code, reason, remote,
                                    connectSuccessfully[0], showConnectErrorNotification); // 执行钩子函数
                            autoReconnect(); // 自动重新连接
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        onDisconnect(); // 不需要执行,因为会执行 onClose 回调
                        if (isDisconnectByUserReason) {
                            // 如果是由用户断开连接的，复位标识，不执行钩子函数
                            isDisconnectByUserReason = false;
                        } else {
                            // 如果不是由用户断开连接的
                            webSocketServerHook.onError(e, connectSuccessfully[0],
                                    showConnectErrorNotification); // 执行钩子函数
                            autoReconnect(); // 自动重新连接
                        }
                    }
                };

                // 建立连接
                webSocketClient.connect();
                webSocketServerHook.afterConnect(); // 执行钩子函数
            } catch (Exception e) {
                onDisconnect();
                webSocketServerHook.onError(e, connectSuccessfully[0],
                        showConnectErrorNotification); // 执行钩子函数
                autoReconnect(); // 自动重新连接
            }
        }).start();
    }

    /**
     * 关闭 Websocket 连接
     */
    public void disconnect() {
        if (webSocketClient != null) {
            webSocketClient.closeConnection(
                    WebSocketCloseType.DISCONNECT.getCode(),
                    WebSocketCloseType.DISCONNECT.getMessage()
            );
        }

    }

    /**
     * 发送文本
     */
    public void send(String text) {
        if (webSocketClient != null) {
            webSocketClient.send(text);
        }
    }

    /**
     * 发送数据
     */
    public void send(byte[] data) {
        if (webSocketClient != null) {
            webSocketClient.send(data);
        }
    }

    /**
     * 掉线后自动重新连接
     */
    private void autoReconnect() {
        // 如果选择断线后自动重连,则自动重新连接
        if (clientSettingUtils.getUiClientConfig().isAutoReconnectWhenDisconnect()) {
            autoReconnectThread = new Thread(() -> {
                try {
                    Integer reconnectWait = clientSettingUtils.getUiClientConfig().getReconnectWait();
                    Thread.sleep(reconnectWait * 1000L);
                    log.warn("[统一推送通知服务客户端] 正在重新连接服务器");
                    connect(true);
                } catch (Exception e) {
                    log.warn("[统一推送通知服务客户端] 重试线程被打断");
//                    e.printStackTrace();
                }
            });
            autoReconnectThread.setName("retry-connect-thread"); // 设置名称
            autoReconnectThread.start(); // 启动线程
        }
    }

    /**
     * 断线时的操作
     */
    private void onDisconnect() {
        WebsocketServerContainer.webSocketClient = null;
        isConnecting = false; // 重置标签
    }

    /**
     * 每分钟第 0 秒和第 30 秒发送心跳包给服务端
     */
    @Scheduled(cron = "0,30 * * * * ?")
    public void sendKeepAlive() {
        try {
            if (WebsocketServerContainer.webSocketClient != null &&
                    WebsocketServerContainer.webSocketClient.isOpen()) {
                webSocketClient.send(MessageCode.KEEP_ALIVE_TAG);
                log.debug("[统一推送通知服务客户端] 发送心跳包 - 成功");
            }
        } catch (Exception e) {
            log.error("[统一推送通知服务客户端] 发送心跳包 - 失败");
            // 断开连接
            if (WebsocketServerContainer.webSocketClient != null &&
                    WebsocketServerContainer.webSocketClient.isOpen()) {
                webSocketClient.closeConnection(
                        WebSocketCloseType.ERROR.getCode(),
                        WebSocketCloseType.ERROR.getMessage()
                );
            }
        }
    }
}
