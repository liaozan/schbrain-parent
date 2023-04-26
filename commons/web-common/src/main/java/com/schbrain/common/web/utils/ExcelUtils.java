package com.schbrain.common.web.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.schbrain.common.util.support.excel.exception.ExcelException;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author liaozan
 * @since 2022/8/24
 */
public class ExcelUtils extends com.schbrain.common.util.ExcelUtils {

    public static <T> void writeToResponse(List<T> dataList, String fileName) {
        if (CollectionUtils.isEmpty(dataList)) {
            throw new ExcelException("DataList is empty");
        }
        writeToResponse(dataList, dataList.get(0).getClass(), fileName);
    }

    public static <T> void writeToResponse(List<T> dataList, Class<?> head, String fileName) {
        if (CollectionUtils.isEmpty(dataList)) {
            throw new ExcelException("DataList is empty");
        }
        try {
            HttpServletResponse response = ServletUtils.getResponse();
            ContentDisposition contentDisposition = ContentDisposition
                    .attachment()
                    .filename(fileName, StandardCharsets.UTF_8)
                    .build();
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
            ServletOutputStream outputStream = response.getOutputStream();
            EasyExcel.write(outputStream)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet()
                    .head(head)
                    .doWrite(dataList);
        } catch (IOException e) {
            throw new ExcelException("Excel download fail", e);
        }
    }

}