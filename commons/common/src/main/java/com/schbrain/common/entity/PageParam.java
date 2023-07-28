package com.schbrain.common.entity;

import com.schbrain.common.constants.PageConstants;
import lombok.Data;

/**
 * @author liaozan
 * @since 2022/1/7
 */
@Data
public class PageParam {

    private int pageIndex = PageConstants.DEFAULT_PAGE_INDEX;

    private int pageSize = PageConstants.DEFAULT_PAGE_SIZE;

    private boolean searchCount = PageConstants.DEFAULT_SEARCH_COUNT;

}
