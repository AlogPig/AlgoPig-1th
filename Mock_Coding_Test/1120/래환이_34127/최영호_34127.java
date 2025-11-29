import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.StringTokenizer;

public class boj34127 {
    static int N;
    static int[] H;
    static long totalH;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        N = Integer.parseInt(br.readLine());
        H = new int[N];

        StringTokenizer st = new StringTokenizer(br.readLine());
        for (int i = 0; i < N; i++) {
            H[i] = Integer.parseInt(st.nextToken());
            totalH += H[i];
        }

        // 목표 1: 총합이 같을 때 시도
        if (solve(totalH)) return;

        // 목표 2: 총합이 1 작을 때 시도
        if (solve(totalH - 1)) return;

        System.out.println("NO");
    }

    static boolean solve(long targetSum) {
        int[] A = new int[N];
        long currentSum = 0;

        // 1. 최소 높이(Skeleton) 만들기
        int prev = 0;
        for (int i = 0; i < N; i++) {
            int minVal = prev + 1;

            // 조건을 만족하는 minVal 찾기
            while (true) {
                if (isValid(i, minVal)) break;
                minVal++;
            }

            A[i] = minVal;
            currentSum += A[i];
            prev = minVal;
        }

        System.out.println(Arrays.toString(A));

        if (currentSum > targetSum) return false; // 최소로 쌓아도 목표 초과

        long rem = targetSum - currentSum;

        // 2. 뒤에서부터 남는 블록(rem) 채우기
        for (int i = N - 1; i >= 0; i--) {
            if (rem == 0) break;

            // 현재 A[i]를 얼마나 더 올릴 수 있는가?
            // i가 마지막이면 무한대(사실상 rem 전체), 아니면 다음 빌딩보다 작아야 함
            long limit = (i == N - 1) ? rem : (long) A[i + 1] - 1 - A[i];

            // limit과 rem 중 작은 값 범위 내에서, 조건을 만족하는 최대 증가량 찾기
            long possibleAdd = Math.min(rem, limit);

            // 가능한 값을 큰 수부터 줄여가며 찾음 (Greedy)
            // 홀/짝 규칙에 따라 가능한 값은 2씩 차이나므로 기껏해야 1~2번 체크
            while (possibleAdd > 0) {
                if (isValid(i, A[i] + (int) possibleAdd)) {
                    break;
                }
                possibleAdd--;
            }

            A[i] += (int) possibleAdd;
            rem -= possibleAdd;
        }

        if (rem == 0) {
            System.out.println("YES");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < N; i++) {
                sb.append(A[i] - H[i]).append(" ");
            }
            System.out.println(sb);
            return true;
        }

        return false;
    }

    // i번째 빌딩의 높이가 val일 때 규칙을 만족하는지 확인
    static boolean isValid(int idx, int val) {
        // idx는 0부터 시작하므로 문제의 "홀수 번째"는 idx 짝수(0, 2...), "짝수 번째"는 idx 홀수(1, 3...)
        // 문제: 1번째(홀) -> idx 0, 2번째(짝) -> idx 1

        boolean isOddIndex = ((idx + 1) % 2 != 0);

        if (!isOddIndex) { // 짝수 번째 빌딩 (문제 기준)
            // 높이 변화량이 짝수여야 함 -> Parity가 같아야 함
            return (val % 2) == (H[idx] % 2);
        } else { // 홀수 번째 빌딩 (문제 기준)
            // 그대로거나(val == H[idx]), 홀수만큼 변화(Parity 다름)
            if (val == H[idx]) return true;
            return (val % 2) != (H[idx] % 2);
        }
    }
}
