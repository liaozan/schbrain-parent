package com.schbrain.framework.autoconfigure.cache.concurrent;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liaozan
 * @since 2023-07-15
 */
public class ExpressionParser {

    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>(256);

    private final SpelExpressionParser parser = new SpelExpressionParser();
    private final ParserContext parserContext = new TemplateParserContext();

    private final ConfigurableListableBeanFactory beanFactory;

    public ExpressionParser(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public String parse(String value, Map<String, Object> variables) {
        String resolved = beanFactory.resolveEmbeddedValue(value);
        Expression expression = expressionCache.computeIfAbsent(resolved, this::parseExpression);
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(new BeanFactoryResolver(beanFactory));
        context.setVariables(variables);
        return expression.getValue(context, String.class);
    }

    private Expression parseExpression(String value) {
        return parser.parseExpression(value, parserContext);
    }

}
