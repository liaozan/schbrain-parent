package com.schbrain.framework.autoconfigure.mybatis.base;

import com.schbrain.common.util.BeanCopyUtils;
import com.schbrain.common.util.StreamUtils;
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
        Target target = BeanCopyUtils.copy(entity, targetType);
        return toTarget(entity, target);
    }

    protected Target toTarget(Source source, Target target) {
        if (target == null) {
            return null;
        }
        editTarget(source, target);
        return target;
    }

    protected void editTarget(Source source, Target target) {

    }

    protected List<Target> toTargetList(List<Source> entityList) {
        return StreamUtils.toList(entityList, this::toTarget);
    }

    protected Source fromTarget(Target target) {
        Source source = BeanCopyUtils.copy(target, entityClass);
        return fromTarget(source, target);
    }

    protected Source fromTarget(Source source, Target target) {
        if (target == null) {
            return null;
        }
        editSource(source, target);
        return source;
    }

    protected void editSource(Source source, Target target) {

    }

    protected List<Source> fromTargetList(List<Target> targetList) {
        return StreamUtils.toList(targetList, this::fromTarget);
    }

}
