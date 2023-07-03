package com.schbrain.framework.autoconfigure.dubbo.filter;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.schbrain.common.exception.BaseException;
import com.schbrain.common.exception.ParamInvalidException;
import com.schbrain.common.util.support.ValidationMessageBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.filter.ExceptionFilter;
import org.apache.dubbo.rpc.service.GenericService;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;

/**
 * @author liaozan
 * @since 2022/1/19
 */
@Slf4j
@Activate(group = CommonConstants.PROVIDER, order = 1)
public class DubboExceptionFilter extends ExceptionFilter {

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
        if (!appResponse.hasException() || GenericService.class == invoker.getInterface()) {
            return;
        }

        Throwable cause = ExceptionUtil.getRootCause(appResponse.getException());

        if (cause instanceof ConstraintViolationException) {
            cause = createParamInvalidException(invocation, (ConstraintViolationException) cause);
        }

        appResponse.setException(cause);
        logErrorDetail(invocation, cause);

        if (cause instanceof BaseException) {
            return;
        }

        super.onResponse(appResponse, invoker, invocation);
    }

    protected ParamInvalidException createParamInvalidException(Invocation invocation, ConstraintViolationException cause) {
        String serviceName = invocation.getInvoker().getInterface().getSimpleName();
        String methodName = invocation.getMethodName();
        String errorMsg = ValidationMessageBuilder.buildConstraintViolationErrorMsg(cause.getConstraintViolations());
        return new ParamInvalidException(String.format("%s.%s %s", serviceName, methodName, errorMsg));
    }

    protected void logErrorDetail(Invocation invocation, Throwable exception) {
        RpcServiceContext context = RpcContext.getCurrentServiceContext();
        String serviceName = invocation.getInvoker().getInterface().getSimpleName();
        String methodName = invocation.getMethodName();
        String arguments = Arrays.toString(context.getArguments());
        String remoteHost = context.getRemoteHost();
        String remoteApplication = context.getRemoteApplicationName();
        String errorMessage = ExceptionUtil.getMessage(exception);
        log.error("Catch rpc exception: {}, client: {}@{}, target: {}#{}, args: {}", errorMessage, remoteApplication, remoteHost, serviceName, methodName, arguments, exception);
    }

}