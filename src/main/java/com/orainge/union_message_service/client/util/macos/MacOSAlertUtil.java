package com.orainge.union_message_service.client.util.macos;

import com.orainge.union_message_service.client.ui.bean.NotificationMessage;
import com.orainge.union_message_service.client.config.opeartion_system.macos.MacOSAlertConfig;
import com.orainge.websocket_forward.ApplicationContextUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * MacOS 弹窗通知
 */
public class MacOSAlertUtil {
    private final RestTemplate restTemplate;

    private final MacOSAlertConfig macOSAlertConfig;

    public MacOSAlertUtil() {
        this.restTemplate = ApplicationContextUtils.getBeanByClass(RestTemplate.class);
        this.macOSAlertConfig = ApplicationContextUtils.getBeanByClass(MacOSAlertConfig.class);
    }

    /**
     * 使用 osascript 执行弹窗
     */
    public String buildCommandDialog(NotificationMessage notificationMessage) {
//        return "osascript -e 'display dialog \"" +
//                notificationMessage.getMessage() + // 内容
//                "\" buttons {\"复制内容\", \"取消\"} default button \"复制内容\" with title \"" +
//                notificationMessage.getTitle() + // 标题
//                "\"' -e 'if button returned of result is \"复制内容\" then' -e 'do shell script \"echo \\\"" +
//                notificationMessage.getMessage() + // 内容
//                "\\\" | pbcopy\"' -e 'end if'";

                return "osascript -e 'display dialog \"" +
                        notificationMessage.getMessage() + // 内容
                        "\" buttons {\"确定\"} default button 1 with title \"" +
                        notificationMessage.getTitle() + // 标题
                        "\"'";
    }

    /**
     * 使用 macos-alert 弹窗<br>
     * <p>
     * https://gitee.com/xiaozhuai/macos-alert/
     */
    public String buildCommandAlert(NotificationMessage notificationMessage) {
        // 处理 ICON
        String iconPath = null;
        String appIconUrl = notificationMessage.getAppIcon();
        if (!StringUtils.isEmpty(appIconUrl)) {
            // 下载 ICON 文件到本地
            iconPath = downloadFile(appIconUrl);
        }

        // 处理内容
        String message = notificationMessage.getMessage();
        message = message.replaceAll("\\n", "\\\\r\\\\n");

        StringBuilder command = new StringBuilder();
        command.append(macOSAlertConfig.getExecPath())
                .append(" <<EOF\n")
                .append("title = ").append(notificationMessage.getTitle()).append("\n") // 标题
                .append("message = ").append(notificationMessage.getTitle()).append("\n") // 标题
                .append("subMessage = ").append(message).append("\n"); // 内容
        if (iconPath != null) {
            command.append("icon = ").append(iconPath).append("\n");
        }
        command.append("buttons = 确定\n")
                .append("EOF");
        return command.toString();
    }

    private String downloadFile(String url) {
        try {
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            String filePath = macOSAlertConfig.getIconFolderPath() + fileName;
            File file = new File(filePath);
            if (file.exists()) {
                // 文件存在，就不下载
                return filePath;
            }

            ResponseEntity<byte[]> forEntity = restTemplate.getForEntity(url, byte[].class);
            Files.write(Paths.get(filePath), Objects.requireNonNull(forEntity.getBody(), "未获取到下载文件"));
            return filePath;

            // 大文件下载
//        RequestCallback requestCallback = request -> request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
//        restTemplate.execute(url, HttpMethod.GET, requestCallback, clientHttpResponse -> {
//            Files.copy(clientHttpResponse.getBody(), Paths.get(filePath));
//            return null;
//        });
        } catch (Exception ignore) {
        }
        return null;
    }
}
