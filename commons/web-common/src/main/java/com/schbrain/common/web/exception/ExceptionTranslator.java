package com.schbrain.common.web.exception;

import com.schbrain.common.web.result.ResponseDTO;
import org.springframework.core.Ordered;

/**
 * @author liaozan
 * @since 2023-06-01
 */
@FunctionalInterface
public interface ExceptionTranslator<T> extends Ordered {

    /**
     * Translate the exception to {@link ResponseDTO}
     */
    T translate(Throwable throwable, int code, int action, String message);

    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 100;
    }

}
