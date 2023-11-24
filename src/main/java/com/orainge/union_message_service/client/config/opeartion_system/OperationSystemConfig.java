package com.orainge.union_message_service.client.config.opeartion_system;

import com.orainge.union_message_service.client.config.opeartion_system.macos.MacOSConfig;
import com.orainge.union_message_service.client.config.opeartion_system.macos.TerminalNotifierConfig;
import com.orainge.union_message_service.client.util.os.OSUtils;
import com.orainge.union_message_service.client.util.settings.helper.FileSaveConstants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

/**
 * 不同操作系统下的配置
 */
@Configuration
@ConfigurationProperties("operation-system-config")
@Data
@Slf4j
public class OperationSystemConfig {
    private MacOSConfig macos;

    @PostConstruct
    public void init() throws IOException {
        // 如果当前系统是 MacOS 就执行初始化
        if (OSUtils.isMacOS() || OSUtils.isMacOSX()) {
            TerminalNotifierConfig config = macos.getTerminalNotifier();
            String execPath = config.getExecPath();
            if (execPath == null || "".equals(execPath)) {
                execPath = FileSaveConstants.APP_CONFIG_FILE_DIC_PATH + File.separator + "bin" + File.separator + "terminal-notifier.app" + File.separator + "Contents" + File.separator + "MacOS" + File.separator + "terminal-notifier";

                // 如果程序文件不存在，则释放文件到配置文件目录
                if (!Files.exists(Paths.get(execPath))) {
                    // 释放 zip 压缩包
                    String zipFilePath = FileSaveConstants.APP_CONFIG_FILE_DIC_PATH + File.separator + "terminal-notifier.app.zip";
                    ClassPathResource classPathSource = new ClassPathResource("bin/terminal-notifier.app.zip");
                    try (InputStream inputStream = classPathSource.getInputStream()) {
                        try (OutputStream outputStream = new FileOutputStream(zipFilePath)) {
                            // 设置缓冲区，提高读写效率
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                        }
                    }

                    // 解压文件
                    unzipFile(zipFilePath, FileSaveConstants.APP_CONFIG_FILE_DIC_PATH + File.separator + "bin");

                    // 删除 zip 文件
                    Files.delete(Paths.get(zipFilePath));

                    // 修改可执行文件的权限
                    Path execPathBean = Paths.get(execPath);
                    Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(execPathBean);

                    // 添加或移除需要的权限，这里示例添加了所有者的写权限
                    permissions.add(PosixFilePermission.GROUP_EXECUTE);
                    permissions.add(PosixFilePermission.OWNER_EXECUTE);
                    permissions.add(PosixFilePermission.OTHERS_EXECUTE);

                    // 设置新的权限
                    Files.setPosixFilePermissions(execPathBean, permissions);
                }

                // 设置新的执行路径
                config.setExecPath(execPath);
            }

//            // macos-alert 相关设置
//            MacOSAlertConfig macOSAlertConfig = macos.getMacosAlert();
//
//            String iconFolderPath = macOSAlertConfig.getIconFolderPath();
//            if (iconFolderPath == null || "".equals(iconFolderPath)) {
//                macOSAlertConfig.setIconFolderPath("~/");
//            } else if (!iconFolderPath.endsWith("/")) {
//                macOSAlertConfig.setIconFolderPath(iconFolderPath + "/");
//            }
//
//            String macosAlertExecPath = macOSAlertConfig.getExecPath();
//            if (macosAlertExecPath == null || "".equals(macosAlertExecPath)) {
//                macOSAlertConfig.setExecPath("macos-alert");
//            }
        }
    }

    // 解压文件的方法
    private static void unzipFile(String zipFilePath, String extractTo) throws IOException {
        Path zipFile = Paths.get(zipFilePath);
        Path extractDirectory = Paths.get(extractTo);

        try (FileSystem zipFileSystem = FileSystems.newFileSystem(zipFile, null)) {
            Path root = zipFileSystem.getRootDirectories().iterator().next();
            Files.walk(root).forEach(source -> {
                Path destination = Paths.get(extractDirectory.toString(), root.relativize(source).toString());
                try {
                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
