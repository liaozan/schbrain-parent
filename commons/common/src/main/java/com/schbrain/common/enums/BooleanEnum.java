package com.schbrain.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    private static final Set<Integer> VALUES = Arrays.stream(values()).map(BooleanEnum::getValue).collect(Collectors.toSet());

    private final Integer value;

    public static boolean validate(Integer value) {
        return VALUES.contains(value);
    }

    public static boolean isTrue(Integer value) {
        return Objects.equals(TRUE.value, value);
    }

    public static boolean isFalse(Integer value) {
        return Objects.equals(FALSE.value, value);
    }

}
