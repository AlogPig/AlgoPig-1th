```java
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Main {
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
    static StringTokenizer st;
    static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;
    // 상 우 하 좌
    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};
    static int N, M, H, K; // N: 격자 크기 N x N (홀수), M: 도망자 수, H: 나무의 개수, K: 반복 횟수
    static List<Node> runaways; // 도망자들
    static Node tagger; // 술래
    static boolean reversed = false; // 술래 역주행 여부
    static boolean[][] changeDirection; // 술래 방향 전환 공간
    static boolean[][] isTree; // 나무 여부
    static int score = 0;


    public static void main(String[] args) throws IOException {
        input();
        solve();
        output();
    }

    static void input() throws IOException {
        // 세팅값 입력
        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        H = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        // 도망자 입력
        runaways = new ArrayList<>();
        for (int m = 0; m < M; m++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            int t = Integer.parseInt(st.nextToken());
            runaways.add(new Node(x, y, t));
        }
        // 나무 입력
        isTree = new boolean[N][N];
        for (int h = 0; h < H; h++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            isTree[x][y] = true;
        }
        // 술래 설정
        tagger = new Node(N / 2, N / 2); // 정중앙 생성
    }

    static void solve() {
        init(); // 술래 디렉션 설정
        for (int k = 1; k <= K; k++) {
            // 도망자가 이동
            for (Node run : runaways) {
                int dist = getDistance(tagger, run);
                if (dist > 3) continue;
                move(run, true);
            }
            // 술래가 이동
            move(tagger, false);
            // 술래가 잡기
            score += k * tag();
        }
    }

    static void output() throws IOException {
        bw.write(String.valueOf(score));
        bw.flush();
    }
    static void init() {
        changeDirection = new boolean[N][N];
        int tmp = N / 2;
        for (int n = 0; n < N; n++) { // y = x
            changeDirection[n][N - 1 - n] = true;
        }
        for (int n = 0; n < tmp; n++) { // y = -x + 1
            changeDirection[n][n + 1] = true;
        }
        for (int n = N - 1; n > N - 1 - tmp; n--) { // y = -x
            changeDirection[n][n] = true;
        }
        changeDirection[0][0] = true;
    }
    static int getDistance(Node node1, Node node2) {
        return Math.abs(node1.x - node2.x) + Math.abs(node1.y - node2.y);
    }

    static void move(Node curr, boolean isRunaway) {
        if (isRunaway) { // 도망자
            int nx = curr.x + dx[curr.d];
            int ny = curr.y + dy[curr.d];
            if (nx < 0 || nx >= N || ny < 0 || ny >= N) { // 먼저 부딪히면 방향을 바꾸어줌
                curr.d = (curr.d + 2) % 4;
            }
            nx =  curr.x + dx[curr.d];
            ny =  curr.y + dy[curr.d];
            if (tagger.x == nx && tagger.y == ny) { // 술래가 있으면 움직이 않음
                return;
            }
            // 이동 처리
            curr.x = nx;
            curr.y = ny;
        } else { // 술래
            tagger.x = tagger.x + dx[tagger.d];
            tagger.y = tagger.y + dy[tagger.d];
            if (changeDirection[tagger.x][tagger.y]) { // 방향을 바꿔야 한다면
                if (tagger.x == 0 &&  tagger.y == 0) { // 끝
                    tagger.d = DOWN;
                    reversed = true;
                } else if (tagger.x == N / 2 &&  tagger.y == N / 2) { // 정중앙
                    tagger.d = UP;
                    reversed = false;
                } else {
                    if (reversed) {
                        tagger.d = (tagger.d - 1 + 4) % 4;
                    } else {
                        tagger.d = (tagger.d + 1) % 4;
                    }
                }
            }
        }
    }

    static int tag() {
        int count = 0;
        Node[] vision = new Node[3];
        for (int i = 0; i < 3; i++) {
            int nx = tagger.x + dx[tagger.d] * i;
            int ny = tagger.y + dy[tagger.d] * i;
            vision[i] = new Node(nx, ny);
        }
        int size = runaways.size();
        for(int j = size - 1; j >= 0; j--) {
            Node run = runaways.get(j);
            for (int v = 0; v < 3; v++) {
                if (run.x == vision[v].x && run.y == vision[v].y && !isTree[run.x][run.y]) {
                    runaways.remove(j);
                    count += 1;
                    break;
                }
            }
        }
        return count;
    }

    static class Node {
        int x, y; // 현재 위치
        int d; // 보고있는 방향 0:상, 1:하, 2:좌, 3:우

        public Node(int x, int y) { // 술래 생성
            this.x = x;
            this.y = y;
            this.d = UP;
        }

        public Node(int x, int y, int type) { // 방향 타입이 1이면 우를 보고 시작, 2이면 하를 보고 시작
            this.x = x;
            this.y = y;
            this.d = type == 1 ? RIGHT : DOWN;
        }
    }
}

```
