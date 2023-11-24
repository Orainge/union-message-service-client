package com.orainge.union_message_service.client.ui.components;

import com.orainge.union_message_service.client.ui.FontManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.io.*;
import java.util.Enumeration;

/**
 * 带有【操作】列的表格
 */
public class OperationTable extends JTable {
    /**
     * 列名
     */
    private final String[] columnNames;
    /**
     * 列宽度, null 表示不设置具体的值
     */
    private final Integer[] columnWidths;
    /**
     * 对齐方式, null 表示不设置对其方式
     */
    private final Integer[] columnAligns;

    /**
     * 表格数据
     */
    private Object[][] data;

    private final PanelTableCellRenderer operationCellRenderer = new PanelTableCellRenderer();
    private final PanelTableCellEditor operationCellEditor = new PanelTableCellEditor();

    /**
     * 带【操作】列的 TableModel<br>
     * 必须要在columnNames显式指定“操作”列
     */
    private final OperationTableModel operationTableModel;

    public OperationTable(String[] columnNames, Integer[] columnWidths, Integer[] columnAligns) {
        super();

        // 初始化变量
        this.columnNames = columnNames;
        this.columnWidths = columnWidths;
        this.columnAligns = columnAligns;

        // 设置 TableModel
        operationTableModel = new OperationTableModel();
        setModel(operationTableModel);

        // 设置单元格样式
        FlowLayout fl1 = new FlowLayout();
        fl1.setAlignment(FlowLayout.CENTER);
        operationCellRenderer.getCellPanel().setLayout(fl1);

        FlowLayout fl2 = new FlowLayout();
        fl2.setAlignment(FlowLayout.CENTER);
        operationCellEditor.getCellPanel().setLayout(fl2);
    }

    /**
     * 刷新表格数据<br>
     * 必须预留【操作】列的空间，即第二维的数字需要在数据列的基础上+1
     */
    public void refreshTableData(Object[][] data) {
        this.data = data;
        refreshTableData();
    }

    /**
     * 刷新表格数据
     */
    public void refreshTableData() {
        operationTableModel.setDataVector(data, columnNames);

        // 设置自定义的 CellRenderer和 CellEditor
        int operationColumnIndex = operationTableModel.getOperationColumnIndex();
        TableColumn operationColumn = getColumnModel().getColumn(operationColumnIndex);
        operationColumn.setCellRenderer(operationCellRenderer);
        operationColumn.setCellEditor(operationCellEditor);

        // 初始化列样式
        if (columnWidths != null || columnAligns != null) {
            TableColumnModel columnModel = getColumnModel();
            int columnIndex = -1;
            for (Enumeration<TableColumn> e = columnModel.getColumns(); e.hasMoreElements(); ) {
                TableColumn column = e.nextElement();
                columnIndex++;

                // 设置每列的宽度
                if (columnWidths != null) {
                    Integer width = columnWidths[columnIndex];
                    if (width != null) {
                        // 只有不等于 null 才会设置指定宽度
                        column.setMinWidth(width);
                        column.setMaxWidth(width);
                        column.setPreferredWidth(width);
                    }
                }

                if (columnAligns != null) {
                    // 设置每列的对齐方式
                    Integer align = columnAligns[columnIndex];
                    if (align != null) {
                        // 只有不等于 null 才会设置对齐方式
                        column.setCellRenderer(new DefaultTableCellRenderer() {{
                            this.setHorizontalAlignment(align);
                        }});
                    }
                }
            }
        }

        // 设置表头字体
        JTableHeader head = getTableHeader(); // 创建表格标题对象
        head.setFont(FontManager.getNormalFont(14));// 设置表格字体

        // 设置表格字体
        setFont(FontManager.getNormalFont(14));
    }

    /**
     * 批量添加组件
     */
    public void addOperationCellComponent(Component... comps) {
        for (Component comp : comps) {
            Component newComp = deepCloneComponent(comp);
            newComp.setFont(comp.getFont()); // 设置字体样式
            operationCellRenderer.getCellPanel().add(newComp);
            operationCellEditor.getCellPanel().add(comp);
        }
    }

    /**
     * 获取单元格面板
     */
    public PanelTableCellPanel getCellPanel() {
        return operationCellEditor.getCellPanel();
    }

    /**
     * 深拷贝 Component 对象
     */
    private Component deepCloneComponent(Component t) {
        // 保存对象为字节数组
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            try (ObjectOutputStream out = new ObjectOutputStream(bout)) {
                out.writeObject(t);
            }

            // 从字节数组中读取克隆对象
            try (InputStream bin = new ByteArrayInputStream(bout.toByteArray())) {
                ObjectInputStream in = new ObjectInputStream(bin);
                return (Component) in.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}