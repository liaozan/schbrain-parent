package com.schbrain.common.web.exception;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.schbrain.common.exception.BaseException;
import com.schbrain.common.util.EnvUtils;
import com.schbrain.common.web.result.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.ClassUtils;
import org.springframework.validation.*;
import org.springframework.web.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.*;

import static com.schbrain.common.constants.ResponseCodeConstants.*;

/**
 * @author liaozan
 * @since 2019/10/14
 */
@Slf4j
@ResponseBody
@ResponseStatus(HttpStatus.OK)
public class DefaultGlobalExceptionHandler implements GlobalExceptionHandler {

    /*************************************  Base Exception Handing  *************************************/
    @ExceptionHandler(BaseException.class)
    public ResponseDTO<String> handleBaseException(BaseException ex) {
        logError(ex);
        return ResponseDTO.error(ex);
    }

    /*************************************  Common Exception Handing  *************************************/
    @ExceptionHandler(Throwable.class)
    public ResponseDTO<String> handleAll(Throwable ex) {
        return loggingThenBuildResponse(ex, SERVER_ERROR);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseDTO<String> handleNullPointerException(NullPointerException ex) {
        return loggingThenBuildResponse(ex, SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseDTO<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return loggingThenBuildResponse(ex, SERVER_ERROR);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseDTO<String> handleIllegalStateException(IllegalStateException ex) {
        return loggingThenBuildResponse(ex, SERVER_ERROR);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseDTO<String> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return loggingThenBuildResponse(ex, PARAM_INVALID);
    }

    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public ResponseDTO<String> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex) {
        return loggingThenBuildResponse(ex, SERVER_ERROR);
    }

    /************************************* SQL Exception Handing  *************************************/
    @ExceptionHandler(SQLException.class)
    public ResponseDTO<String> handleSQLException(SQLException ex) {
        return loggingThenBuildResponse(ex, SERVER_ERROR);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseDTO<String> handleDataAccessException(DataAccessException ex) {
        return loggingThenBuildResponse(ex, SERVER_ERROR);
    }

    /************************************* Http Request Exception Handing  *************************************/
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseDTO<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String errorMsg = StrUtil.format("不支持该HTTP方法: {}, 请使用 {}", ex.getMethod(), Arrays.toString(ex.getSupportedMethods()));
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseDTO<String> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        String errorMsg = StrUtil.format("不支持该媒体类型: {}, 请使用 {}", ex.getContentType(), ex.getSupportedMediaTypes());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseDTO<String> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
        String errorMsg = StrUtil.format("不支持的媒体类型, 请使用 {}", ex.getSupportedMediaTypes());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    /************************************* Method Parameter Exception Handing  *************************************/
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseDTO<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String errorMsg = StrUtil.format("参数解析失败, {}", ex.getMessage());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseDTO<String> handlerMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        Object value = ex.getValue();
        String variableName = ex.getName();
        Class<?> requiredTypeClass = ex.getRequiredType();
        String requiredType = ClassUtils.getQualifiedName(requiredTypeClass == null ? Object.class : requiredTypeClass);
        String providedType = ClassUtils.getDescriptiveType(value);
        String errorMsg = StrUtil.format("参数类型不匹配, 参数名: {}, 需要: {}, 传入: {} 的 {}", variableName, requiredType, providedType, value);
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseDTO<String> handleMissingPathVariableException(MissingPathVariableException ex) {
        String errorMsg = StrUtil.format("丢失路径参数, 参数名: {}, 参数类型: {}", ex.getVariableName(), ex.getParameter().getParameterType());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseDTO<String> handleMissingRequestCookieException(MissingRequestCookieException ex) {
        String errorMsg = StrUtil.format("丢失Cookie参数, 参数名: {}, 参数类型: {}", ex.getCookieName(), ex.getParameter().getParameterType());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseDTO<String> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        String errorMsg = StrUtil.format("丢失Header参数, 参数名: {}, 参数类型: {}", ex.getHeaderName(), ex.getParameter().getParameterType());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseDTO<String> handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
        String errorMsg = StrUtil.format("丢失参数: {}", ex.getRequestPartName());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseDTO<String> handleServletRequestParameterException(MissingServletRequestParameterException ex) {
        String errorMsg = StrUtil.format("丢失Query参数, 参数名: {}, 参数类型: {}", ex.getParameterName(), ex.getParameterType());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseDTO<String> handleServletRequestBindingException(ServletRequestBindingException ex) {
        String errorMsg = StrUtil.format("参数绑定失败: {}", ex.getMessage());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    /*************************************  Parameter Binding Exception Handing *************************************/
    @ExceptionHandler(BindException.class)
    public ResponseDTO<String> handleBindException(BindException ex) {
        String errorMsg = buildBindingErrorMsg(ex.getBindingResult());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseDTO<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMsg = buildBindingErrorMsg(ex.getBindingResult());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseDTO<String> handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMsg = buildBindingErrorMsg(ex.getConstraintViolations());
        log.error(errorMsg);
        return buildResponse(ex, PARAM_INVALID, errorMsg);
    }

    protected ResponseDTO<String> loggingThenBuildResponse(Throwable throwable, int errorCode) {
        Throwable rootCause = ExceptionUtil.getRootCause(throwable);
        logError(rootCause);
        return buildResponse(rootCause, errorCode, rootCause.getMessage());
    }

    protected ResponseDTO<String> buildResponse(Throwable throwable, int code, String message) {
        boolean isProduction = EnvUtils.isProduction();
        ResponseDTO<String> responseDTO = getExceptionResponseMapping(throwable, isProduction);
        if (responseDTO != null) {
            return responseDTO;
        }
        if (isProduction || StringUtils.isBlank(message)) {
            return ResponseDTO.error("系统错误", code);
        }
        return ResponseDTO.error(message, code);
    }

    protected ResponseDTO<String> getExceptionResponseMapping(Throwable throwable, boolean isProduction) {
        return null;
    }

    protected String buildBindingErrorMsg(BindingResult bindingResult) {
        String prefix = "参数验证失败: ";
        StringJoiner joiner = new StringJoiner(", ");
        for (ObjectError error : bindingResult.getAllErrors()) {
            String errorMessage = Optional.ofNullable(error.getDefaultMessage()).orElse("验证失败");
            String source;
            if (error instanceof FieldError) {
                source = ((FieldError) error).getField();
            } else {
                source = error.getObjectName();
            }
            joiner.add(source + " " + errorMessage);
        }
        return prefix + joiner;
    }

    protected String buildBindingErrorMsg(Set<ConstraintViolation<?>> constraintViolations) {
        String prefix = "参数验证失败: ";
        StringJoiner joiner = new StringJoiner(", ");
        for (ConstraintViolation<?> violation : constraintViolations) {
            PathImpl propertyPath = (PathImpl) violation.getPropertyPath();
            joiner.add(propertyPath.asString() + " " + violation.getMessage());
        }
        return prefix + joiner;
    }

    protected void logError(Throwable throwable) {
        String exMsg = ExceptionUtil.getMessage(throwable);
        if (hasCause(throwable)) {
            exMsg = exMsg + ", " + ExceptionUtil.getRootCauseMessage(throwable);
            throwable = ExceptionUtil.getRootCause(throwable);
        }
        log.error(exMsg, throwable);
    }

    private boolean hasCause(Throwable throwable) {
        return throwable.getCause() != null;
    }

}