package com.schbrain.framework.dao.mybatis;

import com.schbrain.common.exception.BaseException;
import com.schbrain.framework.dao.BaseDao;
import com.schbrain.framework.dao.mybatis.mapper.BaseMapper;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.mybatis.spring.SqlSessionTemplate;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liwu
 * @since 2019/7/29
 */
public class BaseDaoInvocationHandler<T> implements InvocationHandler {

    private final Map<Method, MethodHandle> methodHandleCache = new ConcurrentHashMap<>();

    private final T originMapperProxy;
    private final BaseMapper baseMapper;
    private final Lookup mapperInterfaceLookup;

    public BaseDaoInvocationHandler(T originMapperProxy, SqlSessionTemplate sqlSession, Class<T> mapperInterface) throws IllegalAccessException {
        this.originMapperProxy = originMapperProxy;
        this.baseMapper = new BaseMapper(sqlSession, mapperInterface);
        this.mapperInterfaceLookup = MethodHandles.privateLookupIn(mapperInterface, MethodHandles.lookup());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            } else if (method.isDefault()) {
                return invokeDefaultMethod(proxy, method, args);
            } else if (isBaseMethod(method)) {
                return invokeBaseMethod(method, args);
            }
        } catch (Throwable throwable) {
            throw ExceptionUtil.unwrapThrowable(throwable);
        }
        return method.invoke(originMapperProxy, args);
    }

    private Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
        MethodHandle methodHandle = getMethodHandle(proxy, method);
        return methodHandle.invokeWithArguments(args);
    }

    private MethodHandle getMethodHandle(Object proxy, Method method) {
        return methodHandleCache.computeIfAbsent(method, key -> bindHandle(proxy, method));
    }

    private MethodHandle bindHandle(Object proxy, Method method) {
        try {
            return mapperInterfaceLookup.unreflectSpecial(method, method.getDeclaringClass()).bindTo(proxy);
        } catch (IllegalAccessException e) {
            throw new BaseException(e.getMessage(), e);
        }
    }

    private boolean isBaseMethod(Method method) {
        return BaseDao.class.equals(method.getDeclaringClass());
    }

    private Object invokeBaseMethod(Method method, Object[] args) {
        return baseMapper.invokeBaseMethod(method, args);
    }

}
