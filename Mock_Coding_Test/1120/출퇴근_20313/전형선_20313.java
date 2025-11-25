// 20313. [G3] 출퇴근.

import java.io.*;
import java.util.*;

public class Main {
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
    static StringTokenizer st;
    static int N, M, A, B, K; // N: 건물 수, M: 양방향 도로 수, A: 거주지, B: 회사, K: 최대 마법 사용횟수
    static List<List<Edge>> road = new ArrayList<>(); // 도로 번호와 행선지 기입
    static int[][] cost; // 도로의 걸리는 시간을 기입 [k][m] k번 마법을 사용했을때, m번 도로의 걸리는 시간
    static long[][] dp; // 목표지점까지 도달하는 최단시간 [k][n] k번 마법을 사용했을때, n번 마을에 최단으로 도착한 시간
    static long ans = Long.MAX_VALUE;
    public static void main(String[] args) throws IOException {
        input();
        solve();
        output();
    }

    static void input() throws IOException {
        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        A = Integer.parseInt(st.nextToken());
        B = Integer.parseInt(st.nextToken());
        // 도로 초기화
        for (int n = 0; n <= N; n++) {
            road.add(new ArrayList<>());
        }
        int[] initRoad = new int[M + 1];
        // 도로 번호및 초기값 지정
        for (int m = 1; m <= M; m++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            int v = Integer.parseInt(st.nextToken());
            road.get(x).add(new Edge(y, m));
            road.get(y).add(new Edge(x, m));
            initRoad[m] = v;
        }
        // 마법 효과 초기화
        K = Integer.parseInt(br.readLine());
        cost = new int[K + 1][M + 1]; // 1-based;
        for (int m = 1; m <= M; m++) {
            cost[0][m] = initRoad[m];
        }
        for (int k = 1; k <= K; k++) {
            st = new StringTokenizer(br.readLine());
            for (int m = 1; m <= M; m++) {
                int value = Integer.parseInt(st.nextToken());
                cost[k][m] = value;
            }
        }
        // dp 초기화
        dp = new long[K + 1][N + 1]; // 1-based;
        for (long[] d: dp) {
            Arrays.fill(d, Long.MAX_VALUE);
        }
    }

    static void solve() {
        dijkstra();
        for (int k = 0; k <= K; k++) {
            ans = Math.min(ans, dp[k][B]);
        }
    }

    static void output() throws IOException {
        bw.write(String.valueOf(ans));
        bw.flush();
    }

    static void dijkstra() {
        // 우선순위큐 선언 : 시간 빠른순 -> 마법을 적게 사용한 순
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparing(Node::getTime).thenComparing(Node::getMagic));
        // 시작점 초기화
        dp[0][A] = 0;
        pq.offer(new Node(0, A, 0));
        while (!pq.isEmpty()) {
            Node curr  = pq.poll();
            // 건물을 이동
            for (Edge edge: road.get(curr.idx)) {
                long nextTime = curr.time + cost[curr.magic][edge.idx];
                // 더 작은 비용으로 갱신가능 하면 이동
                if (nextTime < dp[curr.magic][edge.to]) {
                    dp[curr.magic][edge.to] = nextTime;
                    pq.offer(new Node(nextTime, edge.to, curr.magic));
                }
            }
            // 마법을 사용 (최대 K번 사용 가능이므로 K미만인지 체크)
            if (curr.magic < K) {
                // 더 작은 비용으로 갱신가능하면 이동
                if (curr.time < dp[curr.magic + 1][curr.idx]) {
                    dp[curr.magic + 1][curr.idx] = curr.time;
                    pq.offer(new Node(curr.time, curr.idx, curr.magic + 1));
                }
            }

        }
    }

    static class Node {
        long time; // 현재시간
        int idx; // 현재 위치한 빌딩
        int magic; // 현재까지 사용한 마법 횟수


        public Node(long time, int idx, int magic) {
            this.time = time;
            this.idx = idx;
            this.magic = magic;
        }

        public long getTime() {
            return time;
        }

        public int getMagic() {
            return magic;
        }
    }

    static class Edge {
        int to; // 이동할 건물
        int idx; // 도로 번호

        public Edge(int to, int idx) {
            this.to = to;
            this.idx = idx;
        }
    }
}
