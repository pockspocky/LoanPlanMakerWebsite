FROM docker.tuna.tsinghua.edu.cn/library/openjdk:17-slim AS builder

WORKDIR /build
COPY pom.xml .
# 缓存Maven依赖
RUN apt-get update && apt-get install -y maven
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

# 运行阶段
FROM swr.cn-north-4.myhuaweicloud.com/openjdk/openjdk:17-jre

WORKDIR /app

# 安装必要的工具
RUN apt-get update && apt-get install -y curl

# 复制构建产物
COPY --from=builder /build/target/*.jar app.jar

# 环境变量
ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    SPRING_PROFILES_ACTIVE="prod" \
    TZ=Asia/Shanghai

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8000/actuator/health || exit 1

# 暴露端口
EXPOSE 8000

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 