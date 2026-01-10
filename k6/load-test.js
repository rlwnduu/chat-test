import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '10s', target: 30 }, // 10명 -> 50명으로 증가
    { duration: '1m', target: 30 },  // 1분간 50명 유지 (이때가 진짜 승부처)
    { duration: '10s', target: 0 },
  ],
};

const BASE_URL = 'http://localhost:8080';

// [추가] 테스트 시작 시각 출력
export function setup() {
  console.log(`Test started at: ${new Date().toISOString()}`);
}

export default function () {
  // 1. 각 VU(가상 유저)마다 고유한 ID 생성
  const idSequence = __VU;
  const loginId = `test${idSequence}`; // user1, user2 ...
  const password = 'test';

  // 2. 로그인 시도
  const loginPayload = JSON.stringify({
    loginId: loginId, // email -> username 변경
    password: password,
  });

  const loginRes = http.post(`${BASE_URL}/api/auth/login`, loginPayload, {
    headers: { 'Content-Type': 'application/json' },
  });

  const isLoginSuccess = check(loginRes, {
    'Login success': (r) => r.status === 200,
  });

  if (!isLoginSuccess) {
    console.error(`Login failed for ${loginId} (Status: ${loginRes.status})`);
    return;
  }

  // 3. 토큰 추출
  const token = loginRes.json('accessToken');

  if (!token) {
      console.error(`Token not found for ${loginId}`);
      return;
  }

  const authHeaders = {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  };

  // 4. 내 채널 목록 조회
  const channelRes = http.get(`${BASE_URL}/api/channels`, authHeaders);

  check(channelRes, {
    'Get Channels success': (r) => r.status === 200,
  });

  sleep(1);
}
