package com.example.student.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.common.PageResult;
import com.example.student.dto.GradeQueryDTO;
import com.example.student.entity.CourseSelection;
import com.example.student.mapper.ClassMapper;
import com.example.student.mapper.CourseMapper;
import com.example.student.mapper.CourseSelectionMapper;
import com.example.student.mapper.CourseOfferingMapper;
import com.example.student.service.GradeService;
import com.example.student.util.ExcelUtils;
import com.example.student.vo.GradeStatisticsVO;
import com.example.student.vo.StudentGradeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.student.exception.ServiceException;
import com.example.student.util.StrUtil;

/**
 * 成绩服务实现类
 *
 * @author example
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GradeServiceImpl implements GradeService {

    private final CourseSelectionMapper courseSelectionMapper;
    private final CourseMapper courseMapper;
    private final ClassMapper classMapper;
    private final CourseOfferingMapper courseOfferingMapper;

    @Override
    public GradeStatisticsVO getGradeStatistics(GradeQueryDTO queryDTO) {
        if (queryDTO == null) {
            throw new ServiceException("查询参数不能为空");
        }
        
        // 处理全部学期的情况
        String semester = queryDTO.getSemester();
        if ("all".equals(semester)) {
            semester = null; // 设置为null表示查询所有学期
        }
        
        Long courseId = queryDTO.getCourseId();
        Long classId = queryDTO.getClassId();
        Long courseOfferingId = queryDTO.getCourseOfferingId();
        
        // 查询成绩概览
        Map<String, Object> overview = courseSelectionMapper.selectGradeOverviewStats(semester, courseId, classId);
        
        // 查询分数分布
        List<Map<String, Object>> scoreDistribution = courseSelectionMapper.selectScoreDistribution(semester, courseId, classId);
        
        // 查询班级统计
        List<Map<String, Object>> classStats = courseSelectionMapper.selectClassGradeStats(semester, courseId, classId);
        
        // 组装返回结果
        GradeStatisticsVO result = new GradeStatisticsVO();
        result.setOverview(overview);
        result.setScoreDistribution(scoreDistribution);
        result.setClassStats(classStats);
        
        return result;
    }

    @Override
    public void exportGradeReport(GradeQueryDTO queryDTO, HttpServletResponse response) throws IOException {
        // 获取统计数据
        GradeStatisticsVO statistics = getGradeStatistics(queryDTO);
        
        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        
        // 创建样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        // 创建概览sheet
        createOverviewSheet(workbook, statistics, headerStyle, dataStyle);
        
        // 创建班级统计sheet
        createClassStatsSheet(workbook, statistics.getClassStats(), headerStyle, dataStyle);
        
        // 生成文件名
        String fileName = "成绩统计报告_" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + ".xlsx";
        
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        
        // 写入响应
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @Override
    public List<Map<String, Object>> getCourseOptions() {
        return courseMapper.selectCourseOptions();
    }

    @Override
    public List<Map<String, Object>> getClassOptions() {
        return classMapper.selectClassOptions();
    }

    @Override
    public PageResult<StudentGradeVO> getStudentGradesList(Long courseId, String semester, Long classId, String studentName, Integer pageNum, Integer pageSize) {
        // 验证参数
        if (courseId == null || semester == null || semester.isEmpty()) {
            // 完全避免类型推断，使用显式指定类型的空列表和其他参数
            List<StudentGradeVO> emptyList = Collections.emptyList();
            return new PageResult<StudentGradeVO>(emptyList, 0L, 1, 10);
        }
        
        // 创建分页对象
        Page<StudentGradeVO> page = new Page<>(pageNum, pageSize);
        
        // 查询数据 - 确保参数顺序与mapper方法一致
        IPage<StudentGradeVO> result = courseSelectionMapper.selectStudentsWithGrades(
            page, 
            courseId,
            studentName,
            classId,
            semester,
            null); // 不使用courseOfferingId
        
        // 处理数据
        return new PageResult<StudentGradeVO>(
            result.getRecords(),
            result.getTotal(),
            pageNum,
            pageSize
        );
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSubmitGrades(List<Map<String, Object>> grades) {
        if (grades == null || grades.isEmpty()) {
            return false;
        }
        
        try {
            for (Map<String, Object> grade : grades) {
                // 获取选课ID和成绩
                Long selectionId = Long.valueOf(grade.get("selectionId").toString());
                BigDecimal score = null;
                
                // 成绩可能为空，表示清除成绩
                if (grade.get("score") != null && !grade.get("score").toString().isEmpty()) {
                    score = new BigDecimal(grade.get("score").toString());
                    // 成绩范围验证
                    if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(new BigDecimal("100")) > 0) {
                        log.error("成绩 {} 超出范围 [0-100]", score);
                        throw new IllegalArgumentException("成绩必须在0-100之间");
                    }
                }
                
                // 更新选课成绩
                CourseSelection courseSelection = new CourseSelection();
                courseSelection.setId(selectionId);
                courseSelection.setScore(score);
                
                // 如果有成绩且大于等于60分，设置为已修完
                if (score != null && score.compareTo(new BigDecimal("60")) >= 0) {
                    courseSelection.setStatus(2); // 已修完
                } else if (score != null) {
                    courseSelection.setStatus(0); // 已选(不及格)
                }
                
                courseSelectionMapper.updateById(courseSelection);
            }
            return true;
        } catch (Exception e) {
            log.error("批量提交学生成绩失败", e);
            throw e;
        }
    }
    
    @Override
    public List<String> getSemesterList() {
        return courseOfferingMapper.selectAllSemesters();
    }

    /**
     * 创建表头样式
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    /**
     * 创建数据样式
     */
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    /**
     * 创建概览sheet
     */
    private void createOverviewSheet(Workbook workbook, GradeStatisticsVO statistics, 
                                    CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("成绩概览");
        
        // 设置列宽
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        
        // 创建标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("成绩统计概览");
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        
        // 创建概览数据
        Row row1 = sheet.createRow(1);
        Cell cell1_1 = row1.createCell(0);
        cell1_1.setCellValue("总人数");
        cell1_1.setCellStyle(headerStyle);
        Cell cell1_2 = row1.createCell(1);
        cell1_2.setCellValue(getIntValue(statistics.getOverview(), "totalStudents", 0));
        cell1_2.setCellStyle(dataStyle);
        
        Row row2 = sheet.createRow(2);
        Cell cell2_1 = row2.createCell(0);
        cell2_1.setCellValue("平均分");
        cell2_1.setCellStyle(headerStyle);
        Cell cell2_2 = row2.createCell(1);
        cell2_2.setCellValue(formatDouble(getDoubleValue(statistics.getOverview(), "averageScore", 0.0), 2));
        cell2_2.setCellStyle(dataStyle);
        
        Row row3 = sheet.createRow(3);
        Cell cell3_1 = row3.createCell(0);
        cell3_1.setCellValue("及格率");
        cell3_1.setCellStyle(headerStyle);
        Cell cell3_2 = row3.createCell(1);
        Double passRate = 0.0;
        Integer passCount = getIntValue(statistics.getOverview(), "passCount", 0);
        Integer totalWithScore = getIntValue(statistics.getOverview(), "totalWithScore", 0);
        if (totalWithScore > 0) {
            passRate = (double) passCount / totalWithScore;
        }
        cell3_2.setCellValue(formatPercent(passRate));
        cell3_2.setCellStyle(dataStyle);
        
        Row row4 = sheet.createRow(4);
        Cell cell4_1 = row4.createCell(0);
        cell4_1.setCellValue("优秀率");
        cell4_1.setCellStyle(headerStyle);
        Cell cell4_2 = row4.createCell(1);
        Double excellentRate = 0.0;
        Integer excellentCount = getIntValue(statistics.getOverview(), "excellentCount", 0);
        if (totalWithScore > 0) {
            excellentRate = (double) excellentCount / totalWithScore;
        }
        cell4_2.setCellValue(formatPercent(excellentRate));
        cell4_2.setCellStyle(dataStyle);
        
        // 创建成绩分布表
        Row distributionTitleRow = sheet.createRow(6);
        Cell distTitleCell = distributionTitleRow.createCell(0);
        distTitleCell.setCellValue("成绩分布");
        distTitleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 0, 1));
        
        Row distHeaderRow = sheet.createRow(7);
        Cell distHeader1 = distHeaderRow.createCell(0);
        distHeader1.setCellValue("分数段");
        distHeader1.setCellStyle(headerStyle);
        Cell distHeader2 = distHeaderRow.createCell(1);
        distHeader2.setCellValue("人数");
        distHeader2.setCellStyle(headerStyle);
        
        // 获取分数分布数据
        List<Map<String, Object>> scoreDistribution = statistics.getScoreDistribution();
        int rowIndex = 8;
        if (scoreDistribution != null && !scoreDistribution.isEmpty()) {
            for (Map<String, Object> dist : scoreDistribution) {
                Row distRow = sheet.createRow(rowIndex++);
                Cell distCell1 = distRow.createCell(0);
                distCell1.setCellValue(String.valueOf(dist.get("scoreRange")));
                distCell1.setCellStyle(dataStyle);
                Cell distCell2 = distRow.createCell(1);
                distCell2.setCellValue(getIntValue(dist, "count", 0));
                distCell2.setCellStyle(dataStyle);
            }
        } else {
            // 创建空的分布数据行
            Row distRow1 = sheet.createRow(8);
            Cell distCell1_1 = distRow1.createCell(0);
            distCell1_1.setCellValue("<60分");
            distCell1_1.setCellStyle(dataStyle);
            Cell distCell1_2 = distRow1.createCell(1);
            distCell1_2.setCellValue(0);
            distCell1_2.setCellStyle(dataStyle);
            
            Row distRow2 = sheet.createRow(9);
            Cell distCell2_1 = distRow2.createCell(0);
            distCell2_1.setCellValue("60-69分");
            distCell2_1.setCellStyle(dataStyle);
            Cell distCell2_2 = distRow2.createCell(1);
            distCell2_2.setCellValue(0);
            distCell2_2.setCellStyle(dataStyle);
            
            Row distRow3 = sheet.createRow(10);
            Cell distCell3_1 = distRow3.createCell(0);
            distCell3_1.setCellValue("70-79分");
            distCell3_1.setCellStyle(dataStyle);
            Cell distCell3_2 = distRow3.createCell(1);
            distCell3_2.setCellValue(0);
            distCell3_2.setCellStyle(dataStyle);
            
            Row distRow4 = sheet.createRow(11);
            Cell distCell4_1 = distRow4.createCell(0);
            distCell4_1.setCellValue("80-89分");
            distCell4_1.setCellStyle(dataStyle);
            Cell distCell4_2 = distRow4.createCell(1);
            distCell4_2.setCellValue(0);
            distCell4_2.setCellStyle(dataStyle);
            
            Row distRow5 = sheet.createRow(12);
            Cell distCell5_1 = distRow5.createCell(0);
            distCell5_1.setCellValue("90-100分");
            distCell5_1.setCellStyle(dataStyle);
            Cell distCell5_2 = distRow5.createCell(1);
            distCell5_2.setCellValue(0);
            distCell5_2.setCellStyle(dataStyle);
        }
    }

    /**
     * 从Map中获取整数值
     */
    private Integer getIntValue(Map<String, Object> map, String key, Integer defaultValue) {
        if (map == null || !map.containsKey(key) || map.get(key) == null) {
            return defaultValue;
        }
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.valueOf(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /**
     * 从Map中获取浮点值
     */
    private Double getDoubleValue(Map<String, Object> map, String key, Double defaultValue) {
        if (map == null || !map.containsKey(key) || map.get(key) == null) {
            return defaultValue;
        }
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.valueOf(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 创建班级统计sheet
     */
    private void createClassStatsSheet(Workbook workbook, List<Map<String, Object>> classStats,
                                      CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("班级成绩统计");
        
        // 设置列宽
        sheet.setColumnWidth(0, 25 * 256);
        sheet.setColumnWidth(1, 15 * 256);
        sheet.setColumnWidth(2, 15 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(6, 15 * 256);
        
        // 创建标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("班级成绩统计");
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
        
        // 创建表头
        Row headerRow = sheet.createRow(1);
        String[] headers = {"班级", "学生人数", "平均分", "最高分", "最低分", "及格率", "优秀率"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 填充数据
        int rowNum = 2;
        if (classStats != null) {
            for (Map<String, Object> stats : classStats) {
                Row row = sheet.createRow(rowNum++);
                
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(String.valueOf(stats.getOrDefault("className", "--")));
                cell0.setCellStyle(dataStyle);
                
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(getIntValue(stats, "totalStudents", 0));
                cell1.setCellStyle(dataStyle);
                
                Cell cell2 = row.createCell(2);
                Double avgScore = getDoubleValue(stats, "averageScore", null);
                cell2.setCellValue(avgScore != null ? formatDouble(avgScore, 2) : "--");
                cell2.setCellStyle(dataStyle);
                
                // 估计最高分和最低分没有在当前数据中
                Cell cell3 = row.createCell(3);
                cell3.setCellValue("--"); // 最高分暂不显示
                cell3.setCellStyle(dataStyle);
                
                Cell cell4 = row.createCell(4);
                cell4.setCellValue("--"); // 最低分暂不显示
                cell4.setCellStyle(dataStyle);
                
                // 计算及格率
                Cell cell5 = row.createCell(5);
                Integer passCount = getIntValue(stats, "passCount", 0);
                Integer failCount = getIntValue(stats, "failCount", 0);
                Integer total = passCount + failCount;
                Double passRate = total > 0 ? (double) passCount / total : 0.0;
                cell5.setCellValue(formatPercent(passRate));
                cell5.setCellStyle(dataStyle);
                
                // 计算优秀率
                Cell cell6 = row.createCell(6);
                Integer excellentCount = getIntValue(stats, "excellentCount", 0);
                Double excellentRate = total > 0 ? (double) excellentCount / total : 0.0;
                cell6.setCellValue(formatPercent(excellentRate));
                cell6.setCellStyle(dataStyle);
            }
        }
    }

    /**
     * 格式化双精度数字
     */
    private String formatDouble(Double value, int scale) {
        if (value == null) {
            return "--";
        }
        return BigDecimal.valueOf(value)
                .setScale(scale, RoundingMode.HALF_UP)
                .toString();
    }

    /**
     * 格式化百分比
     */
    private String formatPercent(Double value) {
        if (value == null) {
            return "0.00%";
        }
        return BigDecimal.valueOf(value * 100)
                .setScale(2, RoundingMode.HALF_UP)
                .toString() + "%";
    }

    @Override
    public List<StudentGradeVO> getStudentGradeList(Page<StudentGradeVO> page, Long courseOfferingId, String semester, Long classId, String keyword) {
        if (courseOfferingId == null || StrUtil.isBlank(semester)) {
            return Collections.emptyList();
        }
        
        // 查询学生成绩列表
        IPage<StudentGradeVO> result = courseSelectionMapper.selectStudentsWithGrades(
                page, 
                null, // 使用courseOfferingId查询，不需要courseId
                keyword,
                classId,
                semester,
                courseOfferingId);
        
        return result.getRecords();
    }
} 