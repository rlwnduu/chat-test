#!/bin/bash

echo "🚀 배포를 시작합니다..."

# 1. 최신 코드 가져오기
echo "📥 Git Pull..."
git pull

# 2. Docker 이미지 빌드 및 컨테이너 실행 (운영 모드)
echo "🐳 Docker Compose Up (Prod)..."
docker-compose -f docker-compose.prod.yml up --build -d

# 3. 불필요한 이미지 정리 (공간 확보)
echo "🧹 Pruning unused images..."
docker image prune -f

echo "✅ 배포가 완료되었습니다!"
