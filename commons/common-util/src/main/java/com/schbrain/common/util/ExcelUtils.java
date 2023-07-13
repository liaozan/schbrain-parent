package com.schbrain.common.util;

import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.excel.EasyExcel;
import com.schbrain.common.util.support.excel.bean.ExcelReadResult;
import com.schbrain.common.util.support.excel.exception.ExcelException;
import com.schbrain.common.util.support.excel.listener.ExcelBeanReadListener;
import com.schbrain.common.util.support.excel.listener.ExcelMapDataReadListener;
import com.schbrain.common.util.support.excel.listener.ExcelReadListenerBase;
import com.schbrain.common.util.support.excel.listener.HierarchicalDataReadListener;
import com.schbrain.common.util.support.excel.listener.HierarchicalDataReadListener.ImportedRecord;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * @author liaozan
 * @since 2022/1/6
 */
public class ExcelUtils extends ExcelUtil {

    // file
    public static ExcelReadResult<Map<Integer, Object>> read(File excelFile) {
        return getMapExcelReadResult(excelFile);
    }

    public static <T> ExcelReadResult<T> readAs(Class<T> contentType, File excelFile) {
        return getObjectExcelReadResult(contentType, excelFile);
    }

    public static <T> ExcelReadResult<T> readAs(Class<T> contentType, File excelFile, ExcelReadListenerBase<T> readListener) {
        EasyExcel.read(excelFile, contentType, readListener).doReadAll();
        return readListener.getReadResult();
    }

    public static List<ImportedRecord> readHierarchicalData(File excelFile) {
        HierarchicalDataReadListener readListener = new HierarchicalDataReadListener();
        return readHierarchicalData(excelFile, readListener);
    }

    public static List<ImportedRecord> readHierarchicalData(File excelFile, HierarchicalDataReadListener readListener) {
        EasyExcel.read(excelFile, readListener).doReadAll();
        return readListener.getImportedRecords();
    }

    // stream
    public static ExcelReadResult<Map<Integer, Object>> read(InputStream inputStream) {
        return getMapExcelReadResult(inputStream);
    }

    public static <T> ExcelReadResult<T> readAs(Class<T> contentType, InputStream inputStream) {
        return getObjectExcelReadResult(contentType, inputStream);
    }

    public static <T> ExcelReadResult<T> readAs(Class<T> contentType, InputStream inputStream, ExcelReadListenerBase<T> readListener) {
        EasyExcel.read(inputStream, contentType, readListener).doReadAll();
        return readListener.getReadResult();
    }

    public static List<ImportedRecord> readHierarchicalData(InputStream inputStream) {
        HierarchicalDataReadListener readListener = new HierarchicalDataReadListener();
        return readHierarchicalData(inputStream, readListener);
    }

    public static List<ImportedRecord> readHierarchicalData(InputStream inputStream, HierarchicalDataReadListener readListener) {
        EasyExcel.read(inputStream, readListener).doReadAll();
        return readListener.getImportedRecords();
    }

    // write
    public static <T> Path writeTo(Path location, List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            throw new ExcelException("DataList is empty");
        }
        return writeTo(location, dataList, dataList.get(0).getClass());
    }

    public static <T> Path writeTo(Path location, List<T> dataList, Class<?> head) {
        if (CollectionUtils.isEmpty(dataList)) {
            throw new ExcelException("DataList is empty");
        }
        EasyExcel.write(location.toFile())
                .sheet()
                .head(head)
                .doWrite(dataList);
        return location;
    }

    public static <T> OutputStream writeTo(OutputStream outputStream, List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            throw new ExcelException("DataList is empty");
        }
        return writeTo(outputStream, dataList, dataList.get(0).getClass());
    }

    public static <T> OutputStream writeTo(OutputStream outputStream, List<T> dataList, Class<?> head) {
        if (CollectionUtils.isEmpty(dataList)) {
            throw new ExcelException("DataList is empty");
        }
        EasyExcel.write(outputStream)
                .sheet()
                .head(head)
                .doWrite(dataList);
        return outputStream;
    }

    private static ExcelReadResult<Map<Integer, Object>> getMapExcelReadResult(Object excelFile) {
        ExcelMapDataReadListener readListener = new ExcelMapDataReadListener();
        if (excelFile instanceof File) {
            EasyExcel.read((File) excelFile, readListener).doReadAll();
        } else if (excelFile instanceof InputStream) {
            EasyExcel.read((InputStream) excelFile, readListener).doReadAll();
        } else {
            throw new ExcelException("Unsupported excel file");
        }
        return readListener.getReadResult();
    }

    private static <T> ExcelReadResult<T> getObjectExcelReadResult(Class<T> contentType, Object excelFile) {
        ExcelReadListenerBase<T> readListener = new ExcelBeanReadListener<>();
        if (excelFile instanceof File) {
            EasyExcel.read((File) excelFile, contentType, readListener).doReadAll();
        } else if (excelFile instanceof InputStream) {
            EasyExcel.read((InputStream) excelFile, contentType, readListener).doReadAll();
        } else {
            throw new ExcelException("Unsupported excel file");
        }
        return readListener.getReadResult();
    }

}
