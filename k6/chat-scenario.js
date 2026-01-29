import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export const options = {
  stages: [
    { duration: '10s', target: 50 }, // 50명까지 증가 (테스트용으로 50명으로 조정)
    { duration: '1m', target: 50 },  // 1분간 유지
    { duration: '10s', target: 0 },  // 종료
  ],
};

const BASE_URL = 'http://localhost:8080';

export default function () {
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
    console.error(`Login failed for ${userId}: ${loginRes.status}`);
    sleep(1);
    return; // 로그인 실패 시 이번 반복 중단
  }

  // 2. 채널 목록 조회 (자동으로 SESSION 쿠키가 포함됨)
  const channelsRes = http.get(`${BASE_URL}/api/channels`);
  check(channelsRes, { 'Get Channels 200': (r) => r.status === 200 });

  // 3. 첫 번째 채널의 메시지 조회 (MongoDB)
  try {
      const channels = channelsRes.json();
      if (channels && channels.length > 0) {
        const channelId = channels[0].channelId;
        const msgRes = http.get(`${BASE_URL}/api/messages?channelId=${channelId}`);
        check(msgRes, { 'Get Messages 200': (r) => r.status === 200 });
      }
  } catch (e) {
      // JSON 파싱 에러 등 무시
  }

  sleep(1);
}
