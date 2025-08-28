import java.io.*;
import java.util.StringTokenizer;

public class Main {
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
    static StringTokenizer st;
    // N: 격자의 크기, M: 박멸이 진행되는 년 수, K: 제초제의 확산 범위, C: 제초제가 남아있는 년 수
    static int N, M, K, C;
    // 0: 빈 칸, 최소값(재정의): 벽
    static final int EMPTY = 0, WALL = Integer.MIN_VALUE;
    // 상하좌우
    static int[] dx = new int[]{-1, 1, 0, 0};
    static int[] dy = new int[]{0, 0, -1, 1};
    // 대각 사분면 표현 (1, 2, 3, 4) (0-based)
    static int[] diagX = new int[]{-1, -1, 1, 1};
    static int[] diagY = new int[]{1, -1, -1, 1};
    // 격자 [x][y]
    static int[][] grid;
    // 제초제 뿌릴 칸
    static int herbX, herbY;
    // 정답
    static int ans = 0;


    public static void main(String[] args) throws IOException {
        input();
        solve();
        output();
    }

    static void input() throws IOException {
        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        grid = new int[N][N];
        // 격자 초기화
        for (int r = 0; r < N; r++) {
            st = new StringTokenizer(br.readLine());
            for (int c = 0; c < N; c++) {
                int value = Integer.parseInt(st.nextToken());
                if (value == -1) {
                    value = WALL;
                }
                grid[r][c] = value;
            }
        }
    }

    static void solve() {
        for (int m = 0; m < M; m++) {
            // 1. 나무 성장
            growTree();
            // 2. 나무 번식
            breedTree();
            // 3. 박멸할 나무 위치 및 개수
            int goal = findHerbicide();
            // 박멸할 나무가 없는 경우 종료
            if (goal == 0) return;
            ans += goal;
            // 4. 제초제 시간 흐르기
            passedYear();
            // 5. 제초 작업 진행
            sprayHerbicide();
        }
    }

    static void output() throws IOException {
        bw.write(String.valueOf(ans));
        bw.flush();
    }
    // TODO : 1. 나무 성장
    static void growTree() {
        for (int x = 0; x < N; x++) {
            for (int y = 0; y < N; y++) {
                // 나무가 아닌 경우 스킵
                if (grid[x][y] <= 0) continue;
                for (int i = 0; i < 4; i++) {
                    int nx = x + dx[i];
                    int ny = y + dy[i];
                    // 범위를 벗어나면 스킵
                    if (nx < 0 || ny < 0 || nx >= N || ny >= N) continue;
                    // 인접한 부분이 나무가 아니라면 스킵
                    if (grid[nx][ny] <= 0) continue;
                    grid[x][y]++; // 나무 수 증가
                }
            }
        }
    }
    // TODO : 2. 번식진행
    static void breedTree() {
        int[][] plus = new int[N][N];
        for (int x = 0; x < N; x++) {
            for (int y = 0; y < N; y++) {
                // 나무가 아닌 경우 스킵
                if (grid[x][y] <= 0) continue;
                // 빈 칸 개수 탐색
                int divisor = 0;
                for (int i = 0; i < 4; i++) {
                    int nx = x + dx[i];
                    int ny = y + dy[i];
                    // 범위를 벗어나면 스킵
                    if (nx < 0 || ny < 0 || nx >= N || ny >= N) continue;
                    // 빈 칸이 아니면 스킵
                    if (grid[nx][ny] != EMPTY) continue;
                    // 다 통과하면 증가
                    divisor++;
                }
                // 빈 칸이 없는 경우 번식 없음 -> 스킵
                if (divisor == 0) continue;
                int div = grid[x][y] / divisor;
                for (int i = 0; i < 4; i++) {
                    int nx = x + dx[i];
                    int ny = y + dy[i];
                    // 범위를 벗어나면 스킵
                    if (nx < 0 || ny < 0 || nx >= N || ny >= N) continue;
                    // 빈 칸이 아니면 스킵
                    if (grid[nx][ny] != EMPTY) continue;
                    // 빈 칸이므로 번식 진행 (몫만큼)
                    plus[nx][ny] += div;
                }
            }
        }
        for (int x = 0; x < N; x++) {
            for (int y = 0; y < N; y++) {
                grid[x][y] += plus[x][y];
            }
        }
    }
    // TODO : 3. 제초제 뿌릴 위치 선정
    static int findHerbicide() {
        // 목표치 설정
        int goal = 0;
        // 목표 구간 초기화
        herbX = -1;
        herbY = -1;
        for (int x = 0; x < N; x++) {
            for (int y = 0; y < N; y++) {
                // 나무가 아닌 경우 무시
                if (grid[x][y] <= 0) continue;
                // 나무인 경우 검사 진행
                int check = grid[x][y];
                // 대각 방향 검사
                for (int i = 0; i < 4; i++) {
                    // 확산 범위까지 검사
                    for (int k = 1; k <= K; k++) {
                        int nx = x + diagX[i] * k;
                        int ny = y + diagY[i] * k;
                        // 범위를 벗어나면 탈출
                        if (nx < 0 || ny < 0 || nx >= N || ny >= N) break;
                        // 나무가 아니면 탈출
                        if (grid[nx][ny] <= 0) break;
                        // 나무 개수 만큼 더하기
                        check += grid[nx][ny];
                    }
                }
                // 박멸시키는 나무의 수가 동일한 칸이 있는 경우 행이 작이 순 -> 열이 작은 순 (선형 loop이므로 초과하는 경우만 탐색)
                if (check > goal) {
                    goal = check;
                    herbX = x;
                    herbY = y;
                }
            }
        }
        return goal;
    }
    // TODO : 4. 제초제 년수 감소
    static void passedYear() {
        for (int x = 0; x < N; x++) {
            for (int y = 0; y < N; y++) {
                // 벽이거나, 빈칸이거나, 나무면 스킵
                if (grid[x][y] == WALL || grid[x][y] >= 0) continue;
                // 제초제 년수 감수 (음수이므로 +1)
                grid[x][y]++;
            }
        }
    }
    // TODO : 5. 제초 작업 진행
    static void sprayHerbicide() {
        if (herbX == -1 || herbY == -1) return;
        // 해당 지역 살포제 살포
        grid[herbX][herbY] = -C;
        // 대각 방향 탐색
        for (int i = 0; i < 4; i++) {
            // 확산치
            for (int k = 1; k <= K; k++) {
                int nx = herbX + diagX[i] * k;
                int ny = herbY + diagY[i] * k;
                // 범위 밖이면 탈출
                if (nx < 0 || ny < 0 || nx >= N || ny >= N) break;
                // 벽인 경우 탈출
                if (grid[nx][ny] == WALL) break;
                // 빈 칸인 경우는 여기까지(살포제를 뿌리고) -> 탈출
                if (grid[nx][ny] <= 0) {
                    // 제초제 살포 (음수 ex> -5: 5년이 남음)
                    grid[nx][ny] = -C;
                    break;
                };
                grid[nx][ny] = -C;
            }
        }
    }

    static void printGrid() {
        for (int x = 0; x < N; x++) {
            for (int y = 0; y < N; y++) {
                if (grid[x][y] == WALL) {
                    System.out.print("WA ");
                }
                else {
                    System.out.printf("%2d ", grid[x][y]);
                }
            }
            System.out.println();
        }
    }
}
