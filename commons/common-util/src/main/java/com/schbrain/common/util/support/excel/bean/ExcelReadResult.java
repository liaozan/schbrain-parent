package com.schbrain.common.util.support.excel.bean;

import com.google.common.collect.Table;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author liaozan
 * @since 2022/1/6
 */
@Data
public class ExcelReadResult<T> {

    private List<T> dataList;

    private Map<Integer, String> headMap;

    private Table<String, Integer, String> errors;

    private String errorsAsString;

    public boolean hasError() {
        return !errors.isEmpty();
    }

}