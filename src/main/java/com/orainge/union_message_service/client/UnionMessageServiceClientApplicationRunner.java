package com.orainge.union_message_service.client;

import com.orainge.union_message_service.client.notification.NotificationUtils;
import com.orainge.union_message_service.client.ui.UIManager;
import com.orainge.union_message_service.client.util.settings.ClientSettingUtils;
import com.orainge.union_message_service.client.websocket.WebsocketServerContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

@Component
@Slf4j
public class UnionMessageServiceClientApplicationRunner implements ApplicationRunner {
    @Resource
    private WebsocketServerContainer websocketServerContainer;

    @Resource
    private ClientSettingUtils clientSettingUtils;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private ThreadPoolTaskScheduler threadPoolScheduler;

    /**
     * 程序运行后执行
     */
    @Override
    public void run(ApplicationArguments args) {
        // 根据条件初始化连接
        if (clientSettingUtils.getUiClientConfig().isConnectOnStart()) {
            websocketServerContainer.connect();
        }

        // 初始化完成后注入
        applicationContext.getBean(NotificationUtils.class)
                .addUiManagerToService(applicationContext.getBean(UIManager.class));
    }

    /**
     * 程序退出前执行
     */
    @PreDestroy
    public void destroy() {
        // 退出前断开连接
        websocketServerContainer.disconnect();

        // 关闭线程池
        if (threadPoolScheduler != null) {
            threadPoolScheduler.getScheduledExecutor().shutdownNow();
        }

        // UIManager 退出时的回调
        applicationContext.getBean(UIManager.class).onExit();
    }
}