package com.schbrain.framework.autoconfigure.mybatis.base;

import com.baomidou.mybatisplus.annotation.*;
import com.schbrain.framework.autoconfigure.mybatis.constant.MybatisConstants;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author liaozan
 * @since 2021/10/14
 */
@Data
public class BaseEntity {

    /**
     * 主键 id
     */
    @TableId(value = MybatisConstants.ID, type = IdType.AUTO)
    protected Long id;

    /**
     * 创建时间
     */
    @TableField(value = MybatisConstants.CREATE_TIME)
    protected LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(value = MybatisConstants.MODIFY_TIME)
    protected LocalDateTime modifyTime;

}