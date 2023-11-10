package com.schbrain.framework.dao.mybatis;

import cn.hutool.aop.ProxyUtil;
import com.github.pagehelper.PageInterceptor;
import com.schbrain.framework.dao.BaseDao;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.util.List;
import java.util.Properties;

/**
 * description
 *
 * @author liwu on 2019/7/29
 */
public class CustomizeMapperFactoryBean<T> extends MapperFactoryBean<T> {

    public CustomizeMapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    @Override
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
        boolean hasPageInterceptor = interceptorList.stream().anyMatch(PageInterceptor.class::isInstance);
        if (!hasPageInterceptor) {
            configuration.addInterceptor(createPageInterceptor());
        }
        // 创建代理
        BaseDaoInvocationHandler<T> handler = new BaseDaoInvocationHandler<>(originMapperProxy, sqlSession, mapperInterface);
        return ProxyUtil.newProxyInstance(mapperInterface.getClassLoader(), handler, mapperInterface);
    }

    private PageInterceptor createPageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        pageInterceptor.setProperties(new Properties());
        return pageInterceptor;
    }

}
