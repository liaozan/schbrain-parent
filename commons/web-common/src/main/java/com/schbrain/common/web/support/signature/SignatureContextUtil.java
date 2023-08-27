package com.schbrain.common.web.support.signature;

import java.util.Optional;
import java.util.function.Supplier;

public class SignatureContextUtil {

    private static final ThreadLocal<SignatureContext> LOCAL = new InheritableThreadLocal<>();

    private static final Supplier<SignatureValidationException> EXCEPTION_SUPPLIER = SignatureValidationException::new;

    /**
     * 取值
     */
    public static <T extends SignatureContext> T get(Class<T> type) {
        return type.cast(Optional.ofNullable(LOCAL.get()).orElseThrow(EXCEPTION_SUPPLIER));
    }

    /**
     * 获取appKey
     */
    public static String getAppKey() {
        return get(SignatureContext.class).getAppKey();
    }

    /**
     * 获取appSecret
     */
    public static String getAppSecret() {
        return get(SignatureContext.class).getAppSecret();
    }

    /**
     * 赋值
     */
    public static <T extends SignatureContext> void set(T signatureContext) {
        LOCAL.set(signatureContext);
    }

    /**
     * 移除
     */
    public static void clear() {
        LOCAL.remove();
    }

}
