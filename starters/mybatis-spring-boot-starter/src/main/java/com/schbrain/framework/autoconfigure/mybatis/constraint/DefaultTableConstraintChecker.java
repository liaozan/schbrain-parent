package com.schbrain.framework.autoconfigure.mybatis.constraint;

import com.mysql.cj.MysqlType;
import com.schbrain.framework.autoconfigure.mybatis.biz.BizIdColumnField;
import com.schbrain.framework.autoconfigure.mybatis.biz.BizIdHelper;
import com.schbrain.framework.autoconfigure.mybatis.exception.TableConstraintException;

import static com.schbrain.framework.autoconfigure.mybatis.constant.MybatisConstants.*;

/**
 * @author liaozan
 * @since 2022/8/30
 */
public class DefaultTableConstraintChecker implements TableConstraintChecker {

    @Override
    public void checkBasicField(Table table) {
        checkIdField(table);
        checkCreateTimeField(table);
        checkModifyTimeField(table);
    }

    @Override
    public void checkLogicDeleteField(Table table) {
        checkDeletedField(table);
        checkDeleteVersionField(table);
    }

    @Override
    public void checkBizIdField(Table table) {
        BizIdColumnField bizColumnField = BizIdHelper.getBizColumnField(table.getTableInfo().getEntityType());
        if (bizColumnField == null) {
            return;
        }

        ColumnMeta bizColumnMeta = getColumnMeta(table, bizColumnField.getColumnName());
        if (bizColumnMeta == null) {
            return;
        }

        checkNotNull(table, bizColumnMeta);
        if (bizColumnMeta.getIndexName() == null) {
            addError(table, bizColumnMeta, "BizId field should create an index");
        }
    }

    protected void checkIdField(Table table) {
        ColumnMeta idColumnMeta = getColumnMeta(table, ID);
        if (idColumnMeta == null) {
            return;
        }

        checkNotNull(table, idColumnMeta);
        if (!MysqlType.BIGINT.getName().equalsIgnoreCase(idColumnMeta.getDataType())) {
            addError(table, idColumnMeta, "should be type of 'bigint'");
        }
        if (!AUTO_INCREMENT.equalsIgnoreCase(idColumnMeta.getExtra())) {
            addError(table, idColumnMeta, "should be 'auto_increment'");
        }
    }

    protected void checkCreateTimeField(Table table) {
        ColumnMeta createTimeColumnMeta = getColumnMeta(table, CREATE_TIME);
        if (createTimeColumnMeta == null) {
            return;
        }

        checkNotNull(table, createTimeColumnMeta);
        checkDatetimeField(table, createTimeColumnMeta);
    }

    protected void checkModifyTimeField(Table table) {
        ColumnMeta modifyTimeColumnMeta = getColumnMeta(table, MODIFY_TIME);
        if (modifyTimeColumnMeta == null) {
            return;
        }

        checkNotNull(table, modifyTimeColumnMeta);
        checkDatetimeField(table, modifyTimeColumnMeta);
        if (!modifyTimeColumnMeta.getExtra().toLowerCase().contains(UPDATE_WITH_CURRENT_TIMESTAMP)) {
            addError(table, modifyTimeColumnMeta, "need set to 'on update current_timestamp'");
        }
    }

    protected void checkDeletedField(Table table) {
        ColumnMeta deletedColumnMeta = getColumnMeta(table, DELETED);
        if (deletedColumnMeta == null) {
            return;
        }

        checkNotNull(table, deletedColumnMeta);
        if (!MysqlType.TINYINT.getName().equalsIgnoreCase(deletedColumnMeta.getDataType())) {
            addError(table, deletedColumnMeta, "should be type of 'tinyint'");
        }
    }

    protected void checkDeleteVersionField(Table table) {
        ColumnMeta deleteVersionColumnMeta = getColumnMeta(table, DELETE_VERSION);
        if (deleteVersionColumnMeta == null) {
            return;
        }

        checkNotNull(table, deleteVersionColumnMeta);
        if (!MysqlType.BIGINT.getName().equalsIgnoreCase(deleteVersionColumnMeta.getDataType())) {
            addError(table, deleteVersionColumnMeta, "should be type of 'bigint'");
        }
        if (!"0".equalsIgnoreCase(deleteVersionColumnMeta.getColumnDefault())) {
            addError(table, deleteVersionColumnMeta, "default value should be '0'");
        }
    }

    protected void checkNotNull(Table table, ColumnMeta columnMeta) {
        if (columnMeta.isNullable()) {
            addError(table, columnMeta, "should be 'not null'");
        }
    }

    private ColumnMeta getColumnMeta(Table table, String columnName) {
        ColumnMeta columnMeta = table.getColumnMeta(columnName);
        if (columnMeta == null) {
            addMissingFieldError(table, columnName);
            return null;
        }
        return columnMeta;
    }

    private void checkDatetimeField(Table table, ColumnMeta columnMeta) {
        if (!MysqlType.DATETIME.getName().equalsIgnoreCase(columnMeta.getDataType())) {
            addError(table, columnMeta, "should be type of 'datetime'");
        }
        if (!CURRENT_TIMESTAMP.equalsIgnoreCase(columnMeta.getColumnDefault())) {
            addError(table, columnMeta, "default value should be 'current_timestamp'");
        }
    }

    private void addMissingFieldError(Table table, String columnName) {
        table.addError(TableConstraintException.ofColumnNotExist(table.getTableName(), columnName));
    }

    private void addError(Table table, ColumnMeta columnMeta, String errorMsg) {
        addError(table, columnMeta.getColumnName(), errorMsg);
    }

    private void addError(Table table, String columnName, String errorMsg) {
        table.addError(new TableConstraintException(table.getTableName(), columnName, errorMsg));
    }

}
