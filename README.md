# ✍️ 우리어리 - 교환 일기 프로젝트

카카오 로그인 기반의 그룹형 교환 일기 서비스입니다.  
사용자는 그룹을 만들고 함께 일기를 쓰고, 댓글로 소통할 수 있습니다.

---

## ✅ 기술 스택

| 구분       | 기술 |
|------------|------|
| **Backend**  | Spring Boot, Spring Security, JWT, OAuth2 (Kakao) |
| **Database** | H2 (개발용), MySQL (RDS - 운영용), JPA (Hibernate) |
| **Storage**  | AWS S3 (Presigned URL 기반 이미지 업로드) |
| **Infra**    | EC2 (Ubuntu), RDS (MySQL), HTTPS (443), GitHub Actions 예정 |

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

---

## 🗄️ DB 설계

### 📌 주요 테이블

| 테이블       | 설명                                                                                                                   |
| ------------ | -------------------------------------------------------------------------------------------------------------------- |
| `users`      | 사용자 정보 테이블 (예: `email`, `nickname`, `created_at` 등)                                                                  |
| `user_group` | **그룹 정보 테이블** (예: `id`, `name`, `code`, `created_at` 등) |
| `group_user` | **사용자-그룹 간 다대다 관계를 나타내는 조인 테이블**<br>`group_id`, `user_id` 로 구성                                                       |
| `diary`      | 일기 정보 테이블 (예: `title`, `content`, `image_path`, `created_at` 등)<br>각 일기는 특정 그룹과 작성자(`user`)에 속함                      |
| `comment`    | 댓글 및 대댓글 테이블<br>`diary_id`, `user_id`, `parent_id`(대댓글 여부 판별) 등 포함                                                   |


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

