import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    static final long INF = Long.MAX_VALUE;

    static class Edge {
        int to;
        int id; 

        public Edge(int to, int id) {
            this.to = to;
            this.id = id;
        }
    }

    static class State implements Comparable<State> {
        long cost;
        int node;
        int k;

        public State(long cost, int node, int k) {
            this.cost = cost;
            this.node = node;
            this.k = k;
        }

        @Override
        public int compareTo(State o) {
            return Long.compare(this.cost, o.cost);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int N = Integer.parseInt(st.nextToken());
        int M = Integer.parseInt(st.nextToken());
        int A = Integer.parseInt(st.nextToken());
        int B = Integer.parseInt(st.nextToken());

      
        List<Edge>[] adj = new ArrayList[N + 1];
        for (int i = 1; i <= N; i++) adj[i] = new ArrayList<>();

        
        List<Long>[] E = new ArrayList[M];
        for (int i = 0; i < M; i++) E[i] = new ArrayList<>();

       
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int u = Integer.parseInt(st.nextToken());
            int v = Integer.parseInt(st.nextToken());
            long t = Long.parseLong(st.nextToken());

           
            E[i].add(t);

            adj[u].add(new Edge(v, i));
            adj[v].add(new Edge(u, i));
        }

        st = new StringTokenizer(br.readLine());
        int K = Integer.parseInt(st.nextToken());

        
        for (int k = 1; k <= K; k++) {
            st = new StringTokenizer(br.readLine());
            for (int i = 0; i < M; i++) {
                long t = Long.parseLong(st.nextToken());
                
                E[i].add(t);
            }
        }

        
        long[][] dist = new long[N + 1][K + 1];
        for (int i = 1; i <= N; i++) Arrays.fill(dist[i], INF);

        PriorityQueue<State> pq = new PriorityQueue<>();
        dist[A][0] = 0;
        pq.offer(new State(0, A, 0));

        while (!pq.isEmpty()) {
            State cur = pq.poll();

            if (dist[cur.node][cur.k] < cur.cost) continue;

            for (Edge next : adj[cur.node]) {
               
                for (int nextK = cur.k; nextK <= K; nextK++) {

                    long weight = E[next.id].get(nextK);

                    if (dist[next.to][nextK] > cur.cost + weight) {
                        dist[next.to][nextK] = cur.cost + weight;
                        pq.offer(new State(dist[next.to][nextK], next.to, nextK));
                    }
                }
            }
        }

        long ans = INF;
        for (int k = 0; k <= K; k++) {
            ans = Math.min(ans, dist[B][k]);
        }

        System.out.println(ans == INF ? -1 : ans);
    }
}
