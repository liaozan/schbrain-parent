package com.schbrain.framework.autoconfigure.oss.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import com.aliyun.oss.*;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.*;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.auth.sts.AssumeRoleResponse.Credentials;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.schbrain.common.util.ApplicationName;
import com.schbrain.common.util.ValidateUtils;
import com.schbrain.framework.autoconfigure.oss.bean.*;
import com.schbrain.framework.autoconfigure.oss.exception.OssException;
import com.schbrain.framework.autoconfigure.oss.properties.OssProperties;
import com.schbrain.framework.autoconfigure.oss.properties.OssProperties.StsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liaozan
 * @since 2021/12/3
 */
@Slf4j
public class OssUtils {

    private static final DateTimeFormatter DATE_WITH_SLASH = DatePattern.createFormatter("yyyy/MM/dd");

    private static OSSClient ossClient;

    private static DefaultAcsClient stsAcsClient;

    private static OssProperties ossProperties;

    private static StsProperties stsProperties;

    private static String directory;

    public static void initialize(OssProperties properties) {
        if (properties == null || properties.isInValid()) {
            log.warn("ossProperties is invalid, OssUtils will not available until reinitialize with the correct configuration");
            return;
        }
        try {
            ossProperties = properties;
            ossClient = (OSSClient) initOssClient(properties);
            stsAcsClient = initStsAcsClient(properties);
            stsProperties = properties.getSts();
            directory = properties.getDirectory();
            if (directory == null) {
                directory = ApplicationName.get();
            }
        } catch (Exception e) {
            log.warn("oss initialize fail, OssUtils will not available until reinitialize with the correct configuration", e);
        }
    }

    public static OssProperties getOssProperties() {
        return ossProperties;
    }

    public static OSSClient getOssClient() {
        if (ossClient == null) {
            throw new OssException("Oss client is null");
        }
        return ossClient;
    }

    // upload file
    public static UploadResult upload(File file) {
        return upload(file, file.getName());
    }

    public static UploadResult upload(File file, String objectKey) {
        return upload(file, objectKey, false);
    }

    public static UploadResult upload(File file, String objectKey, boolean allowOverwrite) {
        return upload(file, objectKey, allowOverwrite, true);
    }

    public static UploadResult upload(File file, String objectKey, boolean allowOverwrite, boolean appendPrefix) {
        return upload(file, ossProperties.getBucketName(), objectKey, allowOverwrite, appendPrefix);
    }

    public static UploadResult upload(File file, String bucket, String objectKey, boolean allowOverwrite, boolean appendPrefix) {
        return upload(file, bucket, objectKey, allowOverwrite, appendPrefix, null);
    }

    public static UploadResult upload(File file, String bucket, String objectKey, boolean allowOverwrite, boolean appendPrefix, ObjectMetadata metadata) {
        return upload0(file, bucket, objectKey, allowOverwrite, appendPrefix, metadata);
    }

    // upload stream
    public static UploadResult upload(InputStream inputStream, String objectKey) {
        return upload(inputStream, objectKey, false);
    }

    public static UploadResult upload(InputStream inputStream, String objectKey, boolean appendPrefix) {
        return upload(inputStream, objectKey, false, appendPrefix);
    }

    public static UploadResult upload(InputStream inputStream, String objectKey, boolean allowOverwrite, boolean appendPrefix) {
        return upload(inputStream, ossProperties.getBucketName(), objectKey, allowOverwrite, appendPrefix);
    }

    public static UploadResult upload(InputStream inputStream, String bucket, String objectKey, boolean allowOverwrite, boolean appendPrefix) {
        return upload0(inputStream, bucket, objectKey, allowOverwrite, appendPrefix, null);
    }

    public static UploadResult upload(InputStream inputStream, String bucket, String objectKey, boolean allowOverwrite, boolean appendPrefix, ObjectMetadata metadata) {
        return upload0(inputStream, bucket, objectKey, allowOverwrite, appendPrefix, metadata);
    }

    public static DownloadResult download(String objectKey) {
        return download(ossProperties.getBucketName(), objectKey);
    }

    public static DownloadResult download(String bucket, String objectKey) {
        boolean exist = exist(bucket, objectKey);
        if (!exist) {
            throw new OssException(String.format("object [%s] does not exist in bucket [%s]", objectKey, bucket));
        }
        return download0(bucket, objectKey);
    }

    public static String generatePreSignedUrl(String objectKey) {
        return generatePreSignedUrl(objectKey, Duration.ofHours(1));
    }

    public static String generatePreSignedUrl(String objectKey, Duration expiration) {
        return generatePreSignedUrl(objectKey, expiration, true);
    }

    public static String generatePreSignedUrl(String objectKey, Duration expiration, boolean https) {
        return generatePreSignedUrl(ossProperties.getBucketName(), objectKey, expiration, https);
    }

    public static String generatePreSignedUrl(String bucket, String objectKey, Duration expiration, boolean https) {
        return generatePreSignedUrl(bucket, objectKey, expiration, https, null);
    }

