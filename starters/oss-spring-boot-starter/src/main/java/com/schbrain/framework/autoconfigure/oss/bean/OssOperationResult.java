package com.schbrain.framework.autoconfigure.oss.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author liaozan
 * @since 2021/12/19
 */
@Data
public class OssOperationResult implements Serializable {

    private static final long serialVersionUID = 3651584115463313214L;

    protected boolean success;

    protected String bucket;

    protected String objectKey;

    protected String errorMsg;

    public boolean isFailed() {
        return Boolean.FALSE.equals(success);
    }

}