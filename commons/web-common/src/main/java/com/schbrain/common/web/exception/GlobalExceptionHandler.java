package com.schbrain.common.web.exception;

import static com.schbrain.common.constants.ResponseCodeConstants.PARAM_INVALID;
import static com.schbrain.common.constants.ResponseCodeConstants.SERVER_ERROR;
import static com.schbrain.common.util.support.ValidationMessageBuilder.buildBindingErrorMsg;
import static com.schbrain.common.util.support.ValidationMessageBuilder.buildConstraintViolationErrorMsg;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.schbrain.common.constants.ResponseActionConstants;
import com.schbrain.common.exception.BaseException;
import com.schbrain.common.web.result.ResponseDTO;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * @author liaozan
 * @since 2019/10/14
 */
@Slf4j
@ResponseBody
@ResponseStatus(HttpStatus.OK)
public class GlobalExceptionHandler {

    private final List<ExceptionTranslator<?>> exceptionTranslators;

    public GlobalExceptionHandler(List<ExceptionTranslator<?>> exceptionTranslators) {
        this.exceptionTranslators = exceptionTranslators;
    }

    /*************************************  Base Exception Handing  *************************************/
    @ExceptionHandler(BaseException.class)
    public Object handleBaseException(BaseException ex) {
        logError(ex);
        return buildResponse(ex, ex.getCode(), ex.getAction(), ex.getMessage());
    }

    /*************************************  Common Exception Handing  *************************************/
    @ExceptionHandler(Throwable.class)
    public Object handleAll(Throwable ex) {
        return loggingThenBuildResponse(ex, SERVER_ERROR);
    }

    @ExceptionHandler(NullPointerException.class)
    public Object handleNullPointerException(NullPointerException ex) {
        return loggingThenBuildResponse(ex, SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgumentException(IllegalArgumentException ex) {
        return loggingThenBuildResponse(ex, SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalStateException.class)
    public Object handleIllegalStateException(IllegalStateException ex) {
        return loggingThenBuildResponse(ex, SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return loggingThenBuildResponse(ex, PARAM_INVALID);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public Object handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex) {
        return loggingThenBuildResponse(ex, SERVER_ERROR);
    }

    /************************************* SQL Exception Handing  *************************************/
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SQLException.class)
    public Object handleSQLException(SQLException ex) {
        return loggingThenBuildResponse(ex, SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataAccessException.class)
    public Object handleDataAccessException(DataAccessException ex) {
        return loggingThenBuildResponse(ex, SERVER_ERROR);
    }

    /************************************* Http Request Exception Handing  *************************************/
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String errorMsg = StrUtil.format("不支持该HTTP方法: {}, 请使用 {}", ex.getMethod(), Arrays.toString(ex.getSupportedMethods()));
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Object handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        String errorMsg = StrUtil.format("不支持该媒体类型: {}, 请使用 {}", ex.getContentType(), ex.getSupportedMediaTypes());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public Object handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
        String errorMsg = StrUtil.format("不支持的媒体类型, 请使用 {}", ex.getSupportedMediaTypes());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    /************************************* Method Parameter Exception Handing  *************************************/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String errorMsg = StrUtil.format("参数解析失败, {}", ex.getMessage());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Object handlerMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        Object value = ex.getValue();
        String variableName = ex.getName();
        Class<?> requiredTypeClass = ex.getRequiredType();
        String requiredType = ClassUtils.getQualifiedName(requiredTypeClass == null ? Object.class : requiredTypeClass);
        String providedType = ClassUtils.getDescriptiveType(value);
        String errorMsg = StrUtil.format("参数类型不匹配, 参数名: {}, 需要: {}, 传入: {} 的 {}", variableName, requiredType, providedType, value);
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingPathVariableException.class)
    public Object handleMissingPathVariableException(MissingPathVariableException ex) {
        String errorMsg = StrUtil.format("丢失路径参数, 参数名: {}, 参数类型: {}", ex.getVariableName(), ex.getParameter().getParameterType());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestCookieException.class)
    public Object handleMissingRequestCookieException(MissingRequestCookieException ex) {
        String errorMsg = StrUtil.format("丢失Cookie参数, 参数名: {}, 参数类型: {}", ex.getCookieName(), ex.getParameter().getParameterType());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestHeaderException.class)
    public Object handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        String errorMsg = StrUtil.format("丢失Header参数, 参数名: {}, 参数类型: {}", ex.getHeaderName(), ex.getParameter().getParameterType());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestPartException.class)
    public Object handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
        String errorMsg = StrUtil.format("丢失参数: {}", ex.getRequestPartName());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Object handleServletRequestParameterException(MissingServletRequestParameterException ex) {
        String errorMsg = StrUtil.format("丢失Query参数, 参数名: {}, 参数类型: {}", ex.getParameterName(), ex.getParameterType());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServletRequestBindingException.class)
    public Object handleServletRequestBindingException(ServletRequestBindingException ex) {
        String errorMsg = StrUtil.format("参数绑定失败: {}", ex.getMessage());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    /*************************************  Parameter Binding Exception Handing *************************************/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public Object handleBindException(BindException ex) {
        String errorMsg = buildBindingErrorMsg(ex.getBindingResult());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Object handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMsg = buildConstraintViolationErrorMsg(ex.getConstraintViolations());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    private Object loggingThenBuildResponse(Throwable throwable, int code) {
        Throwable rootCause = ExceptionUtil.getRootCause(throwable);
        logError(rootCause);
        return buildResponse(rootCause, code, rootCause.getMessage());
    }

    private Object buildResponse(Throwable throwable, int code, String message) {
        return buildResponse(throwable, code, ResponseActionConstants.ALERT, message);
    }

    private Object buildResponse(Throwable throwable, int code, int action, String message) {
        Object translated = translateException(throwable, code, action, message);
        if (translated != null) {
            return translated;
        }
        // fallback
        return ResponseDTO.error(message, code, action);
    }

    private Object translateException(Throwable throwable, int code, int action, String message) {
        for (ExceptionTranslator<?> exceptionTranslator : exceptionTranslators) {
            Object translated = exceptionTranslator.translate(throwable, code, action, message);
            if (translated != null) {
                return translated;
            }
        }
        return null;
    }

    private void logError(Throwable throwable) {
        String exMsg = ExceptionUtil.getMessage(throwable);
        log.error(exMsg, throwable);
    }

}
