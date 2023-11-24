package com.orainge.union_message_service.client.ui.components;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class PanelTableCellRenderer implements TableCellRenderer {
    private final PanelTableCellPanel cellPanel = new PanelTableCellPanel();

    public PanelTableCellRenderer() {
        cellPanel.setOpaque(true);
    }

    public PanelTableCellPanel getCellPanel() {
        return cellPanel;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        cellPanel.setTable(table).setValue(value).setSelected(isSelected).setHasFocus(hasFocus).setRow(row).setColumn(column); // 初始化参数
        return cellPanel;
    }
}