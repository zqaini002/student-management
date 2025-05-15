#!/bin/bash

# 安装依赖
echo "正在安装依赖..."
npm install

# 启动开发服务器
echo "正在启动前端开发服务器..."
echo "请确保后端服务已在8080端口上运行"
npm run dev 