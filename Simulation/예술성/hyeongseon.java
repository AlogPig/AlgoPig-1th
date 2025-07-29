import java.io.*;
import java.util.*;

public class Main {
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
    static StringTokenizer st;
    static int N;
    // 초기 그림, 회전 1, 회전 2, 회전 3
    static int[][][] painting;
    static int totalScore = 0; // 최종 스코어
    // 갈아 넣기
    static int groupId = 1;
    static ArrayList<Group> groups; // 그룹 정보들
    static boolean[][] visited; // 방문 처리

    static int[] dx = new int[]{-1, 1, 0, 0};
    static int[] dy = new int[]{0, 0, -1, 1};


    public static void main(String[] args) throws IOException {
        input();
        solve();
        output();
    }

    static void input() throws IOException {
        N = Integer.parseInt(br.readLine());
        painting = new int[4][N][N];
        for (int x = 0; x < N; x++) {
            st = new StringTokenizer(br.readLine());
            for (int y = 0; y < N; y++) {
                int value = Integer.parseInt(st.nextToken());
                mapping(0, x, y, value);
            }
        }
    }

    static void solve() {
        for (int r = 0; r < 4; r++) {
            init();
            for (int x = 0; x < N; x++) {
                for (int y = 0; y < N; y++) {
                    if (visited[x][y]) { // 이미 처리된 그룹이면 무시
                        continue;
                    }
                    bfs(r, x, y);
                }
            }

            totalScore += calculateScore();
        }
    }

    static void output() throws IOException {
        bw.write(String.valueOf(totalScore));
        bw.flush();
    }

    static boolean isPair(int w1, int w2) {
        int w1x = w1 / 100;
        int w1y = w1 % 100;
        int w2x = w2 / 100;
        int w2y = w2 % 100;
        if (Math.abs(w1x - w2x) + Math.abs(w1y - w2y) == 1) {
            return true;
        }
        return false;
    }

    static void init() {
        groupId = 1;
        groups = new ArrayList<>();
        visited = new boolean[N][N];
    }

    static int calculateScore() {
        int score = 0;
        for (int i = 0; i < groups.size() - 1; i++) {
            Group g1 = groups.get(i);
            for (int j = i + 1; j < groups.size(); j++) {
                Group g2 = groups.get(j);

                int stools = 0;
                for (int w1 : g1.side) {
                    for (int w2 : g2.side) {
                        if (isPair(w1, w2)) {
                            stools++;
                        }
                    }
                }

                if (stools == 0) {
                    continue;
                }
                score += (g1.cnt + g2.cnt) * g1.number * g2.number * stools;
            }
        }
        return score;
    }

    static void bfs(int r, int x, int y) { // 그룹정보들
        Group group = new Group(groupId++, painting[r][x][y]);
        int count = 0;
        Queue<Node> queue = new LinkedList<>();
        visited[x][y] = true;
        queue.add(new Node(x, y));
        count++;
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            for (int i = 0; i < 4; i++) {
                int nx = node.x + dx[i];
                int ny = node.y + dy[i];
                if (nx < 0 || nx >= N || ny < 0 || ny >= N) { // 범위 밖이면 무시
                    continue;
                }
                if (painting[r][node.x][node.y] != painting[r][nx][ny]) { // 다른 숫자면 무시하기 전에 사이드에 추가
                    int key = node.x * 100 + node.y;
                    group.side.add(key);
                    continue;
                }
                if (visited[nx][ny]) { // 방문 했으면 무시
                    continue;
                }
                visited[nx][ny] = true;
                queue.add(new Node(nx, ny));
                count++;
            }
        }
        group.cnt = count;
        groups.add(group);
    }

    /**
     * @param r     회전 수
     * @param x     x 좌표
     * @param y     y 좌표
     * @param value 대입할 값
     */
    static void mapping(int r, int x, int y, int value) {
        if (r == 4) {
            return;
        }
        painting[r][x][y] = value;
        int m = N / 2; // 중앙값
        int nx, ny;
        if (x == m || y == m) { // 십자 모양
            nx = N - 1 - y;
            ny = x;
        } else { // 정사각형 모양
            int sx, sy;
            if (x < m && y > m) { // 1사분면
                sx = 0;
                sy = m + 1;
            } else if (x < m && y < m) { // 2사분면
                sx = 0;
                sy = 0;
            } else if (x > m && y < m) { // 3사분면
                sx = m + 1;
                sy = 0;
            } else { // 3사분면
                sx = m + 1;
                sy = m + 1;
            }
            nx = (y - sy) + sx;
            ny = m - 1 - (x - sx) + sy;
        }
        mapping(r + 1, nx, ny, value);
    }

    static class Node {
        int x, y;

        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Group {
        int id;
        int number;
        int cnt;
        Set<Integer> side;

        Group(int id, int number) {
            this.id = id;
            this.number = number;
            this.cnt = 0;
            this.side = new HashSet<>();
        }
    }
}
