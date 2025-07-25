package boj;

import java.util.*;
import java.math.*;
import java.io.*;


public class CodeTree2 {

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;

    static int N, ans;
    static int total = 20000;

    static Board b = new Board();

    static int[] dx = {0, 1, 0, -1};
    static int[] dy = {1, 0, -1, 0};


    public static void main(String[] args) throws IOException {
        // Please write your code here.
        input();


        for (int j = 0; j < 4; j++) {
            b.seperate();
            ans += b.sum();
            b.cycle();
        }

        System.out.println(ans);
    }

    static void input() throws IOException {
        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());

        b.init();

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int k = 0; k < N; k++) {
                b.board[i][k] = Integer.parseInt(st.nextToken());
            }
        }
    }

    static class Pair {
        int x, y;
        int type;

        Pair(int x, int y, int type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }

        Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Board {
        int[][] board;
        int[][] tboard;


        List<Pair>[] ls;
        List<Pair>[] adj;

        void init() {
            board = new int[N][N];
            clear();
        }

        void cycle() {

            cross();

            other(0, 0);
            other((N / 2) + 1, 0);
            other(0, (N / 2) + 1);
            other((N / 2) + 1, (N / 2) + 1);

            clear();
        }

        void cross() {

            List<Integer> ls1 = new ArrayList<>();
            List<Integer> ls2 = new ArrayList<>();
            List<Integer> ls3 = new ArrayList<>();
            List<Integer> ls4 = new ArrayList<>();


            boolean flag1 = false;
            for (int i = 0; i < N; i++) {

                if (i == N / 2) {
                    flag1 = true;
                    continue;
                }

                if (!flag1) {
                    ls1.add(board[i][N / 2]);
                } else {
                    ls2.add(board[i][N / 2]);
                }
            }


            boolean flag2 = false;
            for (int i = 0; i < N; i++) {

                if (i == N / 2) {
                    flag2 = true;
                    continue;
                }

                if (!flag2) {
                    ls3.add(board[N / 2][i]);
                } else {
                    ls4.add(board[N / 2][i]);
                }
            }

            int idx = 0;
            for (int i = 0; i < ls1.size(); i++) {
                board[N / 2][idx] = ls1.get(i);
                idx++;
            }

            idx = (N / 2) + 1;
            for (int i = 0; i < ls2.size(); i++) {
                board[N / 2][idx] = ls2.get(i);
                idx++;
            }

            idx = (N / 2) + 1;
            for (int i = ls3.size() - 1; i >= 0; i--) {
                board[idx][N / 2] = ls3.get(i);
                idx++;
            }

            idx = 0;
            for (int i = ls4.size() - 1; i >= 0; i--) {
                board[idx][N / 2] = ls4.get(i);
                idx++;
            }
        }

        void other(int x, int y) {

            ArrayDeque<Pair> q = new ArrayDeque<>();
            boolean[][] vis = new boolean[N][N];
            int[] ddx = {0, 1, 0, -1};
            int[] ddy = {1, 0, -1, 0};

            for (int i = 0; i < (N / 2); i++) {

                if (vis[x + i][y + i]) break;


                q.add(new Pair(x + i, y + i, board[x + i][y + i]));
                ArrayDeque<Pair> lls = new ArrayDeque<>();

                vis[x + i][y + i] = false;
                int idx = 0;
                int cycle = 0;

                while (!q.isEmpty()) {
                    Pair cur = q.removeFirst();

                    if (cur.x == x + i && cur.y == y + i && vis[cur.x][cur.y]) break;
                    if (cycle >= 2) return;

                    int nx = cur.x + ddx[idx];
                    int ny = cur.y + ddy[idx];

                    if (nx < 0 || nx == N || nx == N / 2 || ny < 0 || ny == N || ny == N / 2) {

                        idx++;

                        if (idx >= 4) {
                            idx = 0;
                            cycle++;
                        }

                        q.add(cur);
                        continue;
                    }
                    if (vis[nx][ny]) {

                        idx++;

                        if (idx >= 4) {
                            idx = 0;
                            cycle++;
                        }

                        q.add(cur);
                        continue;
                    }

                    lls.add(new Pair(nx, ny, board[nx][ny]));

                    q.add(new Pair(nx, ny, board[nx][ny]));
                    vis[nx][ny] = true;
                }

                Pair t = lls.removeLast();
                lls.addFirst(t);

                List<Pair> llls = new ArrayList<>(lls);

                int size = (llls.size() / 4) + 1;

                List<Pair> llls1 = new ArrayList<>(llls.subList(0, size));
                List<Pair> llls2 = new ArrayList<>(llls.subList(size - 1, size - 1 + size));
                List<Pair> llls3 = new ArrayList<>(llls.subList(size - 1 + size - 1, size - 1 + size - 1 + size));
                List<Pair> llls4 = new ArrayList<>(llls.subList(size - 1 + size - 1 + size - 1, llls.size()));

                llls4.add(llls.get(0));

                for (int j = 0; j < llls1.size(); j++) {
                    board[llls2.get(j).x][llls2.get(j).y] = llls1.get(j).type;
                }

                for (int j = 0; j < llls2.size(); j++) {
                    board[llls3.get(j).x][llls3.get(j).y] = llls2.get(j).type;
                }

                for (int j = 0; j < llls3.size(); j++) {
                    board[llls4.get(j).x][llls4.get(j).y] = llls3.get(j).type;
                }

                for (int j = 0; j < llls4.size(); j++) {
                    board[llls1.get(j).x][llls1.get(j).y] = llls4.get(j).type;
                }
            }
        }

        int sum() {
            int sum = 0;

            for (int i = 1; i < total; i++) {
                for (int k = 0; k < adj[i].size(); k++) {
                    Pair f = ls[i].get(0);
                    Pair s = ls[adj[i].get(k).x].get(0);

                    sum += (ls[i].size() + ls[adj[i].get(k).x].size()) * board[f.x][f.y] * board[s.x][s.y] * adj[i].get(k).y;
                }
            }
            return sum;
        }

        void seperate() {
            ArrayDeque<Pair> q = new ArrayDeque<>();

            boolean[][] vis = new boolean[N][N];

            int idx = 1;

            for (int i = 0; i < N; i++) {
                for (int k = 0; k < N; k++) {
                    if (vis[i][k]) continue;

                    q.add(new Pair(i, k, board[i][k]));
                    vis[i][k] = true;
                    ls[idx].add(new Pair(i, k, board[i][k]));

                    while (!q.isEmpty()) {
                        Pair cur = q.removeFirst();

                        for (int dir = 0; dir < 4; dir++) {
                            int nx = cur.x + dx[dir];
                            int ny = cur.y + dy[dir];

                            if (nx < 0 || nx >= N || ny < 0 || ny >= N) continue;
                            if (vis[nx][ny] || cur.type != board[nx][ny]) continue;

                            q.add(new Pair(nx, ny, cur.type));
                            vis[nx][ny] = true;
                            ls[idx].add(new Pair(nx, ny, cur.type));
                        }
                    }
                    idx++;
                }
            }

            tboard = new int[N][N];

            for (int i = 0; i < total; i++) {
                for (int k = 0; k < ls[i].size(); k++) {
                    tboard[ls[i].get(k).x][ls[i].get(k).y] = i;
                }
            }

            vis = new boolean[N][N];

            for (int i = 0; i < N; i++) {
                for (int k = 0; k < N; k++) {
                    if (vis[i][k]) continue;

                    int[] cntArr = new int[total];

                    q.add(new Pair(i, k, tboard[i][k]));
                    vis[i][k] = true;

                    while (!q.isEmpty()) {
                        Pair cur = q.removeFirst();

                        for (int dir = 0; dir < 4; dir++) {
                            int nx = cur.x + dx[dir];
                            int ny = cur.y + dy[dir];

                            if (nx < 0 || nx >= N || ny < 0 || ny >= N) continue;

                            if (tboard[nx][ny] != cur.type) {
                                cntArr[tboard[nx][ny]]++;
                            }

                            if (vis[nx][ny] || tboard[nx][ny] != cur.type) continue;

                            q.add(new Pair(nx, ny, cur.type));
                            vis[nx][ny] = true;
                        }
                    }

                    for (int j = tboard[i][k]; j < total; j++) {
                        if (cntArr[j] == 0) continue;

                        adj[tboard[i][k]].add(new Pair(j, cntArr[j]));
                    }
                }
            }
        }

        void clear() {

            ls = new List[total];
            adj = new List[total];

            for (int i = 0; i < total; i++) {
                ls[i] = new ArrayList<>();
                adj[i] = new ArrayList<>();
            }
        }
    }
}