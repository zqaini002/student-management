@echo off
REM 设置Node.js环境变量以抑制弃用警告
set NODE_OPTIONS=--no-warnings

REM 启动vite开发服务器
npm run dev 