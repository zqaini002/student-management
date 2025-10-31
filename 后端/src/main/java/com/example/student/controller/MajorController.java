package com.example.student.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.common.Result;
import com.example.student.entity.Major;
import com.example.student.mapper.MajorMapper;
import com.example.student.vo.MajorVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 专业控制器
 *
 * @author example
 */
@Tag(name = "专业管理", description = "专业管理相关接口")
@RestController
@RequestMapping("/education/major")
@Slf4j
public class MajorController {

    @Resource
    private MajorMapper majorMapper;

    /**
     * 获取专业列表（分页）
     * 
     * @param current 当前页
     * @param size 每页数量
     * @param name 专业名称（可选）
     * @param code 专业编码（可选）
     * @param departmentId 所属院系ID（可选）
     * @return 分页专业列表
     */
    @Operation(summary = "获取专业列表", description = "分页获取专业列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('education:major:list')")
    public Result<Page<MajorVO>> getMajorList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Long departmentId) {
        log.info("查询专业列表，参数：pageNum={}, pageSize={}, name={}, code={}, departmentId={}",
                pageNum, pageSize, name, code, departmentId);
        try {
            Page<Major> page = new Page<>(pageNum, pageSize);
            
            // 构建查询条件
            Major queryMajor = new Major();
            queryMajor.setName(name);
            queryMajor.setCode(code);
            queryMajor.setDepartmentId(departmentId);
            
            // 使用关联查询获取带有院系名称的专业列表
            Page<MajorVO> majorVOPage = majorMapper.selectMajorVOPage(page, queryMajor);
            
            log.info("查询到专业数量：{}", majorVOPage.getTotal());
            return Result.success(majorVOPage);
        } catch (Exception e) {
            log.error("查询专业列表失败", e);
            return Result.error("获取专业列表失败：" + e.getMessage());
        }
    }

    /**
     * 添加专业
     *
     * @param major 专业信息
     * @return 添加结果
     */
    @Operation(summary = "添加专业", description = "添加专业信息")
    @PostMapping
    @PreAuthorize("hasAuthority('education:major:add')")
    public Result<Void> addMajor(@RequestBody Major major) {
        log.info("添加专业：{}", major);
        try {
            // 校验专业编码唯一性
            LambdaQueryWrapper<Major> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Major::getCode, major.getCode());
            if (majorMapper.selectCount(queryWrapper) > 0) {
                log.warn("添加专业失败：专业编码已存在，code={}", major.getCode());
                return Result.error("专业编码已存在");
            }
            
            // 设置创建时间和更新时间
            LocalDateTime now = LocalDateTime.now();
            major.setCreateTime(now);
            major.setUpdateTime(now);
            
            int rows = majorMapper.insert(major);
            if (rows > 0) {
                log.info("添加专业成功，ID：{}", major.getId());
                return Result.success("添加专业成功");
            } else {
                log.warn("添加专业失败");
                return Result.error("添加专业失败");
            }
        } catch (Exception e) {
            log.error("添加专业失败", e);
            return Result.error("添加专业失败：" + e.getMessage());
        }
    }

    /**
     * 更新专业
     *
     * @param major 专业信息
     * @return 更新结果
     */
    @Operation(summary = "更新专业", description = "更新专业信息")
    @PutMapping
    @PreAuthorize("hasAuthority('education:major:edit')")
    public Result<Void> updateMajor(@RequestBody Major major) {
        log.info("更新专业：{}", major);
        try {
            if (major.getId() == null) {
                log.warn("更新专业失败：ID不能为空");
                return Result.error("专业ID不能为空");
            }
            
            // 检查专业是否存在
            Major existingMajor = majorMapper.selectById(major.getId());
            if (existingMajor == null) {
                log.warn("更新专业失败：专业不存在，ID：{}", major.getId());
                return Result.error("专业不存在");
            }
            
            // 校验专业编码唯一性（排除自身）
            if (!existingMajor.getCode().equals(major.getCode())) {
                LambdaQueryWrapper<Major> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Major::getCode, major.getCode());
                if (majorMapper.selectCount(queryWrapper) > 0) {
                    log.warn("更新专业失败：专业编码已存在，code={}", major.getCode());
                    return Result.error("专业编码已存在");
                }
            }
            
            // 设置更新时间
            major.setUpdateTime(LocalDateTime.now());
            // 保持创建时间不变
            major.setCreateTime(existingMajor.getCreateTime());
            
            int rows = majorMapper.updateById(major);
            if (rows > 0) {
                log.info("更新专业成功，ID：{}", major.getId());
                return Result.success("更新专业成功");
            } else {
                log.warn("更新专业失败，ID：{}", major.getId());
                return Result.error("更新专业失败");
            }
        } catch (Exception e) {
            log.error("更新专业失败", e);
            return Result.error("更新专业失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有专业（不分页）
     *
     * @return 专业列表
     */
    @Operation(summary = "获取所有专业", description = "获取所有专业，不分页")
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('education:major:list')")
    public Result<List<MajorVO>> getAllMajors() {
        log.info("开始查询所有专业信息");
        try {
            // 使用关联查询获取带有院系名称的专业列表
            List<MajorVO> majorList = majorMapper.selectMajorVOList();
            log.info("查询到{}个专业信息", majorList.size());
            return Result.success(majorList);
        } catch (Exception e) {
            log.error("查询专业列表失败", e);
            return Result.error("获取专业列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取特定院系下的专业
     *
     * @param departmentId 院系ID
     * @return 专业列表
     */
    @Operation(summary = "获取院系下的专业", description = "获取特定院系下的所有专业")
    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasAuthority('education:major:list')")
    public Result<List<MajorVO>> getMajorsByDepartment(
            @PathVariable @Parameter(description = "院系ID") Long departmentId) {
        log.info("查询院系下的专业，院系ID：{}", departmentId);
        try {
            // 构建查询条件
            Major queryMajor = new Major();
            queryMajor.setDepartmentId(departmentId);
            
            // 使用关联查询获取带有院系名称的专业列表
            Page<Major> page = new Page<>(1, 1000); // 使用大页码查询所有数据
            Page<MajorVO> majorVOPage = majorMapper.selectMajorVOPage(page, queryMajor);
            
            log.info("查询到院系{}下的{}个专业", departmentId, majorVOPage.getRecords().size());
            return Result.success(majorVOPage.getRecords());
        } catch (Exception e) {
            log.error("查询院系下的专业失败", e);
            return Result.error("获取院系下的专业失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有专业（不分页，用于下拉选择）
     *
     * @return 专业列表
     */
    @Operation(summary = "获取所有专业（用于下拉选择）", description = "获取所有专业信息，用于前端下拉选择")
    @GetMapping(path = "/listAll", produces = "application/json")
    @PreAuthorize("hasAuthority('education:major:list')")
    public Result<List<MajorVO>> listAllMajors() {
        log.info("开始查询所有专业信息（用于下拉选择）");
        try {
            // 使用关联查询获取带有院系名称的专业列表
            List<MajorVO> majorList = majorMapper.selectMajorVOList();
            log.info("查询到{}个专业信息（用于下拉选择）", majorList.size());
            return Result.success(majorList);
        } catch (Exception e) {
            log.error("查询专业列表失败", e);
            return Result.error("获取专业列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取专业详情
     *
     * @param id 专业ID
     * @return 专业详情
     */
    @Operation(summary = "获取专业详情", description = "根据ID获取专业详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('education:major:list')")
    public Result<MajorVO> getMajorDetail(@PathVariable @Parameter(description = "专业ID") Long id) {
        log.info("查询专业详情，ID：{}", id);
        try {
            // 使用关联查询获取带有院系名称的专业详情
            MajorVO major = majorMapper.selectMajorVOById(id);
                if (major == null) {
                    log.warn("专业不存在，ID：{}", id);
                    return Result.error("专业不存在");
            }
            log.info("查询专业详情成功，ID：{}", id);
            return Result.success(major);
        } catch (Exception e) {
            log.error("查询专业详情失败", e);
            return Result.error("获取专业详情失败：" + e.getMessage());
        }
    }

    /**
     * 删除专业
     *
     * @param id 专业ID
     * @return 删除结果
     */
    @Operation(summary = "删除专业", description = "根据ID删除专业")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('education:major:remove')")
    public Result<Void> deleteMajor(@PathVariable @Parameter(description = "专业ID") Long id) {
        log.info("删除专业，ID：{}", id);
        try {
            // 检查专业是否存在
            Major major = majorMapper.selectById(id);
            if (major == null) {
                log.warn("删除专业失败：专业不存在，ID：{}", id);
                return Result.error("专业不存在");
            }
            
            int rows = majorMapper.deleteById(id);
            if (rows > 0) {
                log.info("删除专业成功，ID：{}", id);
                return Result.success("删除专业成功");
            } else {
                log.warn("删除专业失败，ID：{}", id);
                return Result.error("删除专业失败");
            }
        } catch (Exception e) {
            log.error("删除专业失败", e);
            return Result.error("删除专业失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取专业选项列表（适用于下拉框，不需要权限验证）
     *
     * @return 专业选项列表
     */
    @Operation(summary = "获取专业选项列表", description = "获取专业选项列表，适用于下拉框，不需要权限验证")
    @GetMapping("/options")
    public Result<List<java.util.Map<String, Object>>> getMajorOptions() {
        log.info("开始查询专业选项列表");
        try {
            List<java.util.Map<String, Object>> options = majorMapper.selectMajorOptions();
            log.info("查询到{}个专业选项", options.size());
            return Result.success(options);
        } catch (Exception e) {
            log.error("查询专业选项列表失败", e);
            return Result.error("获取专业选项列表失败：" + e.getMessage());
        }
    }
} 