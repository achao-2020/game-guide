FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu

WORKDIR /app

# 复制已经构建好的 jar 文件
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

