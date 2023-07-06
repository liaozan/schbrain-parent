package com.schbrain.framework.autoconfigure.mybatis.constraint;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.schbrain.common.util.StreamUtils;
import com.schbrain.framework.autoconfigure.mybatis.exception.TableConstraintException;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liaozan
 * @since 2022/9/2
 */
@Getter
public class Table {

    private final TableInfo tableInfo;

    private final List<FieldInfo> fieldInfoList;

    private final Map<String, ColumnMeta> columnMetaMap;

    private final List<TableConstraintException> errors = new ArrayList<>();

    public Table(TableInfo tableInfo, Map<String, ColumnMeta> columnMetaMap) {
        this.tableInfo = tableInfo;
        this.fieldInfoList = StreamUtils.toList(tableInfo.getFieldList(), FieldInfo::new);
        this.columnMetaMap = columnMetaMap;
    }

    public String getTableName() {
        return tableInfo.getTableName();
    }

    @Nullable
    public ColumnMeta getColumnMeta(String column) {
        return columnMetaMap.get(column);
    }

    public boolean containsColumn(String column) {
        return columnMetaMap.containsKey(column);
    }

    public void addError(TableConstraintException error) {
        errors.add(error);
    }

    @Data
    public static class FieldInfo {

        private String field;

        private String column;

        public FieldInfo(TableFieldInfo tableFieldInfo) {
            this.field = tableFieldInfo.getField().getName();
            this.column = StringUtils.unwrap(tableFieldInfo.getColumn(), "`");
        }

    }

}