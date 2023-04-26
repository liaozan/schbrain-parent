package com.schbrain.ci.jenkins.plugins.integration.builder;

import hudson.model.AbstractBuild;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.schbrain.ci.jenkins.plugins.integration.builder.constants.Constants.BuildConstants.*;

/**
 * @author liaozan
 * @since 2022/2/8
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileManager {

    public static File getCurrentBuildDir(AbstractBuild<?, ?> build) {
        return build.getRootDir();
    }

    public static File getWorkspaceBuildScriptDir(AbstractBuild<?, ?> build) {
        return new File(Objects.requireNonNull(build.getWorkspace()).getRemote(), BUILD_SCRIPT_NAME);
    }

    public static File getEnvVarsFile(AbstractBuild<?, ?> build) throws IOException {
        File buildScriptDir = getBuildScriptDir(build);
        File envVarsFile = new File(buildScriptDir, ENV_VARS);
        if (!envVarsFile.exists()) {
            envVarsFile.createNewFile();
        }
        return envVarsFile;
    }

    public static File getBuildScriptDir(AbstractBuild<?, ?> build) {
        File buildDir = getCurrentBuildDir(build);
        File buildScriptDir = new File(buildDir, BUILD_SCRIPT_NAME);
        if (!buildScriptDir.exists()) {
            buildScriptDir.mkdirs();
        }
        return buildScriptDir;
    }

}