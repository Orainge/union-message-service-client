package com.orainge.union_message_service.client.ui.components;

import javax.swing.*;

/**
 * 带面板单元格的面板实体类
 */
public class PanelTableCellPanel extends JPanel {
    private JTable table;
    private Object value;
    private Boolean isSelected;
    private Boolean hasFocus;
    private Integer row;
    private Integer column;

    public JTable getTable() {
        return table;
    }

    public Object getValue() {
        return value;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public Boolean getHasFocus() {
        return hasFocus;
    }

    public Integer getRow() {
        return row;
    }

    public Integer getColumn() {
        return column;
    }

    public PanelTableCellPanel setTable(JTable table) {
        this.table = table;
        return this;
    }

    public PanelTableCellPanel setValue(Object value) {
        this.value = value;
        return this;
    }

    public PanelTableCellPanel setSelected(Boolean selected) {
        isSelected = selected;
        return this;
    }

    public PanelTableCellPanel setHasFocus(Boolean hasFocus) {
        this.hasFocus = hasFocus;
        return this;
    }

    public PanelTableCellPanel setRow(Integer row) {
        this.row = row;
        return this;
    }

    public PanelTableCellPanel setColumn(Integer column) {
        this.column = column;
        return this;
    }
}