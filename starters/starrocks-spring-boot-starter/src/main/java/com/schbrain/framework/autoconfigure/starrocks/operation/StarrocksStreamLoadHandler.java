package com.schbrain.framework.autoconfigure.starrocks.operation;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import com.schbrain.common.exception.ParamInvalidException;
import com.schbrain.common.util.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author liaozan
 * @since 2023/11/27
 */
@Slf4j
public class StarrocksStreamLoadHandler {

    private final String streamLoadUrl;
    private final String username;
    private final String password;

    public StarrocksStreamLoadHandler(String streamLoadUrl, String username, String password) {
        this.streamLoadUrl = streamLoadUrl;
        this.username = username;
        this.password = password;
    }

    public <T> void upsertBatch(List<T> entityList, List<String> columns) {
        log.info("Starrocks upsert, dataSize: {}, sample data: {}", entityList.size(), entityList.get(0));

        String content = JacksonUtils.toJsonString(entityList);
        String upsertResult = createUpsertRequest(content, columns).execute().body();

        if (log.isDebugEnabled()) {
            log.debug("Starrocks streamLoad upsert result: {}", upsertResult);
        }

        checkResponse(upsertResult);
    }

    public <T> void deleteBatch(List<T> entityList) {
        log.info("Starrocks delete, dataSize: {}, sample data: {}", entityList.size(), entityList.get(0));
        String content = JacksonUtils.toJsonString(entityList);
        String deleteResult = createCommonRequest(content).header("columns", "__op='delete'").execute().body();

        if (log.isDebugEnabled()) {
            log.debug("Starrocks streamLoad delete result: {}", deleteResult);
        }

        checkResponse(deleteResult);
    }

    private HttpRequest createUpsertRequest(String content, List<String> columns) {
        HttpRequest request = createCommonRequest(content);
        if (CollectionUtils.isNotEmpty(columns)) {
            request.header("partial_update", Boolean.TRUE.toString());
            request.header("columns", String.join(",", columns));
        }
        return request;
    }

    private void checkResponse(String result) {
        StreamLoadResponse response = JacksonUtils.getObjectFromJson(result, StreamLoadResponse.class);
        if (response == null || response.isFailed()) {
            throw new ParamInvalidException(Optional.ofNullable(response).map(StreamLoadResponse::getMessage).orElse(result));
        }
    }

    private HttpRequest createCommonRequest(String content) {
        return HttpRequest.put(streamLoadUrl)
                .header("label", IdUtil.getSnowflakeNextIdStr())
                .header("strict_mode", Boolean.TRUE.toString())
                .header("Expect", "100-continue")
                .header("format", "json")
                .header("strip_outer_array", Boolean.TRUE.toString())
                .header("ignore_json_size", Boolean.TRUE.toString())
                .basicAuth(username, password)
                .setFollowRedirects(true)
                .body(content);
    }

}
