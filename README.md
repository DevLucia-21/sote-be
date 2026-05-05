# S:ote Backend

> AI 기반 감정 분석을 활용한 개인 맞춤형 음악·챌린지 추천 일기 시스템     
> **S:ote**의 Spring Boot 기반 백엔드 리포지토리입니다.

![Java](https://img.shields.io/badge/Java-007396?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=flat-square&logo=flyway&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=flat-square&logo=firebase&logoColor=black)

---

## Project Overview

**S:ote**는 사용자가 작성한 일기를 기반으로 감정을 분석하고,      
감정에 맞는 음악과 챌린지를 추천하는 감정 기반 일기 서비스입니다.

사용자는 텍스트, 음성, 손글씨 기반으로 일기를 작성할 수 있으며,      
작성된 일기는 AI 감정 분석을 통해 감정 요약, 추천 음악, 감정 회복 챌린지로 이어집니다.

본 백엔드는 S:ote 서비스의 핵심 API 서버로,      
사용자 인증, 일기 데이터 관리, AI 분석 서버 연동, 챌린지 추천, 음악 LP 보상, 알림, 통계, STT/OCR, 웨어러블 연동 기능을 담당합니다.

본 저장소는 Fluxion 팀 캡스톤 프로젝트 **S:ote**의 백엔드 코드를 개인 포트폴리오용으로 정리한 리포지토리입니다.

| Item                     | Description                                                       |
| ------------------------ | ----------------------------------------------------------------- |
| Project                  | S:ote                                                             |
| Team                     | Fluxion                                                           |
| Period                   | 2025 Capstone Design                                              |
| Award                    | 2025 캡스톤 경진대회 아리상                                                 |
| Repository Type          | Portfolio-maintained backend repository                           |
| Original Team Repository | [fluxion-capstone/sote](https://github.com/fluxion-capstone/sote) |
| Personal Repository      | [DevLucia-21/sote-be](https://github.com/DevLucia-21/sote-be)     |
| Main Role                | Backend / AI / Frontend                                           |

---

## Service Concept

일기 서비스는 사용자의 감정을 기록할 수는 있지만, 기록 이후의 행동까지 자연스럽게 연결하는 경우는 많지 않습니다.

S:ote는 단순히 일기를 저장하는 데 그치지 않고,      
감정 분석 결과를 기반으로 사용자가 자신의 감정을 이해하고 회복 행동까지 이어갈 수 있도록 설계했습니다.

핵심 서비스 흐름은 다음과 같습니다.

```text
일기 작성
  → AI 감정 분석
  → 감정 요약 확인
  → 감정 기반 음악 추천
  → 감정 회복 챌린지 수행
  → LP 보상 저장
  → 캘린더 / 통계 / LP 보관함에서 회고
```

백엔드는 이 흐름이 안정적으로 이어질 수 있도록      
프론트엔드, FastAPI 기반 AI 서버, Spotify API, Firebase Cloud Messaging, PostgreSQL 데이터베이스 사이의 데이터 흐름을 연결합니다.

---

## Branch Guide

| Branch                                                                                   | Description                               |
| ---------------------------------------------------------------------------------------- | ----------------------------------------- |
| [`main`](https://github.com/DevLucia-21/sote-be/tree/main)                               | 포트폴리오용 최종 정리 브랜치                          |
| [`refactor/local`](https://github.com/DevLucia-21/sote-be/tree/refactor/local)           | 프로젝트 종료 후 기능 재점검 및 로컬 실행 안정화를 위한 리팩토링 브랜치 |
| [`demo/exhibition`](https://github.com/DevLucia-21/sote-be/tree/demo/exhibition)         | 캡스톤 경진대회 전시 및 시연용 브랜치                     |
| [`dev`](https://github.com/DevLucia-21/sote-be/tree/dev)                                 | 백엔드 개발 및 기능 통합 브랜치                        |
| [`release/deploy-main`](https://github.com/DevLucia-21/sote-be/tree/release/deploy-main) | 기존 배포용 `main` 상태 보존 브랜치                   |

---

## Backend Role

S:ote 백엔드는 단순 CRUD 서버가 아니라,      
사용자 일기 데이터가 AI 분석, 챌린지 추천, 음악 보상, 통계, 알림으로 이어지는 흐름을 관리하는 API 서버입니다.

주요 역할은 다음과 같습니다.

| Area           | Responsibility                       |
| -------------- | ------------------------------------ |
| Authentication | JWT 기반 로그인, 인증 사용자 식별, 보호 API 접근 제어  |
| Diary          | 사용자별 일기 작성, 조회, 수정, 삭제 처리            |
| AI Analysis    | FastAPI AI 서버와 연동하여 감정 분석 요청 및 결과 저장 |
| Challenge      | 감정 분석 결과 기반 챌린지 추천, 상태 조회, 완료 처리     |
| LP Reward      | 챌린지 완료 기반 음악 LP 보상 생성 및 조회           |
| Statistics     | 일기, 감정 분석, 챌린지 수행 기록 기반 통계 제공        |
| Notification   | FCM 토큰 관리, 사용자 알림 설정, 알림 발송 처리       |
| STT / OCR      | 음성 및 손글씨 기반 일기 입력을 위한 외부 AI 기능 연동    |
| Wear OS        | 웨어러블 클라이언트와 연동되는 건강 데이터 흐름 지원        |

---

## Core Features

### 1. Authentication

사용자 회원가입, 로그인, JWT 기반 인증 흐름을 구현했습니다.

인증된 요청에서는 JWT를 통해 사용자를 식별하고, 사용자별 일기, 분석 결과, 챌린지, 알림 데이터를 분리하여 처리합니다.

관련 패키지:

```text
src/main/java/com/fluxion/sote/auth
```

---

### 2. Diary

사용자의 일기 작성, 조회, 수정, 삭제 흐름을 처리합니다.

일기 데이터는 감정 분석 결과, 키워드, 캘린더, 통계 기능과 연결되며,      
분석 결과가 없는 일기도 정상적으로 조회될 수 있도록 일기 저장 흐름과 분석 결과 조회 흐름을 분리했습니다.

관련 패키지:

```text
src/main/java/com/fluxion/sote/diary
```

---

### 3. AI Emotion Analysis

일기 작성 이후 FastAPI 기반 AI 서버와 연동하여 감정 분석 결과를 처리합니다.

백엔드는 분석 요청을 전달하고, 분석 결과를 저장하며,      
프론트엔드가 감정 라벨, 감정 요약, 추천 음악, 추천 챌린지 정보를 조회할 수 있도록 API 흐름을 제공합니다.

관련 패키지:

```text
src/main/java/com/fluxion/sote/analysis
```

---

### 4. Challenge

감정 분석 결과를 기반으로 사용자에게 감정 회복 챌린지를 추천합니다.

사용자는 오늘의 챌린지를 확인하고 완료할 수 있으며, 챌린지 완료 결과는 LP 보상과 통계 데이터로 이어집니다.

관련 패키지:

```text
src/main/java/com/fluxion/sote/challenge
```

---

### 5. Music LP Reward

챌린지 완료 이후 감정 기반 음악 보상을 LP 형태로 저장합니다.

LP 보상은 추천 음악 정보와 챌린지 완료 이력을 연결하는 기록이며,      
사용자는 오늘의 LP와 주간 LP 기록을 통해 감정 경험을 음악 기록처럼 다시 확인할 수 있습니다.

관련 패키지:

```text
src/main/java/com/fluxion/sote/lpmusic
```

---

### 6. Spotify Integration

추천 음악의 제목, 아티스트, 앨범, 커버 이미지 등 프론트엔드에 필요한 음악 메타데이터를 제공하기 위해 Spotify API와 연동했습니다.

백엔드에서는 음악 추천 결과와 LP 보상 데이터가 함께 조회될 수 있도록 음악 메타데이터 흐름을 관리합니다.

관련 패키지:

```text
src/main/java/com/fluxion/sote/lpmusic
```

---

### 7. Statistics

일기 작성 기록, 감정 분석 결과, 챌린지 수행 기록을 기반으로 통계 데이터를 제공합니다.

프론트엔드 통계 화면에서 감정 분포, 일기 작성 추이, 챌린지 수행률 등을 확인할 수 있도록 API 응답 구조를 구성했습니다.

관련 패키지:

```text
src/main/java/com/fluxion/sote/statistics
```

---

### 8. Calendar

감정 분석 결과와 일기 기록을 날짜 기준으로 조회할 수 있도록 캘린더 데이터를 제공합니다.

프론트엔드는 해당 데이터를 기반으로 감정 캘린더와 감정 악보 화면을 구성합니다.

관련 패키지:

```text
src/main/java/com/fluxion/sote/calendar
```

---

### 9. Notification

Firebase Cloud Messaging 기반 알림 기능을 처리합니다.

사용자별 FCM 토큰을 등록하고, 알림 설정에 따라 일기 작성, 챌린지 수행, 감정 분석 완료, 주간 통계 등 서비스 행동을 돕는 알림 흐름을 관리합니다.

관련 패키지:

```text
src/main/java/com/fluxion/sote/setting
```

---

### 10. STT / OCR

텍스트 입력뿐만 아니라 음성 및 손글씨 기반 일기 입력을 지원하기 위해 STT, OCR 관련 API 흐름을 구성했습니다.

음성 또는 이미지 기반 입력 결과는 일기 작성 흐름과 연결됩니다.

관련 패키지:

```text
src/main/java/com/fluxion/sote/stt
src/main/java/com/fluxion/sote/ocr
```

---

### 11. Health & Wear OS

웨어러블 클라이언트와 연동되는 건강 데이터 및 감정 기록 흐름을 지원합니다.

사용자의 건강 데이터와 감정 기록을 함께 다룰 수 있도록 관련 API 영역을 분리했습니다.

관련 패키지:

```text
src/main/java/com/fluxion/sote/health
src/main/java/com/fluxion/sote/watch
```

---

## User Flow

```text
회원가입 / 로그인
        ↓
일기 작성
        ↓
백엔드에 일기 저장
        ↓
FastAPI AI 서버로 감정 분석 요청
        ↓
감정 분석 결과 저장
        ↓
추천 음악 + 추천 챌린지 조회
        ↓
챌린지 완료
        ↓
LP 보상 생성
        ↓
캘린더 / 통계 / LP 보관함에서 기록 확인
        ↓
FCM 알림을 통한 서비스 행동 리마인드
```

---

## Backend Architecture

S:ote 백엔드는 도메인별 패키지 구조를 기반으로 Controller, Service, Repository, Entity, DTO를 분리하여 구성했습니다.

각 도메인은 인증된 사용자 정보를 기준으로 데이터를 처리하며,      
감정 분석, 챌린지, LP 보상처럼 서로 연결되는 기능은 서비스 계층에서 흐름을 조율하도록 구성했습니다.

```text
Frontend
  ↓
Spring Boot Backend
  ├── Auth / User
  ├── Diary
  ├── Analysis
  ├── Challenge
  ├── LP Music
  ├── Statistics / Calendar
  ├── Notification / Setting
  ├── STT / OCR
  └── Health / Watch
  ↓
PostgreSQL / Redis / Firebase / Spotify / FastAPI
```

---

## Project Structure

```text
src/main/java/com/fluxion/sote
├── analysis        # 감정 분석 결과 및 AI 서버 연동
├── answer          # 질문 답변 관련 기능
├── auth            # 인증, JWT, 사용자 인증 처리
├── calendar        # 감정 캘린더 관련 데이터 조회
├── challenge       # 챌린지 추천, 상태, 완료 처리
├── diary           # 일기 작성, 조회, 수정, 삭제
├── global          # 공통 설정, 예외 처리, 보안 설정
├── health          # 건강 데이터 관련 기능
├── lpmusic         # 음악 LP 보상 및 음악 메타데이터 관리
├── ocr             # 손글씨 이미지 기반 일기 입력 연동
├── question        # 하루 질문 관련 기능
├── setting         # 사용자 설정, 알림 설정, FCM 토큰 처리
├── statistics      # 감정, 일기, 챌린지 통계
├── stress          # 스트레스 관련 데이터 처리
├── stt             # 음성 기반 일기 입력 연동
├── user            # 사용자 프로필 및 사용자 정보 관리
├── watch           # Wear OS 연동 기능
└── SoteApplication.java
```

---

## External Integration

| Integration              | Description                       |
| ------------------------ | --------------------------------- |
| FastAPI AI Server        | 감정 분석, STT, OCR 등 AI 처리 서버와 연동    |
| Spotify API              | 추천 음악 메타데이터 조회 및 LP 보상 흐름에 활용     |
| Firebase Cloud Messaging | 사용자 알림 발송 및 FCM 토큰 관리             |
| Google Cloud Storage     | 프로필 이미지 및 외부 파일 저장 흐름에 활용         |
| PostgreSQL               | 사용자, 일기, 분석 결과, 챌린지, LP 보상 데이터 저장 |
| Redis                    | 인증 및 서비스 보조 데이터 처리를 위한 인메모리 저장소   |

---

## Technical Challenges

### 1. 커진 서비스 범위에 맞춘 도메인 구조 관리

S:ote는 단순 일기 작성 기능만 있는 서비스가 아니라,     
일기, 감정 분석, 챌린지, 음악 보상, 알림, 통계, STT/OCR, Wear OS 연동까지 포함하는 프로젝트였습니다.

기능이 늘어날수록 각 도메인의 책임을 분리하고,     
Controller, Service, Repository, Entity, DTO 구조를 유지하는 것이 중요했습니다.

백엔드에서는 기능별 패키지를 분리하여 API 흐름을 관리하고,     
서로 연결되는 기능은 서비스 계층에서 조율하는 방식으로 구조를 정리했습니다.

---

### 2. 일기 저장 이후 AI 분석으로 이어지는 흐름 처리

S:ote는 일기 작성 직후 감정 분석 결과가 즉시 준비되지 않을 수 있는 구조입니다.

따라서 백엔드는 일기 저장과 감정 분석 결과 저장 흐름을 분리하고,     
프론트엔드가 분석 결과 준비 여부에 따라 적절히 분기할 수 있도록 API 응답 흐름을 구성했습니다.

이 구조를 통해 분석 결과가 아직 없더라도 사용자의 일기 데이터는 보존되고,     
이후 분석 결과가 생성되면 일기 상세 화면과 감정 기반 기능으로 연결될 수 있도록 했습니다.

---

### 3. 감정 분석 결과 기반 챌린지·LP 보상 상태 관리

챌린지는 감정 분석 결과를 기반으로 추천되지만,    
사용자가 실제로 수행하는 챌린지는 사용자와 날짜 기준으로 상태가 관리되어야 했습니다.

백엔드에서는 오늘의 챌린지 상태, 완료 여부, 완료 시간, LP 보상 생성 흐름이 서로 어긋나지 않도록 처리해야 했습니다.

특히 챌린지 완료 이후 LP 보상이 생성되고,     
해당 보상이 오늘의 보상과 주간 LP 기록으로 조회되는 흐름까지 연결해야 했기 때문에 상태 관리의 디테일이 중요했습니다.

---

### 4. Flyway 기반 데이터베이스 마이그레이션 관리

프로젝트에서는 Flyway를 사용해 PostgreSQL 스키마와 초기 데이터를 관리했습니다.

개발 과정에서 이미 적용된 migration SQL을 수정하면 checksum이 달라져     
`flywayInitializer` Bean 생성 실패와 같은 오류가 발생할 수 있었습니다.

이를 통해 마이그레이션 파일은 단순 SQL 파일이 아니라 배포 이력으로 관리해야 하며,     
이미 반영된 migration은 수정하기보다 새로운 migration으로 변경 사항을 누적하는 방식이 필요하다는 점을 경험했습니다.

---

### 5. 배포 환경과 로컬 환경의 차이 해결

로컬 개발 환경에서 정상 동작하던 기능도 Render와 같은 외부 서버 환경에서는 다르게 동작할 수 있었습니다.

배포 과정에서 환경변수 설정 충돌, API 엔드포인트 경로 차이, 인증키 누락, 외부 서버 응답 지연 등의 문제가 반복적으로 발생했습니다.

백엔드에서는 실제 배포 환경에 맞추어 설정값을 점검하고,     
외부 AI 서버, Spotify API, Firebase, GCP Storage와 연결되는 지점을 하나씩 확인하며 오류를 해결했습니다.

이 과정에서 단순히 코드를 작성하는 것뿐만 아니라,
실행 환경과 설정 관리까지 포함해 서비스를 안정화하는 과정이 중요하다는 것을 배웠습니다.

---

### 6. 외부 API 연동 실패 가능성 고려

S:ote는 FastAPI AI 서버, Spotify API, Firebase Cloud Messaging, Google Cloud Storage 등 여러 외부 서비스와 연동됩니다.

외부 API는 응답 지연, 권한 제한, 네트워크 문제, 인증키 누락 등으로 인해 항상 정상 응답을 보장할 수 없습니다.

백엔드에서는 외부 연동 기능을 도메인별 서비스로 분리하고,      
설정값을 환경 변수 기반으로 관리하여 공개 저장소에 민감 정보가 포함되지 않도록 정리했습니다.

---

### 7. 공개 리포지토리 전환을 위한 보안 설정 정리

포트폴리오 공개 리포지토리로 전환하면서 실제 DB 접속 정보, JWT Secret, Spotify Secret, Firebase Admin SDK JSON, GCP 인증 파일 등 민감 정보를 저장소에서 제외했습니다.

실행에 필요한 설정값은 `application.yml`에 직접 포함하지 않고,      
`application-example.yml`을 통해 필요한 환경 변수 구조만 확인할 수 있도록 정리했습니다.

---

## My Contribution

본 프로젝트에서는 초기 백엔드와 AI 서버 개발을 함께 담당했으며,     
프로젝트 진행 과정에서 프론트엔드 개발까지 맡아 전체 서비스 흐름을 구현했습니다.

백엔드에서는 인증, 일기, 감정 분석 연동, 챌린지 추천, 음악 보상, 알림 등 주요 기능 흐름의 구현과 수정에 참여했습니다.

프로젝트 규모가 커지면서 단순 기능 구현보다 기능 간 연결, 예외 상황, 배포 환경 설정, 데이터 흐름의 일관성이 중요하다는 점을 경험했습니다.

프로젝트 종료 이후에는 다시 서비스를 실행하며 발견된 오류를 수정하고,     
백엔드 구조와 기능 흐름을 포트폴리오용으로 재정리했습니다.

최종적으로 백엔드, 프론트엔드, AI 서버가 연결되는 전체 사용자 흐름을 이해하고,     
공개 리포지토리 전환을 위한 환경 변수 정리, 민감 정보 제거, 브랜치 정리, README 작성까지 담당했습니다.

| Area              | Contribution                                       |
| ----------------- | -------------------------------------------------- |
| Backend API       | 인증, 일기, 분석 결과, 챌린지, LP 보상, 알림 관련 API 흐름 구현 및 수정    |
| AI Integration    | FastAPI 기반 감정 분석 서버와 Spring Boot 백엔드 연동 흐름 구성      |
| Authentication    | JWT 기반 사용자 인증 및 인증 사용자 기준 데이터 처리 흐름 구성             |
| Challenge Flow    | 감정 분석 기반 챌린지 추천, 오늘의 챌린지 상태, 완료 처리 흐름 점검           |
| LP Reward Flow    | 챌린지 완료 후 음악 LP 보상 생성 및 조회 흐름 정리                    |
| Notification      | Firebase Cloud Messaging 기반 토큰 저장 및 알림 처리 흐름 점검    |
| Data Management   | PostgreSQL, JPA, Flyway 기반 도메인 데이터 관리 구조 유지        |
| Deployment Check  | 배포 환경에서 환경변수, API 경로, 외부 인증키 설정 문제 점검              |
| Refactoring       | 프로젝트 종료 후 로컬 실행 오류 수정 및 주요 기능 흐름 안정화               |
| Portfolio Cleanup | 공개 리포지토리 전환을 위한 민감 정보 제거, 환경 변수 예시, README, 브랜치 정리 |

---

## Tech Stack

| Category                | Stack                                        |
| ----------------------- | -------------------------------------------- |
| Language                | Java 17                                      |
| Framework               | Spring Boot 3.5.3                            |
| Security                | Spring Security, JWT                         |
| Database                | PostgreSQL                                   |
| ORM                     | Spring Data JPA, Hibernate                   |
| Migration               | Flyway                                       |
| Cache / Session Support | Redis                                        |
| Validation              | Spring Validation                            |
| Mail                    | Spring Boot Mail                             |
| Notification            | Firebase Admin SDK, Firebase Cloud Messaging |
| Storage                 | Google Cloud Storage                         |
| External API Client     | Apache HttpClient 5                          |
| Build Tool              | Gradle                                       |
| Test                    | JUnit, Spring Security Test                  |

---

## Environment Variables

프로젝트 실행을 위해 로컬 환경에서 `application.yml`을 생성하고 필요한 값을 설정해야 합니다.

공개 저장소에는 실제 API Key, DB 접속 정보, Firebase Admin SDK JSON, GCP 인증 파일 등 민감 정보를 포함하지 않습니다.

환경 변수 구성 예시는 아래 파일을 참고합니다.

```text
src/main/resources/application-example.yml
```

주요 설정 항목은 다음과 같습니다.

```text
DB_URL
DB_USERNAME
DB_PASSWORD

JWT_SECRET

REDIS_HOST
REDIS_PORT

MAIL_HOST
MAIL_PORT
MAIL_USERNAME
MAIL_PASSWORD

FASTAPI_STT_URL
AI_ANALYSIS_BASE_URL

SPOTIFY_CLIENT_ID
SPOTIFY_CLIENT_SECRET

GCP_PROJECT_ID
GCP_BUCKET
GCP_CREDENTIALS_LOCATION

FIREBASE_SERVICE_ACCOUNT_PATH
```

---

## Running the Project

```bash
git clone https://github.com/DevLucia-21/sote-be.git
cd sote-be
```

로컬 실행을 위해 `application-example.yml`을 참고하여 `application.yml`을 생성합니다.

```text
src/main/resources/application.yml
```

실행 명령어는 다음과 같습니다.

```bash
./gradlew bootRun
```

Windows 환경에서는 다음 명령어를 사용할 수 있습니다.

```bash
gradlew.bat bootRun
```

테스트 명령어는 다음과 같습니다.

```bash
./gradlew test
```

---

## Related Repositories

| Repository                                                        | Description              |
| ----------------------------------------------------------------- | ------------------------ |
| [sote-fe](https://github.com/DevLucia-21/sote-fe)                 | S:ote 프론트엔드 리포지토리        |
| [sote-be](https://github.com/DevLucia-21/sote-be)                 | Spring Boot 기반 백엔드 리포지토리 |
| [sote-ai](https://github.com/DevLucia-21/sote-ai)                 | FastAPI 기반 AI 서버 리포지토리   |
| [fluxion-capstone/sote](https://github.com/fluxion-capstone/sote) | S:ote 원본 팀 백엔드 리포지토리     |

---

## Note

본 저장소는 Fluxion 팀 캡스톤 프로젝트의 백엔드 코드를 개인 포트폴리오용으로 정리한 리포지토리입니다.

실제 운영 환경에서 사용한 민감 설정값은 포함하지 않으며,       
로컬 실행을 위해서는 별도의 환경 변수 및 외부 서비스 설정이 필요합니다.

원본 팀 리포지토리는 팀 프로젝트 진행 당시 사용한 저장소이며, 현재 접근 권한 또는 공개 여부가 변경되었을 수 있습니다.

---

## Author

**Yeeun Park**

* GitHub: [DevLucia-21](https://github.com/DevLucia-21)
