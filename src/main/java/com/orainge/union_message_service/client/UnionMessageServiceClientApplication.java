package com.orainge.union_message_service.client;

import com.apple.eawt.Application;
import com.orainge.union_message_service.client.notification.NotificationService;
import com.orainge.union_message_service.client.ui.UIManager;
import com.orainge.union_message_service.client.ui.frame.UIFrame;
import com.orainge.union_message_service.client.util.os.OSUtils;
import com.orainge.websocket_forward.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.Objects;

@SpringBootApplication
@Slf4j
public class UnionMessageServiceClientApplication extends SpringBootServletInitializer {
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource[]{
                new ClassPathResource("client-config.yml")
        });
        pspc.setProperties(Objects.requireNonNull(yaml.getObject()));
        return pspc;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(UnionMessageServiceClientApplication.class);
    }

    public static void main(String[] args) {
        if (!(OSUtils.isMacOS() || OSUtils.isMacOSX() || OSUtils.isWindows() || OSUtils.isLinux())) {
            // 不是 MacOS / Windows / Linux 系统
            log.error("不支持该操作系统使用");
            JOptionPane.showMessageDialog(null, "不支持该操作系统使用", "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        // 如果是 Macos 系统，更改 ICON
        //获得操作系统
        //是mac 就设置dock图标
        if (OSUtils.isMacOS() || OSUtils.isMacOSX()) {
            System.setProperty("apple.awt.UIElement", "true");

            // 修改 Macos 下 Dock 的图标
            Image dockIcon = UIFrame.iconImage;
            Application app = Application.getApplication();
            app.setDockIconImage(dockIcon);
        }

        // 启动主程序
        SpringApplicationBuilder builder = new SpringApplicationBuilder(UnionMessageServiceClientApplication.class);
        builder.headless(false);
        builder.web(WebApplicationType.NONE); // 不启动 Web 容器
        ApplicationContextUtils.applicationContext = builder.run(args);
    }
}
