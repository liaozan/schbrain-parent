package com.schbrain.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author huangxi
 * @since 2022/08/23
 */
@Getter
@AllArgsConstructor
public enum BooleanEnum {

    /**
     * true
     */
    TRUE(1),
    /**
     * false
     */
    FALSE(0);

    private final Integer value;

    public static boolean validate(Integer value) {
        return Objects.equals(TRUE.value, value);
    }

}
