package com.schbrain.ci.jenkins.plugins.integration.builder.config;

import com.schbrain.ci.jenkins.plugins.integration.builder.util.FileUtils;
import com.schbrain.ci.jenkins.plugins.integration.builder.util.TemplateUtils;
import hudson.*;
import hudson.model.Descriptor;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static com.schbrain.ci.jenkins.plugins.integration.builder.constants.Constants.BuildConstants.BUILD_SCRIPT_NAME;
import static com.schbrain.ci.jenkins.plugins.integration.builder.constants.Constants.DockerConstants.*;
import static com.schbrain.ci.jenkins.plugins.integration.builder.util.FileUtils.lookupFile;

/**
 * @author liaozan
 * @since 2022/1/16
 */
@SuppressWarnings("unused")
public class DockerConfig extends BuildConfig<DockerConfig> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.systemDefault());

    private final Boolean buildImage;

    private final PushConfig pushConfig;

    private final Boolean deleteImageAfterBuild;

    private final String javaOpts;

    private final String buildScriptUrl;

    private final String buildScriptBranch;

    private final Boolean disableSkywalking;

    @DataBoundConstructor
    public DockerConfig(Boolean buildImage, PushConfig pushConfig, Boolean deleteImageAfterBuild, String javaOpts,
                        String buildScriptUrl, String buildScriptBranch, Boolean disableSkywalking) {
        this.buildImage = Util.fixNull(buildImage, false);
        this.pushConfig = pushConfig;
        this.deleteImageAfterBuild = Util.fixNull(deleteImageAfterBuild, false);
        this.javaOpts = Util.fixNull(javaOpts);
        this.buildScriptUrl = buildScriptUrl;
        this.buildScriptBranch = buildScriptBranch;
        this.disableSkywalking = disableSkywalking;
    }

    @Nullable
    public PushConfig getPushConfig() {
        return pushConfig;
    }

    public Boolean getBuildImage() {
        return buildImage;
    }

    public Boolean getDeleteImageAfterBuild() {
        return deleteImageAfterBuild;
    }

    public String getJavaOpts() {
        return javaOpts;
    }

    public String getBuildScriptUrl() {
        return buildScriptUrl;
    }

    public String getBuildScriptBranch() {
        return buildScriptBranch;
    }

    public Boolean getDisableSkywalking() {
        return disableSkywalking;
    }

    @Override
    public void doBuild() throws IOException, InterruptedException {
        if (!getBuildImage()) {
            context.log("docker build image is skipped");
            return;
        }

        String javaOpts = this.javaOpts;
        if (Boolean.TRUE.equals(getDisableSkywalking())) {
            if (StringUtils.isBlank(javaOpts)) {
                javaOpts = DISABLE_SKYWALKING_OPTIONS;
            } else {
                javaOpts = javaOpts + " " + DISABLE_SKYWALKING_OPTIONS;
            }
        }
        envVars.put(JAVA_OPTS, javaOpts);

        String imageName = getFullImageName();
        if (imageName == null) {
            return;
        }
        envVars.put(IMAGE, imageName);

        FilePath dockerfile = lookupFile(workspace.child(BUILD_SCRIPT_NAME), DOCKERFILE_NAME, context.getLogger());
        if (dockerfile == null) {
            context.log("Dockerfile not exist, skip docker build");
            return;
        }

        TemplateUtils.evaluate(Paths.get(dockerfile.getRemote()), context);

        String relativePath = FileUtils.toRelativePath(workspace, dockerfile);
        String command = String.format("docker build --pull -t %s -f %s .", imageName, relativePath);
        context.execute(command);
        context.setImageHasBeenBuilt();
    }

    private String getFullImageName() {
        String registry = null;
        PushConfig pushConfig = getPushConfig();
        if (pushConfig != null) {
            registry = pushConfig.getRegistry();
        }
        if (StringUtils.isBlank(registry)) {
            registry = envVars.get(REGISTRY);
        }
        if (StringUtils.isBlank(registry)) {
            throw new IllegalArgumentException("REGISTRY is null or empty");
        }

        String appName = envVars.get(APP_NAME);
        String version = envVars.get(VERSION);
        String buildTime = DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(build.getStartTimeInMillis()));
        return String.format("%s/%s:%s-%s", registry, appName, version, buildTime);
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<DockerConfig> {

    }

    public static class PushConfig extends BuildConfig<PushConfig> {

        private final Boolean pushImage;

        private final String registry;

        @DataBoundConstructor
        public PushConfig(Boolean pushImage, String registry) {
            this.pushImage = pushImage;
            this.registry = registry;
        }

        public Boolean getPushImage() {
            return pushImage;
        }

        public String getRegistry() {
            return registry;
        }

        @Override
        public void doBuild() throws IOException, InterruptedException {
            if (!getPushImage()) {
                logger.println("docker push image is skipped");
                return;
            }

            String imageName = envVars.get(IMAGE);
            if (imageName == null) {
                return;
            }
            String command = String.format("docker push %s", imageName);
            context.execute(command);
        }

        @Extension
        @SuppressWarnings("unused")
        public static class DescriptorImpl extends Descriptor<PushConfig> {

        }

    }

}