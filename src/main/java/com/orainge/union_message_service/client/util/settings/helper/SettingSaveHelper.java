package com.orainge.union_message_service.client.util.settings.helper;

import java.util.Map;

/**
 * 配置文件读取 Helper
 */
public interface SettingSaveHelper {
    /**
     * 以 Map 的方式读取所有配置
     */
    Map<String, Object> readAllConfig();

    /**
     * 以 Map 方式写入所有配置
     */
    void writeAllConfig(Map<String, Object> configMap);
}
