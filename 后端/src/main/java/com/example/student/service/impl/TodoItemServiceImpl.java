package com.example.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.student.entity.TodoItem;
import com.example.student.mapper.TodoItemMapper;
import com.example.student.service.TodoItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 待办事项Service实现
 */
@Service
@RequiredArgsConstructor
public class TodoItemServiceImpl extends ServiceImpl<TodoItemMapper, TodoItem> implements TodoItemService {

    private final TodoItemMapper todoItemMapper;

    /**
     * 查询待办事项列表
     *
     * @param todoItem 待办事项信息
     * @return 待办事项集合
     */
    @Override
    public List<TodoItem> selectTodoItemList(TodoItem todoItem) {
        LambdaQueryWrapper<TodoItem> queryWrapper = new LambdaQueryWrapper<>();
        // 用户ID精确查询
        if (todoItem.getUserId() != null) {
            queryWrapper.eq(TodoItem::getUserId, todoItem.getUserId());
        }
        // 标题模糊查询
        if (todoItem.getTitle() != null) {
            queryWrapper.like(TodoItem::getTitle, todoItem.getTitle());
        }
        // 状态精确查询
        if (todoItem.getStatus() != null) {
            queryWrapper.eq(TodoItem::getStatus, todoItem.getStatus());
        }
        // 优先级精确查询
        if (todoItem.getPriority() != null) {
            queryWrapper.eq(TodoItem::getPriority, todoItem.getPriority());
        }
        // 按优先级降序，到期时间升序
        queryWrapper.orderByDesc(TodoItem::getPriority);
        queryWrapper.orderByAsc(TodoItem::getDueDate);
        
        return list(queryWrapper);
    }

    /**
     * 根据用户ID查询待办事项列表
     *
     * @param userId 用户ID
     * @return 待办事项列表
     */
    @Override
    public List<TodoItem> selectTodoItemsByUserId(Long userId) {
        return todoItemMapper.selectTodoItemsByUserId(userId);
    }

    /**
     * 根据ID查询待办事项
     *
     * @param id 待办事项ID
     * @return 待办事项信息
     */
    @Override
    public TodoItem selectTodoItemById(Long id) {
        return getById(id);
    }

    /**
     * 新增待办事项
     *
     * @param todoItem 待办事项信息
     * @return 结果
     */
    @Override
    public int insertTodoItem(TodoItem todoItem) {
        return todoItemMapper.insert(todoItem) > 0 ? 1 : 0;
    }

    /**
     * 修改待办事项
     *
     * @param todoItem 待办事项信息
     * @return 结果
     */
    @Override
    public int updateTodoItem(TodoItem todoItem) {
        return todoItemMapper.updateById(todoItem) > 0 ? 1 : 0;
    }

    /**
     * 删除待办事项信息
     *
     * @param id 待办事项ID
     * @return 结果
     */
    @Override
    public int deleteTodoItemById(Long id) {
        return todoItemMapper.deleteById(id) > 0 ? 1 : 0;
    }

    /**
     * 批量删除待办事项信息
     *
     * @param ids 需要删除的待办事项ID
     * @return 结果
     */
    @Override
    public int deleteTodoItemByIds(Long[] ids) {
        return baseMapper.deleteBatchIds(List.of(ids)) > 0 ? 1 : 0;
    }

    /**
     * 完成待办事项
     *
     * @param id 待办事项ID
     * @return 结果
     */
    @Override
    public int completeTodoItem(Long id) {
        TodoItem todoItem = getById(id);
        if (todoItem != null) {
            todoItem.setStatus(1); // 已完成状态
            return updateTodoItem(todoItem);
        }
        return 0;
    }

    /**
     * 统计用户未完成的待办事项数量
     *
     * @param userId 用户ID
     * @return 未完成的待办事项数量
     */
    @Override
    public int countUncompletedTodoItems(Long userId) {
        return todoItemMapper.countUncompletedTodoItems(userId);
    }

    /**
     * 获取用户即将到期的待办事项
     *
     * @param userId 用户ID
     * @param days 天数
     * @return 待办事项列表
     */
    @Override
    public List<TodoItem> selectUpcomingTodoItems(Long userId, int days) {
        return todoItemMapper.selectUpcomingTodoItems(userId, days);
    }
} 