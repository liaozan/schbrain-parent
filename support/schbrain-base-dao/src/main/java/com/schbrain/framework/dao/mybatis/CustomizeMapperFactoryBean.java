package com.schbrain.framework.dao.mybatis;

import com.github.pagehelper.PageInterceptor;
import com.schbrain.framework.dao.BaseDao;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Properties;

/**
 * description
 *
 * @author liwu on 2019/7/29
 */
public class CustomizeMapperFactoryBean<T> extends MapperFactoryBean<T> {

    public CustomizeMapperFactoryBean() {
        super();
    }

    public CustomizeMapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() throws Exception {
        T originMapperProxy = super.getObject();
        Class<T> mapperInterface = getMapperInterface();
        if (!BaseDao.class.isAssignableFrom(mapperInterface)) {
            return originMapperProxy;
        }
        // 判断是否已经添加分页过滤器
        SqlSessionTemplate sqlSession = getSqlSessionTemplate();
        Configuration configuration = sqlSession.getConfiguration();
        List<Interceptor> interceptorList = configuration.getInterceptors();
        boolean hasPageInterceptor = interceptorList.stream().anyMatch(e -> PageInterceptor.class.isAssignableFrom(e.getClass()));
        if (!hasPageInterceptor) {
            PageInterceptor pageInterceptor = new PageInterceptor();
            pageInterceptor.setProperties(new Properties());
            configuration.addInterceptor(pageInterceptor);
        }
        // 创建代理
        BaseMethodInvocationHandler<T> handler = new BaseMethodInvocationHandler<>(originMapperProxy, sqlSession, mapperInterface);
        return (T) Proxy.newProxyInstance(getMapperInterface().getClassLoader(), new Class[]{mapperInterface}, handler);
    }

}