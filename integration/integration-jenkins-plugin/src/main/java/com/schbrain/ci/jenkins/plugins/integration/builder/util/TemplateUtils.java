package com.schbrain.ci.jenkins.plugins.integration.builder.util;

import com.schbrain.ci.jenkins.plugins.integration.builder.BuilderContext;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author liaozan
 * @since 2022/1/23
 */
public class TemplateUtils {

    public static void evaluate(Path templateFile, BuilderContext context) throws IOException, InterruptedException {
        if (templateFile == null) {
            return;
        }

        String resolved = evaluate(Files.readString(templateFile), context.getEnvVars());
        Files.write(templateFile, resolved.getBytes(StandardCharsets.UTF_8));
    }

    public static String evaluate(String template, Map<String, String> variables) {
        if (null == template) {
            return null;
        }
        if (null == variables || variables.isEmpty()) {
            return template;
        }

        Map<String, Object> params = new LinkedHashMap<>(variables);
        VelocityContext velocityContext = new VelocityContext(params);
        StringWriter writer = new StringWriter();
        Velocity.evaluate(velocityContext, writer, "Template Evaluate", template);
        return writer.getBuffer().toString();
    }

}