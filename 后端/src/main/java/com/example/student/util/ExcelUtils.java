package com.example.student.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Excel工具类
 *
 * @author example
 */
@Slf4j
public class ExcelUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 读取Excel文件
     *
     * @param file Excel文件
     * @param clazz 实体类
     * @param headerMap 表头映射 key:Excel表头 value:实体属性名
     * @param <T> 泛型
     * @return 实体列表
     * @throws IOException IO异常
     */
    public static <T> List<T> readExcel(MultipartFile file, Class<T> clazz, Map<String, String> headerMap) throws IOException {
        List<T> list = new ArrayList<>();
        
        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // 读取表头
            Row headerRow = sheet.getRow(0);
            Map<Integer, String> colFieldMap = new HashMap<>();
            for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
                Cell cell = headerRow.getCell(i);
                String headerName = cell.getStringCellValue().trim();
                String fieldName = headerMap.get(headerName);
                if (fieldName != null) {
                    colFieldMap.put(i, fieldName);
                }
            }
            
            // 读取数据
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                
                try {
                    T entity = clazz.getDeclaredConstructor().newInstance();
                    
                    for (Map.Entry<Integer, String> entry : colFieldMap.entrySet()) {
                        int colIndex = entry.getKey();
                        String fieldName = entry.getValue();
                        Cell cell = row.getCell(colIndex);
                        
                        if (cell != null) {
                            Field field = clazz.getDeclaredField(fieldName);
                            field.setAccessible(true);
                            
                            setCellValueToField(cell, entity, field);
                        }
                    }
                    
                    list.add(entity);
                } catch (Exception e) {
                    log.error("Excel解析第{}行出错", i + 1, e);
                }
            }
        }
        
        return list;
    }
    
    /**
     * 导出Excel文件
     *
     * @param dataList 数据列表
     * @param headers 表头数组
     * @param fields 字段数组
     * @param processRow 行处理回调
     * @param <T> 泛型
     * @return Workbook对象
     */
    public static <T> Workbook exportExcel(List<T> dataList, String[] headers, String[] fields, BiConsumer<Row, T> processRow) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        
        // 创建表头
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        
        // 填充数据
        if (dataList != null && !dataList.isEmpty()) {
            for (int i = 0; i < dataList.size(); i++) {
                Row row = sheet.createRow(i + 1);
                T data = dataList.get(i);
                
                if (processRow != null) {
                    processRow.accept(row, data);
                } else {
                    try {
                        Class<?> clazz = data.getClass();
                        for (int j = 0; j < fields.length; j++) {
                            Field field = clazz.getDeclaredField(fields[j]);
                            field.setAccessible(true);
                            Object value = field.get(data);
                            
                            Cell cell = row.createCell(j);
                            setFieldValueToCell(cell, value);
                        }
                    } catch (Exception e) {
                        log.error("Excel导出第{}行出错", i + 1, e);
                    }
                }
            }
        }
        
        return workbook;
    }
    
    /**
     * 将单元格值设置到字段
     *
     * @param cell 单元格
     * @param entity 实体
     * @param field 字段
     * @throws Exception 异常
     */
    private static void setCellValueToField(Cell cell, Object entity, Field field) throws Exception {
        Class<?> fieldType = field.getType();
        
        switch (cell.getCellType()) {
            case STRING:
                String stringValue = cell.getStringCellValue();
                if (fieldType == String.class) {
                    field.set(entity, stringValue);
                } else if (fieldType == Integer.class || fieldType == int.class) {
                    field.set(entity, Integer.parseInt(stringValue));
                } else if (fieldType == Long.class || fieldType == long.class) {
                    field.set(entity, Long.parseLong(stringValue));
                } else if (fieldType == Double.class || fieldType == double.class) {
                    field.set(entity, Double.parseDouble(stringValue));
                } else if (fieldType == BigDecimal.class) {
                    field.set(entity, new BigDecimal(stringValue));
                } else if (fieldType == LocalDate.class) {
                    field.set(entity, LocalDate.parse(stringValue, DATE_FORMATTER));
                } else if (fieldType == LocalDateTime.class) {
                    field.set(entity, LocalDateTime.parse(stringValue, DATETIME_FORMATTER));
                } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                    field.set(entity, Boolean.parseBoolean(stringValue));
                }
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    if (fieldType == LocalDate.class) {
                        field.set(entity, date.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDate());
                    } else if (fieldType == LocalDateTime.class) {
                        field.set(entity, date.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDateTime());
                    } else if (fieldType == Date.class) {
                        field.set(entity, date);
                    }
                } else {
                    double numericValue = cell.getNumericCellValue();
                    if (fieldType == Integer.class || fieldType == int.class) {
                        field.set(entity, (int) numericValue);
                    } else if (fieldType == Long.class || fieldType == long.class) {
                        field.set(entity, (long) numericValue);
                    } else if (fieldType == Double.class || fieldType == double.class) {
                        field.set(entity, numericValue);
                    } else if (fieldType == BigDecimal.class) {
                        field.set(entity, BigDecimal.valueOf(numericValue));
                    } else if (fieldType == String.class) {
                        field.set(entity, String.valueOf(numericValue));
                    }
                }
                break;
            case BOOLEAN:
                boolean booleanValue = cell.getBooleanCellValue();
                if (fieldType == Boolean.class || fieldType == boolean.class) {
                    field.set(entity, booleanValue);
                } else if (fieldType == String.class) {
                    field.set(entity, String.valueOf(booleanValue));
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * 将字段值设置到单元格
     *
     * @param cell 单元格
     * @param value 字段值
     */
    private static void setFieldValueToCell(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
            return;
        }
        
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) value).doubleValue());
        } else if (value instanceof LocalDate) {
            cell.setCellValue(((LocalDate) value).format(DATE_FORMATTER));
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue(((LocalDateTime) value).format(DATETIME_FORMATTER));
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 导出Excel文件（兼容旧版ExcelUtil接口）
     *
     * @param list 数据列表
     * @param sheetName 工作表名称
     * @param clazz 实体类
     * @param response HTTP响应
     * @param <T> 泛型
     * @throws IOException IO异常
     */
    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, HttpServletResponse response) throws IOException {
        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode(sheetName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            
            // 使用EasyExcel导出
            EasyExcel.write(response.getOutputStream(), clazz).sheet(sheetName).doWrite(list);
        } catch (Exception e) {
            log.error("导出Excel异常", e);
            throw new IOException("导出Excel失败: " + e.getMessage(), e);
        }
    }
} 