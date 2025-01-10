#!/bin/bash

while true; do
    # 使用 Maven 构建项目
    echo "Building the project..."
    mvn clean package -DskipTests

    # 运行应用
    echo "Starting the application..."
    java -jar target/*.jar

    # 如果应用正常退出（退出码为0），则重新启动
    if [ $? -eq 0 ]; then
        echo "Application exited normally, restarting..."
        sleep 5
    else
        echo "Application crashed with exit code $?, stopping..."
        break
    fi
done 