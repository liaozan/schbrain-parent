package com.schbrain.framework.autoconfigure.mybatis.constraint;

import com.schbrain.framework.autoconfigure.mybatis.exception.TableConstraintException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * @author liaozan
 * @since 2022/8/31
 */
@Slf4j
public class TableConstraintCheckFailureAnalyzer extends AbstractFailureAnalyzer<TableConstraintException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, TableConstraintException cause) {
        return new FailureAnalysis(cause.getMessage(), null, cause);
    }

}