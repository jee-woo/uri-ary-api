# ✍️ 우리어리 - 교환 일기 프로젝트

카카오 로그인 기반의 그룹형 교환 일기 서비스입니다.
사용자는 그룹을 만들고 함께 일기를 쓰고, 댓글로 소통할 수 있습니다.

---

## ✅ 기술 스택

| 구분 | 기술 |
| --- | --- |
| **Backend** | Spring Boot, Spring Security, JWT, OAuth2 (Kakao) |
| **Database** | H2 (개발용), MySQL (RDS - 운영용), JPA (Hibernate) |
| **Storage** | AWS S3 (Presigned URL 기반 이미지 업로드) |
| **Infra** | EC2 (Ubuntu), RDS (MySQL), HTTPS (443), GitHub Actions 예정 |

---

## 🧩 주요 기능

### 🔐 인증
- 카카오 OAuth2 로그인
- JWT 발급 및 저장
- 로그인 성공 시 프론트 리디렉션 + 토큰 전달

### 👥 그룹
- 그룹 생성, 참여, 구성원 추가
- 초대 코드 기반 가입

### 📔 일기
- 그룹별 일기 목록 / 상세 조회
- 일기 작성 (텍스트 + 이미지)

### 💬 댓글
- 댓글 / 대댓글 작성 및 트리 구조 렌더링
- 일기별 댓글 목록 조회

### 🔒 E2EE (End-to-End Encryption)
- 모든 일기 내용은 사용자 기기에서 암호화되어 서버에 저장됩니다.
- 그룹에 새로운 멤버가 추가될 때, 기존 멤버가 새로운 멤버의 공개키를 사용하여 일기 암호화 키를 다시 암호화하여 공유합니다.
- 이를 통해 새로운 멤버도 과거의 일기를 복호화하여 볼 수 있습니다.

---

## 🗄️ DB 설계

### 📌 주요 테이블

| 테이블 | 설명 |
| --- | --- |
| `users` | 사용자 정보 테이블 |
| `groups` | 그룹 정보 테이블 |
| `group_members` | 사용자와 그룹의 관계를 나타내는 테이블 |
| `diary` | 일기 정보 테이블 |
| `diary_keys` | 일기 암호화 키 정보 테이블 |
| `comment` | 댓글 정보 테이블 |
| `notification` | 알림 정보 테이블 |


### 🧩 ERD

![ERD](https://github.com/user-attachments/assets/e2959342-736c-45f7-af5c-654b7fc3a4f8)


---

## 📖 API Endpoints

### Auth
- `POST /api/auth/login`: 카카오 로그인
- `POST /api/auth/refresh`: JWT 토큰 재발급

### User
- `GET /api/user/me`: 내 정보 조회
- `POST /api/user/me/publicKey`: 공개키 등록

### Groups
- `POST /api/groups`: 그룹 생성
- `GET /api/groups/{groupId}`: 그룹 상세 조회
- `GET /api/groups/{groupId}/members`: 그룹 멤버 목록 조회
- `POST /api/groups/join-requests`: 그룹 참여 요청
- `GET /api/groups/join-requests/{targetId}/approval-info`: 그룹 가입 승인에 필요한 정보 조회
- `POST /api/groups/join-requests/{targetId}/approve`: 그룹 가입 요청 승인 및 키 교환

### Diaries
- `POST /api/groups/{groupId}/diaries`: 일기 작성
- `GET /api/diaries/{diaryId}`: 일기 상세 조회

### Comments
- `POST /api/diaries/{diaryId}/comments`: 댓글 작성

### Notifications
- `GET /api/notifications`: 알림 목록 조회
- `POST /api/notifications/{notificationId}/read`: 알림 읽음 처리

---

## 🖼️ 이미지 처리

- 이미지 경로(`imagePath`)만 DB에 저장
- 프론트는 해당 경로로 `GET` 요청하여 렌더링
- Presigned URL은 1시간 유효 (보안성 확보)

---

## 🛡️ 보안 설계

- Spring Security + JWT
- 인증된 사용자만 API 접근 허용 (`/api/**`)
- CORS 설정, CSRF 비활성화

---

## 🚀 배포

- EC2 (Ubuntu) 서버에 JAR 파일 배포
- MySQL RDS와 연결
- `application-prod.properties` 분리 사용
