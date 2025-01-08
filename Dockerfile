FROM maven:3.9-amazoncorretto-17 AS builder

WORKDIR /build
COPY pom.xml .
# 缓存Maven依赖
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

# 运行阶段
FROM amazoncorretto:17-alpine

WORKDIR /app

# 安装必要的工具
RUN apk add --no-cache curl

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