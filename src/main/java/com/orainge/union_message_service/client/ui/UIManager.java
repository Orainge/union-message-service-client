package com.orainge.union_message_service.client.ui;

import com.orainge.union_message_service.client.ui.bean.NotificationMessage;
import com.orainge.union_message_service.client.ui.enums.UIConnectStatus;
import com.orainge.union_message_service.client.ui.frame.AboutFrame;
import com.orainge.union_message_service.client.ui.frame.HistoryDetailsFrame;
import com.orainge.union_message_service.client.ui.frame.HistoryFrame;
import com.orainge.union_message_service.client.util.settings.ClientSettingUtils;
import com.orainge.union_message_service.client.ui.frame.SettingFrame;
import com.orainge.union_message_service.client.util.settings.NotificationHistoryUtils;
import com.orainge.union_message_service.client.websocket.WebsocketServerContainer;
import com.orainge.websocket_forward.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;

@Component
@Slf4j
public class UIManager {
    @Resource
    private WebsocketServerContainer websocketServerContainer;
    private final ClientSettingUtils configUtils;

    @Resource
    private NotificationHistoryUtils notificationHistoryUtils;

    /**
     * 托盘 ICON classpath 路径
     */
    private static final String IMAGE_PATH_CONNECTING = "icon/trayIcon-connecting.png";
    private static final String IMAGE_PATH_CONNECTED = "icon/trayIcon-connected.png";
    private static final String IMAGE_PATH_DISCONNECT = "icon/trayIcon-disconnected.png";
    private static final String IMAGE_PATH_NEW_MESSAGE = "icon/trayIcon-new-message.png";

    /**
     * 托盘 ICON Image 对象
     */
    private static Image trayIconConnecting, trayIconConnected, trayIconDisconnected, trayIconNewMessage;

    /**
     * 系统托盘
     */
    private final SystemTray tray = SystemTray.getSystemTray();

    /**
     * 系统托盘 ICON
     */
    private final TrayIcon trayIcon;

    /**
     * 连接状态菜单项
     */
    private final MenuItem mUIConnectStatus = new MenuItem();

    /**
     * 更改连接状态按钮
     */
    private final MenuItem mChangeUIConnectStatus = new MenuItem();

    /**
     * 面板 - 显示历史通知
     */
    private HistoryFrame historyFrame = null;

    /**
     * 面板 - 设置
     */
    private SettingFrame settingFrame = null;

    /**
     * 面板 - 关于我
     */
    private AboutFrame aboutFrame = null;

    /**
     * 最新一条【未读】通知
     */
    private NotificationMessage latestMessage = null;

