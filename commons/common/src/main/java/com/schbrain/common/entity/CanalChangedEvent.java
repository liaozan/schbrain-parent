package com.schbrain.common.entity;

import lombok.*;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author liaozan
 * @since 2023/12/6
 */
@Data
public class CanalChangedEvent {

    /**
     * 库名
     */
    private String schemaName;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 变更类型
     */
    private CanalEventType eventType;

    /**
     * 变更前的数据
     */
    private Map<String, Object> before;

    /**
     * 变更后的数据
     */
    private Map<String, Object> after;

    @Getter
    @AllArgsConstructor
    public enum CanalEventType {

        /**
         * INSERT
         */
        INSERT(1),

        /**
         * UPDATE
         */
        UPDATE(2),

        /**
         * DELETE
         */
        DELETE(3);

        private static final Map<Integer, CanalEventType> ENUM_MAP = Arrays.stream(values()).collect(Collectors.toMap(CanalEventType::getValue, Function.identity()));

        private final int value;

        public static CanalEventType of(int value) {
            return ENUM_MAP.get(value);
        }

    }

}
