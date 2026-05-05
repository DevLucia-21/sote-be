# S:ote Backend - Exhibition Demo Branch

> AI 기반 감정 분석을 활용한 개인 맞춤형 음악·챌린지 추천 일기 시스템      
> **S:ote**의 캡스톤 경진대회 전시 및 시연용 백엔드 브랜치입니다.

---

## Branch Purpose

이 브랜치는 2025 캡스톤 경진대회 전시 환경에서 서비스를 안정적으로 시연하기 위해 구성한 백엔드 데모 브랜치입니다.

해당 전시에서 S:ote는 **아리상**을 수상했으며,      
본 브랜치는 일기 작성, 감정 분석 결과 조회, 챌린지 추천, 음악 보상, 알림 등 핵심 사용자 흐름을 시연하기 위해 사용되었습니다.

실제 운영 또는 포트폴리오 최종 정리본이 아니라, 전시 현장에서 주요 기능 흐름을 빠르게 보여주기 위한 버전입니다.

---

## Demo Scope

전시 시연을 위해 다음 백엔드 기능 흐름을 중심으로 구성했습니다.

* JWT 기반 사용자 인증 및 요청 검증
* 일기 작성 및 감정 분석 결과 조회
* FastAPI 기반 AI 감정 분석 서버 연동
* 감정 기반 챌린지 추천 및 완료 처리
* 음악 LP 보상 데이터 관리
* Spotify API 기반 음악 메타데이터 연동
* Firebase Cloud Messaging 기반 알림 처리

---

## Tech Stack

| Category        | Stack                                          |
| --------------- | ---------------------------------------------- |
| Language        | Java 17                                        |
| Framework       | Spring Boot                                    |
| Security        | Spring Security, JWT                           |
| Database        | PostgreSQL                                     |
| ORM / Migration | Spring Data JPA, Hibernate, Flyway             |
| External API    | FastAPI, Spotify API, Firebase Cloud Messaging |
| Build Tool      | Gradle                                         |

---

## Running the Project

```bash
./gradlew bootRun
```

Windows 환경에서는 다음 명령어를 사용할 수 있습니다.

```bash
gradlew.bat bootRun
```

실행을 위해서는 로컬 환경에서 별도의 설정 파일이 필요합니다.

```text
src/main/resources/application.yml
```

공개 저장소에는 실제 API Key, DB 접속 정보, Firebase Admin SDK JSON, GCP 인증 파일 등 민감 정보를 포함하지 않습니다.      
환경 변수 구성 예시는 아래 파일을 참고합니다.

```text
src/main/resources/application-example.yml
```

---

## Branch Guide

| Branch                | Description           |
| --------------------- | --------------------- |
| `main`                | 포트폴리오용 최종 정리 브랜치      |
| `demo/exhibition`     | 캡스톤 경진대회 전시 및 시연용 브랜치 |
| `dev`                 | 기능 통합 및 개발 브랜치        |
| `refactor/local`      | 리팩토링 및 최종 기능 정리 브랜치   |
| `release/deploy-main` | 기존 배포용 main 상태 보존 브랜치 |

---

## Note

본 브랜치는 전시 시연 목적에 맞춰 구성된 버전입니다.      
최신 포트폴리오 정리본은 `main` 브랜치를 기준으로 확인하는 것을 권장합니다.
