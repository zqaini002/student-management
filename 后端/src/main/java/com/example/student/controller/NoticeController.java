package com.example.student.controller;

import com.example.student.common.Result;
import com.example.student.entity.Notice;
import com.example.student.entity.SysUser;
import com.example.student.service.NoticeService;
import com.example.student.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告管理Controller
 */
@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final SysUserService userService;

    /**
     * 查询公告列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:notice:list')")
    public Result<List<Notice>> list(Notice notice) {
        List<Notice> list = noticeService.selectNoticeList(notice);
        return Result.success(list);
    }

    /**
     * 查询已发布的公告列表
     */
    @GetMapping("/published")
    public Result<List<Notice>> publishedList() {
        List<Notice> list = noticeService.selectPublishedNotices();
        return Result.success(list);
    }

    /**
     * 获取公告详细信息
     */
    @GetMapping("/{id}")
    public Result<Notice> getInfo(@PathVariable("id") Long id) {
        return Result.success(noticeService.selectNoticeById(id));
    }

    /**
     * 新增公告
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:notice:add')")
    public Result<Integer> add(@RequestBody Notice notice, Authentication authentication) {
        SysUser user = userService.findByUsername(authentication.getName());
        notice.setPublisherId(user.getId());
        notice.setPublisherName(user.getName());
        notice.setCreateTime(LocalDateTime.now());
        return Result.success(noticeService.insertNotice(notice));
    }

    /**
     * 修改公告
     */
    @PutMapping
    @PreAuthorize("hasAuthority('system:notice:edit')")
    public Result<Integer> edit(@RequestBody Notice notice) {
        return Result.success(noticeService.updateNotice(notice));
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:notice:remove')")
    public Result<Integer> remove(@PathVariable Long id) {
        return Result.success(noticeService.deleteNoticeById(id));
    }

    /**
     * 批量删除公告
     */
    @DeleteMapping("/batch/{ids}")
    @PreAuthorize("hasAuthority('system:notice:remove')")
    public Result<Integer> removeBatch(@PathVariable Long[] ids) {
        return Result.success(noticeService.deleteNoticeByIds(ids));
    }

    /**
     * 发布公告
     */
    @PutMapping("/publish/{id}")
    @PreAuthorize("hasAuthority('system:notice:edit')")
    public Result<Integer> publish(@PathVariable Long id) {
        return Result.success(noticeService.publishNotice(id));
    }

    /**
     * 下线公告
     */
    @PutMapping("/offline/{id}")
    @PreAuthorize("hasAuthority('system:notice:edit')")
    public Result<Integer> offline(@PathVariable Long id) {
        return Result.success(noticeService.offlineNotice(id));
    }

    /**
     * 获取最新公告
     */
    @GetMapping("/latest/{limit}")
    public Result<List<Notice>> latest(@PathVariable("limit") int limit) {
        return Result.success(noticeService.selectLatestNotices(limit));
    }
} 