# 1. 베이스 이미지 (어떤 환경에서 돌릴지)
# 자바 17 버전이 설치된 리눅스 환경을 가져옵니다.
FROM eclipse-temurin:17-jdk

# 2. 작업 디렉토리 설정
# 컨테이너 내부의 /app 이라는 폴더에서 작업을 하겠다는 뜻입니다.
WORKDIR /app

# 3. JAR 파일 복사 (내 컴퓨터 -> 컨테이너)
# 빌드된 jar 파일을 컨테이너 내부로 가져옵니다.
# (주의: 먼저 ./gradlew build 를 해서 jar 파일이 생성되어 있어야 합니다!)
COPY build/libs/*-SNAPSHOT.jar app.jar

# 4. 실행 명령어
# 컨테이너가 시작될 때 이 명령어를 실행합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]
