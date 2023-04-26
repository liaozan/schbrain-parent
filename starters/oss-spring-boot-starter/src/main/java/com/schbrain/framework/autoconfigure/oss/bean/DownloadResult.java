package com.schbrain.framework.autoconfigure.oss.bean;

import com.aliyun.oss.model.OSSObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liaozan
 * @since 2021/12/3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DownloadResult extends OssOperationResult {

    private static final long serialVersionUID = -9010079317621184645L;

    private OSSObject ossObject;

    public static DownloadResult success(String bucket, String objectKey, OSSObject ossObject) {
        DownloadResult result = new DownloadResult();
        result.setSuccess(true);
        result.setBucket(bucket);
        result.setObjectKey(objectKey);
        result.setOssObject(ossObject);
        return result;
    }

    public static DownloadResult fail(String bucket, String objectKey, String errorMsg) {
        DownloadResult result = new DownloadResult();
        result.setSuccess(false);
        result.setBucket(bucket);
        result.setObjectKey(objectKey);
        result.setErrorMsg(errorMsg);
        return result;
    }

}