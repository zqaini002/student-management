#!/usr/bin/env node

// 抑制Node.js的弃用警告
process.noDeprecation = true;

// 启动vite开发服务器
require('child_process').spawn(
  process.platform === 'win32' ? 'npm.cmd' : 'npm',
  ['run', 'dev:silent'],
  { 
    stdio: 'inherit',
    env: {
      ...process.env,
      NODE_OPTIONS: '--no-warnings'
    }
  }
); 