package com.schbrain.framework.autoconfigure.oss.bean;

import lombok.*;

/**
 * @author lik
 * @since 2022/9/6
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CopyResult extends OssOperationResult {

    private String destinationKey;

    private String destinationBucket;

    public static CopyResult success(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey) {
        CopyResult result = new CopyResult();
        result.setSuccess(true);
        result.setBucket(sourceBucket);
        result.setObjectKey(sourceKey);
        result.setDestinationBucket(destinationBucket);
        result.setDestinationKey(destinationKey);
        return result;
    }

    public static CopyResult fail(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey, String errorMsg) {
        CopyResult result = new CopyResult();
        result.setSuccess(false);
        result.setBucket(sourceBucket);
        result.setObjectKey(sourceKey);
        result.setDestinationBucket(destinationBucket);
        result.setDestinationKey(destinationKey);
        result.setErrorMsg(errorMsg);
        return result;
    }

}
