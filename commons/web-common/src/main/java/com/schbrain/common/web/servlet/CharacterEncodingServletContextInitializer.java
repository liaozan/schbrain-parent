package com.schbrain.common.web.servlet;

import org.springframework.boot.web.servlet.ServletContextInitializer;

import javax.servlet.ServletContext;

/**
 * @author liaozan
 * @since 2022/11/11
 */
public class CharacterEncodingServletContextInitializer implements ServletContextInitializer {

    private final String encoding;

    public CharacterEncodingServletContextInitializer(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public void onStartup(ServletContext servletContext) {
        servletContext.setRequestCharacterEncoding(encoding);
        servletContext.setResponseCharacterEncoding(encoding);
    }

}
