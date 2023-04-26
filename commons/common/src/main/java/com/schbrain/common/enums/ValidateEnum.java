package com.schbrain.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author huangxi
 * @since 2022/08/23
 */
@Getter
@AllArgsConstructor
public enum ValidateEnum {

    /**
     * 有效
     */
    VALID(0),
    /**
     * 无效
     */
    INVALID(-1);

    private final Integer value;

}