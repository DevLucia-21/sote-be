# ============================
# 1) Build Stage
# ============================
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

COPY . .
RUN ./gradlew clean build -x test --no-daemon


# ============================
# 2) Run Stage
# ============================
FROM eclipse-temurin:17-jdk AS runner

WORKDIR /app

COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar

RUN mkdir -p /app/config

# build 단계가 아닌 "run 단계"에서 파일을 생성해야 함
CMD sh -c "\
  printf \"%s\" \"$GCP_OCR_JSON\" > /app/config/gcp-ocr.json && \
  printf \"%s\" \"$GCP_PROFILE_JSON\" > /app/config/gcp-profile.json && \
  printf \"%s\" \"$FIREBASE_JSON\" > /app/config/firebase-service-account.json && \
  java -jar app.jar \
"
