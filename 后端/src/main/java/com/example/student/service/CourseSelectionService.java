package com.example.student.service;

import com.example.student.common.PageResult;
import com.example.student.dto.CourseSelectionDTO;
import com.example.student.dto.CourseSelectionQueryDTO;
import com.example.student.vo.CourseSelectionManageVO;
import com.example.student.vo.CourseSelectionStatisticsVO;
import com.example.student.vo.CourseTypeStatisticsVO;
import com.example.student.vo.SemesterStatisticsVO;
import com.example.student.vo.SemesterVO;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 选课服务接口
 *
 * @author example
 */
public interface CourseSelectionService {

    /**
     * 查询选课列表
     *
     * @param queryDTO 查询参数
     * @return 选课列表
     */
    PageResult<CourseSelectionManageVO> getSelectionList(CourseSelectionQueryDTO queryDTO);

    /**
     * 根据ID获取选课信息
     *
     * @param id 选课ID
     * @return 选课信息
     */
    CourseSelectionManageVO getSelectionById(Long id);

    /**
     * 更新选课信息
     *
     * @param courseSelectionDTO 选课信息
     * @return 是否成功
     */
    boolean updateSelection(CourseSelectionDTO courseSelectionDTO);

    /**
     * 退课操作
     *
     * @param id 选课ID
     * @return 是否成功
     */
    boolean withdrawCourse(Long id);

    /**
     * 批量退课操作
     *
     * @param ids 选课ID列表
     * @return 是否成功
     */
    boolean batchWithdrawCourses(List<Long> ids);

    /**
     * 导出选课数据
     *
     * @param queryDTO 查询参数
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    void exportSelection(CourseSelectionQueryDTO queryDTO, HttpServletResponse response) throws IOException;

    /**
     * 获取选课统计数据
     *
     * @return 选课统计数据
     */
    CourseSelectionStatisticsVO getSelectionStatistics();

    /**
     * 获取课程类型选课比例数据
     *
     * @return 课程类型选课比例数据
     */
    List<CourseTypeStatisticsVO> getCourseTypeStatistics();

    /**
     * 获取学期选课统计数据
     *
     * @return 学期选课统计数据
     */
    List<SemesterStatisticsVO> getSemesterStatistics();

    /**
     * 获取学期列表
     *
     * @return 学期列表
     */
    List<SemesterVO> getSemesters();

    /**
     * 生成选课报表
     *
     * @param params 参数
     * @return 是否成功
     */
    boolean generateSelectionReport(Map<String, Object> params);

    /**
     * 获取学生已选课程
     *
     * @param studentId 学生ID
     * @param params 查询参数
     * @return 已选课程分页列表
     */
    PageResult<Map<String, Object>> getStudentSelectedCourses(Long studentId, Map<String, Object> params);

    /**
     * 获取课程详情
     *
     * @param courseId 课程ID
     * @return 课程详情
     */
    Map<String, Object> getCourseDetail(Long courseId);

    /**
     * 获取课程教材信息
     *
     * @param courseId 课程ID
     * @return 教材列表
     */
    List<Map<String, Object>> getCourseMaterials(Long courseId);

    /**
     * 获取学生可选课程列表
     *
     * @param studentId 学生ID
     * @param params 查询参数
     * @return 可选课程分页列表
     */
    PageResult<Map<String, Object>> getAvailableCourses(Long studentId, Map<String, Object> params);

    /**
     * 学生选课
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 是否成功
     */
    boolean selectCourse(Long studentId, Long courseId);

    /**
     * 学生退选课程
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 是否成功
     */
    boolean withdrawCourse(Long studentId, Long courseId);

    /**
     * 获取选课设置
     *
     * @return 选课设置信息
     */
    Map<String, Object> getSelectionSettings();

    /**
     * 检查课程时间冲突
     *
     * @param courseTime1 课程时间1
     * @param courseTime2 课程时间2
     * @return 是否冲突
     */
    boolean checkTimeConflict(String courseTime1, String courseTime2);
    
    /**
     * 获取选课统计信息
     * 
     * @param params 查询参数
     * @return 选课统计信息
     */
    Map<String, Object> getSelectionStats(Map<String, Object> params);
    
    /**
     * 为班级创建选课记录
     * 
     * @param courseOfferingId 课程开设ID
     * @param classId 班级ID
     * @return 是否成功
     */
    boolean createSelectionsForClass(Long courseOfferingId, Long classId);
    
    /**
     * 取消班级选课记录
     * 
     * @param courseOfferingId 课程开设ID
     * @param classId 班级ID
     * @return 是否成功
     */
    boolean cancelSelectionsForClass(Long courseOfferingId, Long classId);
    
    /**
     * 删除指定课程开设的所有选课记录
     * 
     * @param courseOfferingId 课程开设ID
     * @return 是否成功
     */
    boolean deleteByOfferingId(Long courseOfferingId);
} 