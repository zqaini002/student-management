package com.example.student.controller;

import com.example.student.common.Result;
import com.example.student.entity.TodoItem;
import com.example.student.entity.SysUser;
import com.example.student.service.TodoItemService;
import com.example.student.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 待办事项Controller
 */
@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoItemController {

    private final TodoItemService todoItemService;
    private final SysUserService userService;

    /**
     * 查询待办事项列表
     */
    @GetMapping("/list")
    public Result<List<TodoItem>> list(TodoItem todoItem, Authentication authentication) {
        // 普通用户只能查看自己的待办事项
        SysUser user = userService.findByUsername(authentication.getName());
        if (!userService.isAdmin(user)) {
            todoItem.setUserId(user.getId());
        }
        List<TodoItem> list = todoItemService.selectTodoItemList(todoItem);
        return Result.success(list);
    }

    /**
     * 获取当前用户的待办事项列表
     */
    @GetMapping("/my")
    public Result<List<TodoItem>> myTodoList(Authentication authentication) {
        SysUser user = userService.findByUsername(authentication.getName());
        List<TodoItem> list = todoItemService.selectTodoItemsByUserId(user.getId());
        return Result.success(list);
    }

    /**
     * 获取待办事项详细信息
     */
    @GetMapping("/{id}")
    public Result<TodoItem> getInfo(@PathVariable("id") Long id, Authentication authentication) {
        TodoItem todoItem = todoItemService.selectTodoItemById(id);
        
        // 权限检查：普通用户只能查看自己的待办事项
        SysUser user = userService.findByUsername(authentication.getName());
        if (!userService.isAdmin(user) && !todoItem.getUserId().equals(user.getId())) {
            return Result.error("无权访问此待办事项");
        }
        
        return Result.success(todoItem);
    }

    /**
     * 新增待办事项
     */
    @PostMapping
    public Result<Integer> add(@RequestBody TodoItem todoItem, Authentication authentication) {
        // 如果未指定用户ID，则默认为当前用户
        if (todoItem.getUserId() == null) {
            SysUser user = userService.findByUsername(authentication.getName());
            todoItem.setUserId(user.getId());
        } else {
            // 权限检查：普通用户只能为自己创建待办事项
            SysUser user = userService.findByUsername(authentication.getName());
            if (!userService.isAdmin(user) && !todoItem.getUserId().equals(user.getId())) {
                return Result.error("无权为其他用户创建待办事项");
            }
        }
        
        return Result.success(todoItemService.insertTodoItem(todoItem));
    }

    /**
     * 修改待办事项
     */
    @PutMapping
    public Result<Integer> edit(@RequestBody TodoItem todoItem, Authentication authentication) {
        // 权限检查：普通用户只能修改自己的待办事项
        SysUser user = userService.findByUsername(authentication.getName());
        TodoItem original = todoItemService.selectTodoItemById(todoItem.getId());
        
        if (original == null) {
            return Result.error("待办事项不存在");
        }
        
        if (!userService.isAdmin(user) && !original.getUserId().equals(user.getId())) {
            return Result.error("无权修改此待办事项");
        }
        
        return Result.success(todoItemService.updateTodoItem(todoItem));
    }

    /**
     * 删除待办事项
     */
    @DeleteMapping("/{id}")
    public Result<Integer> remove(@PathVariable Long id, Authentication authentication) {
        // 权限检查：普通用户只能删除自己的待办事项
        SysUser user = userService.findByUsername(authentication.getName());
        TodoItem todoItem = todoItemService.selectTodoItemById(id);
        
        if (todoItem == null) {
            return Result.error("待办事项不存在");
        }
        
        if (!userService.isAdmin(user) && !todoItem.getUserId().equals(user.getId())) {
            return Result.error("无权删除此待办事项");
        }
        
        return Result.success(todoItemService.deleteTodoItemById(id));
    }

    /**
     * 批量删除待办事项
     */
    @DeleteMapping("/batch/{ids}")
    @PreAuthorize("hasAuthority('system:todo:remove')")
    public Result<Integer> removeBatch(@PathVariable Long[] ids) {
        return Result.success(todoItemService.deleteTodoItemByIds(ids));
    }

    /**
     * 完成待办事项
     */
    @PutMapping("/complete/{id}")
    public Result<Integer> complete(@PathVariable Long id, Authentication authentication) {
        // 权限检查：普通用户只能完成自己的待办事项
        SysUser user = userService.findByUsername(authentication.getName());
        TodoItem todoItem = todoItemService.selectTodoItemById(id);
        
        if (todoItem == null) {
            return Result.error("待办事项不存在");
        }
        
        if (!userService.isAdmin(user) && !todoItem.getUserId().equals(user.getId())) {
            return Result.error("无权操作此待办事项");
        }
        
        return Result.success(todoItemService.completeTodoItem(id));
    }

    /**
     * 获取未完成的待办事项数量
     */
    @GetMapping("/count/uncompleted")
    public Result<Integer> countUncompleted(Authentication authentication) {
        SysUser user = userService.findByUsername(authentication.getName());
        int count = todoItemService.countUncompletedTodoItems(user.getId());
        return Result.success(count);
    }

    /**
     * 获取即将到期的待办事项
     */
    @GetMapping("/upcoming/{days}")
    public Result<List<TodoItem>> upcomingTodos(@PathVariable("days") int days, Authentication authentication) {
        SysUser user = userService.findByUsername(authentication.getName());
        List<TodoItem> list = todoItemService.selectUpcomingTodoItems(user.getId(), days);
        return Result.success(list);
    }
} 