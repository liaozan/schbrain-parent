package com.schbrain.framework.autoconfigure.mybatis.sql.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.AbstractSqlInjector;
import com.baomidou.mybatisplus.core.injector.methods.*;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;
import com.google.common.collect.Lists;
import com.schbrain.framework.autoconfigure.mybatis.constant.MybatisConstants;
import com.schbrain.framework.autoconfigure.mybatis.sql.method.Delete;
import com.schbrain.framework.autoconfigure.mybatis.sql.method.DeleteBatchByIds;
import com.schbrain.framework.autoconfigure.mybatis.sql.method.DeleteByMap;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author liaozan
 * @since 2021/11/26
 */
@Slf4j
public class DefaultMethodSqlInjector extends AbstractSqlInjector {

    /**
     * <ul>
     *     <li>replace {@link com.baomidou.mybatisplus.core.injector.methods.Delete} to {@link Delete}</li>
     *     <li>replace {@link com.baomidou.mybatisplus.core.injector.methods.DeleteById} to {@link com.schbrain.framework.autoconfigure.mybatis.sql.method.DeleteById}</li>
     *     <li>replace {@link com.baomidou.mybatisplus.core.injector.methods.DeleteByMap} to {@link DeleteByMap}</li>
     *     <li>replace {@link com.baomidou.mybatisplus.core.injector.methods.DeleteBatchByIds} to {@link DeleteBatchByIds}</li>
     * </ul>
     */
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = Lists.newArrayListWithExpectedSize(20);
        methodList.add(new Insert());
        methodList.add(new Delete());
        methodList.add(new DeleteByMap());
        methodList.add(new Update());
        methodList.add(new SelectByMap());
        methodList.add(new SelectCount());
        methodList.add(new SelectMaps());
        methodList.add(new SelectMapsPage());
        methodList.add(new SelectObjs());
        methodList.add(new SelectList());
        methodList.add(new SelectPage());
        if (tableInfo.havePK()) {
            methodList.add(new com.schbrain.framework.autoconfigure.mybatis.sql.method.DeleteById());
            methodList.add(new DeleteBatchByIds());
            methodList.add(new UpdateById());
            methodList.add(new AlwaysUpdateSomeColumnById(field -> !field.getColumn().equals(MybatisConstants.DELETE_VERSION)));
            methodList.add(new SelectById());
            methodList.add(new SelectBatchByIds());
        } else {
            log.warn("{} ,Not found @TableId annotation, Cannot use Mybatis-Plus 'xxById' Method.", tableInfo.getEntityType());
        }
        return methodList;
    }

}