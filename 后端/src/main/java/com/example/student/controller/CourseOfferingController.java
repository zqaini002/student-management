package com.example.student.controller;

import com.example.student.common.PageResult;
import com.example.student.common.Result;
import com.example.student.dto.CourseOfferingDTO;
import com.example.student.dto.CourseOfferingQueryDTO;
import com.example.student.service.CourseOfferingService;
import com.example.student.vo.CourseOfferingVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courseOffering")
@Tag(name = "课程开设管理")
@RequiredArgsConstructor
@Slf4j
public class CourseOfferingController {
    private final CourseOfferingService courseOfferingService;

    @Operation(summary = "分页查询课程开设记录")
    @GetMapping("/list")
    public Result<PageResult<CourseOfferingVO>> list(CourseOfferingQueryDTO queryDTO) {
        return Result.success(courseOfferingService.pageList(queryDTO));
    }

    @Operation(summary = "获取课程开设详情")
    @GetMapping("/{id}")
    public Result<CourseOfferingVO> get(@PathVariable Long id) {
        return Result.success(courseOfferingService.getById(id));
    }

    @Operation(summary = "新增课程开设")
    @PostMapping
    public Result<Void> add(@RequestBody @Valid CourseOfferingDTO dto) {
        courseOfferingService.add(dto);
        return Result.success();
    }

    @Operation(summary = "修改课程开设")
    @PutMapping
    public Result<Void> update(@RequestBody @Valid CourseOfferingDTO dto) {
        courseOfferingService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除课程开设")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        courseOfferingService.delete(id);
        return Result.success();
    }
} 