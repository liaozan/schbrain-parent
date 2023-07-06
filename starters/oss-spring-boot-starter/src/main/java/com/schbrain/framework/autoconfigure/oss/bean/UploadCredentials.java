package com.schbrain.framework.autoconfigure.oss.bean;

import com.aliyuncs.auth.sts.AssumeRoleResponse.Credentials;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author liaozan
 * @since 2021/12/4
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UploadCredentials extends OssOperationResult {

    private static final long serialVersionUID = 5546792221041679671L;

    private String accessKeyId;

    private String accessKeySecret;

    private String securityToken;

    private LocalDateTime expiration;

    // for json deserialize
    public UploadCredentials() {

    }

    public UploadCredentials(Credentials credentials) {
        this.success = true;
        this.accessKeyId = credentials.getAccessKeyId();
        this.accessKeySecret = credentials.getAccessKeySecret();
        this.securityToken = credentials.getSecurityToken();
        // example: 2021-12-04T11:03:37Z
        this.expiration = LocalDateTime.parse(credentials.getExpiration(), DateTimeFormatter.ISO_DATE_TIME)
                .toInstant(ZoneOffset.UTC)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public UploadCredentials(String errorMsg) {
        this.success = false;
        this.errorMsg = errorMsg;
    }

}