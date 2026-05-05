# S:ote Backend - Dev Branch

> AI 기반 감정 분석을 활용한 개인 맞춤형 음악·챌린지 추천 일기 시스템     
> **S:ote**의 백엔드 개발 및 기능 통합 브랜치입니다.

---

## Branch Purpose

이 브랜치는 S:ote 백엔드 개발 초기부터 사용한 개발 브랜치입니다.

인증, 일기, 감정 분석, 챌린지, 음악 LP 보상, 알림, 통계, STT/OCR, 웨어러블 연동 등      
백엔드 주요 기능을 구현하고 통합하는 과정에서 사용되었습니다.

전시 시연용으로 안정화한 `demo/exhibition` 브랜치나 포트폴리오 최종 정리용 `main` 브랜치와 달리,      
본 브랜치는 기능 개발과 구조 확장이 누적된 개발 기준 브랜치입니다.

---

## Development Scope

본 브랜치에서는 다음 백엔드 기능 개발을 중심으로 작업했습니다.

* Spring Security 및 JWT 기반 인증 처리
* 사용자 회원가입, 로그인, 프로필 관련 기능
* 일기 작성, 조회, 수정, 삭제 API
* FastAPI 기반 AI 감정 분석 서버 연동
* 감정 분석 결과 저장 및 조회
* 감정 기반 챌린지 추천 및 완료 처리
* 음악 LP 보상 및 Spotify API 연동
* Firebase Cloud Messaging 기반 알림 처리
* 통계, 캘린더, 질문, 설정 기능
* STT, OCR, 건강 데이터, Wear OS 연동 기능
* PostgreSQL 및 Flyway 기반 데이터베이스 관리

---

## Tech Stack

| Category                | Stack                                          |
| ----------------------- | ---------------------------------------------- |
| Language                | Java 17                                        |
| Framework               | Spring Boot                                    |
| Security                | Spring Security, JWT                           |
| Database                | PostgreSQL                                     |
| ORM / Migration         | Spring Data JPA, Hibernate, Flyway             |
| Cache / Session Support | Redis                                          |
| External API            | FastAPI, Spotify API, Firebase Cloud Messaging |
| Storage                 | Google Cloud Storage                           |
| Build Tool              | Gradle                                         |

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
| `dev`                 | 백엔드 개발 및 기능 통합 브랜치    |
| `refactor/local`      | 리팩토링 및 최종 기능 정리 브랜치   |
| `release/deploy-main` | 기존 배포용 main 상태 보존 브랜치 |

---

## Note

본 브랜치는 백엔드 기능 개발과 통합 과정이 누적된 개발 브랜치입니다.     
최신 포트폴리오 정리본은 `main` 브랜치를 기준으로 확인하는 것을 권장합니다.
