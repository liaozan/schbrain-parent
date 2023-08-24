package com.schbrain.framework.autoconfigure.mybatis.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.schbrain.framework.autoconfigure.mybatis.constant.MybatisConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 带逻辑删除的基础实体类, 此类属性不需要显式设置, 框架会自动处理
 *
 * @author liaozan
 * @since 2021/11/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseEntityWithLogicDelete extends BaseEntity {

    /**
     * 逻辑删除
     * <p>
     * 注意：默认不参与查询, 只有写 sql 明确指定查询此字段的时候才有值
     */
    @TableLogic
    @TableField(value = MybatisConstants.DELETED, select = false)
    protected Boolean deleted;

    /**
     * 逻辑删除版本
     * <p>
     * 注意：默认不参与查询, 只有写 sql 明确指定查询此字段的时候才有值
     *
     * @see com.schbrain.framework.autoconfigure.mybatis.core.LogicDeleteSupportSqlSource
     */
    @TableField(value = MybatisConstants.DELETE_VERSION, select = false)
    protected Long deleteVersion;

}
