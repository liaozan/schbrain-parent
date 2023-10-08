package com.schbrain.framework.autoconfigure.oss.bean;

import lombok.*;

import java.util.List;

/**
 * @author lik
 * @since 2022/9/6
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DeleteResult extends OssOperationResult {

    private List<String> deleteObjectKeys;

    public static DeleteResult success(String bucket, List<String> objectKeys) {
        DeleteResult result = new DeleteResult();
        result.setSuccess(true);
        result.setBucket(bucket);
        result.setDeleteObjectKeys(objectKeys);
        return result;
    }

    public static DeleteResult fail(String bucket, List<String> objectKeys, String errorMsg) {
        DeleteResult result = new DeleteResult();
        result.setSuccess(false);
        result.setBucket(bucket);
        result.setErrorMsg(errorMsg);
        result.setDeleteObjectKeys(objectKeys);
        return result;
    }

}
