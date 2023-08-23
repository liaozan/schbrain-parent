package com.schbrain.framework.autoconfigure.mybatis.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.schbrain.framework.autoconfigure.mybatis.constant.MybatisConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liaozan
 * @since 2021/11/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseEntityWithLogicDelete extends BaseEntity {

    /**
     * 逻辑删除
     * 注意：只有写 sql 明确指定查询此字段的时候才有值, update 时，无法修改此字段
     */
    @TableLogic
    @TableField(value = MybatisConstants.DELETED, select = false)
    protected Boolean deleted;

    /**
     * 逻辑删除版本
     * 注意：只有写 sql 明确指定查询此字段的时候才有值
     */
    @TableField(value = MybatisConstants.DELETE_VERSION, select = false)
    protected Long deleteVersion;

}
