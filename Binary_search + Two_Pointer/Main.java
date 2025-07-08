package BAEKJOON._02470;
// 02470. [G5] 두 용액.

import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Main {
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
    static StringTokenizer st;
    static int N;
    static int[] solution; // 용액
    static int mix, lS, rS; // 혼합값, 좌용액, 우용액

    public static void main(String[] args) throws IOException {
        input();
        solve();
        output();
    }

    static void input() throws IOException {
        mix = Integer.MAX_VALUE;
        N = Integer.parseInt(br.readLine());
        solution = new int[N];
        st = new StringTokenizer(br.readLine());
        for (int n = 0; n < N; n++) {
            solution[n] = Integer.parseInt(st.nextToken());
        }
    }

    static void solve() {
        Arrays.sort(solution);
        if (checkException()) {
            return;
        }
        int left = 0, right = N - 1;
        while (left < right) {
            int m = solution[left] + solution[right];
            if (Math.abs(m) < mix) { // 최적의 해를 찾은 경우
                mix = Math.abs(m);
                lS = solution[left];
                rS = solution[right];
            }
            if (m < 0) { // 음수인 경우
                if (solution[right] < 0) { // 우측도 음수면, 더 이상 최적의 해는 단 하나이거나 없음
                    left = right - 1;
                    int check = solution[left] + solution[right];
                    if (Math.abs(check) < mix) {
                        lS = solution[left];
                        rS = solution[right];
                    }
                    break;
                } else {
                    left++;
                }
            } else if (m > 0) { // 양수인 경우
                if (solution[left] > 0) { // 좌측도 양수면, 더 이상 최적의 해는 단 하나이거나 없음
                    right = left + 1;
                    int check = solution[left] + solution[right];
                    if (Math.abs(check) < mix) {
                        lS = solution[left];
                        rS = solution[right];
                    }
                    break;
                } else {
                    right--;
                }
            } else {
                lS = solution[left];
                rS = solution[right];
                return;
            }
        }
    }

    static void output() throws IOException {
        bw.write(lS + " " + rS);
        bw.flush();
    }

    static boolean checkException() {
        int l = solution[0];
        int r = solution[N - 1];
        int m = (int) (Math.signum(l) * Math.signum(r));
        if (m > 0) { // 같은 특성일때 ( [-, -], [+, +]
            if (l < 0) { // -
                lS = solution[N - 2];
                rS = solution[N - 1];
                return true;
            }
            // +
            lS = solution[0];
            rS = solution[1];
            return true;
        } else if (m == 0) {
            if (l == 0) {
                lS = solution[0];
                rS = solution[1];
                return true;
            }
            lS = solution[N - 2];
            rS = solution[N - 1];
            return true;
        }
        return false;
    }
}
