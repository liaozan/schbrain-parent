package com.schbrain.framework.autoconfigure.oss.bean;

import lombok.Data;

/**
 * @author liaozan
 * @since 2021/12/19
 */
@Data
public class OssOperationResult {

    protected boolean success;

    protected String bucket;

    protected String objectKey;

    protected String errorMsg;

    public boolean isFailed() {
        return Boolean.FALSE.equals(success);
    }

}
