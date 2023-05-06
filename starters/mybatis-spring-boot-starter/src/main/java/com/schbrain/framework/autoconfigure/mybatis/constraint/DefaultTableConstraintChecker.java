package com.schbrain.framework.autoconfigure.mybatis.constraint;

import com.mysql.cj.MysqlType;
import com.schbrain.framework.autoconfigure.mybatis.biz.BizIdHelper;
import com.schbrain.framework.autoconfigure.mybatis.core.BizIdColumnField;
import com.schbrain.framework.autoconfigure.mybatis.exception.TableConstraintException;

import static com.schbrain.framework.autoconfigure.mybatis.constant.MybatisConstants.*;

/**
 * @author liaozan
 * @since 2022/8/30
 */
@SuppressWarnings("DuplicatedCode")
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
        ColumnMeta columnMeta = table.getColumnMeta(bizColumnField.getColumnName());
        if (columnMeta == null) {
            addMissingFieldError(table, bizColumnField.getColumnName());
            return;
        }
        if (columnMeta.getIndexName() == null) {
            addError(table, columnMeta, "BizId field should create an index");
        }
    }

    protected void checkIdField(Table table) {
        ColumnMeta idColumnMeta = table.getColumnMeta(ID);
        if (idColumnMeta == null) {
            addMissingFieldError(table, ID);
            return;
        }

        checkNullable(table, idColumnMeta);
        if (!MysqlType.BIGINT.getName().equalsIgnoreCase(idColumnMeta.getDataType())) {
            addError(table, idColumnMeta, "should be type of 'bigint'");
        }
        if (!AUTO_INCREMENT.equalsIgnoreCase(idColumnMeta.getExtra())) {
            addError(table, idColumnMeta, "should be 'auto_increment'");
        }
    }

    protected void checkCreateTimeField(Table table) {
        ColumnMeta createTimeColumnMeta = table.getColumnMeta(CREATE_TIME);
        if (createTimeColumnMeta == null) {
            addMissingFieldError(table, CREATE_TIME);
            return;
        }

        checkNullable(table, createTimeColumnMeta);
        if (!MysqlType.DATETIME.getName().equalsIgnoreCase(createTimeColumnMeta.getDataType())) {
            addError(table, createTimeColumnMeta, "should be type of 'datetime'");
        }
        if (!CURRENT_TIMESTAMP.equalsIgnoreCase(createTimeColumnMeta.getColumnDefault())) {
            addError(table, createTimeColumnMeta, "default value should be 'current_timestamp'");
        }
    }

    protected void checkModifyTimeField(Table table) {
        ColumnMeta modifyTimeColumnMeta = table.getColumnMeta(MODIFY_TIME);
        if (modifyTimeColumnMeta == null) {
            addMissingFieldError(table, MODIFY_TIME);
            return;
        }

        checkNullable(table, modifyTimeColumnMeta);
        if (!MysqlType.DATETIME.getName().equalsIgnoreCase(modifyTimeColumnMeta.getDataType())) {
            addError(table, modifyTimeColumnMeta, "should be type of 'datetime'");
        }
        if (!CURRENT_TIMESTAMP.equalsIgnoreCase(modifyTimeColumnMeta.getColumnDefault())) {
            addError(table, modifyTimeColumnMeta, "default value should be 'current_timestamp'");
        }
        if (!modifyTimeColumnMeta.getExtra().toLowerCase().contains(UPDATE_WITH_CURRENT_TIMESTAMP)) {
            addError(table, modifyTimeColumnMeta, "need set to 'on update current_timestamp'");
        }
    }

    protected void checkDeletedField(Table table) {
        ColumnMeta deletedColumnMeta = table.getColumnMeta(DELETED);
        if (deletedColumnMeta == null) {
            addMissingFieldError(table, DELETED);
            return;
        }

        checkNullable(table, deletedColumnMeta);
        if (!MysqlType.TINYINT.getName().equalsIgnoreCase(deletedColumnMeta.getDataType())) {
            addError(table, deletedColumnMeta, "should be type of 'tinyint'");
        }
    }

    protected void checkDeleteVersionField(Table table) {
        ColumnMeta deleteVersionColumnMeta = table.getColumnMeta(DELETE_VERSION);
        if (deleteVersionColumnMeta == null) {
            addMissingFieldError(table, DELETE_VERSION);
            return;
        }

        checkNullable(table, deleteVersionColumnMeta);
        if (!MysqlType.BIGINT.getName().equalsIgnoreCase(deleteVersionColumnMeta.getDataType())) {
            addError(table, deleteVersionColumnMeta, "should be type of 'bigint'");
        }
        if (!"0".equalsIgnoreCase(deleteVersionColumnMeta.getColumnDefault())) {
            addError(table, deleteVersionColumnMeta, "default value should be '0'");
        }
    }

    protected void checkNullable(Table table, ColumnMeta columnMeta) {
        if (columnMeta.isNullable()) {
            addError(table, columnMeta, "should be 'not null'");
        }
    }

    private void addError(Table table, ColumnMeta columnMeta, String errorMsg) {
        addError(table, columnMeta.getColumnName(), errorMsg);
    }

    private void addMissingFieldError(Table table, String columnName) {
        addError(table, columnName, "not exist");
    }

    private void addError(Table table, String columnName, String errorMsg) {
        table.addError(new TableConstraintException(table.getTableName(), columnName, errorMsg));
    }

}