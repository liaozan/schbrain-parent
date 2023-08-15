package com.schbrain.common.util.support;

import cn.hutool.core.text.StrPool;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

/**
 * @author liaozan
 * @since 2023-07-04
 */
public class ValidationMessageBuilder {

    public static String buildBindingErrorMsg(BindingResult bindingResult) {
        StringJoiner joiner = new StringJoiner(", ");
        for (ObjectError error : bindingResult.getAllErrors()) {
            String errorMessage = Optional.ofNullable(error.getDefaultMessage()).orElse("验证失败");
            String source;
            if (error instanceof FieldError) {
                source = ((FieldError) error).getField();
            } else {
                source = error.getObjectName();
            }
            joiner.add(source + " " + errorMessage);
        }
        return joiner.toString();
    }

    public static String buildConstraintViolationErrorMsg(Set<ConstraintViolation<?>> violations) {
        StringJoiner joiner = new StringJoiner(", ");
        for (ConstraintViolation<?> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            joiner.add(getActualProperty(propertyPath) + " " + violation.getMessage());
        }
        return joiner.toString();
    }

    private static String getActualProperty(String propertyPath) {
        if (StringUtils.isBlank(propertyPath)) {
            return propertyPath;
        }
        if (!propertyPath.contains(StrPool.DOT)) {
            return propertyPath;
        }
        return propertyPath.substring(propertyPath.lastIndexOf(StrPool.DOT) + 1);
    }

}
