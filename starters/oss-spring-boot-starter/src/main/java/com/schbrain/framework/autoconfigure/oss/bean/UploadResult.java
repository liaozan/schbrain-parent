package com.schbrain.framework.autoconfigure.oss.bean;

import lombok.*;

/**
 * @author liaozan
 * @since 2021/12/3
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UploadResult extends OssOperationResult {

    private String url;

    public static UploadResult success(String bucket, String objectKey, String url) {
        UploadResult result = new UploadResult();
        result.setSuccess(true);
        result.setBucket(bucket);
        result.setObjectKey(objectKey);
        result.setUrl(url);
        return result;
    }

    public static UploadResult fail(String bucket, String objectKey, String errorMsg) {
        UploadResult result = new UploadResult();
        result.setSuccess(false);
        result.setBucket(bucket);
        result.setObjectKey(objectKey);
        result.setErrorMsg(errorMsg);
        return result;
    }

}
