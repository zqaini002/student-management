<template>
  <component :is="type" v-bind="linkProps">
    <slot />
  </component>
</template>

<script setup>
import { computed } from 'vue'
import { isExternal } from '@/utils/validate'

const props = defineProps({
  to: {
    type: String,
    required: true
  }
})

// 链接类型
const type = computed(() => {
  return isExternal(props.to) ? 'a' : 'router-link'
})

// 链接属性
const linkProps = computed(() => {
  // 记录链接生成过程
  console.log('生成链接属性, 目标路径:', props.to)
  
  if (isExternal(props.to)) {
    return { href: props.to, target: '_blank', rel: 'noopener' }
  } else {
    // 确保内部路由路径不会重复，去除可能的多余斜杠
    const path = props.to.replace(/\/+/g, '/')
    console.log('处理后的路径:', path)
    return { to: path }
  }
})
</script> 