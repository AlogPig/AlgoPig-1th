import java.io.*;
import java.util.*;

public class TreeEradication {
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;

    static int N, M, K, C;
    static Board b = new Board();
    static Tree t = new Tree();

    static int[] tx = {-1, 1, 0, 0};
    static int[] ty = {0, 0, -1, 1};

    static class Tree {
        // 나무 퍼트리기
        void spreadTree() {
            int[][] tBoard = new int[N][N];

            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (b.board[i][j] > 0) {
                        int cnt = 0;
                        for (int dir = 0; dir < 4; dir++) {
                            int nx = i + tx[dir];
                            int ny = j + ty[dir];
                            if (isABoolean(nx, ny)) continue;

                            if (b.board[nx][ny] == 0) {
                                cnt++;
                            }
                        }


                        for (int dir = 0; dir < 4; dir++) {
                            int nx = i + tx[dir];
                            int ny = j + ty[dir];
                            if (isABoolean(nx, ny)) continue;

                            if (b.board[nx][ny] == 0) {
                                tBoard[nx][ny] += b.board[i][j] / cnt;
                            }
                        }


                    }
                }
            }

            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    b.board[i][j] += tBoard[i][j];
                }
            }
        }

        // 나무 성장
        void growUp() {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (b.board[i][j] > 0) {
                        int cnt = 0;
                        for (int dir = 0; dir < 4; dir++) {
                            int nx = i + tx[dir];
                            int ny = j + ty[dir];
                            if (isABoolean(nx, ny)) continue;
                            if (b.board[nx][ny] > 0) {
                                cnt++;
                            }
                        }

                        b.board[i][j] += cnt;

                    }
                }
            }
        }
    }

    private static boolean isABoolean(int nx, int ny) {
        return nx < 0 || ny < 0 || nx >= N || ny >= N;
    }

    static class Pair {
        int x;
        int y;
        int time;

        int dir;

        public Pair(int x, int y, int time) {
            this.x = x;
            this.y = y;
            this.time = time;
        }

        public Pair(int x, int y, int time, int dir) {
            this.x = x;
            this.y = y;
            this.time = time;
            this.dir = dir;
        }
    }

    static int[] dx = {-1, -1, 1, 1};
    static int[] dy = {-1, 1, -1, 1};

    static class Board {
        int[][] board;
        int answer = 0;

        // 고엽제 뿌리기
        void spreadGo() {

            int[][] tBoard = new int[N][N];

            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (board[i][j] > 0) {
                        findMaxValue(i, j, tBoard);
                    }
                }
            }

            Pair maxValue = new Pair(-1, -1, -1);
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (tBoard[i][j] > maxValue.time) {
                        maxValue = new Pair(i, j, tBoard[i][j]);
                    }
                }
            }

            answer += maxValue.time;
            removeGo();

            ArrayDeque<Pair> q2 = new ArrayDeque<>();
            boolean[][] vis = new boolean[N][N];

            q2.add(new Pair(maxValue.x, maxValue.y, 0, 0));
            q2.add(new Pair(maxValue.x, maxValue.y, 0, 1));
            q2.add(new Pair(maxValue.x, maxValue.y, 0, 2));
            q2.add(new Pair(maxValue.x, maxValue.y, 0, 3));
            board[maxValue.x][maxValue.y] = C * -1;


            while (!q2.isEmpty()) {
                Pair cur = q2.removeFirst();

                if (K <= cur.time) continue;

                int nx = cur.x + dx[cur.dir];
                int ny = cur.y + dy[cur.dir];


                if (isABoolean(nx, ny)) continue;
                if (board[nx][ny] == Integer.MIN_VALUE) continue;
                if (vis[nx][ny]) continue;


                if (board[nx][ny] <= 0) {
                    board[nx][ny] = C * -1;
                    continue;
                }

                board[nx][ny] = C * -1;
                q2.add(new Pair(nx, ny, cur.time + 1, cur.dir));
                vis[nx][ny] = true;

            }


        }

        private void findMaxValue(int i, int j, int[][] tBoard) {
            ArrayDeque<Pair> q = new ArrayDeque<>();
            boolean[][] vis = new boolean[N][N];
            int value = board[i][j];

            q.add(new Pair(i, j, 0, 0));
            q.add(new Pair(i, j, 0, 1));
            q.add(new Pair(i, j, 0, 2));
            q.add(new Pair(i, j, 0, 3));


            while (!q.isEmpty()) {
                Pair cur = q.removeFirst();
                if (K <= cur.time) continue;


                int nx = cur.x + dx[cur.dir];
                int ny = cur.y + dy[cur.dir];

                if (isABoolean(nx, ny)) continue;
                if (board[nx][ny] == Integer.MIN_VALUE) continue;
                if (vis[nx][ny]) continue;
                if (board[nx][ny] <= 0) continue;

                if (board[nx][ny] > 0) {
                    value += board[nx][ny];
                }
                q.add(new Pair(nx, ny, cur.time + 1, cur.dir));
                vis[nx][ny] = true;

            }
            tBoard[i][j] = value;
        }

        // 고엽제 지속 년도 체크
        void removeGo() {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (board[i][j] < 0 && board[i][j] != Integer.MIN_VALUE) {
                        board[i][j]++;
                    }
                }
            }
        }


    }

    public static void main(String[] args) throws IOException {
        input();

        for (int i = 0; i < M; i++) {
            t.growUp();
            t.spreadTree();
            b.spreadGo();
        }



        System.out.println(b.answer);

    }

    private static void see() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(b.board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void input() throws IOException {
        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());

        b.board = new int[N][N];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                b.board[i][j] = Integer.parseInt(st.nextToken());
                if (b.board[i][j] == -1) {
                    b.board[i][j] = Integer.MIN_VALUE;
                }
            }
        }
    }
}
