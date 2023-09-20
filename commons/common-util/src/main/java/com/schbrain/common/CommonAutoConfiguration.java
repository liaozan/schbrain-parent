package com.schbrain.common;

import com.schbrain.common.util.support.jackson.ObjectMapperModuleConfiguration;
import com.schbrain.common.util.support.task.ThreadPoolConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * @author liaozan
 * @since 2022/1/11
 */
@AutoConfiguration
@Import({ThreadPoolConfiguration.class, ObjectMapperModuleConfiguration.class})
public class CommonAutoConfiguration {

}
