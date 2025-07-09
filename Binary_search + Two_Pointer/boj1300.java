package boj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class boj1300 {
    public static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    public static StringTokenizer st;

    public static int N, K;

    public static void main(String[] args) throws IOException {
        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        st = new StringTokenizer(br.readLine());
        K = Integer.parseInt(st.nextToken());


        int l = 1;
        int r = K;

        while (l < r) {
            int mid = (l + r) / 2;
            int c = 0;


            for (int i = 1; i <= N; i++) {
                c += Math.min(mid / i, N);
            }

            System.out.println(l + " " + r + " " + mid + " " + c);


            if (K <= c) {
                r = mid;
            } else {
                l = mid + 1;
            }
        }

        System.out.println(l);
    }
}
/*
 *
 * 11 12 13 14 15
 * 21 22 23 24 25
 * 31 32 33 34 35
 * 41 42 43 44 45
 * 51 52 53 54 55
 *
 * 1 2 3 2 4 6 3 6 9
 * 1 2 2 3 3 4 6 6 9
 * */