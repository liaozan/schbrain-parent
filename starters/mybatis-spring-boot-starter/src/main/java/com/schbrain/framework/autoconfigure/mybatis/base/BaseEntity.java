package com.schbrain.framework.autoconfigure.mybatis.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.schbrain.framework.autoconfigure.mybatis.constant.MybatisConstants;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 基础实体类, 此类属性不需要显式设置, 框架会自动处理
 *
 * @author liaozan
 * @since 2021/10/14
 */
@Data
public class BaseEntity {

    /**
     * 主键 id
     * <p>
     * 数据库中需设置为自增主键
     */
    @TableId(value = MybatisConstants.ID, type = IdType.AUTO)
    protected Long id;

    /**
     * 创建时间
     * <p>
     * 数据库中需设置默认值为 current_timestamp
     */
    @TableField(value = MybatisConstants.CREATE_TIME)
    protected LocalDateTime createTime;

    /**
     * 修改时间
     * <p>
     * 数据库中需设置默认值为 current_timestamp, on update current_timestamp
     */
    @TableField(value = MybatisConstants.MODIFY_TIME)
    protected LocalDateTime modifyTime;

}
