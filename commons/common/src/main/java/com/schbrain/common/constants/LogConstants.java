package com.schbrain.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.NoSuchElementException;

public class LogConstants {

    @Getter
    @AllArgsConstructor
    public enum ProductTypeEnum {
        JSC("jsc", "驾驶舱"),
        ZS("zs", "中枢"),
        ZYB("zyb", "作业宝"),
        SZ("sz", "数治");

        private final String type;

        private final String desc;

        public static ProductTypeEnum valueOfType(String type) {
            for (ProductTypeEnum productTypeEnum : values()) {
                if (productTypeEnum.getType().equals(type)) {
                    return productTypeEnum;
                }
            }
            throw new NoSuchElementException(type);
        }
    }

}