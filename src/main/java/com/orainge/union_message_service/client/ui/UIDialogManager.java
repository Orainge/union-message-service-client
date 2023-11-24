package com.orainge.union_message_service.client.ui;

import org.springframework.stereotype.Component;

import javax.swing.*;

/**
 * 弹框管理器
 */
@Component
public class UIDialogManager {
    /**
     * 父容器
     */
    private final java.awt.Component parentComponent = null;

    /**
     * 显示提示信息
     *
     * @param content 内容
     */
    public void showInfoDialog(String content) {
        JOptionPane.showMessageDialog(parentComponent, content, "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 显示提示信息
     *
     * @param title   标题
     * @param content 内容
     */
    public void showInfoDialog(String title, String content) {
        JOptionPane.showMessageDialog(parentComponent, content, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 显示警告信息
     *
     * @param content 内容
     */
    public void showWarningDialog(String content) {
        JOptionPane.showMessageDialog(parentComponent, content, "警告", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * 显示警告信息
     *
     * @param title   标题
     * @param content 内容
     */
    public void showWarningDialog(String title, String content) {
        JOptionPane.showMessageDialog(parentComponent, content, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * 显示错误信息
     *
     * @param content 内容
     */
    public void showErrorDialog(String content) {
        JOptionPane.showMessageDialog(parentComponent, content, "错误", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 显示错误信息
     *
     * @param title   标题
     * @param content 内容
     */
    public void showErrorDialog(String title, String content) {
        JOptionPane.showMessageDialog(parentComponent, content, title, JOptionPane.ERROR_MESSAGE);
    }
}
