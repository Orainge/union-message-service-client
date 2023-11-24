package com.orainge.union_message_service.client.config.opeartion_system.macos;

import lombok.Data;

import javax.annotation.PostConstruct;

/**
 * MacOS Alert 配置
 */
@Data
public class MacOSAlertConfig {
    /**
     * ICON 文件夹路径
     */
    private String iconFolderPath;

    /**
     * macos-alert 执行文件路径
     */
    private String execPath;
}
