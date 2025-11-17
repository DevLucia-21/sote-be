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

# ---- config 폴더 생성 ----
RUN mkdir -p /app/config

# ---- 환경변수(JSON) → 파일 생성 ----
RUN printf "%s" "$GCP_OCR_JSON" > /app/config/gcp-ocr.json
RUN printf "%s" "$GCP_PROFILE_JSON" > /app/config/gcp-profile.json
RUN printf "%s" "$FIREBASE_JSON" > /app/config/firebase-service-account.json

# 실행 포트
EXPOSE 8080

# Spring Boot 실행
CMD ["java", "-jar", "app.jar"]
