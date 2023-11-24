package com.orainge.union_message_service.client.util.settings;

import com.orainge.union_message_service.client.config.ClientVersionInfo;
import com.orainge.union_message_service.client.config.WebsocketClientConfig;
import com.orainge.union_message_service.client.config.WebsocketServerConfig;
import com.orainge.union_message_service.client.util.settings.bean.UIClientConfig;
import com.orainge.union_message_service.client.util.settings.helper.SettingSaveHelper;
import com.orainge.union_message_service.client.ui.enums.UIConnectStatus;
import com.orainge.union_message_service.client.websocket.WebsocketServerContainer;
import com.orainge.websocket_forward.ApplicationContextUtils;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 客户端配置工具类
 */
@Component
@Data
public class ClientSettingUtils {
    /**
     * 配置文件读取 Helper
     */
    private final SettingSaveHelper settingSaveHelper;

    /**
     * 客户端版本信息
     */
    private ClientVersionInfo clientVersionInfo;

    /**
     * UI 界面上的配置
     */
    private UIClientConfig uiClientConfig;

    /**
     * 连接状态
     */
    private UIConnectStatus uiConnectStatus = UIConnectStatus.DISCONNECTED;

    public ClientSettingUtils(
            SettingSaveHelper settingSaveHelper,
            ClientVersionInfo clientVersionInfo,
            WebsocketClientConfig websocketClientConfig,
            WebsocketServerConfig websocketServerConfig
    ) {
        this.settingSaveHelper = settingSaveHelper;
        this.clientVersionInfo = clientVersionInfo;
        this.uiClientConfig = new UIClientConfig();

        // 检查是否存在已保存的配置
        Map<String, Object> allConfigMap = settingSaveHelper.readAllConfig();
        if (allConfigMap == null) {
            // 没有保存过的配置，写入
            uiClientConfig.setClientId(websocketClientConfig.getId());
            uiClientConfig.setClientSecurityKey(websocketClientConfig.getSecurityKey());
            uiClientConfig.setServerUrl(websocketServerConfig.getUrl());
            uiClientConfig.setReconnectWait(websocketServerConfig.getReconnectWait());
            uiClientConfig.setConnectOnStart(true);
            uiClientConfig.setAutoReconnectWhenDisconnect(true);
            saveConfig();
        } else {
            // 用自定义的配置保存
            uiClientConfig.setClientId((String) allConfigMap.get("clientId"));
            uiClientConfig.setClientSecurityKey((String) allConfigMap.get("clientSecurityKey"));
            uiClientConfig.setServerUrl((String) allConfigMap.get("serverUrl"));
            uiClientConfig.setReconnectWait(Integer.valueOf(allConfigMap.get("reconnectWait").toString()));
            uiClientConfig.setConnectOnStart(Boolean.parseBoolean(allConfigMap.get("connectOnStart").toString()));
            uiClientConfig.setAutoReconnectWhenDisconnect(Boolean.parseBoolean(allConfigMap.get("autoReconnectWhenDisconnect").toString()));
        }
    }

    /**
     * 保存配置
     */
    public void saveConfig() {
        Map<String, Object> newConfigMap = new HashMap<>();
        newConfigMap.put("clientId", uiClientConfig.getClientId());
        newConfigMap.put("clientSecurityKey", uiClientConfig.getClientSecurityKey());
        newConfigMap.put("serverUrl", uiClientConfig.getServerUrl());
        newConfigMap.put("reconnectWait", uiClientConfig.getReconnectWait());
        newConfigMap.put("connectOnStart", uiClientConfig.isConnectOnStart());
        newConfigMap.put("autoReconnectWhenDisconnect", uiClientConfig.isAutoReconnectWhenDisconnect());
        settingSaveHelper.writeAllConfig(newConfigMap);
    }

    /**
     * 确认配置<br>
     * UI 界面上点击[确定]
     */
    public void confirmConfig(UIClientConfig uiClientConfig) {
        // 判断是否修改过服务器地址/协议
        String originalUrl = this.uiClientConfig.getUiServerUrl();
        String newUrl = uiClientConfig.getUiServerUrl();
        this.uiClientConfig = uiClientConfig;
        saveConfig();

        // 如果修改了服务器地址，且现在的状态为：已连接/连接中
        if (!Objects.equals(originalUrl, newUrl) &&
                (uiConnectStatus.equals(UIConnectStatus.CONNECTED) ||
                        uiConnectStatus.equals(UIConnectStatus.CONNECTING))) {
            // 需要重连
            WebsocketServerContainer container = ApplicationContextUtils.applicationContext
                    .getBean(WebsocketServerContainer.class);
            container.disconnect();
            container.connect();
        }
    }
}
