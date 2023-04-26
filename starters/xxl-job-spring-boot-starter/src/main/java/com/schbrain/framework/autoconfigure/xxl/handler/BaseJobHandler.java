package com.schbrain.framework.autoconfigure.xxl.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.schbrain.common.util.JacksonUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.log.XxlJobLogger;

/**
 * @author liaozan
 * @since 2022/8/23
 */
public abstract class BaseJobHandler extends IJobHandler {

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        JsonNode jsonNode = JacksonUtils.getJsonNode(param);
        return execute(jsonNode);
    }

    protected ReturnT<String> execute(JsonNode param) throws Exception {
        XxlJobLogger.log("{} does not implement the execute method, so return success directly", getClass());
        return success();
    }

    protected ReturnT<String> success() {
        return ReturnT.SUCCESS;
    }

    protected ReturnT<String> failed(String errMsg) {
        ReturnT<String> result = ReturnT.FAIL;
        result.setMsg(errMsg);
        return result;
    }

}