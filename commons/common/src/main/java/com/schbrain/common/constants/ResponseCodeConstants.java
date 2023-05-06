package com.schbrain.common.constants;

/**
 * @author liwu
 * @since 2019/3/29
 */
public class ResponseCodeConstants {

    /**
     * 成功
     */
    public static final int SUCCESS = 0;

    /**
     * 服务器错误，空指针、数组越界等非业务代码抛出异常
     */
    public static final int SERVER_ERROR = -1;

    /**
     * 非法请求，参数异常、参数格式错误等接口的请求非法性抛出的通用错误
     */
    public static final int PARAM_INVALID = -2;

    /**
     * 无权限
     */
    public static final int ACCESS_DENIED = -3;

    /**
     * 用户未登录，且该接口需要登录
     */
    public static final int LOGIN_REQUIRED = -4;

    /**
     * 系统维护
     */
    public static final int SYSTEM_MAINTENANCE = -5;

}