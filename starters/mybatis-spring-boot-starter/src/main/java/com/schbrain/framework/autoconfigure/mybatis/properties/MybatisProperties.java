package com.schbrain.framework.autoconfigure.mybatis.properties;

import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liaozan
 * @since 2021/11/23
 */
@Data
@ConfigurationProperties(prefix = "schbrain.mybatis")
public class MybatisProperties implements ConfigurableProperties {

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
    public String getNamespace() {
        return "mybatis-common";
    }

}