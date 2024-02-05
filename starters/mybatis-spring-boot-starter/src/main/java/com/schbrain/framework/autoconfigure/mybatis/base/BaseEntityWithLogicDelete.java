package com.schbrain.framework.autoconfigure.mybatis.base;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.schbrain.framework.autoconfigure.mybatis.constant.MybatisConstants;
import lombok.*;

import java.io.IOException;

/**
 * 带逻辑删除的基础实体类, 此类属性不需要显式设置, 框架会自动处理
 *
 * @author liaozan
 * @since 2021/11/25
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BaseEntityWithLogicDelete extends BaseEntity {

    /**
     * 逻辑删除
     * <p>
     * 注意：默认不参与查询, 只有写 sql 明确指定查询此字段的时候才有值
     */
    @TableLogic
    @JsonDeserialize(using = LogicDeleteDeserializer.class)
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

    public static class LogicDeleteDeserializer extends JsonDeserializer<Boolean> {

        @Override
        public Boolean deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            String currentValue = parser.getValueAsString();
            if (currentValue == null) {
                return null;
            }
            if (NumberUtil.isNumber(currentValue)) {
                return NumberUtil.parseInt(currentValue) == 1;
            }
            return Boolean.parseBoolean(currentValue);
        }

    }

}
