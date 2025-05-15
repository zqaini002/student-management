/**
 * 配置Sass行为，强制使用新API
 */
process.env.SASS_API = 'new';

// 禁用Node.js的弃用警告
process.noDeprecation = true;

// 导出sass配置
module.exports = {
  // 使用新的API方式
  api: 'new',
  // 禁用JS API
  javascriptEnabled: false,
  // 格式化选项
  outputStyle: 'expanded'
};