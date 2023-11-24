package com.orainge.union_message_service.client.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 客户端版本信息
 */
//@Configuration
//@ConfigurationProperties("client-config")
@Data
@Slf4j
@Component
public class ClientVersionInfo {
    /**
     * 客户端名称
     */
    private String name = "统一推送通知服务";

    /**
     * 版本号
     */
    private String version = "V1.1";

    /**
     * 版权信息
     */
    private String copyright = "Copyright © 2023 Eason Huang. All Rights Reserved.";

    /**
     * 个人主页
     */
    private String homepage = "https://easonhuang.net";

    /**
     * Github 主页
     */
    private String githubPage = "https://github.com/Orainge";
}
