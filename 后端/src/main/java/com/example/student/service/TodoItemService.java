package com.example.student.service;

import com.example.student.entity.TodoItem;

import java.util.List;

/**
 * 待办事项Service接口
 */
public interface TodoItemService {
    
    /**
     * 查询待办事项列表
     *
     * @param todoItem 待办事项信息
     * @return 待办事项集合
     */
    List<TodoItem> selectTodoItemList(TodoItem todoItem);
    
    /**
     * 根据用户ID查询待办事项列表
     *
     * @param userId 用户ID
     * @return 待办事项列表
     */
    List<TodoItem> selectTodoItemsByUserId(Long userId);
    
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
     * 删除待办事项信息
     *
     * @param id 待办事项ID
     * @return 结果
     */
    int deleteTodoItemById(Long id);
    
    /**
     * 批量删除待办事项信息
     *
     * @param ids 需要删除的待办事项ID
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
    int countUncompletedTodoItems(Long userId);
    
    /**
     * 获取用户即将到期的待办事项
     *
     * @param userId 用户ID
     * @param days 天数
     * @return 待办事项列表
     */
    List<TodoItem> selectUpcomingTodoItems(Long userId, int days);
} 