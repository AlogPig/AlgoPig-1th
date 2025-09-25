import java.io.*;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.StringTokenizer;


public class Main {
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
    static StringTokenizer st;
    // 상, 우, 하, 좌 ↑ → ↓ ←
    static final int UP = 0, LIGHT = 1, DOWN = 2, LEFT = 3;
    static final int[] dx = {-1, 0, 1, 0};
    static final int[] dy = {0, 1, 0, -1};
    // N: 격자 크기, M: 플레이어의 수, K: 라운드 수
    static int N, M, K;
    // 게임 보드
    static PriorityQueue<Integer>[][] gunBoard;
    // 플레이어 정보들
    static Player[] players;

    public static void main(String[] args) throws IOException {
        input();
        solve();
        output();
    }

    static void input() throws IOException {
        // 1. 격자, 플레이어, 라운드 입력
        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        // 보드 초기화
        gunBoard = new PriorityQueue[N][N];
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                gunBoard[r][c] = new PriorityQueue<>(Comparator.reverseOrder()); // 총의 공격력이 더 높은 순위가 오게끔 설정
            }
        }
        // 보드 입력
        for (int r = 0; r < N; r++) {
            st = new StringTokenizer(br.readLine());
            for (int c = 0; c < N; c++) {
                int gun = Integer.parseInt(st.nextToken());
                if (gun == 0) continue;
                gunBoard[r][c].offer(gun);
            }
        }
        // 플레이어 초기화
        players = new Player[M];
        // 플레이어 입력
        for (int m = 0; m < M; m++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            int d = Integer.parseInt(st.nextToken());
            int s = Integer.parseInt(st.nextToken());
            players[m] = new Player(x, y, d, s);
        }

    }

    static void solve() {
        for (int k = 0; k < K; k++) {
            movePlayer();
        }
    }

    static void output() throws IOException {
        for (Player player : players) {
            bw.write(player.sco + " ");
        }
        bw.flush();
    }

    static void movePlayer() {
        for (Player player : players) {
            int nx = player.x + dx[player.d];
            int ny = player.y + dy[player.d];
            if (outOfBoard(nx, ny)) {
                player.turnBack();
                nx = player.x + dx[player.d];
                ny = player.y + dy[player.d];
            }
            Player enemy = findPlayer(nx, ny);
            // 플레이어 이동
            player.x = nx;
            player.y = ny;
            // 해당 구역에 적군이 없으면 스킵
            if (enemy == null) {
                // 총 줍기
                pickUpGun(player);
                continue;
            }
            ;
            int pVal = player.getPower();
            int eVal = enemy.getPower();
            Player winner, loser;
            if (pVal > eVal) {
                winner = player;
                loser = enemy;
            } else if (pVal < eVal) {
                winner = enemy;
                loser = player;
            } else {
                if (player.s > enemy.s) {
                    winner = player;
                    loser = enemy;
                } else {
                    winner = enemy;
                    loser = player;
                }
            }
            // 이긴 사람 포인트 획득
            winner.sco += Math.abs(pVal - eVal);
            // 진 플레이어 행동
            moveLosePlayer(loser);
            // 이긴 플레이어 행동
            swapGunWinPlayer(winner);
        }
    }

    static void moveLosePlayer(Player player) {
        if (player.g != 0) {
            // 총을 버림
            gunBoard[player.x][player.y].offer(player.g);
            player.g = 0;
        }
        // 플레이어 이동
        while (true) {
            int nx = player.x + dx[player.d];
            int ny = player.y + dy[player.d];
            // 격자 밖이거나, 해당 공간에 플레이어가 있다면
            if (outOfBoard(nx, ny) || findPlayer(nx, ny) != null) {
                // 우측으로 90도 회전
                player.d = (player.d + 1) % 4;
                continue;
            } else {
                player.x = nx;
                player.y = ny;
                pickUpGun(player);
                break;
            }
        }
    }

    static void swapGunWinPlayer(Player player) {
        pickUpGun(player);
    }

    static boolean outOfBoard(int x, int y) {
        return x < 0 || x >= N || y < 0 || y >= N;
    }

    static Player findPlayer(int x, int y) {
        for (Player p : players) {
            if (p.x == x && p.y == y) return p;
        }
        return null;
    }

    static void pickUpGun(Player player) {
        if (gunBoard[player.x][player.y].isEmpty()) {
            return;
        }
        int gun = gunBoard[player.x][player.y].poll();
        player.swapGun(gun);
    }

    static class Player {
        int x, y; // 현재 위치
        int d; // 현재 방향
        int s; // 초기 능력치
        int g = 0; // 총의 능력치
        int sco = 0; // 플레이어의 점수

        // 초기 생성자
        public Player(int x, int y, int d, int s) {
            this.x = x;
            this.y = y;
            this.d = d;
            this.s = s;
        }

        void turnBack() {
            this.d = (this.d + 2) % 4;
        }

        /**
         * 능력치 합 구하기
         *
         * @return 초기 능력치 + 총의 능력치
         */
        int getPower() {
            return s + g;
        }

        /**
         * 총 변경
         *
         * @param newGun 새로 획득한 총의 능력치
         */
        void swapGun(int newGun) {
            if (newGun > this.g) {
                // 원래 총이 있었다면(0보다 크면) 바닥에 내려놓는다.
                if (this.g > 0) {
                    gunBoard[this.x][this.y].offer(this.g);
                }
                // 더 강한 새 총을 장착한다.
                this.g = newGun;
            } else {
                // 주운 총이 더 약하면 다시 바닥에 내려놓는다.
                gunBoard[this.x][this.y].offer(newGun);
            }
        }

    }
}
