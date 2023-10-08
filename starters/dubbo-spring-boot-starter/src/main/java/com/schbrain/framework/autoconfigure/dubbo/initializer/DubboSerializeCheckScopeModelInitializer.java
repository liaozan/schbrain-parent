package com.schbrain.framework.autoconfigure.dubbo.initializer;

import com.alibaba.fastjson2.JSONFactory;
import org.apache.dubbo.common.beans.factory.ScopeBeanFactory;
import org.apache.dubbo.common.utils.SerializeCheckStatus;
import org.apache.dubbo.common.utils.SerializeSecurityManager;
import org.apache.dubbo.rpc.model.*;

/**
 * @author liaozan
 * @since 2023-05-30
 */
public class DubboSerializeCheckScopeModelInitializer implements ScopeModelInitializer {

    public DubboSerializeCheckScopeModelInitializer() {
        JSONFactory.setUseJacksonAnnotation(false);
        System.setProperty("dubbo.hessian.allowNonSerializable", Boolean.TRUE.toString());
    }

    @Override
    public void initializeFrameworkModel(FrameworkModel frameworkModel) {
        ScopeBeanFactory beanFactory = frameworkModel.getBeanFactory();
        SerializeSecurityManager securityManager = beanFactory.getBean(SerializeSecurityManager.class);
        securityManager.setCheckSerializable(false);
        securityManager.setCheckStatus(SerializeCheckStatus.DISABLE);
        securityManager.setDefaultCheckStatus(SerializeCheckStatus.DISABLE);
    }

    @Override
    public void initializeApplicationModel(ApplicationModel applicationModel) {

    }

    @Override
    public void initializeModuleModel(ModuleModel moduleModel) {

    }

}
