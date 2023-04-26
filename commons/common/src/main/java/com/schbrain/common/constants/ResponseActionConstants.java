package com.schbrain.common.constants;

/**
 * @author liwu
 * @since 2019/3/29
 */
public class ResponseActionConstants {

    /**
     * 业务无异常时统一返回0
     */
    public static final int NO_ACTION = 0;
    /**
     * 忽略异常
     */
    public static final int IGNORE = -1;
    /**
     * 弹框
     */
    public static final int ALERT = -2;
    /**
     * toast
     */
    public static final int TOAST = -3;
    /**
     * 弹框，点击确定后刷新页面
     */
    public static final int ALERT_REFRESH = -4;

}