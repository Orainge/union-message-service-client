package com.orainge.union_message_service.client.config.opeartion_system.macos;

import lombok.Data;

/**
 * MacOS 系统配置
 */
@Data
public class MacOSConfig {
    private TerminalNotifierConfig terminalNotifier;
    private MacOSAlertConfig macosAlert;
}
