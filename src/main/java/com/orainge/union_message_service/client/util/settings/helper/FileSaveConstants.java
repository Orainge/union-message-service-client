package com.orainge.union_message_service.client.util.settings.helper;

import com.orainge.union_message_service.client.util.os.OSUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件保存常量
 */
public class FileSaveConstants {
    /**
     * APP 文件夹名称
     */
    public static final String APP_DICTIONARY_NAME = "nsClient"; // Notification Service Client

    /**
     * APP 配置文件名称
     */
    public static final String APP_CONFIG_NAME = "client_config.txt";

    /**
     * 历史通知文件名称
     */
    public static final String APP_NOTIFICATION_HISTORY_FILE_NAME = "notification_history.txt";

    /**
     * 配置文件所在文件夹路径
     */
    public static final String APP_CONFIG_FILE_DIC_PATH;

    /**
     * 配置文件路径
     */
    public static final String APP_CONFIG_FILE_PATH;

    /**
     * 历史通知文件路径
     */
    public static final String APP_NOTIFICATION_HISTORY_FILE_PATH;

    static {
        try {
            // 根据系统，选择存放程序根目录的文件夹
            if (OSUtils.isWindows()) {
                // Windows 系统
                APP_CONFIG_FILE_DIC_PATH = System.getenv("APPDATA") + File.separator + APP_DICTIONARY_NAME;
            } else if (OSUtils.isLinux()) {
                // Linux 系统
                APP_CONFIG_FILE_DIC_PATH = System.getProperty("user.home") + File.separator + "." + APP_DICTIONARY_NAME;
            } else if (OSUtils.isMacOS() || OSUtils.isMacOSX()) {
                // MacOS 系统
                APP_CONFIG_FILE_DIC_PATH = System.getProperty("user.home") + File.separator + "." + APP_DICTIONARY_NAME;
//            systemConfigPathString = System.getProperty("user.home") + "/Library/Application Support/" + APP_DICTIONARY_NAME;
            } else {
                // 其他系统
                APP_CONFIG_FILE_DIC_PATH = System.getProperty("user.home") + File.separator + "." + APP_DICTIONARY_NAME;
            }

            // 检查配置文件夹是否存在
            Path appConfigDicPath = Paths.get(APP_CONFIG_FILE_DIC_PATH);
            if (Files.exists(appConfigDicPath)) {
                // 存在，检查是否是文件夹
                if (!Files.isDirectory(appConfigDicPath)) {
                    // 不是文件夹，删除该文件，重建文件夹
                    Files.delete(appConfigDicPath);
                    Files.createDirectories(appConfigDicPath);
                }
                // 其他情况不做处理
            } else {
                // 创建文件夹
                Files.createDirectories(appConfigDicPath);
            }

            // 创建配置文件
            APP_CONFIG_FILE_PATH = APP_CONFIG_FILE_DIC_PATH + File.separator + APP_CONFIG_NAME;
            if (!Files.exists(Paths.get(APP_CONFIG_FILE_PATH))) {
                // 如果不存在文件，就创建文件
                new File(APP_CONFIG_FILE_PATH).createNewFile();
            }

            // 创建配置文件
            APP_NOTIFICATION_HISTORY_FILE_PATH = APP_CONFIG_FILE_DIC_PATH + File.separator + APP_NOTIFICATION_HISTORY_FILE_NAME;
            if (!Files.exists(Paths.get(APP_NOTIFICATION_HISTORY_FILE_PATH))) {
                // 如果不存在文件，就创建文件
                new File(APP_NOTIFICATION_HISTORY_FILE_PATH).createNewFile();
            }
        } catch (Exception e) {
            throw new RuntimeException("初始化配置文件错误", e);
        }
    }
}
