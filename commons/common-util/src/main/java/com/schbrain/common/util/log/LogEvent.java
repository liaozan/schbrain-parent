package com.schbrain.common.util.log;

import lombok.Data;

@Data
public class LogEvent<T extends LogEventAction> {

    /**
     * @see com.schbrain.common.constants.LogConstants.ProductTypeEnum
     */
    private String product;

    private T eventAction;

    public LogEvent(String product) {
        this.product = product;
    }

}