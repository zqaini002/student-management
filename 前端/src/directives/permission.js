/**
 * 角色指令
 * 用法：
 * 1. 单个角色：v-hasRoles="'admin'"
 * 2. 多个角色：v-hasRoles="['admin', 'common']"
 * 表示当前用户有任一角色时，元素显示
 */
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

export const hasRoles = {
  mounted(el, binding) {
    const { value } = binding
    const userStore = useUserStore()
    const roles = userStore.roles
    
    console.log("角色检查 - 需要角色:", value)
    console.log("角色检查 - 用户角色:", roles)
      
    if (value && value instanceof Array && value.length > 0) {
      const hasRole = roles.some(role => {
        return value.includes(role)
      })
      
      if (!hasRole) {
        // 修改：不删除元素，而是隐藏它
        el.style.display = 'none'
        console.log("角色检查 - 无角色，隐藏元素")
      } else {
        console.log("角色检查 - 有角色，显示元素")
      }
    } else if (value && typeof value === 'string') {
      if (!roles.includes(value)) {
        // 修改：不删除元素，而是隐藏它
        el.style.display = 'none'
        console.log("角色检查 - 无角色，隐藏元素")
      } else {
        console.log("角色检查 - 有角色，显示元素")
      }
    } else {
      throw new Error('使用方式有误，请参考文档')
    }
  }
}

// 权限别名映射表
const permissionAliases = {
  'education:department:remove': ['education:department:delete'],
  'education:department:delete': ['education:department:remove']
}

/**
 * 检查权限，考虑别名
 * @param {Array|String} requiredPerms 需要的权限
 * @param {Array} userPerms 用户拥有的权限
 * @returns {Boolean} 是否有权限
 */
function checkPermissionWithAliases(requiredPerms, userPerms) {
  if (Array.isArray(requiredPerms)) {
    return requiredPerms.some(perm => {
      // 直接匹配
      if (userPerms.includes(perm)) return true;
      
      // 检查别名
      const aliases = permissionAliases[perm] || [];
      return aliases.some(alias => userPerms.includes(alias));
    });
  } else {
    // 直接匹配
    if (userPerms.includes(requiredPerms)) return true;
    
    // 检查别名
    const aliases = permissionAliases[requiredPerms] || [];
    return aliases.some(alias => userPerms.includes(alias));
  }
}

/**
 * 权限指令
 * 用法：
 * 1. 单个权限：v-hasPerms="'system:user:add'"
 * 2. 多个权限：v-hasPerms="['system:user:add', 'system:user:edit']"
 * 表示当前用户有任一权限时，元素可用；否则禁用元素并添加提示
 */
export const hasPerms = {
  mounted(el, binding) {
    const { value } = binding
    const userStore = useUserStore()
    const permissions = userStore.permissions
    
    // 调试日志，帮助排查权限问题
    console.log("权限检查 - 元素:", el)
    console.log("权限检查 - 需要权限:", value)
    console.log("权限检查 - 用户权限:", permissions)
    
    if (!value) {
      return;
    }
    
    const hasPerm = checkPermissionWithAliases(value, permissions);
    
    if (!hasPerm) {
      console.log("权限检查 - 无权限，禁用元素")
      disableElement(el, getPermissionTip(value))
    } else {
      console.log("权限检查 - 有权限，启用元素")
    }
  }
}

/**
 * 禁用指令
 * 用法：v-disabled="true/false"
 * 当值为true时，禁用元素
 */
export const disabled = {
  mounted(el, binding) {
    if (binding.value) {
      disableElement(el)
    }
  },
  updated(el, binding) {
    if (binding.value) {
      disableElement(el)
    } else {
      enableElement(el)
    }
  }
}

/**
 * 禁用元素并添加提示
 * @param {HTMLElement} el 目标元素 
 * @param {String} tip 提示信息
 */
function disableElement(el, tip) {
  el.disabled = true
  el.classList.add('is-disabled')
  
  // 设置样式
  el.style.cursor = 'not-allowed'
  el.style.opacity = '0.7'
  el.style.pointerEvents = 'none'
  // 确保元素显示而不是隐藏
  el.style.display = '' // 恢复默认显示状态
  
  // 处理内部按钮、输入框等元素
  const inputs = el.querySelectorAll('input, button, textarea, select')
  inputs.forEach(input => {
    input.disabled = true
    input.classList.add('is-disabled')
  })
  
  // 添加提示
  if (tip) {
    el.setAttribute('title', tip)
    
    // 保存原始点击事件
    if (!el._originalClick) {
      el._originalClick = el.onclick
      
      // 替换点击事件，显示提示
      el.onclick = function(e) {
        e.stopPropagation()
        ElMessage.warning(tip)
        return false
      }
    }
  }
}

/**
 * 启用元素
 * @param {HTMLElement} el 目标元素
 */
function enableElement(el) {
  el.disabled = false
  el.classList.remove('is-disabled')
  
  // 恢复样式
  el.style.cursor = ''
  el.style.opacity = ''
  el.style.pointerEvents = ''
  
  // 恢复内部元素
  const inputs = el.querySelectorAll('input, button, textarea, select')
  inputs.forEach(input => {
    input.disabled = false
    input.classList.remove('is-disabled')
  })
  
  // 恢复原始点击事件
  if (el._originalClick) {
    el.onclick = el._originalClick
    el._originalClick = null
  }
}

/**
 * 获取权限提示信息
 * @param {String|Array} perms 权限标识
 * @returns {String} 提示信息
 */
function getPermissionTip(perms) {
  if (Array.isArray(perms)) {
    return `您没有${perms.join('或')}权限，请联系管理员`
  }
  return `您没有${perms}权限，请联系管理员`
}

