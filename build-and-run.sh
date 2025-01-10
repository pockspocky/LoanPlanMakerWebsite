#!/bin/bash

# 停止现有容器
docker-compose down

# 构建新镜像
docker-compose build

# 启动服务
docker-compose up -d

# 显示容器状态
docker-compose ps

# 显示应用日志
docker-compose logs -f app 