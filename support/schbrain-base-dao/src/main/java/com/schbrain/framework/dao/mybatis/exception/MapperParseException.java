package com.schbrain.framework.dao.mybatis.exception;

/**
 * description
 *
 * @author liwu on 2019/8/1
 */
public class MapperParseException extends RuntimeException {

    private static final long serialVersionUID = -4680549953650814744L;

    public MapperParseException() {
        super();
    }

    public MapperParseException(String message) {
        super(message);
    }

}