# S:ote Backend - Refactor Local Branch

> AI 기반 감정 분석을 활용한 개인 맞춤형 음악·챌린지 추천 일기 시스템     
> **S:ote**의 백엔드 리팩토링 및 최종 기능 정리 브랜치입니다.

---

## Branch Purpose

이 브랜치는 S:ote 백엔드의 주요 기능 흐름을 정리하고, 로컬 환경에서 최종 동작을 점검하기 위해 사용한 리팩토링 브랜치입니다.

개발 초기부터 기능을 누적한 `dev` 브랜치와 전시 시연에 맞춰 안정화한 `demo/exhibition` 브랜치 이후,      
인증, 일기, 감정 분석, 챌린지, 음악 보상, 알림 등 주요 백엔드 흐름을 다시 점검하고 정리하는 용도로 사용되었습니다.

---

## Branch Scope

본 브랜치에서는 다음 백엔드 흐름을 중심으로 정리했습니다.

* JWT 기반 인증 및 사용자 요청 처리 흐름 점검
* 일기 작성, 수정, 조회와 감정 분석 결과 연결 흐름 정리
* FastAPI 기반 AI 감정 분석 서버 연동 흐름 점검
* 감정 분석 결과 기반 챌린지 추천 및 완료 처리 흐름 정리
* 챌린지 완료 후 음악 LP 보상 생성 흐름 점검
* Spotify API 기반 음악 메타데이터 연동 유지
* Firebase Cloud Messaging 기반 알림 처리 흐름 점검
* 사용자 설정, 통계, 캘린더, STT/OCR, Wear OS 관련 기능 구조 유지
* 민감 설정 파일 제외 및 공개용 설정 예시 파일 정리

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

본 브랜치는 백엔드 주요 기능을 로컬 기준으로 다시 정리하고 점검한 브랜치입니다.      
최신 포트폴리오 정리본은 `main` 브랜치를 기준으로 확인하는 것을 권장합니다.
