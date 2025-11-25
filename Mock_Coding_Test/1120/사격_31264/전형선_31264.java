// 31264. [G5] 사격.

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;

public class Main {
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
    static StringTokenizer st;
    // N: 표적의 개수, M: 최대 사격 횟수, A: 진급에 필요한 최소 사격 점수
    static int N, M, A;
    // 필요 사격 실력
    static long initValue = (long) Math.pow(10, 10);
    // 스코어 보드
    static int[] scores;

    public static void main(String[] args) throws IOException {
        input();
        solve();
        output();
    }

    static void input() throws IOException {
        // N, M, A 입력
        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        A = Integer.parseInt(st.nextToken());
        scores = new int[N];
        // 스코어 입력
        st = new StringTokenizer(br.readLine());
        for (int n = 0; n < N; n++) {
            scores[n] = Integer.parseInt(st.nextToken());
        }
        // 정렬
        Arrays.sort(scores);
    }

    static void solve() {
        long left = 1, right = initValue;
        // 파라매트릭 서치
        while (left <= right) {
            // 중간값을 초기 능력치로 설정
            long mid = (left + right) / 2;
            long total = 0;
            // 과녁 쏘기
            for (int cnt = M; cnt > 0; cnt--) {
                int l = 0, r = N - 1;
                while (l <= r) {
                    int m = (l + r) / 2;
                    if (mid + total < scores[m]) {
                        r = m - 1;
                    } else {
                        l = m + 1;
                    }
                }
                if (r < 0) break;
                total += scores[r];
            }
            if (total >= A) {
                initValue = Math.min(initValue, mid);
                right = mid - 1;
            } else {
                left = mid + 1;
            }

        }
    }

    static void output() throws IOException {
        bw.write(String.valueOf(initValue));
        bw.flush();
    }
}
