# S:ote Backend - Deploy Main Backup Branch

> AI 기반 감정 분석을 활용한 개인 맞춤형 음악·챌린지 추천 일기 시스템     
> **S:ote**의 기존 배포용 `main` 상태를 보존한 백엔드 백업 브랜치입니다.

---

## Branch Purpose

이 브랜치는 S:ote 백엔드의 기존 배포용 `main` 브랜치 상태를 보존하기 위해 분리한 브랜치입니다.

포트폴리오용 최종 정리 과정에서 `main` 브랜치를 새 기준으로 재정리하기 전에,      
이전 배포 기준 코드를 안전하게 남겨두기 위한 백업 용도로 사용합니다.

현재 최신 포트폴리오 정리본이나 리팩토링 기준 브랜치가 아니라,       
기존 배포 상태를 확인하거나 필요할 때 되돌아가기 위한 보존용 브랜치입니다.

---

## Branch Scope

본 브랜치는 기존 배포 기준 백엔드 상태를 보존하기 위해 다음 흐름을 포함합니다.

* Spring Boot 기반 백엔드 API 서버
* JWT 기반 사용자 인증 및 요청 검증
* 일기, 감정 분석, 챌린지, 음악 LP 보상 관련 API
* FastAPI 기반 AI 감정 분석 서버 연동
* Spotify API 기반 음악 메타데이터 연동
* Firebase Cloud Messaging 기반 알림 처리
* PostgreSQL 및 Flyway 기반 데이터베이스 관리
* Docker 기반 배포 구성 파일

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
| Deploy                  | Docker                                         |

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

본 브랜치는 기존 배포용 `main` 상태를 보존하기 위한 백업 브랜치입니다.      
최신 포트폴리오 정리본은 `main` 브랜치를 기준으로 확인하는 것을 권장합니다.
