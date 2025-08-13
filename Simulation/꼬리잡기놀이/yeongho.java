package codetree;

import java.util.*;
import java.io.*;

public class TailCatchGame {

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;

    static int N, M, K, ans;
    static int[][] board;

    static List<Route> rls = new ArrayList<>();
    static List<Human> hls = new ArrayList<>();
    static Ball b = new Ball();

    static int[] dx = {0, -1, 0, 1};
    static int[] dy = {1, 0, -1, 0};

    static class Human {
        List<Pair> innerHuman = new ArrayList<>();
        boolean state = true;
        int idx = -1;

        void see() {
            innerHuman.forEach(cur -> {
                System.out.print(cur.x + " " + cur.y + " " + cur.z + " -> ");
            });
            System.out.println();
        }

        void switchState() {
            // 방향 전환은 리스트 순서를 뒤집는 것으로 처리
            Collections.reverse(innerHuman);
        }

        void moveFactory() {
            // 기존 move2() 로직을 개선
            moveTeam();
            makeBoard();
        }

        void makeBoard() {
            // 먼저 해당 팀의 경로를 4로 설정
            for (int i = 0; i < M; i++) {
                rls.get(i).innerRoute.forEach(cur -> {
                    board[cur.x][cur.y] = 4;
                });
            }
            // 그 다음 사람들을 1로 설정 (모든 사람을 1로 통일)
            for (int i = 0; i < M; i++) {
                hls.get(i).innerHuman.forEach(cur -> {
                    board[cur.x][cur.y] = 1;
                });
            }
        }

        void moveTeam() {
            if (innerHuman.isEmpty()) return;
            
            // 현재 머리와 두 번째 사람의 위치로 다음 머리 위치 찾기
            Pair head = innerHuman.get(0);
            Pair second = innerHuman.size() > 1 ? innerHuman.get(1) : null;
            
            Pair nextHead = findNextPosition(head, second);
            if (nextHead == null) return;
            
            // 꼬리 위치는 경로로 변경
            Pair tail = innerHuman.get(innerHuman.size() - 1);
            board[tail.x][tail.y] = 4;
            
            // 팀 이동: 꼬리 제거, 새 머리 추가
            innerHuman.remove(innerHuman.size() - 1);
            innerHuman.add(0, new Pair(nextHead.x, nextHead.y, 1));
        }

        Pair findNextPosition(Pair head, Pair second) {
            // 머리 주변에서 이동 가능한 위치 찾기 (second 위치 제외)
            for (int dir = 0; dir < 4; dir++) {
                int nx = head.x + dx[dir];
                int ny = head.y + dy[dir];
                
                if (nx < 0 || nx >= N || ny < 0 || ny >= N) continue;
                if (board[nx][ny] == 0) continue; // 빈 칸은 이동 불가
                
                // 바로 뒤따르는 사람 위치가 아닌 경우만 이동 가능
                if (second == null || (nx != second.x || ny != second.y)) {
                    return new Pair(nx, ny, 1);
                }
            }
            return null;
        }
    }

    static class Route {
        List<Pair> innerRoute = new ArrayList<>();

        Route() {}

        void see() {
            innerRoute.forEach(cur -> {
                System.out.print(cur.x + " " + cur.y + " -> ");
            });
            System.out.println();
        }
    }

    static class Ball {
        int round = 0; // 0부터 시작

        List<Pair> bls = new ArrayList<>();

        void init() {
            // 4N개의 공 경로 초기화
            // 첫 번째 방향: 위에서 아래로 (0 -> N-1 행, 0열부터)
            for (int i = 0; i < N; i++) {
                bls.add(new Pair(i, 0, 0)); // 오른쪽으로
            }
            // 두 번째 방향: 오른쪽에서 왼쪽으로 (N-1행, 0 -> N-1열부터)
            for (int i = 0; i < N; i++) {
                bls.add(new Pair(N - 1, i, 1)); // 위쪽으로
            }
            // 세 번째 방향: 아래에서 위로 (N-1 -> 0 행, N-1열부터)
            for (int i = N - 1; i >= 0; i--) {
                bls.add(new Pair(i, N - 1, 2)); // 왼쪽으로
            }
            // 네 번째 방향: 왼쪽에서 오른쪽으로 (0행, N-1 -> 0열부터)
            for (int i = N - 1; i >= 0; i--) {
                bls.add(new Pair(0, i, 3)); // 아래쪽으로
            }
        }

