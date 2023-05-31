package com.schbrain.framework.autoconfigure.dubbo.filter;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.schbrain.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.filter.ExceptionFilter;
import org.apache.dubbo.rpc.service.GenericService;

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
        appResponse.setException(cause);
        logErrorDetail(invoker, cause);

        if (cause instanceof BaseException) {
            return;
        }

        super.onResponse(appResponse, invoker, invocation);
    }

    private void logErrorDetail(Invoker<?> invoker, Throwable exception) {
        RpcServiceContext context = RpcContext.getCurrentServiceContext();
        String arguments = Arrays.toString(context.getArguments());
        String serviceName = invoker.getInterface().getSimpleName();
        String methodName = context.getMethodName();
        String remoteHost = context.getRemoteHost();
        String remoteApplication = context.getRemoteApplicationName();
        String errorMessage = ExceptionUtil.getMessage(exception);
        log.error("Catch rpc exception: {}, client: {}@{}, target: {}#{}, args: {}", errorMessage, remoteApplication, remoteHost, serviceName, methodName, arguments, exception);
    }

}
