# ============================
# 1) Build Stage
# ============================
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Gradle 캐시 최적화 (dependencies 먼저 캐시)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew

# Gradle dependencies 다운로드
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY . .

# 실제 빌드
RUN ./gradlew clean build -x test --no-daemon


# ============================
# 2) Run Stage
# ============================
FROM eclipse-temurin:17-jdk AS runner

WORKDIR /app

# builder stage에서 빌드된 jar 복사
COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar

# 실행 포트
EXPOSE 8080

# Spring Boot 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
