import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '10s', target: 30 }, // 10명 -> 30명으로 증가
    { duration: '1m', target: 30 },  // 1분간 30명 유지
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

  // 2. 로그인 시도 (Form Data 전송)
  // K6의 http.post는 두 번째 인자가 객체일 경우 자동으로 x-www-form-urlencoded로 변환하여 전송합니다.
  const loginPayload = {
    loginId: loginId,
    password: password,
  };

  const loginRes = http.post(`${BASE_URL}/api/auth/login`, loginPayload);

  const isLoginSuccess = check(loginRes, {
    'Login success': (r) => r.status === 200,
  });

  if (!isLoginSuccess) {
    console.error(`Login failed for ${loginId} (Status: ${loginRes.status})`);
    return;
  }

  // 3. 토큰 추출 로직 제거 (세션 쿠키 자동 관리)
  // K6는 응답의 Set-Cookie 헤더를 자동으로 처리하여 이후 요청에 포함시킵니다.

  // 4. 내 채널 목록 조회
  // 별도의 Authorization 헤더 없이 요청하면 쿠키가 자동으로 전송됩니다.
  const channelRes = http.get(`${BASE_URL}/api/channels`);

  check(channelRes, {
    'Get Channels success': (r) => r.status === 200,
  });

  sleep(1);
}
