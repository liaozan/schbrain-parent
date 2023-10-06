package com.schbrain.framework.autoconfigure.dubbo.filter;

import com.schbrain.common.util.TraceIdUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * @author liaozan
 * @since 2021/10/10
 */
@Activate(group = CommonConstants.CONSUMER)
public class TraceConsumerFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String traceId = TraceIdUtils.get();
        RpcContext.getClientAttachment().setAttachment(TraceIdUtils.TRACE_ID, traceId);
        return invoker.invoke(invocation);
    }

}
