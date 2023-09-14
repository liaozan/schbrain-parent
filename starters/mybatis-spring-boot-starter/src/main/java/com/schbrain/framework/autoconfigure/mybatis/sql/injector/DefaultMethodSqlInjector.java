package com.schbrain.framework.autoconfigure.mybatis.sql.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.AbstractSqlInjector;
import com.baomidou.mybatisplus.core.injector.methods.Insert;
import com.baomidou.mybatisplus.core.injector.methods.SelectBatchByIds;
import com.baomidou.mybatisplus.core.injector.methods.SelectById;
import com.baomidou.mybatisplus.core.injector.methods.SelectCount;
import com.baomidou.mybatisplus.core.injector.methods.SelectList;
import com.baomidou.mybatisplus.core.injector.methods.SelectMaps;
import com.baomidou.mybatisplus.core.injector.methods.SelectObjs;
import com.baomidou.mybatisplus.core.injector.methods.Update;
import com.baomidou.mybatisplus.core.injector.methods.UpdateById;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;
import com.google.common.collect.Lists;
import com.schbrain.framework.autoconfigure.mybatis.sql.method.Delete;
import com.schbrain.framework.autoconfigure.mybatis.sql.method.DeleteBatchByIds;
import com.schbrain.framework.autoconfigure.mybatis.sql.method.DeleteById;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

import static com.schbrain.framework.autoconfigure.mybatis.constant.MybatisConstants.CREATE_TIME;
import static com.schbrain.framework.autoconfigure.mybatis.constant.MybatisConstants.DELETE_VERSION;
import static com.schbrain.framework.autoconfigure.mybatis.constant.MybatisConstants.MODIFY_TIME;

/**
 * @author liaozan
 * @since 2021/11/26
 */
@Slf4j
public class DefaultMethodSqlInjector extends AbstractSqlInjector {

    /**
     * 更新时忽略的字段
     */
    private static final Set<String> FIELDS_TO_IGNORE_WHEN_UPDATE = Set.of(CREATE_TIME, MODIFY_TIME, DELETE_VERSION);

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = Lists.newArrayListWithExpectedSize(20);
        methodList.add(new Insert());
        methodList.add(new Delete());
        methodList.add(new Update());
        methodList.add(new SelectCount());
        methodList.add(new SelectMaps());
        methodList.add(new SelectObjs());
        methodList.add(new SelectList());
        if (tableInfo.havePK()) {
            methodList.add(new DeleteById());
            methodList.add(new DeleteBatchByIds());
            methodList.add(new UpdateById());
            methodList.add(new AlwaysUpdateSomeColumnById(field -> !FIELDS_TO_IGNORE_WHEN_UPDATE.contains(field.getColumn())));
            methodList.add(new SelectById());
            methodList.add(new SelectBatchByIds());
        } else {
            log.warn("{} Not found @TableId annotation, Cannot use Mybatis-Plus 'xxById' Method.", tableInfo.getEntityType());
        }
        return methodList;
    }

}
