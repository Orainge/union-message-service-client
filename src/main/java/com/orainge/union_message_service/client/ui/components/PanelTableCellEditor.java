package com.orainge.union_message_service.client.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelTableCellEditor extends DefaultCellEditor {
    private final PanelTableCellPanel cellPanel = new PanelTableCellPanel();

    public PanelTableCellEditor() {
        super(new JCheckBox());
        cellPanel.setOpaque(true);
        cellPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                fireEditingStopped();
            }
        });
    }

    public PanelTableCellPanel getCellPanel() {
        return cellPanel;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        cellPanel.setTable(table).setValue(value).setSelected(isSelected).setRow(row).setColumn(column); // 初始化参数
        cellPanel.setForeground(table.getSelectionForeground());
        cellPanel.setBackground(table.getSelectionBackground());
        return cellPanel;
    }

    @Override
    public Object getCellEditorValue() {
        return cellPanel;
    }

    @Override
    public boolean stopCellEditing() {
        return super.stopCellEditing();
    }
}