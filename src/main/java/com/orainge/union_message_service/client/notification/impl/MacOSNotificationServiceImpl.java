package com.orainge.union_message_service.client.notification.impl;

import com.orainge.union_message_service.client.ui.UIManager;
import com.orainge.union_message_service.client.ui.bean.NotificationMessage;
import com.orainge.union_message_service.client.config.opeartion_system.macos.TerminalNotifierConfig;
import com.orainge.union_message_service.client.notification.NotificationService;
import com.orainge.union_message_service.client.util.os.OSCommandProcess;
import com.orainge.union_message_service.client.util.macos.MacOSAlertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * MacOS 通知<br>
 * https://github.com/caronc/apprise<br>
 * https://github.com/julienXX/terminal-notifier
 */
@Slf4j
public class MacOSNotificationServiceImpl implements NotificationService {
    /**
     * terminal-notifier 执行路径
     */
    private final String terminalNotifierExecPath;
    private final MacOSAlertUtil macOSAlertUtil;

    public MacOSNotificationServiceImpl(TerminalNotifierConfig terminalNotifierConfig) {
        terminalNotifierExecPath = terminalNotifierConfig.getExecPath();
        macOSAlertUtil = new MacOSAlertUtil();
    }

    /**
     * 发送通知
     */
    public void notify(NotificationMessage systemMsg) {
        List<String> cmdList = new LinkedList<>();
        cmdList.add(terminalNotifierExecPath);

        String title = systemMsg.getTitle();
        if (StringUtils.isEmpty(title)) {
            title = "通知";
        }
        cmdList.add("-title");
        cmdList.add(title);

        String subtitle = systemMsg.getSubtitle();
        if (!StringUtils.isEmpty(subtitle)) {
            cmdList.add("-subtitle");
            cmdList.add(subtitle);
        }

        String message = systemMsg.getMessage();
        if (StringUtils.isEmpty(message)) {
            message = "";
        }
        cmdList.add("-message");
        cmdList.add(message);

        String appIcon = systemMsg.getAppIcon();
        if (!StringUtils.isEmpty(appIcon)) {
            cmdList.add("-appIcon");
            cmdList.add(appIcon);
            cmdList.add("-contentImage");
            cmdList.add(appIcon);
        }

        String url = systemMsg.getUrl();
        if (!StringUtils.isEmpty(url)) {
            cmdList.add("-open");
            cmdList.add(url);
        }

        String activate = systemMsg.getActivate();
        if (!StringUtils.isEmpty(activate)) {
            cmdList.add("-activate");
            cmdList.add(activate);
        }

        String executeCommand = systemMsg.getExecuteCommand();
        if (!StringUtils.isEmpty(executeCommand)) {
            cmdList.add("-execute");
            cmdList.add(executeCommand);
        } else {
            // 默认执行弹窗命令
            cmdList.add("-execute");
            cmdList.add(macOSAlertUtil.buildCommandDialog(systemMsg));
        }

        String[] cmdArray = cmdList.toArray(new String[0]);

        log.debug("[MacOS 通知服务] - 执行命令 [" + String.join(", ", cmdList) + "]");

        // 执行 command
        OSCommandProcess.build().setCommand(cmdArray).start();
    }

    /**
     * 设置 UiManager bean,解决循环依赖问题
     */
    public void setUiManager(UIManager uiManager) {
    }
}
