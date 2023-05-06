package com.schbrain.common.util.support.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.schbrain.common.util.support.excel.exception.ExcelException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 层级数据 excel 读取类
 *
 * @author liaozan
 * @since 2022/3/29
 */
@Slf4j
public class HierarchicalDataReadListener extends ExcelReadListenerBase<Map<Integer, String>> {

    private final List<ImportedRecord> importedRecords = new LinkedList<>();

    private final Table<Integer, Integer, ImportedRecord> coordinateTable = HashBasedTable.create();

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        super.invoke(data, context);
        Integer rowIndex = context.readRowHolder().getRowIndex();
        data.forEach((columnIndex, value) -> {
            if (StringUtils.isNotBlank(value)) {
                buildImportedRow(rowIndex, columnIndex, value);
            }
        });
    }

    public List<ImportedRecord> getImportedRecords() {
        return importedRecords;
    }

    @Override
    protected boolean validate(Map<Integer, String> data, AnalysisContext context) {
        Integer rowIndex = context.readRowHolder().getRowIndex();
        if (MapUtils.isEmpty(data)) {
            throw new ExcelException(String.format("第 %d 行未读到数据", rowIndex + 1));
        }
        if (rowIndex == 0) {
            if (StringUtils.isBlank(data.get(0))) {
                throw new ExcelException("第一行第一列未读到数据");
            }
        }
        return true;
    }

    protected void buildImportedRow(Integer rowIndex, Integer columnIndex, String text) {
        ImportedRecord importedRecord = new ImportedRecord();
        importedRecord.setText(text);
        coordinateTable.put(rowIndex, columnIndex, importedRecord);
        if (columnIndex == 0) {
            importedRecords.add(importedRecord);
        } else {
            int currentRowIndex = rowIndex;
            // 定位到前一列的单元格
            Map<Integer, ImportedRecord> prevColumn = coordinateTable.column(columnIndex - 1);
            // 从当前行往上找，找到非空的记录，即视为 parent
            ImportedRecord parent = prevColumn.get(currentRowIndex);
            while (parent == null && currentRowIndex > 0) {
                parent = prevColumn.get(--currentRowIndex);
            }
            if (parent == null) {
                throw new ExcelException("数据格式错误,请对比模板调整");
            }
            parent.getChildren().add(importedRecord);
        }
    }

    @Data
    public static class ImportedRecord {

        private String text;

        private List<ImportedRecord> children = new LinkedList<>();

        public boolean hasChildren() {
            return CollectionUtils.isNotEmpty(children);
        }

    }

}