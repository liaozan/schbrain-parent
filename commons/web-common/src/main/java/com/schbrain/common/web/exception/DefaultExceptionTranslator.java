package com.schbrain.common.web.exception;

import com.schbrain.common.exception.BaseException;
import com.schbrain.common.util.EnvUtils;
import com.schbrain.common.web.result.ResponseDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;

/**
 * @author liaozan
 * @since 2023-06-01
 */
public class DefaultExceptionTranslator implements ExceptionTranslator<ResponseDTO<String>> {

    private final boolean isProduction;

    public DefaultExceptionTranslator() {
        this.isProduction = EnvUtils.isProduction();
    }

    @Override
    public ResponseDTO<String> translate(Throwable throwable, int code, int action, String message) {
        if (throwable instanceof BaseException) {
            return ResponseDTO.error((BaseException) throwable);
        }
        if (isProduction || StringUtils.isBlank(message)) {
            return ResponseDTO.error("系统错误", code);
        }
        return ResponseDTO.error(message, code, action);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
