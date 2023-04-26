package com.schbrain.common;

import com.schbrain.common.util.support.jackson.ObjectMapperModuleConfiguration;
import com.schbrain.common.util.support.task.ThreadPoolConfiguration;
import com.schbrain.common.util.support.trace.TraceParamAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * @author liaozan
 * @since 2022/1/11
 */
@AutoConfiguration
@Import({TraceParamAspect.class, ThreadPoolConfiguration.class, ObjectMapperModuleConfiguration.class})
public class CommonAutoConfiguration {

}