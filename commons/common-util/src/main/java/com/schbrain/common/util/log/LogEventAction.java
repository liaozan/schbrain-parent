package com.schbrain.common.util.log;

import java.util.HashMap;

public class LogEventAction extends HashMap<Object, Object> {

    private static final long serialVersionUID = -8663962491116732785L;

    private static final String NAME = "name";

    public LogEventAction(String actionName) {
        put(NAME, actionName);
    }

    public String getActionName() {
        return (String) get(NAME);
    }

    public void setActionName(String actionName) {
        put(NAME, actionName);
    }

    public void setActionParam(Object key, Object value) {
        put(key, value);
    }

    public Object getActionParam(Object key) {
        return get(key);
    }

}