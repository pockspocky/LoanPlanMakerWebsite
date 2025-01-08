# 使用 JDK 17 作为基础镜像
FROM registry.cn-hangzhou.aliyuncs.com/library/openjdk:17-slim

# 设置工作目录
WORKDIR /app

# 复制 Maven 配置文件
COPY pom.xml .

# 复制源代码
COPY src ./src

# 复制 Maven wrapper 文件
COPY mvnw .
COPY .mvn .mvn

# 设置 Maven wrapper 的执行权限
RUN chmod +x mvnw

# 构建应用
RUN ./mvnw package -DskipTests

# 暴露应用端口
EXPOSE 8080

# 运行应用
CMD ["java", "-jar", "target/hello-0.0.1-SNAPSHOT.jar"] 