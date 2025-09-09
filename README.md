# InsightOps_mailcontents

## 🚀 메일 콘텐츠 생성 서비스

이 서비스는 DB에서 데이터를 조회하고 OpenAI GPT를 사용하여 개선 리포트 메일을 자동 생성합니다.

## 📋 API 엔드포인트

### Health Check
```bash
GET /mail/health
```

### 메일 생성
```bash
POST /mail/generate
Content-Type: application/json

{
  "consulting_category": "도난/분실 신청/해제"
}
```

## 🔧 환경변수 설정

### 로컬 개발 환경
```bash
# 터미널에서 직접 설정
export DB_URL="jdbc:mysql://insightops-admin.mysql.database.azure.com:3306/normalization_db?useSSL=true&serverTimezone=UTC"
export DB_USERNAME="insightopsadmin"
export DB_PASSWORD="your-actual-password"
export OPENAI_API_KEY="your-actual-api-key"

# 애플리케이션 실행
./gradlew bootRun
```

### Docker Compose 환경
```bash
# 환경변수 설정 후 실행
export DB_PASSWORD="your-actual-password"
export OPENAI_API_KEY="your-actual-api-key"
docker-compose up -d
```

## 🐳 Docker 실행

```bash
# 이미지 빌드
docker buildx build --platform linux/amd64,linux/arm64 -t eunse/insightops-mailcontents:latest .

# 컨테이너 실행
docker run -d -p 8080:8080 \
  -e DB_URL="jdbc:mysql://insightops-admin.mysql.database.azure.com:3306/normalization_db?useSSL=true&serverTimezone=UTC" \
  -e DB_USERNAME="insightopsadmin" \
  -e DB_PASSWORD="your-actual-password" \
  -e OPENAI_API_KEY="your-actual-api-key" \
  --name insightops-mailcontents \
  eunse/insightops-mailcontents:latest
```

## 🔒 보안

- 민감한 정보(DB 비밀번호, API 키)는 환경변수로 관리
- `.env` 파일은 `.gitignore`에 포함되어 Git에 업로드되지 않음
- GitHub Secrets를 통한 CI/CD 환경변수 관리