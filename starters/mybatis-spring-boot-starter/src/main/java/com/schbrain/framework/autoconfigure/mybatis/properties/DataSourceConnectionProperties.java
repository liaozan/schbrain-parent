package com.schbrain.framework.autoconfigure.mybatis.properties;

import com.mysql.cj.conf.PropertyDefinitions.ZeroDatetimeBehavior;
import com.mysql.cj.conf.PropertyKey;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author liaozan
 * @since 2021/11/23
 */
@Data
@ConfigurationProperties(prefix = "schbrain.datasource.connection")
public class DataSourceConnectionProperties {

    /**
     * 使用 ssl 连接
     */
    private Boolean useSsl = false;

    /**
     * tinyint(1) 视为 boolean
     */
    private Boolean tinyInt1isBit = true;

    /**
     * 重写批处理sql
     */
    private Boolean rewriteBatchedStatements = true;

    /**
     * 是否允许一个 statement 用分号分割执行多个查询语句
     */
    private Boolean allowMultiQueries = true;

    /**
     * 允许从服务端获取公钥进行连接
     */
    private Boolean allowPublicKeyRetrieval = true;

    /**
     * 连接数据库使用的时区
     */
    private ZoneId serverTimeZone = ZoneId.systemDefault();

    /**
     * 时间格式字段值为 0 的时候的处理方式
     */
    private ZeroDatetimeBehavior zeroDatetimeBehavior = ZeroDatetimeBehavior.CONVERT_TO_NULL;

    /**
     * 数据库连接字符编码
     */
    private String characterEncoding = StandardCharsets.UTF_8.name();

    public Map<String, String> toConfigurationMap() {
        Map<String, String> properties = new LinkedHashMap<>();
        properties.put(PropertyKey.useSSL.getKeyName(), this.useSsl.toString());
        properties.put(PropertyKey.tinyInt1isBit.getKeyName(), this.tinyInt1isBit.toString());
        properties.put(PropertyKey.rewriteBatchedStatements.getKeyName(), this.rewriteBatchedStatements.toString());
        properties.put(PropertyKey.allowMultiQueries.getKeyName(), this.allowMultiQueries.toString());
        properties.put(PropertyKey.connectionTimeZone.getKeyName(), this.serverTimeZone.getId());
        properties.put(PropertyKey.allowPublicKeyRetrieval.getKeyName(), this.allowPublicKeyRetrieval.toString());
        properties.put(PropertyKey.zeroDateTimeBehavior.getKeyName(), this.zeroDatetimeBehavior.name());
        properties.put(PropertyKey.characterEncoding.getKeyName(), this.characterEncoding);
        return properties;
    }

}