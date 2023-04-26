package com.schbrain.framework.dao.mybatis;

import com.schbrain.framework.dao.BaseDao;
import com.schbrain.framework.dao.mybatis.mapper.BaseMapper;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.mybatis.spring.SqlSessionTemplate;

import java.io.Serializable;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.*;

/**
 * description
 *
 * @author liwu on 2019/7/29
 */
public class BaseMethodInvocationHandler<T> implements InvocationHandler, Serializable {

    private static final long serialVersionUID = -5077848994288044943L;

    private final T originMapperProxy;

    private final BaseMapper baseMapper;

    public BaseMethodInvocationHandler(T originMapperProxy, SqlSessionTemplate sqlSession, Class<T> mapperInterface) {
        this.originMapperProxy = originMapperProxy;
        baseMapper = new BaseMapper(sqlSession, mapperInterface);
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
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }
        return method.invoke(originMapperProxy, args);
    }

    private Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
        final Constructor<Lookup> constructor = Lookup.class.getDeclaredConstructor(Class.class, int.class);
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        final Class<?> declaringClass = method.getDeclaringClass();
        return constructor
                .newInstance(declaringClass, Lookup.PRIVATE | Lookup.PROTECTED | Lookup.PACKAGE | Lookup.PUBLIC)
                .unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
    }

    private boolean isBaseMethod(Method method) {
        return BaseDao.class.equals(method.getDeclaringClass());
    }

    private Object invokeBaseMethod(Method method, Object[] args) {
        return baseMapper.invokeBaseMethod(method, args);
    }

}