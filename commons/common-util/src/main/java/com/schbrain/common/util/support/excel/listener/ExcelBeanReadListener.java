package com.schbrain.common.util.support.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.metadata.holder.ReadSheetHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static com.schbrain.common.util.support.ValidationMessageBuilder.buildConstraintViolationErrorMsg;

/**
 * @author liaozan
 * @since 2022/1/7
 */
@Slf4j
public class ExcelBeanReadListener<T> extends ExcelReadListenerBase<T> {

    @Override
    protected boolean validate(T data, AnalysisContext context) {
        Set<ConstraintViolation<T>> violations = getValidator().validate(data);
        if (CollectionUtils.isEmpty(violations)) {
            return true;
        }
        collectErrorMsg(context, violations);
        return false;
    }

    protected void collectErrorMsg(AnalysisContext context, Set<ConstraintViolation<T>> violations) {
        ReadSheetHolder currentSheet = context.readSheetHolder();
        String sheetName = currentSheet.getSheetName();
        Integer rowIndex = currentSheet.getRowIndex();
        getErrors().put(sheetName, rowIndex + 1, buildConstraintViolationErrorMsg(violations));
    }

}
