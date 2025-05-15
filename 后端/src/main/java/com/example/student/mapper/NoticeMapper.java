package com.example.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.student.entity.Notice;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统公告Mapper接口
 */
@Repository
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

    /**
     * 查询已发布的系统公告列表
     *
     * @return 系统公告列表
     */
    @Select("SELECT * FROM notice WHERE status = 1 ORDER BY publish_time DESC LIMIT 10")
    List<Notice> selectPublishedNotices();

    /**
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    List<Notice> selectNoticeList(Notice notice);
    
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
     * 删除公告
     *
     * @param id 公告ID
     * @return 结果
     */
    int deleteNoticeById(Long id);
    
    /**
     * 批量删除公告
     *
     * @param ids 需要删除的数据ID
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
    List<Notice> selectLatestNotices(@Param("limit") int limit);
} 