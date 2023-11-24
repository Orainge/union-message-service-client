package com.orainge.union_message_service.client.ui.components;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

/**
 * 带操作面板的 TableModel
 */
public class OperationTableModel extends DefaultTableModel {
    /**
     * 【操作】列所在索引
     */
    private Integer operationColumnIndex;

    @Override
    public boolean isCellEditable(int row, int col) {
        // 默认所有列都不可以编辑，如果存在【操作】列，则该列是可以编辑的
        if (operationColumnIndex == null) {
            return false;
        } else {
            return col == operationColumnIndex;
        }
    }

    @Override
    public void setDataVector(Vector dataVector, Vector columnIdentifiers) {
        super.setDataVector(dataVector, columnIdentifiers);

        // 找到【操作】列所在
        for (int i = 0; i < columnIdentifiers.size(); i++) {
            Object item = columnIdentifiers.get(i);
            if ("操作".equals(item)) {
                operationColumnIndex = i;
                break;
            }
        }
    }

    public Integer getOperationColumnIndex() {
        return operationColumnIndex;
    }
}