    static {
        try {
            trayIconConnecting = createImage(IMAGE_PATH_CONNECTING);
            trayIconConnected = createImage(IMAGE_PATH_CONNECTED);
            trayIconDisconnected = createImage(IMAGE_PATH_DISCONNECT);
            trayIconNewMessage = createImage(IMAGE_PATH_NEW_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UIManager(ClientSettingUtils clientSettingUtils) throws Exception {
        if (!SystemTray.isSupported()) {
            // 系统不支持创建托盘，抛出异常
            log.error("[UI 管理器] - 该系统不支持创建托盘，无法启动");
            throw new UnsupportedOperationException("该系统不支持创建托盘，无法启动");
        }

        // 设置菜单样式
        mUIConnectStatus.setFont(FontManager.getBoldFont(14));
        mChangeUIConnectStatus.setFont(FontManager.getNormalFont(14));

        // 设置弹窗样式
        // 设置按钮显示效果
        javax.swing.UIManager.put("OptionPane.buttonFont", new FontUIResource(FontManager.getNormalFont(14)));
        // 设置文本显示效果
        javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(FontManager.getNormalFont(14)));

        // 初始化托盘图标
        trayIcon = new TrayIcon(trayIconDisconnected);

        // 鼠标单击监听
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (latestMessage != null) {
                        // 如果有未读消息，打开它
                        HistoryDetailsFrame.showMessage(latestMessage);
                        // 清除未读消息
                        onClearUnreadNotification();
                    }
                }
            }
        });

        // 鼠标左键双击监听，打开【历史记录】窗口
        trayIcon.addActionListener(e -> showHistoryWindow());

        // 初始化 Bean
        this.configUtils = clientSettingUtils;

        // 设置托盘图标
        trayIcon.setToolTip(configUtils.getClientVersionInfo().getName());
        trayIcon.setImageAutoSize(true);

        // 初始化菜单
        initMenu();

        // 添加图标到系统托盘
        tray.add(trayIcon);
    }

    /**
     * 发送 Toast 通知
     */
    public void notifyToast(NotificationMessage systemMsg) {
        // Toast 标题
        String caption = systemMsg.getTitle();
        if (!StringUtils.isEmpty(systemMsg.getSubtitle())) {
            caption += " - " + systemMsg.getSubtitle();
        }

        // Toast 内容
        String text = systemMsg.getMessage();

        // 显示通知
        trayIcon.displayMessage(caption, text, systemMsg.getMessageType());
    }

    /**
     * 当更新连接状态时，变更菜单项
     */
    public void updateUiConnectStatus() {
        UIConnectStatus status = configUtils.getUiConnectStatus();
        // 选项：连接状态
        mUIConnectStatus.setLabel("连接状态: " + status.getMenuText());
        // 选项：更改连接状态 【连接/断开连接】
        mChangeUIConnectStatus.setLabel(status.getConnectButtonText());

        // 更改连接状态时，切换显示的图标，区别连接状态（灰，蓝）
        if (status.equals(UIConnectStatus.CONNECTING)) {
            // 连接中
            trayIcon.setImage(trayIconConnecting);
        } else if (status.equals(UIConnectStatus.CONNECTED)) {
            // 已连接
            trayIcon.setImage(trayIconConnected);
        } else if (status.equals(UIConnectStatus.DISCONNECTED)) {
            // 连接断开
            trayIcon.setImage(trayIconDisconnected);
            latestMessage = null;
        }
    }

    /**
     * 当收到通知时的回调
     */
    public void onReceiveNotification(NotificationMessage message) {
        trayIcon.setImage(trayIconNewMessage);
        latestMessage = message;
        // 刷新历史记录窗口
        if (historyFrame != null) {
            historyFrame.onReceiveNotification();
        }
    }

    /**
     * 当清空未读消息时
     */
    public void onClearUnreadNotification() {
        if (latestMessage != null) {
            latestMessage = null;
            updateUiConnectStatus();
        }
    }

    /**
     * 初始化菜单
     */
    private void initMenu() {
        // 添加弹出菜单
        PopupMenu popup = new PopupMenu();

        // 添加菜单项
        // 选项：程序标题
        MenuItem m0 = new MenuItem(
                configUtils.getClientVersionInfo().getName() + " "
                        + configUtils.getClientVersionInfo().getVersion()
        );
        m0.setFont(FontManager.getBoldFont(16));
        m0.setEnabled(false);
        popup.add(m0);
        popup.addSeparator(); // 分隔符

//        // 连接信息区
//        // 选项：程序标题
//        MenuItem m1 = new MenuItem("服务器地址：" + " " + configUtils.getServerUrl());
//        m1.setEnabled(false);
//        popup.add(m1);
//        popup.addSeparator(); // 分隔符

        // 选项：连接状态
        mUIConnectStatus.setEnabled(false);
        popup.add(mUIConnectStatus);

        // 选项：更改连接状态 【连接/断开连接】
        popup.add(mChangeUIConnectStatus);
        mChangeUIConnectStatus.addActionListener(e -> {
            UIConnectStatus status = configUtils.getUiConnectStatus();
            if (status.equals(UIConnectStatus.CONNECTED) ||
                    status.equals(UIConnectStatus.CONNECTING)) {
                // 已连接/连接中 -> do 断开连接
                websocketServerContainer.disconnect();
            } else if (status.equals(UIConnectStatus.DISCONNECTED)) {
                // 断开连接-> do 连接
                websocketServerContainer.connect();
            }
        });
        popup.addSeparator(); // 分隔符

        // 手动初始化两个选项状态
        updateUiConnectStatus();

        // 选项：查看历史通知
        MenuItem mHistory = new MenuItem("查看历史通知");
        mHistory.setFont(FontManager.getNormalFont(14));
        mHistory.addActionListener(e -> showHistoryWindow());
        popup.add(mHistory);

        // 选项：设置
        MenuItem mSetting = new MenuItem("设置");
        mSetting.setFont(FontManager.getNormalFont(14));
        mSetting.addActionListener(e -> {
            if (settingFrame == null) {
                settingFrame = new SettingFrame(configUtils);
            }
            settingFrame.setVisible(true);
        });
        popup.add(mSetting);
        popup.addSeparator(); // 分隔符

        // 选项：关于
        MenuItem mAbout = new MenuItem("关于");
        mAbout.setFont(FontManager.getNormalFont(14));
        mAbout.addActionListener(e -> {
            if (aboutFrame == null) {
                aboutFrame = new AboutFrame(configUtils.getClientVersionInfo());
            }
            aboutFrame.setVisible(true);
        });
        popup.add(mAbout);

        // 选项：退出
        MenuItem mExit = new MenuItem("退出");
        mExit.setFont(FontManager.getNormalFont(14));
        mExit.addActionListener(e -> {
            final int exitCode = 0;
            ExitCodeGenerator exitCodeGenerator = () -> exitCode;
            System.exit(SpringApplication.exit(ApplicationContextUtils.applicationContext, exitCodeGenerator));
        });
        popup.add(mExit);

        // 添加当前菜单到当前系统托盘图标
        trayIcon.setPopupMenu(popup);
    }

    /**
     * 退出程序时执行
     */
    public void onExit() {
        tray.remove(trayIcon);
    }

    /**
     * 显示历史记录对话框
     */
    private void showHistoryWindow() {
        if (historyFrame == null) {
            historyFrame = new HistoryFrame(this, notificationHistoryUtils);
        }
        historyFrame.setVisible(true);
    }

    /**
     * 获取 Image
     */
    private static Image createImage(String path) throws Exception {
        byte[] content;
        ClassPathResource classPathResource = new ClassPathResource(path);
        try (InputStream inputStream = classPathResource.getInputStream()) {
            content = StreamUtils.copyToByteArray(inputStream);
        }
        return Toolkit.getDefaultToolkit().createImage(content);
    }
}
