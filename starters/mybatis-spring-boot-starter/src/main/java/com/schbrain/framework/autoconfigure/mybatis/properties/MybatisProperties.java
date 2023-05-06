package com.schbrain.framework.autoconfigure.mybatis.properties;

import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liaozan
 * @since 2021/11/23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "schbrain.mybatis")
public class MybatisProperties extends ConfigurableProperties {

    /**
     * 分页拦截器
     */
    private boolean addPageInterceptor = true;

    /**
     * 阻断全表更新操作，禁止不带 where 更新，删除
     */
    private boolean addBlockAttackInterceptor = true;

    /**
     * 是否开启表约束检查
     */
    private boolean enableTableConstraintCheck = true;

    /**
     * Instant 转为 long
     */
    private boolean convertInstantToLong = true;

    @Override
    public String getDefaultNamespace() {
        return "mybatis-common";
    }

}