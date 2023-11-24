package com.orainge.union_message_service.client.ui.frame;

import com.orainge.union_message_service.client.bean.MessageBean;
import com.orainge.union_message_service.client.ui.FontManager;
import com.orainge.union_message_service.client.ui.bean.NotificationMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 【查看通知详情】页面<br>
 * 单例窗口
 */
public class HistoryDetailsFrame extends UIFrame {
    private final JLabel titleLabel = new JLabel();
    private final JLabel dateLabel = new JLabel();
    private final JTextArea bodyTextArea = new JTextArea();
    private String body;

    private static HistoryDetailsFrame detailsFrame = null;

    /**
     * 显示消息
     */
    public static void showMessage(MessageBean message) {
        if (detailsFrame == null) {
            detailsFrame = new HistoryDetailsFrame();
        }
        detailsFrame.initUI(message.getTitle(), message.getNotifyDateString(), message.getBody());
    }

    /**
     * 显示消息
     */
    public static void showMessage(NotificationMessage message) {
        if (detailsFrame == null) {
            detailsFrame = new HistoryDetailsFrame();
        }

        detailsFrame.initUI(message.getTitle(), message.getNotifyDateString(), message.getMessage());
    }

    private HistoryDetailsFrame() {
        setTitle("查看详情");
        setSize(600, 300);
        setLayout(new BorderLayout());

        // 页面顶部
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        titleLabel.setFont(FontManager.getBoldFont(18));
        dateLabel.setFont(FontManager.getNormalFont(16));
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(dateLabel, BorderLayout.EAST);

        // 通知内容文本框
        bodyTextArea.setFont(FontManager.getNormalFont(16));
        bodyTextArea.setLineWrap(true);
        bodyTextArea.setWrapStyleWord(true);
        bodyTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(bodyTextArea);

        // 【复制内容】按钮
        JPanel bottomPanel = new JPanel();
        JButton copyButton = new JButton("复制内容");
        copyButton.setFont(FontManager.getNormalFont(14));
        copyButton.addActionListener(e -> {
            StringSelection stringSelection = new StringSelection(body);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            JOptionPane.showMessageDialog(HistoryDetailsFrame.this, "内容已复制到剪贴板", "提示", JOptionPane.INFORMATION_MESSAGE);
        });
        bottomPanel.add(copyButton);

        // 中间面板
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);

        // 四周的分隔
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        JPanel p4 = new JPanel();
        p1.setPreferredSize(new Dimension(0, 20));
        p2.setPreferredSize(new Dimension(0, 20));
        p3.setPreferredSize(new Dimension(20, 0));
        p4.setPreferredSize(new Dimension(20, 0));
        add(p1, BorderLayout.NORTH);
        add(p2, BorderLayout.SOUTH);
        add(p3, BorderLayout.EAST);
        add(p4, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);

        // 添加窗口关闭监听器
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 重置变量
                detailsFrame = null;
            }
        });
    }

    private void initUI(String title, String date, String body) {
        titleLabel.setText(title);
        dateLabel.setText(date);
        bodyTextArea.setText(body);
        this.body = body;
        setVisible(true);
    }
}
