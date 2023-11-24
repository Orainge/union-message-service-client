package com.orainge.union_message_service.client.util.settings.helper.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orainge.union_message_service.client.util.settings.helper.SettingSaveHelper;
import com.orainge.union_message_service.client.util.settings.helper.FileSaveConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Map;

/**
 * 文件配置文件读取 Helper
 */
@Service
@Slf4j
public class FileSettingSaveHelperImpl implements SettingSaveHelper {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 以 Map 的方式读取所有配置
     */
    @Override
    public Map<String, Object> readAllConfig() {
        try {
            String configStr = readFirstLineTextFromFile();
            if (!StringUtils.isEmpty(configStr)) {
                return objectMapper.readValue(configStr, new TypeReference<Map<String, Object>>() {
                });
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("[文件配置工具] - 反序列化失败", e);
            return null;
        }
    }

    /**
     * 以 Map 方式写入所有配置
     */
    @Override
    public void writeAllConfig(Map<String, Object> configMap) {
        try {
            String configStr = objectMapper.writeValueAsString(configMap);
            writeFileInCover(configStr);
        } catch (Exception e) {
            log.error("[文件配置工具] - 反序列化失败", e);
        }
    }

    /**
     * 从文件读取第一行数据
     */
    public String readFirstLineTextFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FileSaveConstants.APP_CONFIG_FILE_PATH))) {
            String line;
            if ((line = reader.readLine()) != null) {
                return line;
            }
        } catch (Exception e) {
            log.error("[文件配置工具] - 读取配置文件失败", e);
        }
        return "";
    }

    /**
     * 以覆盖的方式写入文件
     **/
    public void writeFileInCover(String newContent) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FileSaveConstants.APP_CONFIG_FILE_PATH, false))) {
            writer.write(newContent);
        } catch (Exception e) {
            log.error("[文件配置工具] - 写入配置文件失败", e);
        }
    }
}
