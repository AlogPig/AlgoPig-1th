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
        long end = arr[N - 1];
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

    
    static boolean check(long mid) {
        long currentSkill = mid;
        long currentScore = 0;

        for (int i = 0; i < M; i++) {
            int s = 0;
            int e = N - 1;
            int idx = -1;

           
            while (s <= e) {
                int m = (s + e) / 2;

                if (arr[m] <= currentSkill) {
                    idx = m;      
                    s = m + 1;
                } else {
                    e = m - 1;    
                }
            }

            if (idx == -1) break; 
            currentSkill += arr[idx];
            currentScore += arr[idx];

            
            if (currentScore >= A) return true;
        }

        return currentScore >= A;
    }
}
