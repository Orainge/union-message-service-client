package com.orainge.union_message_service.client.ui.frame;

import com.orainge.union_message_service.client.bean.MessageBean;
import com.orainge.union_message_service.client.ui.FontManager;
import com.orainge.union_message_service.client.ui.UIManager;
import com.orainge.union_message_service.client.ui.components.OperationTable;
import com.orainge.union_message_service.client.util.settings.NotificationHistoryUtils;
import com.orainge.union_message_service.client.util.settings.helper.FileSaveConstants;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * 历史通知对话框
 */
public class HistoryFrame extends UIFrame {
    private final UIManager uiManager;
    private List<MessageBean> messageList;

    /**
     * 上次显示时，数据显示的条数
     */
    private Integer lastDisplayCount = null;

    /**
     * 带有【操作】列的表格
     */
    private final OperationTable table;

    public HistoryFrame(UIManager uiManager, NotificationHistoryUtils notificationHistoryUtils) {
        this.uiManager = uiManager;
        this.messageList = notificationHistoryUtils.getAllNotification();

        // 对话框设置
        setTitle("查看历史通知");
        setSize(800, 600);
        setResizable(true);
        setLayout(new BorderLayout());

        // 页面样式初始化
        // 页面顶部
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        JPanel topRight = new JPanel();
        topPanel.add(topRight, BorderLayout.EAST);

//        // 删除所有记录
//        JLabel titleLabel = new JLabel("所有历史通知");
//        titleLabel.setFont(FontManager.getBoldFont(16));
//        topPanel.add(titleLabel, BorderLayout.WEST);

        // 【刷新】按钮
        JButton refreshButton = new JButton("刷新");
        refreshButton.setFont(FontManager.getNormalFont(16));
        refreshButton.addActionListener(e -> {
            this.messageList = notificationHistoryUtils.getAllNotification(true); // 从外部存储重新载入历史记录
            initTable(); // 初始化表格
        });
        topRight.add(refreshButton); // 【刷新】按钮

        // 【删除全部】按钮
        JButton deleteAllButton = new JButton("删除所有记录");
        deleteAllButton.setFont(FontManager.getNormalFont(16));
        deleteAllButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                    HistoryFrame.this,
                    "这将删除所有通知记录，是否继续？", "警告",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                // 删除所有通知
                notificationHistoryUtils.deleteAllNotification();
                // 重新初始化表格
                initTable();
                // 清空未读消息时
                uiManager.onClearUnreadNotification();
            }
        });
        topRight.add(deleteAllButton);

        // 表格初始化
        // 创建表格
        // 初始化参数
        String[] columnNames = new String[]{"序号", "标题", "内容", "时间", "操作"};
        Integer[] columnWidths = new Integer[]{35, 90, null, 140, 175};
        Integer[] columnAligns = new Integer[]{SwingConstants.CENTER, SwingConstants.CENTER, null, SwingConstants.CENTER, null};
        table = new OperationTable(columnNames, columnWidths, columnAligns);
        table.setRowHeight(38); // 设置行高
//        table.getTableHeader().setResizingAllowed(false); // 禁止调整宽度

        // 添加操作按钮
        JButton detailsButton = new JButton("详情");
        JButton deleteButton = new JButton("删除");
        detailsButton.setFont(FontManager.getNormalFont(14));
        deleteButton.setFont(FontManager.getNormalFont(14));
        detailsButton.setPreferredSize(new Dimension(75, 30));
        deleteButton.setPreferredSize(new Dimension(75, 30));

        // 点击【详情】按钮事件
        detailsButton.addActionListener(e -> {
            int index = table.getCellPanel().getRow();
            HistoryDetailsFrame.showMessage(messageList.get(index));
        });

        // 点击【删除】按钮事件
        deleteButton.addActionListener(e -> {
            int index = table.getCellPanel().getRow();
            int option = JOptionPane.showConfirmDialog(
                    HistoryFrame.this,
                    "是否删除这条记录【" + messageList.get(index).getTitle() + "】？", "警告",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                // 删除所有通知
                notificationHistoryUtils.deleteNotification(index);
                // 重新初始化表格
                initTable();
            }
        });

        table.addOperationCellComponent(detailsButton, deleteButton);

        // 添加表格到窗口中
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 底部 Panel
        JPanel bottomPanel = new JPanel();

        // 历史文件记录
        String historyFilePath = FileSaveConstants.APP_NOTIFICATION_HISTORY_FILE_PATH;
        JLabel configPathLabel = new JLabel("历史记录文件: " + historyFilePath);
        configPathLabel.setFont(FontManager.getNormalFont(16));
        JButton openAppConfigFileDicButton = new JButton("浏览");
        openAppConfigFileDicButton.setFont(FontManager.getNormalFont(14));
        openAppConfigFileDicButton.addActionListener(e -> {
            // 调用系统文件浏览器打开文件夹
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(new java.io.File(historyFilePath));
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "文件不存在", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        bottomPanel.add(configPathLabel);
        bottomPanel.add(openAppConfigFileDicButton); // 【浏览】按钮
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * 初始化表格数据
     */
    private void initTable() {
        // 封装表格数据
        Object[][] data = new Object[messageList.size()][5];
        for (int i = 0; i < messageList.size(); i++) {
            MessageBean message = messageList.get(i);
            data[i][0] = i + 1;
            data[i][1] = message.getTitle();
            data[i][2] = message.getBody();
            data[i][3] = message.getNotifyDateString();
        }
        table.refreshTableData(data);
    }

    /**
     * 当收到通知时的回调
     */
    public void onReceiveNotification() {
        initTable();
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            // 如果显示对话框，需要重新刷新数据
            // 如果现在实际的条数与上次显示的条数不一致，需要刷新
            if (lastDisplayCount == null || messageList.size() != lastDisplayCount) {
                initTable();
            }
            // 清空未读消息
            uiManager.onClearUnreadNotification();
        } else {
            // 记录上次显示的条数
            lastDisplayCount = messageList.size();
        }
        super.setVisible(b);
    }
}