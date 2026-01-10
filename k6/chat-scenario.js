import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '10s', target: 150 }, // 50명까지 증가
    { duration: '1m', target: 150 },  // 1분간 유지
    { duration: '10s', target: 0 },  // 종료
  ],
};

const BASE_URL = 'http://localhost:8080';

// [Setup] 테스트 시작 전 50명 미리 로그인 -> 토큰 발급
export function setup() {
  const tokens = [];
  for (let i = 1; i <= 150; i++) {
    const payload = JSON.stringify({
      loginId: `test${i}`,
      password: 'test',
    });

    const res = http.post(`${BASE_URL}/api/auth/login`, payload, {
      headers: { 'Content-Type': 'application/json' },
    });

    if (res.status === 200) {
      tokens.push(res.json('accessToken'));
    } else {
      console.error(`Setup login failed for test${i}: ${res.status}`);
    }
  }
  return tokens;
}

export default function (tokens) {
  // 토큰이 없으면 중단
  if (!tokens || tokens.length === 0) return;

  // 각 VU에게 토큰 분배
  const myToken = tokens[(__VU - 1) % tokens.length];

  const authHeaders = {
    headers: {
      'Authorization': `Bearer ${myToken}`,
      'Content-Type': 'application/json',
    },
  };

  // 1. 채널 목록 조회 (MySQL)
  const channelsRes = http.get(`${BASE_URL}/api/channels`, authHeaders);
  check(channelsRes, { 'Get Channels 200': (r) => r.status === 200 });

  // 2. 첫 번째 채널의 메시지 조회 (MongoDB)
  // (채널이 하나도 없으면 에러 날 수 있으니 체크)
  try {
      const channels = channelsRes.json();
      if (channels && channels.length > 0) {
        const channelId = channels[0].channelId;
        const msgRes = http.get(`${BASE_URL}/api/messages?channelId=${channelId}`, authHeaders);
        check(msgRes, { 'Get Messages 200': (r) => r.status === 200 });
      }
  } catch (e) {
      // JSON 파싱 에러 등 무시
  }

  sleep(1);
}
