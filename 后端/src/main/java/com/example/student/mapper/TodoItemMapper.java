package com.example.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.student.entity.TodoItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 待办事项Mapper接口
 */
@Repository
@Mapper
public interface TodoItemMapper extends BaseMapper<TodoItem> {

    /**
     * 查询用户的待办事项列表
     *
     * @param userId 用户ID
     * @return 待办事项列表
     */
    @Select("SELECT * FROM todo_item WHERE user_id = #{userId} ORDER BY priority DESC, due_date ASC")
    List<TodoItem> selectTodoItemsByUserId(@Param("userId") Long userId);

    /**
     * 查询待办事项列表
     *
     * @param todoItem 待办事项信息
     * @return 待办事项集合
     */
    List<TodoItem> selectTodoItemList(TodoItem todoItem);
    
    /**
     * 根据ID查询待办事项
     *
     * @param id 待办事项ID
     * @return 待办事项信息
     */
    TodoItem selectTodoItemById(Long id);
    
    /**
     * 新增待办事项
     *
     * @param todoItem 待办事项信息
     * @return 结果
     */
    int insertTodoItem(TodoItem todoItem);
    
    /**
     * 修改待办事项
     *
     * @param todoItem 待办事项信息
     * @return 结果
     */
    int updateTodoItem(TodoItem todoItem);
    
    /**
     * 删除待办事项
     *
     * @param id 待办事项ID
     * @return 结果
     */
    int deleteTodoItemById(Long id);
    
    /**
     * 批量删除待办事项
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteTodoItemByIds(Long[] ids);
    
    /**
     * 完成待办事项
     *
     * @param id 待办事项ID
     * @return 结果
     */
    int completeTodoItem(Long id);
    
    /**
     * 统计用户未完成的待办事项数量
     *
     * @param userId 用户ID
     * @return 未完成的待办事项数量
     */
    @Select("SELECT COUNT(*) FROM todo_item WHERE user_id = #{userId} AND status = 0")
    int countUncompletedTodoItems(@Param("userId") Long userId);
    
    /**
     * 获取用户即将到期的待办事项
     *
     * @param userId 用户ID
     * @param days 天数
     * @return 待办事项列表
     */
    @Select("SELECT * FROM todo_item WHERE user_id = #{userId} AND status = 0 AND due_date <= DATE_ADD(NOW(), INTERVAL #{days} DAY) ORDER BY due_date ASC")
    List<TodoItem> selectUpcomingTodoItems(@Param("userId") Long userId, @Param("days") int days);
} 