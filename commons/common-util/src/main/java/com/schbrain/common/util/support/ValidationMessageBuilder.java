package com.schbrain.common.util.support;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.validation.*;

import javax.validation.ConstraintViolation;
import java.util.*;

/**
 * @author liaozan
 * @since 2023-07-04
 */
public class ValidationMessageBuilder {

    public static String buildBindingErrorMsg(BindingResult bindingResult) {
        String prefix = "参数验证失败: ";
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
        return prefix + joiner;
    }

    public static String buildConstraintViolationErrorMsg(Set<ConstraintViolation<?>> constraintViolations) {
        String prefix = "参数验证失败: ";
        StringJoiner joiner = new StringJoiner(", ");
        for (ConstraintViolation<?> violation : constraintViolations) {
            PathImpl propertyPath = (PathImpl) violation.getPropertyPath();
            joiner.add(propertyPath.asString() + " " + violation.getMessage());
        }
        return prefix + joiner;
    }

}