        int hitBall() {
            int currentRound = round % (4 * N);
            Pair start = bls.get(currentRound);
            
            int x = start.x;
            int y = start.y;
            int dir = start.z;
            
            // 공이 날아가면서 첫 번째로 만나는 사람 찾기
            while (x >= 0 && x < N && y >= 0 && y < N) {
                if (board[x][y] == 1) { // 사람을 만남
                    // 어느 팀인지 찾고 점수 계산
                    for (int teamIdx = 0; teamIdx < M; teamIdx++) {
                        for (int personIdx = 0; personIdx < hls.get(teamIdx).innerHuman.size(); personIdx++) {
                            Pair person = hls.get(teamIdx).innerHuman.get(personIdx);
                            if (person.x == x && person.y == y) {
                                // 점수 계산: (머리부터의 순서)^2
                                int score = (personIdx + 1) * (personIdx + 1);
                                // 해당 팀 방향 전환
                                hls.get(teamIdx).switchState();
                                round++;
                                return score;
                            }
                        }
                    }
                }
                
                // 다음 위치로 이동
                x += dx[dir];
                y += dy[dir];
            }
            
            round++;
            return 0; // 아무도 못 맞춤
        }
    }

    static class Pair {
        int x, y, z;

        Pair(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) throws IOException {
        input();
        b.init();

        for (int i = 0; i < K; i++) {
            // 모든 팀 이동
            for (int k = 0; k < M; k++) {
                hls.get(k).moveFactory();
            }
            
            // 공 던지기 및 점수 계산
            ans += b.hitBall();
        }
        System.out.println(ans);
    }

    private static void input() throws IOException {
        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        board = new int[N][N];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int k = 0; k < N; k++) {
                board[i][k] = Integer.parseInt(st.nextToken());
            }
        }

        // 팀 초기화 (DFS 방식으로 개선)
        boolean[][] visited = new boolean[N][N];
        
        for (int i = 0; i < M; i++) {
            rls.add(new Route());
            hls.add(new Human());
        }

        int teamIdx = 0;
        
        // 머리사람(1)을 찾아서 팀 구성
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] == 1 && !visited[i][j]) {
                    // DFS로 연결된 경로와 사람들 찾기
                    List<Pair> route = new ArrayList<>();
                    List<Pair> people = new ArrayList<>();
                    
                    dfs(i, j, visited, route, people);
                    
                    rls.get(teamIdx).innerRoute = route;
                    hls.get(teamIdx).innerHuman = people;
                    hls.get(teamIdx).idx = teamIdx;
                    
                    teamIdx++;
                }
            }
        }
    }
    
    private static void dfs(int x, int y, boolean[][] visited, List<Pair> route, List<Pair> people) {
        visited[x][y] = true;
        route.add(new Pair(x, y));
        
        if (board[x][y] != 4) { // 경로가 아닌 사람들만 추가
            people.add(new Pair(x, y, board[x][y]));
        }
        
        // 상하좌우 탐색
        for (int dir = 0; dir < 4; dir++) {
            int nx = x + dx[dir];
            int ny = y + dy[dir];
            
            if (nx < 0 || nx >= N || ny < 0 || ny >= N) continue;
            if (board[nx][ny] == 0 || visited[nx][ny]) continue;
            
            // 머리(1)에서 몸통(2)으로 연결되거나, 다른 경우는 방문하지 않은 경로만
            if ((board[x][y] == 1 && board[nx][ny] == 2) || 
                (board[x][y] != 1 && !visited[nx][ny])) {
                dfs(nx, ny, visited, route, people);
            }
        }
        
        // 사람들을 머리부터 꼬리 순서로 정렬
        people.sort((a, b) -> a.z - b.z);
    }
}
