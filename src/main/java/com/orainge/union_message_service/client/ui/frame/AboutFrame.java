package com.orainge.union_message_service.client.ui.frame;

import com.orainge.union_message_service.client.config.ClientVersionInfo;
import com.orainge.union_message_service.client.ui.FontManager;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

/**
 * UI 面版 - 关于
 */
public class AboutFrame extends UIFrame {
    public AboutFrame(ClientVersionInfo clientVersionInfo) {
        setTitle("关于");
        setSize(550, 350);

        JPanel mPanel = new JPanel();
        mPanel.setLayout(new BoxLayout(mPanel, BoxLayout.Y_AXIS));
        // 程序标题
        JLabel titleLabel = new JLabel(clientVersionInfo.getName());
        titleLabel.setFont(FontManager.getBoldFont(24));
        mPanel.add(createPanelWithComp(45, titleLabel));
        // 版本号
        JLabel versionLabel = new JLabel(clientVersionInfo.getVersion());
        versionLabel.setFont(FontManager.getNormalFont(16));
        mPanel.add(createPanelWithComp(versionLabel));

        // 分隔
        mPanel.add(Box.createVerticalStrut(30));

        // 版权信息
        JLabel copyrightLabel = new JLabel(clientVersionInfo.getCopyright());
        copyrightLabel.setFont(FontManager.getNormalFont(16));
        mPanel.add(createPanelWithComp(copyrightLabel));
        // 个人主页
        JPanel homepagePanel = new JPanel();
        homepagePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton homepageButton = new JButton("浏览");
        homepageButton.setFont(FontManager.getNormalFont(14));
        homepageButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI(clientVersionInfo.getHomepage()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        JLabel homepageLabel = new JLabel(clientVersionInfo.getHomepage());
        homepageLabel.setFont(FontManager.getNormalFont(16));
        mPanel.add(createPanelWithComp(homepageLabel, homepageButton));

        // Github 主页
        JPanel githubPanel = new JPanel();
        githubPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton githubButton = new JButton("浏览");
        githubButton.setFont(FontManager.getNormalFont(14));
        githubButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI(clientVersionInfo.getGithubPage()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        JLabel githubPageLabel = new JLabel(clientVersionInfo.getGithubPage());
        githubPageLabel.setFont(FontManager.getNormalFont(16));
        mPanel.add(createPanelWithComp(githubPageLabel, githubButton));

        // 主面板
        JPanel mainPanel = new JPanel();
        add(mainPanel);
        mainPanel.setLayout(new BorderLayout());

        // 四周的分隔
        mainPanel.add(Box.createVerticalStrut(40), BorderLayout.NORTH);
        mainPanel.add(Box.createVerticalStrut(40), BorderLayout.SOUTH);
        mainPanel.add(Box.createHorizontalStrut(40), BorderLayout.EAST);
        mainPanel.add(Box.createHorizontalStrut(40), BorderLayout.WEST);
        mainPanel.add(mPanel, BorderLayout.CENTER);
    }

    private JPanel createPanelWithComp(Component... comps) {
        return createPanelWithComp(38, comps);
    }

    private JPanel createPanelWithComp(int height, Component... comps) {
        Dimension d = new Dimension(getWidth() - 80, height);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        for (Component comp : comps) {
            panel.add(comp);
        }
        panel.setMinimumSize(d);
        panel.setMaximumSize(d);
        panel.setPreferredSize(d);
        return panel;
    }
}
