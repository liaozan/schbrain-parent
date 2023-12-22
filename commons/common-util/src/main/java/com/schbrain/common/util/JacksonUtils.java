package com.schbrain.common.util;

import cn.hutool.core.lang.Singleton;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.NullNode;
import com.schbrain.common.util.exception.JSONException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.ParameterizedType;
import java.util.*;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

/**
 * @author liaozan
 * @since 2021/10/12
 */
@Slf4j
public class JacksonUtils {

    private static ObjectMapper OBJECT_MAPPER;

    private static ObjectMapper PRETTY_OBJECT_MAPPER;

    public static ObjectMapper getObjectMapper() {
        return Singleton.get(JacksonUtils.class.getName(), () -> {
            if (OBJECT_MAPPER == null) {
                // Delay to get ObjectMapper from spring container to keep the same behavior with application
                try {
                    OBJECT_MAPPER = SpringUtil.getBean(ObjectMapper.class).copy();
                } catch (Exception e) {
                    log.warn("Could not get ObjectMapper from Spring Container, return new instance for use");
                    OBJECT_MAPPER = new ObjectMapper();
                }
                OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            }
            return OBJECT_MAPPER;
        });
    }

    public static ObjectMapper getPrettyObjectMapper() {
        if (PRETTY_OBJECT_MAPPER == null) {
            PRETTY_OBJECT_MAPPER = getObjectMapper().copy().enable(INDENT_OUTPUT);
        }
        return PRETTY_OBJECT_MAPPER;
    }

    public static String toJsonString(Object data) {
        return toJsonString(data, false);
    }

    public static String toPrettyJsonString(Object data) {
        return toJsonString(data, true);
    }

    public static String toJsonString(Object data, boolean pretty) {
        if (data == null) {
            return null;
        }
        try {
            if (pretty) {
                return getPrettyObjectMapper().writeValueAsString(data);
            }
            return getObjectMapper().writeValueAsString(data);
        } catch (Exception e) {
            throw new JSONException("Object 转 JSON 出错", e);
        }
    }

    public static JsonNode getJsonNode(String json) {
        try {
            if (StringUtils.isBlank(json)) {
                return NullNode.getInstance();
            }
            return getObjectMapper().readTree(json);
        } catch (Exception e) {
            throw new JSONException("JSON 转 JsonNode 出错", e);
        }
    }

    public static <T> T getObjectFromBytes(byte[] bytes, Class<T> type) {
        if (ArrayUtils.isEmpty(bytes)) {
            return null;
        }
        try {
            return getObjectMapper().readValue(bytes, type);
        } catch (Exception e) {
            throw new JSONException("Byte 转 Object 出错", e);
        }
    }

    public static <T> T getObjectFromBytes(byte[] bytes, ParameterizedType type) {
        if (ArrayUtils.isEmpty(bytes)) {
            return null;
        }
        try {
            JavaType valueType = getObjectMapper().getTypeFactory().constructType(type);
            return getObjectMapper().readValue(bytes, valueType);
        } catch (Exception e) {
            throw new JSONException("Byte 转 Object 出错", e);
        }
    }

    public static <T> T getObjectFromBytes(byte[] bytes, TypeReference<T> typeReference) {
        if (ArrayUtils.isEmpty(bytes)) {
            return null;
        }
        try {
            return getObjectMapper().readValue(bytes, typeReference);
        } catch (Exception e) {
            throw new JSONException("Byte 转 Object 出错", e);
        }
    }

    public static <T> T getObjectFromBytesWithTypeConstruct(byte[] bytes, Class<T> genericsType, Class<?>... innerTypes) {
        if (ArrayUtils.isEmpty(bytes)) {
            return null;
        }
        try {
            JavaType valueType = constructType(genericsType, innerTypes);
            return getObjectMapper().readValue(bytes, valueType);
        } catch (Exception e) {
            throw new JSONException("Byte 转 Object 出错", e);
        }
    }

    public static <T> T getObjectFromJson(String json, Class<T> type) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return getObjectMapper().readValue(json, type);
        } catch (Exception e) {
            throw new JSONException("JSON 转 Object 出错", e);
        }
    }

    public static <T> T getObjectFromJson(String json, ParameterizedType type) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            JavaType valueType = getObjectMapper().getTypeFactory().constructType(type);
            return getObjectMapper().readValue(json, valueType);
        } catch (Exception e) {
            throw new JSONException("JSON 转 Object 出错", e);
        }
    }

    public static <T> T getObjectFromJson(String json, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return getObjectMapper().readValue(json, typeReference);
        } catch (Exception e) {
            throw new JSONException("JSON 转 Object 出错", e);
        }
    }

    public static <T> T getObjectFromJsonWithTypeConstruct(String json, Class<T> genericsType, Class<?>... innerTypes) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            JavaType valueType = constructType(genericsType, innerTypes);
            return getObjectMapper().readValue(json, valueType);
        } catch (Exception e) {
            throw new JSONException("JSON 转 Object 出错", e);
        }
    }

    public static Map<String, Object> getMapFromJson(String json) {
        return getMapFromJson(json, Object.class);
    }

    public static <V> Map<String, V> getMapFromJson(String json, Class<V> valueType) {
        return getMapFromJson(json, String.class, valueType);
    }

    public static <K, V> Map<K, V> getMapFromJson(String json, Class<K> keyType, Class<V> valueType) {
        if (StringUtils.isBlank(json)) {
            return new LinkedHashMap<>();
        }
        try {
            JavaType mapType = getObjectMapper().getTypeFactory().constructMapType(LinkedHashMap.class, keyType, valueType);
            return getObjectMapper().readValue(json, mapType);
        } catch (Exception e) {
            throw new JSONException("JSON 转 Map 出错", e);
        }
    }

    public static <T> List<T> getListFromJson(String json, Class<T> itemType) {
        if (StringUtils.isBlank(json)) {
            return new ArrayList<>();
        }
        try {
            JavaType listType = getObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, itemType);
            return getObjectMapper().readValue(json, listType);
        } catch (Exception e) {
            throw new JSONException("JSON 转 List 出错", e);
        }
    }

    public static byte[] writeObjectAsBytes(Object data) {
        try {
            return getObjectMapper().writeValueAsBytes(data);
        } catch (Exception e) {
            throw new JSONException("Object 转 Byte 出错", e);
        }
    }

    public static JavaType constructType(Class<?> genericsType, Class<?>... innerTypes) {
        return getObjectMapper().getTypeFactory().constructType(TypeUtils.parameterize(genericsType, innerTypes));
    }

    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return convertValue(fromValue, constructType(toValueType));
    }

    public static <T> T convertValue(Object fromValue, JavaType toValueType) {
        try {
            return getObjectMapper().convertValue(fromValue, toValueType);
        } catch (Exception e) {
            throw new JSONException("JSON 转换出错", e);
        }
    }

}
