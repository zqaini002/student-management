import { useUserStore } from '@/stores/user';

/**
 * 检查用户是否具有指定角色
 * @param {Array} roles - 需要的角色数组
 * @returns {boolean} - 是否具有权限
 */
export function checkPermission(roles) {
  if (!roles || roles.length === 0) {
    return true;
  }
  
  const userStore = useUserStore();
  const userRoles = userStore.roles;
  
  if (!userRoles || userRoles.length === 0) {
    return false;
  }
  
  return userRoles.some(role => roles.includes(role));
}

/**
 * 检查用户是否具有指定权限
 * @param {Array} permissions - 需要的权限数组
 * @returns {boolean} - 是否具有权限
 */
export function checkHasPermission(permissions) {
  if (!permissions || permissions.length === 0) {
    return true;
  }
  
  const userStore = useUserStore();
  const userPermissions = userStore.permissions;
  
  if (!userPermissions || userPermissions.length === 0) {
    return false;
  }
  
  return userPermissions.some(permission => permissions.includes(permission));
} 