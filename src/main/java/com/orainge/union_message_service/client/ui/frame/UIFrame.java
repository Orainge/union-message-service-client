package com.orainge.union_message_service.client.ui.frame;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * 自定义的 JFrame
 */
@Slf4j
public class UIFrame extends JFrame {
    public static Image iconImage = null;

    static {
        // 初始化窗口 Icon
        byte[] content;
        ClassPathResource classPathSource = new ClassPathResource("icon/icon.png");
        try (InputStream inputStream = classPathSource.getInputStream()) {
            content = StreamUtils.copyToByteArray(inputStream);
            iconImage = Toolkit.getDefaultToolkit().createImage(content);
        } catch (IOException e) {
            log.error("无法初始化窗口 icon", e);
        }
    }

    public UIFrame() {
        super();
        this.setIconImage(iconImage);
        init();
    }

    private void init() {
        setBackground(new Color(240, 240, 240));
        setResizable(false);
    }

    /**
     * 窗口居中显示
     */
    @Override
    public void setVisible(boolean b) {
        int width = getWidth();
        int height = getHeight();
        Point p = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        setBounds(p.x - width / 2, p.y - height / 2, width, height);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 关闭就销毁窗口
        super.setVisible(b);
    }
}