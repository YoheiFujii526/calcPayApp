# ビルドステージ
FROM gradle:8.10-jdk21-alpine AS builder
WORKDIR /app
COPY . .
RUN gradle bootJar -x test --no-daemon

# 実行ステージ
FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache dumb-init
WORKDIR /app
ENV JAVA_TOOL_OPTIONS="-Djava.net.preferIPv4Stack=true"
EXPOSE 8080
ENTRYPOINT ["dumb-init", "java", "-jar", "app.jar"]
COPY --from=builder /app/build/libs/*.jar app.jar
