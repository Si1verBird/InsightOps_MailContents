
# .env 파일을 읽어서 환경 변수로 export하는 함수
load_env() {
    set -a
    source .env
    set +a
}

# 환경 변수 로드 후 애플리케이션 실행
load_env && ./gradlew bootRun
