package com.schbrain.framework.autoconfigure.oss.bean;

import com.aliyuncs.auth.sts.AssumeRoleResponse.Credentials;
import lombok.*;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * @author liaozan
 * @since 2021/12/4
 */
@Data
@NoArgsConstructor // for json deserialize
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UploadCredentials extends OssOperationResult {

    private String accessKeyId;

    private String accessKeySecret;

    private String securityToken;

    private LocalDateTime expiration;

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