    public static String generatePreSignedUrl(String bucket, String objectKey, Duration expiration, boolean https, ResponseHeaderOverrides headerOverrides) {
        LocalDateTime expirationTime = LocalDateTime.now().plus(expiration);
        Instant instant = expirationTime.atZone(ZoneId.systemDefault()).toInstant();

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, objectKey);
        request.setMethod(HttpMethod.GET);
        request.setExpiration(Date.from(instant));
        request.setResponseHeaders(headerOverrides);

        String preSignedUrl = getOssClient().generatePresignedUrl(request).toString();
        if (HttpUtil.isHttp(preSignedUrl) && https) {
            preSignedUrl = preSignedUrl.replaceFirst("http", "https");
        }
        return replaceWithDefaultDomain(preSignedUrl);
    }

    public static UploadCredentials generateUploadToken() {
        AssumeRoleRequest request = new AssumeRoleRequest();
        request.setSysMethod(MethodType.POST);
        request.setRoleArn(stsProperties.getRoleArn());
        request.setRoleSessionName(stsProperties.getRoleSessionName());
        request.setDurationSeconds(stsProperties.getDurationSeconds());
        AssumeRoleResponse response;
        try {
            response = getStsAcsClient().getAcsResponse(request);
        } catch (com.aliyuncs.exceptions.ClientException e) {
            return new UploadCredentials(e.getErrMsg());
        }
        Credentials credentials = response.getCredentials();
        return new UploadCredentials(credentials);
    }

    public static String buildUploadPath(String objectKey) {
        objectKey = removePossibleSlash(objectKey);
        String date = DATE_WITH_SLASH.format(LocalDate.now());
        return String.format("%s/%s/%s", directory, date, objectKey);
    }

    public static String withBucketPrefix(String objectKey) {
        return withBucketPrefix(objectKey, ossProperties.getBucketName());
    }

    public static String withBucketPrefix(String objectKey, String bucket) {
        // oss supports https by default
        String prefix = "https://" + bucket + "." + ossProperties.getEndpoint();
        return URLUtil.completeUrl(prefix, objectKey);
    }

    public static String replaceWithDefaultDomain(String ossUrl) {
        return replaceWithDomain(ossProperties.getDomain(), ossUrl);
    }

    public static String replaceWithDomain(String domain, String ossUrl) {
        if (!StringUtils.hasText(domain)) {
            return ossUrl;
        }
        domain = URLUtil.normalize(domain);
        URL originUrl = URLUtil.url(ossUrl);
        URL domainUrl = URLUtil.url(domain);
        try {
            return new URL(domainUrl.getProtocol(), domainUrl.getHost(), domainUrl.getPort(), originUrl.getPath()).toString();
        } catch (MalformedURLException e) {
            log.warn("replace domain fail, return the default url instead", e);
            return ossUrl;
        }
    }

    public static CopyResult copyObject(String sourceKey, String destinationKey) {
        return copyObject(ossProperties.getBucketName(), sourceKey, ossProperties.getBucketName(), destinationKey, false);
    }

    public static CopyResult copyObject(String sourceKey, String destinationBucket, String destinationKey) {
        return copyObject(ossProperties.getBucketName(), sourceKey, destinationBucket, destinationKey, false);
    }

    public static CopyResult copyObject(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey, boolean allowOverwrite) {
        ValidateUtils.notEmpty(destinationKey, "destinationKey can not be empty");
        ValidateUtils.notEmpty(destinationBucket, "destinationBucket can not be empty");
        ValidateUtils.notEmpty(sourceKey, "sourceKey can not be empty");
        ValidateUtils.notEmpty(sourceBucket, "sourceBucket can not be empty");

        boolean exist = exist(sourceBucket, sourceKey);
        if (!exist) {
            String errorMsg = StrFormatter.format("sourceKey:{} at sourceBucket:{} not exist", sourceKey, sourceBucket);
            return CopyResult.fail(sourceBucket, sourceKey, destinationBucket, destinationKey, errorMsg);
        }

        if (!allowOverwrite) {
            boolean isExist = exist(destinationBucket, destinationKey);
            if (isExist) {
                String errorMsg = StrFormatter.format("destinationKey:{} at destinationBucket:{} has already exist");
                return CopyResult.fail(sourceBucket, sourceKey, destinationBucket, destinationKey, errorMsg);
            }
        }

        ossClient.copyObject(sourceBucket, sourceKey, destinationBucket, destinationKey);
        return CopyResult.success(sourceBucket, sourceKey, destinationBucket, destinationKey);
    }

    public static DeleteResult deleteObject(String objectKey) {
        return deleteObject(List.of(objectKey));
    }

    public static DeleteResult deleteObject(List<String> objectKeys) {
        return deleteObject(ossProperties.getBucketName(), objectKeys);
    }

    public static DeleteResult deleteObject(String bucket, List<String> objectKeys) {
        ValidateUtils.notEmpty(bucket, "bucket can not be empty");
        ValidateUtils.notEmpty(objectKeys, "objectKeys can not be empty");
        String notExistKeys = objectKeys.stream().filter(key -> !exist(bucket, key)).collect(Collectors.joining(","));
        if (StringUtils.hasText(notExistKeys)) {
            String errorMsg = StrFormatter.format("objectKeys:[{}] not exist", notExistKeys);
            return DeleteResult.fail(bucket, objectKeys, errorMsg);
        }
        DeleteObjectsRequest request = new DeleteObjectsRequest(bucket).withKeys(objectKeys);
        DeleteObjectsResult deleteObjectsResult = getOssClient().deleteObjects(request);
        List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
        return DeleteResult.success(bucket, deletedObjects);
    }

    public static boolean exist(String key) {
        return exist(ossProperties.getBucketName(), key);
    }

    public static boolean exist(String bucket, String key) {
        return getOssClient().doesObjectExist(bucket, key);
    }

    private static DownloadResult download0(String bucket, String objectKey) {
        GetObjectRequest request = new GetObjectRequest(bucket, objectKey);
        OSSObject ossObject = getOssClient().getObject(request);
        return DownloadResult.success(bucket, objectKey, ossObject);
    }

    private static UploadResult upload0(Object object, String bucket, String objectKey, boolean allowOverwrite, boolean appendPrefix, ObjectMetadata metadata) {
        ValidateUtils.notEmpty(objectKey, "objectKey must not be empty");

        String uploadPath = objectKey;
        if (appendPrefix) {
            uploadPath = buildUploadPath(objectKey);
        }

        if (!allowOverwrite) {
            boolean exist = exist(bucket, uploadPath);
            if (exist) {
                String errorMsg = String.format("object with path [%s] already exist in bucket [%s]", uploadPath, bucket);
                return UploadResult.fail(bucket, objectKey, errorMsg);
            }
        }

        if (object instanceof File) {
            File fileToUpload = (File) object;
            if (fileToUpload.isDirectory()) {
                if (FileUtil.isDirEmpty(fileToUpload)) {
                    return UploadResult.fail(bucket, objectKey, "upload directory is empty");
                }
                String rootPath = FileUtil.getCanonicalPath(fileToUpload);
                List<File> fileList = FileUtil.loopFiles(fileToUpload);
                for (File file : fileList) {
                    String subPath = FileUtil.subPath(rootPath, file);
                    doUpload(file, bucket, uploadPath + "/" + subPath, metadata);
                }
                // upload directory has no url to return
                return UploadResult.success(bucket, null, null);
            }
        }

        return doUpload(object, bucket, uploadPath, metadata);
    }

    private static UploadResult doUpload(Object object, String bucket, String uploadPath, ObjectMetadata metadata) {
        return doUpload(createPutRequest(object, bucket, uploadPath, metadata));
    }

    private static UploadResult doUpload(PutObjectRequest request) {
        PutObjectResult result;
        String bucketName = request.getBucketName();
        String objectKey = request.getKey();
        try {
            result = getOssClient().putObject(request);
        } catch (OSSException | ClientException exception) {
            log.error("upload object to oss fail", exception);
            return UploadResult.fail(bucketName, objectKey, exception.getMessage());
        }

        ResponseMessage response = result.getResponse();
        if (response.isSuccessful()) {
            String urlWithBucket = withBucketPrefix(objectKey, bucketName);
            String finalUrl = replaceWithDefaultDomain(urlWithBucket);
            return UploadResult.success(bucketName, objectKey, finalUrl);
        }

        String errorMsg = response.getErrorResponseAsString();
        return UploadResult.fail(bucketName, objectKey, errorMsg);
    }

    private static DefaultAcsClient getStsAcsClient() {
        if (stsAcsClient == null) {
            throw new OssException("stsAcsClient is null");
        }
        return stsAcsClient;
    }

    private static OSS initOssClient(OssProperties ossProperties) {
        return new OSSClientBuilder().build(ossProperties.getEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getSecretAccessKey());
    }

    private static DefaultAcsClient initStsAcsClient(OssProperties ossProperties) {
        StsProperties stsProperties = ossProperties.getSts();
        DefaultProfile.addEndpoint("", "Sts", stsProperties.getEndpoint());
        IClientProfile profile = DefaultProfile.getProfile("", ossProperties.getAccessKeyId(), ossProperties.getSecretAccessKey());
        return new DefaultAcsClient(profile);
    }

    private static String removePossibleSlash(String objectKey) {
        while (objectKey.startsWith("/")) {
            objectKey = objectKey.substring(1);
        }
        return objectKey;
    }

    private static PutObjectRequest createPutRequest(Object object, String bucket, String objectKey, ObjectMetadata metadata) {
        PutObjectRequest request;
        if (object instanceof InputStream) {
            request = new PutObjectRequest(bucket, objectKey, (InputStream) object, metadata);
        } else if (object instanceof File) {
            request = new PutObjectRequest(bucket, objectKey, (File) object, metadata);
        } else {
            throw new OssException("unknown upload object type");
        }
        request.setProcess("");
        return request;
    }

}
