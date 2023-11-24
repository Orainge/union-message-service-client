package com.orainge.union_message_service.client.ui.frame;

import com.orainge.union_message_service.client.ui.FontManager;
import com.orainge.union_message_service.client.util.settings.ClientSettingUtils;
import com.orainge.union_message_service.client.util.settings.helper.FileSaveConstants;
import com.orainge.union_message_service.client.util.settings.bean.UIClientConfig;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * 【设置】页面
 */
public class SettingFrame extends UIFrame {
    private final ClientSettingUtils configUtils;

    private final JTextField clientIdField;
    private final JTextField clientSecurityKeyField;
    private final JTextField serverUrlField;
    private final JTextField reconnectWaitField;
    private final JCheckBox uiServerSSLCheckCheckBox;
    private final JCheckBox connectOnStartCheckBox;
    private final JCheckBox autoReconnectCheckBox;

    public SettingFrame(ClientSettingUtils configUtils) {
        this.configUtils = configUtils;
        setTitle("设置");
        setSize(500, 220);
        setLayout(new BorderLayout());

        // 主面板区域设置
        JPanel mPanel = new JPanel();
        mPanel.setLayout(new GridLayout(7, 2));

        // 创建 UI 组件
        JLabel connectOnStartLabel = new JLabel("启动时自动连接服务器");
        connectOnStartCheckBox = new JCheckBox();
        connectOnStartLabel.setFont(FontManager.getNormalFont(16));

        JLabel uiServerSSLCheckLabel = new JLabel("使用 SSL 连接 (wss 协议)");
        uiServerSSLCheckCheckBox = new JCheckBox();
        uiServerSSLCheckLabel.setFont(FontManager.getNormalFont(16));

        JLabel serverUrlLabel = new JLabel("服务器 URL");
        serverUrlField = new JTextField();
        serverUrlLabel.setFont(FontManager.getNormalFont(16));
        serverUrlField.setFont(FontManager.getNormalFont(16));

        JLabel clientIdLabel = new JLabel("客户端 ID");
        clientIdField = new JTextField();
        clientIdLabel.setFont(FontManager.getNormalFont(16));
        clientIdField.setFont(FontManager.getNormalFont(16));

        JLabel clientSecurityKeyLabel = new JLabel("客户端密钥");
        clientSecurityKeyField = new JTextField();
        clientSecurityKeyLabel.setFont(FontManager.getNormalFont(16));
        clientSecurityKeyField.setFont(FontManager.getNormalFont(16));

        JLabel autoReconnectLabel = new JLabel("与服务器断开连接后自动尝试连接");
        autoReconnectCheckBox = new JCheckBox();
        autoReconnectLabel.setFont(FontManager.getNormalFont(16));

        JLabel reconnectWaitLabel = new JLabel("重试连接等待时间 (秒)");
        reconnectWaitField = new JTextField();
        reconnectWaitLabel.setFont(FontManager.getNormalFont(16));
        reconnectWaitField.setFont(FontManager.getNormalFont(16));

        // 添加组件
        mPanel.add(connectOnStartLabel);
        mPanel.add(connectOnStartCheckBox);
        mPanel.add(uiServerSSLCheckLabel);
        mPanel.add(uiServerSSLCheckCheckBox);
        mPanel.add(serverUrlLabel);
        mPanel.add(serverUrlField);
        mPanel.add(clientIdLabel);
        mPanel.add(clientIdField);
        mPanel.add(clientSecurityKeyLabel);
        mPanel.add(clientSecurityKeyField);
        mPanel.add(autoReconnectLabel);
        mPanel.add(autoReconnectCheckBox);
        mPanel.add(reconnectWaitLabel);
        mPanel.add(reconnectWaitField);

        // 按钮面板
        JPanel buttonPanel = new JPanel();
        FlowLayout bpLayout = new FlowLayout();
        bpLayout.setAlignment(FlowLayout.CENTER);
        buttonPanel.setLayout(bpLayout);

        // 添加按钮
        JButton okButton = new JButton("确定");
        JButton cancelButton = new JButton("取消");
        okButton.setFont(FontManager.getNormalFont(16));
        cancelButton.setFont(FontManager.getNormalFont(16));
        okButton.setPreferredSize(new Dimension(80, 35));
        cancelButton.setPreferredSize(new Dimension(80, 35));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // 点击【确定】按钮事件
        okButton.addActionListener(e -> {
            // 创建新配置
            UIClientConfig clientConfig = new UIClientConfig();

            // 从 UI 获取配置
            clientConfig.setClientId(clientIdField.getText());
            clientConfig.setClientSecurityKey(clientSecurityKeyField.getText());
            clientConfig.setUiServerSSL(uiServerSSLCheckCheckBox.isSelected());
            clientConfig.setUiServerUrl(serverUrlField.getText());
            clientConfig.setReconnectWait(Integer.parseInt(reconnectWaitField.getText()));
            clientConfig.setConnectOnStart(connectOnStartCheckBox.isSelected());
            clientConfig.setAutoReconnectWhenDisconnect(autoReconnectCheckBox.isSelected());

            // 确定配置
            configUtils.confirmConfig(clientConfig);

            // 关闭对话框
            setVisible(false);
        });

        // 点击【取消】按钮事件
        cancelButton.addActionListener(e -> setVisible(false));

        // 配置文件路径
        JPanel configPathPanel = new JPanel();
        String appConfigFilePath = FileSaveConstants.APP_CONFIG_FILE_PATH;
        String appConfigFileDicPath = FileSaveConstants.APP_CONFIG_FILE_DIC_PATH;
        JLabel configPathLabel = new JLabel("配置文件: " + appConfigFilePath);
        configPathLabel.setFont(FontManager.getNormalFont(16));
        JButton openAppConfigFileDicButton = new JButton("浏览");
        openAppConfigFileDicButton.setPreferredSize(new Dimension(75, 35));
        openAppConfigFileDicButton.setFont(FontManager.getNormalFont(16));
        openAppConfigFileDicButton.addActionListener(e -> {
            // 调用系统文件浏览器打开文件夹
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(new java.io.File(appConfigFileDicPath));
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "文件不存在", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        configPathPanel.add(configPathLabel);
        configPathPanel.add(openAppConfigFileDicButton);

        // 中间面板
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
//        centerPanel.add(titleLabel);
        centerPanel.add(mPanel);
        centerPanel.add(configPathPanel);
        centerPanel.add(buttonPanel);

        // 四周的分隔
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        JPanel p4 = new JPanel();
        p1.setPreferredSize(new Dimension(0, 40));
        p2.setPreferredSize(new Dimension(0, 40));
        p3.setPreferredSize(new Dimension(40, 0));
        p4.setPreferredSize(new Dimension(40, 0));
        add(p1, BorderLayout.NORTH);
        add(p2, BorderLayout.SOUTH);
        add(p3, BorderLayout.EAST);
        add(p4, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);

        // 页面紧凑
        pack();
    }

    public void setVisible(boolean b) {
        if (b) {
            // 如果是显示窗口，需要在显示前重新拉取配置
            UIClientConfig displayUIClientConfig = configUtils.getUiClientConfig();
            connectOnStartCheckBox.setSelected(displayUIClientConfig.isConnectOnStart());
            uiServerSSLCheckCheckBox.setSelected(displayUIClientConfig.isUiServerSSL());
            serverUrlField.setText(displayUIClientConfig.getUiServerUrl());
            clientIdField.setText(displayUIClientConfig.getClientId());
            clientSecurityKeyField.setText(displayUIClientConfig.getClientSecurityKey());
            autoReconnectCheckBox.setSelected(displayUIClientConfig.isAutoReconnectWhenDisconnect());
            reconnectWaitField.setText(displayUIClientConfig.getReconnectWait().toString());
        }
        super.setVisible(b);
    }
}
