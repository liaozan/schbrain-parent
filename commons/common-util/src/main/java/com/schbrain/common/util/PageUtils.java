package com.schbrain.common.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schbrain.common.entity.PageParam;
import com.schbrain.common.entity.PaginationInfo;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static com.schbrain.common.constants.PageConstants.DEFAULT_SEARCH_COUNT;
import static com.schbrain.common.constants.PageConstants.DEFAULT_TOTAL_COUNT;

/**
 * @author liaozan
 * @since 2022/1/7
 */
public class PageUtils {

    public static <T> IPage<T> fromParam(PageParam pageParam) {
        return fromParam(pageParam.getPageIndex(), pageParam.getPageSize(), pageParam.isSearchCount());
    }

    public static <T> IPage<T> fromParam(long pageIndex, long pageSize) {
        return fromParam(pageIndex, pageSize, DEFAULT_SEARCH_COUNT);
    }

    public static <T> IPage<T> fromParam(long pageIndex, long pageSize, boolean searchCount) {
        return Page.of(pageIndex, pageSize, searchCount);
    }

    public static <T> PaginationInfo<T> fromResult(IPage<T> page) {
        return fromResult(page, page.getRecords());
    }

    public static <T, R> PaginationInfo<R> fromResult(IPage<T> page, Function<T, R> mapper) {
        return fromResult(page.convert(mapper));
    }

    public static <T, R> PaginationInfo<R> fromResult(IPage<T> page, List<R> dataList) {
        return new PaginationInfo<>(page.getCurrent(), page.getSize(), page.getTotal(), dataList);
    }

    public static <T, R> PaginationInfo<R> fromResult(PaginationInfo<T> info, Function<T, R> mapper) {
        List<R> dataList = StreamUtils.toList(info.getDataList(), mapper);
        return fromResult(info, dataList);
    }

    public static <T, R> PaginationInfo<R> fromResult(PaginationInfo<T> info, List<R> dataList) {
        return new PaginationInfo<>(info.getPageIndex(), info.getPageSize(), info.getTotalCount(), dataList);
    }

    public static <T> PaginationInfo<T> emptyResult(IPage<T> page) {
        return new PaginationInfo<>(page.getCurrent(), page.getSize(), DEFAULT_TOTAL_COUNT, Collections.emptyList());
    }

    public static <T> PaginationInfo<T> emptyResult(PageParam pageParam) {
        return new PaginationInfo<>(pageParam.getPageIndex(), pageParam.getPageSize(), DEFAULT_TOTAL_COUNT, Collections.emptyList());
    }

    public static <T> PaginationInfo<T> emptyResult(PaginationInfo<?> page) {
        return new PaginationInfo<>(page.getPageIndex(), page.getPageSize(), DEFAULT_TOTAL_COUNT, Collections.emptyList());
    }

    public static PageParam toParam(IPage<?> page) {
        PageParam pageParam = new PageParam();
        pageParam.setPageIndex(Math.toIntExact(page.getCurrent()));
        pageParam.setPageSize(Math.toIntExact(page.getSize()));
        pageParam.setSearchCount(page.searchCount());
        return pageParam;
    }

}