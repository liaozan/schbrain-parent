package com.schbrain.maven.plugin.mojo;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author liaozan
 * @since 2022/1/4
 */
@SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
@Mojo(name = "prepare", threadSafe = true, defaultPhase = LifecyclePhase.PACKAGE,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class PrepareMojo extends AbstractMojo {

    private static final String DOCKER_BUILD_INFO = "dockerBuildInfo";

    /**
     * maven project
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * maven session
     */
    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    /**
     * 构造最终的名字
     */
    @Parameter(defaultValue = "${project.build.finalName}", readonly = true)
    private String finalName;

    /**
     * 构建输出目录
     */
    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    private File buildDirectory;

    /**
     * docker 镜像推送地址
     */
    @Parameter(required = true, defaultValue = "${docker.registry}")
    private String dockerRegistry;

    /**
     * spring 启动的 profile
     */
    @Parameter(required = true, defaultValue = "${spring.profile}")
    private String springProfile;

    /**
     * 是否包含系统属性
     */
    @Parameter(defaultValue = "false")
    private boolean includeSystemProperties;

    /**
     * 是否包含系统环境变量
     */
    @Parameter(defaultValue = "false")
    private boolean includeSystemEnv;

    /**
     * 打包的名字, 为空取 root project artifactId
     */
    @Parameter
    private String appName;

    /**
     * 打包的版本, 为空取 root project version
     */
    @Parameter
    private String version;

    /**
     * 额外的属性
     */
    @Parameter
    private Map<String, String> additionalProperties;

    public void execute() throws MojoExecutionException {
        if (shouldSkip()) {
            return;
        }

        try {
            validateParam();

            Path executableJarFile = getExecutableJarFile(finalName, buildDirectory);
            if (!Files.exists(executableJarFile)) {
                throw new MojoExecutionException("target jar is not present, it's required for build");
            }

            Map<Object, Object> variables = collectProjectVariables(executableJarFile);
            String content = storeVariablesToFile(variables);
            getLog().info("generated build properties: \n" + content);
        } catch (Exception e) {
            String errorMsg = String.format("%s: %s", e.getClass().getName(), e.getMessage());
            getLog().error(errorMsg);
            throw new MojoExecutionException(errorMsg, e);
        }
    }

    private void validateParam() throws MojoExecutionException {
        MavenProject topLevelProject = session.getTopLevelProject();
        if (isBlank(dockerRegistry)) {
            throw new MojoExecutionException("docker.registry is required for build, but found empty value, please check the pom configuration");
        }
        if (isBlank(springProfile)) {
            throw new MojoExecutionException("spring.profile is required for build, but found empty value, please check the pom configuration");
        }
        if (isBlank(appName)) {
            appName = topLevelProject.getArtifactId();
        }
        if (isBlank(version)) {
            version = topLevelProject.getVersion();
        }
    }

    private Map<Object, Object> collectProjectVariables(Path executableJarFile) {
        MavenProject topProject = session.getTopLevelProject();
        Map<Object, Object> variables = new HashMap<>();
        // required for docker build
        variables.put("APP_NAME", appName);
        variables.put("JAR_FILE", getJarFileRelativePath(topProject.getBasedir(), executableJarFile));
        variables.put("VERSION", version);
        variables.put("REGISTRY", dockerRegistry);
        variables.put("PROFILE", springProfile);
        // add additionalProperties
        if (additionalProperties != null) {
            variables.putAll(additionalProperties);
        }
        // add system properties
        if (includeSystemProperties) {
            variables.putAll(System.getProperties());
        }
        // add system env
        if (includeSystemEnv) {
            variables.putAll(System.getenv());
        }
        return variables;
    }

    private String storeVariablesToFile(Map<Object, Object> variables) throws IOException {
        Path dockerBuildInfo = Paths.get(buildDirectory.getAbsolutePath(), DOCKER_BUILD_INFO);
        if (Files.exists(dockerBuildInfo)) {
            Files.delete(dockerBuildInfo);
        }
        String content = variablesToString(variables);
        Files.writeString(dockerBuildInfo, content);
        return content;
    }

    private String getJarFileRelativePath(File basedir, Path jarFile) {
        return Paths.get(basedir.getPath()).relativize(jarFile).toString();
    }

    private boolean shouldSkip() {
        if ("pom".equals(this.project.getPackaging())) {
            getLog().warn("docker-build goal could not be applied to pom project");
            return true;
        }
        return false;
    }

    private Path getExecutableJarFile(String finalName, File targetDirectory) {
        return Paths.get(targetDirectory.getPath(), withExtension(finalName));
    }

    private String withExtension(String fileName) {
        return String.format("%s.%s", fileName, project.getArtifact().getArtifactHandler().getExtension());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String variablesToString(Map<Object, Object> variables) {
        StringBuilder builder = new StringBuilder();
        for (Entry<Object, Object> entry : variables.entrySet()) {
            builder.append(entry.getKey()).append("=").append(entry.getValue());
            builder.append(System.lineSeparator());
        }
        return builder.deleteCharAt(builder.length() - 1).toString();
    }

}