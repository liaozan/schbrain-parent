package com.schbrain.common.util.support.excel.listener;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.schbrain.common.util.support.excel.bean.ExcelReadResult;
import com.schbrain.common.util.support.excel.exception.ExcelException;
import lombok.*;

import javax.validation.Validator;
import java.util.*;

/**
 * @author liaozan
 * @since 2022/1/6
 */
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ExcelReadListenerBase<T> extends AnalysisEventListener<T> {

    protected final Validator validator = SpringUtil.getBean(Validator.class);
    protected final Map<Integer, String> headers = new HashMap<>();

    protected List<T> dataList = new LinkedList<>();
    protected Table<String, Integer, String> errors = HashBasedTable.create();

    protected boolean terminateOnValidateFail = false;

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        this.headers.putAll(headMap);
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        throw new ExcelException(exception.getMessage(), exception);
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        if (validate(data, context)) {
            this.dataList.add(data);
        } else {
            if (isTerminateOnValidateFail()) {
                throw new ExcelException(getErrorMsg());
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }

    public ExcelReadResult<T> getReadResult() {
        ExcelReadResult<T> readResult = new ExcelReadResult<>();
        readResult.setDataList(dataList);
        readResult.setHeadMap(headers);
        readResult.setErrors(errors);
        readResult.setErrorsAsString(getErrorMsg());
        return readResult;
    }

    protected String getErrorMsg() {
        StringBuilder msgBuilder = new StringBuilder();
        errors.rowMap().forEach((sheetName, rows) -> {
            msgBuilder.append("sheet: [ ").append(sheetName).append(" ] ");
            rows.forEach((rowIndex, error) -> {
                String formattedErrorMsg = String.format("第%d行: [ %s ] ", rowIndex, error);
                msgBuilder.append(formattedErrorMsg);
            });
            msgBuilder.append("\n");
        });
        return msgBuilder.toString();
    }

    protected boolean validate(T data, AnalysisContext context) {
        return true;
    }

}
