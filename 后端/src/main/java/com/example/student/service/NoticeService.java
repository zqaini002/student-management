package com.example.student.service;

import com.example.student.entity.Notice;

import java.util.List;

/**
 * 公告管理Service接口
 */
public interface NoticeService {
    
    /**
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    List<Notice> selectNoticeList(Notice notice);
    
    /**
     * 查询已发布的公告列表
     *
     * @return 公告集合
     */
    List<Notice> selectPublishedNotices();
    
    /**
     * 根据ID查询公告
     *
     * @param id 公告ID
     * @return 公告信息
     */
    Notice selectNoticeById(Long id);
    
    /**
     * 新增公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    int insertNotice(Notice notice);
    
    /**
     * 修改公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    int updateNotice(Notice notice);
    
    /**
     * 删除公告信息
     *
     * @param id 公告ID
     * @return 结果
     */
    int deleteNoticeById(Long id);
    
    /**
     * 批量删除公告信息
     *
     * @param ids 需要删除的公告ID
     * @return 结果
     */
    int deleteNoticeByIds(Long[] ids);
    
    /**
     * 发布公告
     *
     * @param id 公告ID
     * @return 结果
     */
    int publishNotice(Long id);
    
    /**
     * 下线公告
     *
     * @param id 公告ID
     * @return 结果
     */
    int offlineNotice(Long id);
    
    /**
     * 获取最新公告
     *
     * @param limit 条数限制
     * @return 公告列表
     */
    List<Notice> selectLatestNotices(int limit);
} 