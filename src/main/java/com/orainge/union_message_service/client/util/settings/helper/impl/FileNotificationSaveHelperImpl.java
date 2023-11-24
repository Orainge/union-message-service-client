package com.orainge.union_message_service.client.util.settings.helper.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orainge.union_message_service.client.bean.MessageBean;
import com.orainge.union_message_service.client.util.settings.helper.FileSaveConstants;
import com.orainge.union_message_service.client.util.settings.helper.NotificationSaveHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 通知历史记录保存 Helper
 */
@Service
@Slf4j
public class FileNotificationSaveHelperImpl implements NotificationSaveHelper {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取所有通知
     * 文件内是按照时间升序，返回的数据是按照时间降序
     */
    @Override
    public List<MessageBean> getAllNotification() {
        List<MessageBean> messageList = new LinkedList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FileSaveConstants.APP_NOTIFICATION_HISTORY_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!StringUtils.isEmpty(line) && !"\n".equals(line)) {
                    MessageBean message = objectMapper.readValue(line, new TypeReference<MessageBean>() {
                    });
                    messageList.add(message);
                }
            }
        } catch (Exception e) {
            log.error("[通知历史记录工具] - 从文件读取历史记录失败", e);
        }

        // 将列表逆序
        if (!messageList.isEmpty()) {
            Collections.reverse(messageList);
        }

        // 返回结果
        return messageList;
    }

    /**
     * 保存通知信息
     */
    @Override
    public void saveNotification(MessageBean message) {
        // 为了实现从文件头部追加信息，需要通过整个列表写入的方式实现
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FileSaveConstants.APP_NOTIFICATION_HISTORY_FILE_PATH, true))) {
            writer.write(objectMapper.writeValueAsString(message) + "\n");
        } catch (Exception e) {
            log.error("[通知历史记录工具] - 单个通知写入文件失败", e);
        }
    }

    /**
     * 保存所有通知信息
     */
    @Override
    public void saveAllNotification(List<MessageBean> messageList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FileSaveConstants.APP_NOTIFICATION_HISTORY_FILE_PATH, false))) {
            for (MessageBean message : messageList) {
                writer.write(objectMapper.writeValueAsString(message) + "\n");
            }
        } catch (Exception e) {
            log.error("[通知历史记录工具] - 多个通知写入文件失败", e);
        }
    }

    /**
     * 删除所有通知
     */
    @Override
    public void deleteAllNotification() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FileSaveConstants.APP_NOTIFICATION_HISTORY_FILE_PATH, false))) {
            writer.write("");
        } catch (Exception e) {
            log.error("[通知历史记录工具] - 删除所有通知失败", e);
        }
    }
}
