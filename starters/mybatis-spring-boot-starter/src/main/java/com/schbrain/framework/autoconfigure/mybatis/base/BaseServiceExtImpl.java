package com.schbrain.framework.autoconfigure.mybatis.base;

import com.schbrain.common.util.BeanCopyUtils;
import org.springframework.core.ResolvableType;

import java.util.List;

/**
 * @author liaozan
 * @since 2023/7/7
 */
public class BaseServiceExtImpl<M extends BaseMapper<Source>, Source extends BaseEntity, Target> extends BaseServiceImpl<M, Source> {

    protected final Class<Target> targetType;

    public BaseServiceExtImpl() {
        // noinspection unchecked
        this.targetType = (Class<Target>) ResolvableType.forInstance(this).getSuperType().getGeneric(2).getRawClass();
    }

    protected Target toTarget(Source entity) {
        return BeanCopyUtils.copy(entity, targetType);
    }

    protected List<Target> toTargetList(List<Source> entityList) {
        return BeanCopyUtils.copyList(entityList, targetType);
    }

    protected Source fromTarget(Target target) {
        return BeanCopyUtils.copy(target, entityClass);
    }

    protected List<Source> fromTargetList(List<Target> targetList) {
        return BeanCopyUtils.copyList(targetList, entityClass);
    }

}
