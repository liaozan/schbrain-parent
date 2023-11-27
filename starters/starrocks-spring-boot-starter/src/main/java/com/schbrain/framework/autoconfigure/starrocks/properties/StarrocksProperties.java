package com.schbrain.framework.autoconfigure.starrocks.properties;

import com.mysql.cj.jdbc.Driver;
import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author liaozan
 * @since 2023/11/27
 */
@Data
@Validated
@ConfigurationProperties(prefix = "starrocks")
public class StarrocksProperties implements ConfigurableProperties {

    /**
     * 驱动
     */
    @NotBlank
    private String driverClassName = Driver.class.getName();

    /**
     * 连接地址
     */
    @NotBlank
    private String host;

    /**
     * 数据库名
     */
    @NotBlank
    private String database;

    /**
     * 数据库直连端口
     */
    @NotNull
    private Integer port = 9030;

    /**
     * http 连接地址
     */
    @NotNull
    private Integer httpPort = 8030;

    /**
     * 用户名
     */
    @NotBlank
    private String username;

    /**
     * 密码
     */
    @NotBlank
    private String password;

    @Override
    public String getNamespace() {
        return "starrocks-common";
    }

}
