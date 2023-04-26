package com.schbrain.ci.jenkins.plugins.integration.builder.config;

import com.schbrain.ci.jenkins.plugins.integration.builder.BuilderContext;
import com.schbrain.ci.jenkins.plugins.integration.builder.util.Logger;
import hudson.*;
import hudson.model.*;

import java.io.IOException;

/**
 * @author liaozan
 * @since 2022/1/17
 */
public abstract class BuildConfig<T extends AbstractDescribableImpl<T>> extends AbstractDescribableImpl<T> {

    protected AbstractBuild<?, ?> build;
    protected Launcher launcher;
    protected FilePath workspace;
    protected BuildListener listener;
    protected Logger logger;
    protected EnvVars envVars;
    protected BuilderContext context;

    public void build(BuilderContext context) throws IOException, InterruptedException {
        this.context = context;
        this.build = context.getBuild();
        this.launcher = context.getLauncher();
        this.workspace = context.getWorkspace();
        this.listener = context.getListener();
        this.logger = context.getLogger();
        this.envVars = context.getEnvVars();
        doBuild();
    }

    protected abstract void doBuild() throws InterruptedException, IOException;

}