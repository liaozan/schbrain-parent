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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    protected List<T> dataList = new LinkedList<>();

    protected Map<Integer, String> headers = new HashMap<>();

    protected Table<String, Integer, String> errors = HashBasedTable.create();

    protected boolean terminateOnValidateFail = false;

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        this.headers = headMap;
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        throw new ExcelException(exception.getMessage(), exception);
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        boolean validated = validate(data, context);
        if (!validated) {
            if (isTerminateOnValidateFail()) {
                throw new ExcelException(getErrorMsg());
            }
        }
        this.dataList.add(data);
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