import java.io.*;
import java.util.*;

public class Main {
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
    static StringTokenizer st;
    static int N, M, K; // N: 격자의 크기, M: 팀의 개수, K: 라운드 수
    static final int BLANK = 0, HEAD_PEOPLE = 1, OTHER_PEOPLE = 2, TAIL_PEOPLE = 3, MOVE_LINE = 4;
    static int[] dx = new int[]{0, -1, 0, 1};
    static int[] dy = new int[]{1, 0, -1, 0};
    static int[][] board; // 보드 정보
    static Team[] teams; // 팀 정보
    static int totalScore = 0;

    public static void main(String[] args) throws IOException {
        input();
        solve();
        output();
    }

    static void input() throws IOException {
        // 1. 격자 크기, 팀 개수, 라운드 수 입력
        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        // 2. 게임 보드 입력
        board = new int[N][N];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                board[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        // 3. 팀 정보 초기화
        teams = new Team[M];
    }

    static void solve() {
        boardInit();
        for (int round = 0; round < K; round++) { // 라운드 만큼 진행
            // 1. 팀 별로 머리사람을 따라서 한 칸 이동
            for (int m = 0; m < M; m++) {
                moveTeam(m);
            }
            // 2. 라운드 마다 정해진 선을 따라서 공을 던짐
            totalScore += throwBall(round);
        }
    }

    static void output() throws IOException {
        bw.write(String.valueOf(totalScore));
        bw.flush();
    }

    /**
     * 보드 초기화 함수
     */
    static void boardInit() {
        boolean[][] visited = new boolean[N][N];
        int teamId = 0;
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                // 이미 검사했거나, 머리가 아닌경우 무시
                if (visited[r][c] || board[r][c] != HEAD_PEOPLE) continue;
                // 머리를 찾았으므로 bfs를 통해 그룹 매핑
                Node head = new Node(r, c);
                // 순서 기입
                int seq = 0;
                Deque<Node> peopleQueue = new LinkedList<>();
                Queue<Node> queue = new LinkedList<>();
                queue.offer(head);
                visited[r][c] = true;
                // 사람 탐색
                while (!queue.isEmpty()) {
                    Node curr = queue.poll();
                    peopleQueue.offerLast(curr);
                    // 꼬리 검사 -> 꼬리와 맞닿는 빈공간 넣고 탈출
                    if (board[curr.x][curr.y] == TAIL_PEOPLE) {
                        for (int i = 0; i < 4; i++) {
                            int nx = curr.x + dx[i];
                            int ny = curr.y + dy[i];
                            // 범위 밖이면 무시
                            if (nx < 0 || ny < 0 || nx >= N || ny >= N) continue;
                            // 이미 방문 했으면 무시
                            if (visited[nx][ny]) continue;
                            // 빈칸이면 무시
                            if (board[nx][ny] == BLANK) continue;
                            queue.offer(new Node(nx, ny));
                            visited[nx][ny] = true;
                        }
                        // 가중치 적용
                        board[curr.x][curr.y] = applyWeight(teamId);
                        break;
                    }
                    // 가중치 적용
                    board[curr.x][curr.y] = applyWeight(teamId);
                    for (int i = 0; i < 4; i++) {
                        int nx = curr.x + dx[i];
                        int ny = curr.y + dy[i];
                        // 범위 밖이면 무시
                        if (nx < 0 || ny < 0 || nx >= N || ny >= N) continue;
                        // 이미 방문 했으면 무시
                        if (visited[nx][ny]) continue;
                        // 빈칸이거나 이동경로면 무시
                        if (board[nx][ny] == BLANK || board[nx][ny] == MOVE_LINE) continue;
                        // 3명 이상이 있으므로 머리에서 꼬리로 바로 가는 경우 무시
                        if (curr.x == r && curr.y == c && board[nx][ny] == TAIL_PEOPLE) continue;
                        queue.offer(new Node(nx, ny));
                        visited[nx][ny] = true;
                    }
                }
                // 이동 경로 탐색
                Deque<Node> lineQueue = new LinkedList<>();
                while (!queue.isEmpty()) {
                    Node curr = queue.poll();
                    lineQueue.offerLast(curr);
                    for (int i = 0; i < 4; i++) {
                        int nx = curr.x + dx[i];
                        int ny = curr.y + dy[i];
                        // 범위 밖이면 무시
                        if (nx < 0 || ny < 0 || nx >= N || ny >= N) continue;
                        // 이미 방문 했으면 무시
                        if (visited[nx][ny]) continue;
                        // 이동 경로가 아니면 무시
                        if (board[nx][ny] != MOVE_LINE) continue;
                        queue.offer(new Node(nx, ny));
                        visited[nx][ny] = true;
                    }
                }
                // 팀 정보 기입
                teams[teamId++] = new Team(peopleQueue, lineQueue);
            }
        }
    }

    /**
     * 팀 이동
     *
     * @param id 이동하는 팀 번호
     */
    static void moveTeam(int id) {
        Team curr = teams[id];
        if (!curr.isReversed) { // 정방향
            if (!curr.lines.isEmpty()) { // 사람으로 꽉차 있지 않다면,
                // 머리 전진
                Node goalPeople = curr.lines.pollLast(); // 머리가 전진할 칸을 뺌 (머리 앞 삭제)
                curr.peoples.offerFirst(goalPeople); // 머리 앞에 넣어줌
                board[goalPeople.x][goalPeople.y] = applyWeight(id);
                // 꼬리 삭제
                Node goalLine = curr.peoples.pollLast(); // 꼬리였던 공간을 빈공간 으로
                curr.lines.offerFirst(goalLine); // 하기 위해 라인에 추가
                board[goalLine.x][goalLine.y] = MOVE_LINE;
            } else { // 사람으로 꽉차 있다면,
                Node tail = curr.peoples.pollLast(); // 꼬리를 뺌
                curr.peoples.offerFirst(tail); // 꼬리를 머리 앞에 넣어줌
            }
        } else { // 역방향
            if (!curr.lines.isEmpty()) { // 사람으로 꽉차 있지 않다면,
                // 꼬리 전진
                Node goalPeople = curr.lines.pollFirst(); // 꼬리가 전진할 칸을 뺌 (꼬리 앞 삭제)
                curr.peoples.offerLast(goalPeople); // 꼬리 뒤에 넣어줌
                board[goalPeople.x][goalPeople.y] = applyWeight(id);
                // 머리 삭제
                Node goalLine = curr.peoples.pollFirst(); // 머리였던 공간을 빈공간 으로
                curr.lines.offerLast(goalLine); // 하기 위해 라인에 추가
                board[goalLine.x][goalLine.y] = MOVE_LINE;
            } else {
                Node head = curr.peoples.pollFirst(); // 머리를 뺌
                curr.peoples.offerLast(head); // 머리를 꼬리 앞에 넣어줌
            }
        }

    }

    /**
     * 해당 노드가 그 팀의 머리로 부터 몇번째인지 구하고 머리와 꼬리를 변경
     * @param id 팀 id
     * @param node 맞은 위치
     * @return 순서
     */
    static int getSequenceAndReverse(int id, Node node) {
        int seq = 1;
        if (!teams[id].isReversed) { // 정방향
            for (Node people: teams[id].peoples) {
                if (people.x == node.x && people.y == node.y) {
                    break;
                }
                seq++;
            }
        } else {
            for (Iterator<Node> it = teams[id].peoples.descendingIterator(); it.hasNext();) {
                Node people = it.next();
                if (people.x == node.x && people.y == node.y) {
                    break;
                }
                seq++;
            }
        }
        teams[id].reverse();
        return seq;
    }
    /**
     * 공 던지고 점수 계산하기
     * @param round 라운드
     * @return 점수
     */
    static int throwBall(int round) {
        // 방향 구하기
        int dr = (round % (4 * N)) / N;
        int offset = round % N;
        int sx = 0, sy = 0;
        // 방향과 기준점에 따라 시작 위치 구하기
        switch (dr) {
            case 0:
                sx = offset;
                sy = 0;
                break;
            case 1:
                sx = N - 1;
                sy = offset;
                break;
            case 2:
                sx = N - 1 - offset;
                sy = N - 1;
                break;
            case 3:
                sx = 0;
                sy = N - 1 - offset;
                break;
        }
        int nx = 0, ny = 0;
        boolean success = false;
        for (int k = 0; k < N; k++) {
            nx = sx + dx[dr] * k;
            ny = sy + dy[dr] * k;
            if (board[nx][ny] % 10 == OTHER_PEOPLE) {
                success = true;
                break;
            }
        }
        if (!success) {
            return 0;
        }
        Node check = new Node(nx, ny);
        int teamId = board[nx][ny] / 10;
        return (int) Math.pow(getSequenceAndReverse(teamId, check), 2);
    }

    static int applyWeight(int teamId) {
        return teamId * 10 + OTHER_PEOPLE;
    }

    static class Team {
        Deque<Node> peoples; // 머리 ~ 꼬리
        Deque<Node> lines; // 꼬리와 붙어 있는 이동 칸 ~ 머리와 붙어 있는 이동 칸
        boolean isReversed; // 머리와 꼬리가 바뀐 경우

        public Team(Deque<Node> peoples, Deque<Node> lines) {
            this.peoples = peoples;
            this.lines = lines;
            this.isReversed = false;
        }

        void reverse() {
            isReversed = !isReversed;
        }
    }

    static class Node {
        int x, y;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
