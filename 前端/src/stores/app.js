import { defineStore } from 'pinia'
import Cookies from 'js-cookie'

const getSidebarStatus = () => Cookies.get('sidebarStatus') ? !!+Cookies.get('sidebarStatus') : true
const getSize = () => Cookies.get('size') || 'default'

export const useAppStore = defineStore('app', {
  state: () => ({
    sidebar: {
      opened: getSidebarStatus(),
      withoutAnimation: false
    },
    device: 'desktop',
    size: getSize(),
    theme: 'light',
    // 标签栏
    visitedViews: [],
    cachedViews: []
  }),
  actions: {
    // 切换侧边栏展开/收起
    toggleSidebar() {
      this.sidebar.opened = !this.sidebar.opened
      this.sidebar.withoutAnimation = false
      if (this.sidebar.opened) {
        Cookies.set('sidebarStatus', 1)
      } else {
        Cookies.set('sidebarStatus', 0)
      }
    },
    
    // 关闭侧边栏
    closeSidebar({ withoutAnimation }) {
      Cookies.set('sidebarStatus', 0)
      this.sidebar.opened = false
      this.sidebar.withoutAnimation = withoutAnimation
    },
    
    // 设置设备类型
    toggleDevice(device) {
      this.device = device
    },
    
    // 设置大小
    setSize(size) {
      this.size = size
      Cookies.set('size', size)
    },
    
    // 设置主题
    setTheme(theme) {
      this.theme = theme
      document.documentElement.className = `theme-${theme}`
    },
    
    // 添加访问的标签页
    addVisitedView(view) {
      if (this.visitedViews.some(v => v.path === view.path)) return
      
      this.visitedViews.push(
        Object.assign({}, view, {
          title: view.meta.title || 'no-name'
        })
      )
    },
    
    // 添加缓存的标签页
    addCachedView(view) {
      if (this.cachedViews.includes(view.name)) return
      if (view.meta && view.meta.keepAlive) {
        this.cachedViews.push(view.name)
      }
    },
    
    // 删除访问的标签页
    delVisitedView(view) {
      for (const [i, v] of this.visitedViews.entries()) {
        if (v.path === view.path) {
          this.visitedViews.splice(i, 1)
          break
        }
      }
    },
    
    // 删除缓存的标签页
    delCachedView(view) {
      const index = this.cachedViews.indexOf(view.name)
      if (index > -1) {
        this.cachedViews.splice(index, 1)
      }
    },
    
    // 删除其他标签页
    delOthersVisitedViews(view) {
      this.visitedViews = this.visitedViews.filter(v => {
        return v.meta.affix || v.path === view.path
      })
    },
    
    // 删除其他缓存页
    delOthersCachedViews(view) {
      const index = this.cachedViews.indexOf(view.name)
      if (index > -1) {
        this.cachedViews = this.cachedViews.slice(index, index + 1)
      } else {
        this.cachedViews = []
      }
    },
    
    // 删除所有标签页
    delAllVisitedViews() {
      // 保留固定的标签
      this.visitedViews = this.visitedViews.filter(v => v.meta.affix)
    },
    
    // 删除所有缓存页
    delAllCachedViews() {
      this.cachedViews = []
    }
  },
  persist: {
    enabled: true,
    strategies: [
      {
        key: 'app-store',
        storage: localStorage,
        paths: ['theme', 'size']
      }
    ]
  }
}) 