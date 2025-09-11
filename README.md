# InsightOps MailContents

## 서비스명: InsightOps MailContents
역할: 메일 내용 생성 서비스
포트: 8080

---

### 주요 기능
- **POST** `/api/mail/generate` – 메일 내용 생성
- **GET** '/api/healthcheck' - 서비스 헬스 체크

---

### 실행 방법
```bash
./gradlew bootRun
```
또는
```bash
docker build -t insightops-mailcontents .
docker run -p 8080:8080 insightops-mailcontents
```

---

### API 테스트
- **Swagger UI**: 현재 제공되지 않음
- **Postman**: Postman collection 파일을 사용하여 API 테스트 가능

---

### 데이터베이스 정보
- **데이터베이스**: 직접적인 데이터베이스 연결 없음
- **API 연동**: Admin API, Normalization API, Mail Send API를 통해 데이터 처리

---

### 환경변수 설정
#### 로컬 개발 환경
```bash
# 터미널에서 직접 설정
export OPENAI_API_KEY="your-actual-openai-api-key"
export ADMIN_API_BASE_URL="https://insightops-admin-bnbchyhyc3hzb8ge.koreacentral-01.azurewebsites.net"
export NORMALIZATION_API_BASE_URL="https://insightops-classification-d2acc8afftgmhubt.koreacentral-01.azurewebsites.net"
export MAILSEND_API_BASE_URL="https://insightops-mailsend-e4drbwhqhge4bzam.koreacentral-01.azurewebsites.net"

# 애플리케이션 실행
./gradlew bootRun
```

#### Docker Compose 환경
```bash
# 환경변수 설정 후 실행
export OPENAI_API_KEY="your-actual-openai-api-key"
export ADMIN_API_BASE_URL="https://insightops-admin-bnbchyhyc3hzb8ge.koreacentral-01.azurewebsites.net"
export NORMALIZATION_API_BASE_URL="https://insightops-classification-d2acc8afftgmhubt.koreacentral-01.azurewebsites.net"
export MAILSEND_API_BASE_URL="https://insightops-mailsend-e4drbwhqhge4bzam.koreacentral-01.azurewebsites.net"
docker-compose up -d
```

---

### Docker 실행
```bash
# 이미지 빌드
docker buildx build --platform linux/amd64,linux/arm64 -t eunse/insightops-mailcontents:latest .

# 컨테이너 실행
docker run -d -p 8080:8080 \
  -e OPENAI_API_KEY="your-actual-openai-api-key" \
  -e ADMIN_API_BASE_URL="https://insightops-admin-bnbchyhyc3hzb8ge.koreacentral-01.azurewebsites.net" \
  -e NORMALIZATION_API_BASE_URL="https://insightops-classification-d2acc8afftgmhubt.koreacentral-01.azurewebsites.net" \
  -e MAILSEND_API_BASE_URL="https://insightops-mailsend-e4drbwhqhge4bzam.koreacentral-01.azurewebsites.net" \
  --name insightops-mailcontents \
  eunse/insightops-mailcontents:latest
```

---

### 보안
- 민감한 정보(DB 비밀번호, API 키)는 환경변수로 관리
- `.env` 파일은 `.gitignore`에 포함되어 Git에 업로드되지 않음
- GitHub Secrets를 통한 CI/CD 환경변수 관리

---

### 담당자
- **이름**: Eunse
- **담당 기능**: 전체 서비스 관리 및 API 연동