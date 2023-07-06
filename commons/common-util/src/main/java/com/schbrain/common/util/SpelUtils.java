package com.schbrain.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpelUtils {

    private static final ExpressionParser spELParser = new SpelExpressionParser();

    private static final ConcurrentHashMap<String, Expression> expressionCache = new ConcurrentHashMap<>();

    public static <T> T parse(String express, Map<String, Object> variables, Class<T> valueType) {
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        ctx.setVariables(variables);
        return parse(express, ctx, valueType);
    }

    public static <T> T parse(String express, EvaluationContext context, Class<T> valueType) {
        if (StringUtils.isBlank(express)) {
            return null;
        }
        return getExpression(express).getValue(context, valueType);
    }

    private static Expression getExpression(String exp) {
        Expression expression = expressionCache.get(exp);
        if (null == expression) {
            expression = spELParser.parseExpression(exp);
            expressionCache.put(exp, expression);
        }
        return expression;
    }

}