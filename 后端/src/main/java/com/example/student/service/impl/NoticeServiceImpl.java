package com.example.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.student.entity.Notice;
import com.example.student.mapper.NoticeMapper;
import com.example.student.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告管理Service实现
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    private final NoticeMapper noticeMapper;

    /**
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<Notice> selectNoticeList(Notice notice) {
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        // 标题模糊查询
        if (notice.getTitle() != null) {
            queryWrapper.like(Notice::getTitle, notice.getTitle());
        }
        // 类型精确查询
        if (notice.getType() != null) {
            queryWrapper.eq(Notice::getType, notice.getType());
        }
        // 状态精确查询
        if (notice.getStatus() != null) {
            queryWrapper.eq(Notice::getStatus, notice.getStatus());
        }
        // 按发布时间倒序
        queryWrapper.orderByDesc(Notice::getPublishTime);
        
        return list(queryWrapper);
    }

    /**
     * 查询已发布的公告列表
     *
     * @return 公告集合
     */
    @Override
    public List<Notice> selectPublishedNotices() {
        return noticeMapper.selectPublishedNotices();
    }

    /**
     * 根据ID查询公告
     *
     * @param id 公告ID
     * @return 公告信息
     */
    @Override
    public Notice selectNoticeById(Long id) {
        return getById(id);
    }

    /**
     * 新增公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int insertNotice(Notice notice) {
        return noticeMapper.insert(notice) > 0 ? 1 : 0;
    }

    /**
     * 修改公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int updateNotice(Notice notice) {
        return noticeMapper.updateById(notice) > 0 ? 1 : 0;
    }

    /**
     * 删除公告信息
     *
     * @param id 公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeById(Long id) {
        return noticeMapper.deleteById(id) > 0 ? 1 : 0;
    }

    /**
     * 批量删除公告信息
     *
     * @param ids 需要删除的公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeByIds(Long[] ids) {
        return baseMapper.deleteBatchIds(List.of(ids)) > 0 ? 1 : 0;
    }

    /**
     * 发布公告
     *
     * @param id 公告ID
     * @return 结果
     */
    @Override
    public int publishNotice(Long id) {
        Notice notice = getById(id);
        if (notice != null) {
            notice.setStatus(1); // 已发布状态
            notice.setPublishTime(LocalDateTime.now());
            return updateNotice(notice);
        }
        return 0;
    }

    /**
     * 下线公告
     *
     * @param id 公告ID
     * @return 结果
     */
    @Override
    public int offlineNotice(Long id) {
        Notice notice = getById(id);
        if (notice != null) {
            notice.setStatus(2); // 已下线状态
            return updateNotice(notice);
        }
        return 0;
    }

    /**
     * 获取最新公告
     *
     * @param limit 条数限制
     * @return 公告列表
     */
    @Override
    public List<Notice> selectLatestNotices(int limit) {
        return noticeMapper.selectLatestNotices(limit);
    }
} 