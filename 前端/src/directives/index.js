import { hasPerms, hasRoles, disabled } from './permission'


// 注册指令集合
export default {
  install(app) {
    // 注册权限指令
    app.directive('hasPerms', hasPerms)
    app.directive('hasRoles', hasRoles)
    
    // 添加hasPermi别名指向hasPerms实现，解决指令名称不一致问题
    app.directive('hasPermi', hasPerms)
    
    // 注册禁用指令
    app.directive('disabled', disabled)
    
    // 其他指令可以在此添加
  }
} 