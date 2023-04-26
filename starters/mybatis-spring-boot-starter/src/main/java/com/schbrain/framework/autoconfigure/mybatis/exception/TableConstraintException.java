package com.schbrain.framework.autoconfigure.mybatis.exception;

import cn.hutool.core.text.StrFormatter;
import com.schbrain.common.exception.BaseException;
import com.schbrain.common.util.StreamUtils;
import lombok.Getter;

import java.util.List;

/**
 * @author liaozan
 * @since 2022/8/30
 */
@Getter
public class TableConstraintException extends BaseException {

    private static final long serialVersionUID = -3139175416089223586L;

    public TableConstraintException(String message, Object... args) {
        super(StrFormatter.format(message, args));
    }

    public TableConstraintException(String tableName, String column, String message) {
        super("Table: '" + tableName + "' , Column: '" + column + "' " + message);
    }

    public TableConstraintException(List<TableConstraintException> errors) {
        super(StreamUtils.join(StreamUtils.toList(errors, Throwable::getMessage), System.lineSeparator()));
    }

}