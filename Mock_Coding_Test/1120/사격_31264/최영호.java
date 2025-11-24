import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Main {
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;

    static int N, M, A;
    static int[] arr;

    public static void main(String[] args) throws IOException {
        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        A = Integer.parseInt(st.nextToken());

        arr = new int[N];
        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < N; i++) {
            arr[i] = Integer.parseInt(st.nextToken());
        }

        Arrays.sort(arr);

        long start = 0;
        long end = arr[N - 1]; // 최대 필요 실력은 가장 큰 표적 점수
        long ans = end;

        while (start <= end) {
            long mid = (start + end) / 2;

            if (check(mid)) {
                ans = mid;
                end = mid - 1;
            } else {
                start = mid + 1;
            }
        }

        System.out.println(ans);
    }

    // boolean 반환이 더 직관적입니다 (A점 이상 획득 가능 여부)
    static boolean check(long mid) {
        long currentSkill = mid;
        long currentScore = 0;

        for (int i = 0; i < M; i++) {
            int s = 0;
            int e = N - 1;
            int idx = -1;

            // 현재 실력(currentSkill) 이하인 것 중 가장 큰 값 찾기
            while (s <= e) {
                int m = (s + e) / 2;

                if (arr[m] <= currentSkill) {
                    idx = m;      // 쏠 수 있음. 더 큰 것도 쏠 수 있나 확인
                    s = m + 1;
                } else {
                    e = m - 1;    // 못 쏨. 더 작은 것 확인
                }
            }

            if (idx == -1) break; // 쏠 수 있는 게 없으면 훈련 종료

            currentSkill += arr[idx];
            currentScore += arr[idx];

            // 최적화: 이미 목표 점수를 넘었으면 true 리턴
            if (currentScore >= A) return true;
        }

        return currentScore >= A;
    }
}
