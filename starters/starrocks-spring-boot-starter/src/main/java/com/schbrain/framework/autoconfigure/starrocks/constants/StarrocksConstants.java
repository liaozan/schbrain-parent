package com.schbrain.framework.autoconfigure.starrocks.constants;

/**
 * @author liaozan
 * @since 2023/12/6
 */
public class StarrocksConstants {

    /**
     * StreamLoad api 路径模板
     */
    public static final String STREAM_LOAD_TEMPLATE = "http://%s:%s/api/%s/%s/_stream_load";

    /**
     * jdbc url连接模板
     */
    public static final String JDBC_URL_TEMPLATE = "jdbc:mysql://%s:%s/%s";

}
