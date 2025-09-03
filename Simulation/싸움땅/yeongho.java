package codetree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class FightingGround {
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;

    static int N, M, K;

    static List<Integer>[][] board2;
    static List<Person> pls = new ArrayList<>();

    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};

    static class Person {
        int idx;
        int x, y;
        int dir = -1;
        int point = 0;
        int stat = 0;
        int gun = -1;

        // 총 리스트?

        public Person(int idx, int x, int y, int dir, int stat) {
            this.idx = idx;
            this.x = x;
            this.y = y;
            this.dir = dir;
            this.stat = stat;
        }

        // 0번 부터 순차적으로 움직이기
        void move() {
            int nx = x + dx[dir];
            int ny = y + dy[dir];
            if (nx < 0 || nx >= N || ny < 0 || ny >= N) {
                dir = (dir + 2) % 4;
                move();
                return;
            }

            x = nx;
            y = ny;
        }

        void check() {
            int find = findPerson();

            if (find != -1) {
                // 사람이 있는 경우
                // 싸움
                fight(find);

            } else {
                // 사람이 없는 경우
                getWeapon(idx);
            }
        }

        private int findPerson() {
            for (int i = 0; i < M; i++) {
                if (i == idx) continue;
                if (x == pls.get(i).x && y == pls.get(i).y) {
                    return i;
                }
            }
            return -1;
        }

        void fight(int find) {
            int me = calcPower(idx);
            int other = calcPower(find);

            if (me > other) {
                losePerson(find);
                winPerson(idx, me, other);
            } else if (other > me) {
                losePerson(idx);
                winPerson(find, me, other);
            } else {
                // 기본 스탯 비교
                if (stat > pls.get(find).stat) {

                    losePerson(find);
                    winPerson(idx, me, other);
                } else {

                    losePerson(idx);
                    winPerson(find, me, other);
                }
            }
        }

        void winPerson(int pIdx, int me, int other) {
            // 점수 계산
            pls.get(pIdx).point += Math.abs(other - me);
            getWeapon(pIdx);
        }

        void losePerson(int pIdx) {
            // 총 버리고
            int px = pls.get(pIdx).x;
            int py = pls.get(pIdx).y;

            board2[px][py].add(pls.get(pIdx).gun);
            pls.get(pIdx).gun = -1;

            // 사람 or 격자 밖
            // 빈칸 나올 때 까지 회전

            for (int i = 0; i < 4; i++) {
                int nx = px + dx[pls.get(pIdx).dir];
                int ny = py + dy[pls.get(pIdx).dir];

                if (nx < 0 || nx >= N || ny < 0 || ny >= N || checkPerson(nx, ny)) {
                    // 90 회전
                    pls.get(pIdx).dir++;
                    if (pls.get(pIdx).dir >= 4) {
                        pls.get(pIdx).dir = 0;
                    }
                    continue;
                }

                pls.get(pIdx).x = nx;
                pls.get(pIdx).y = ny;
                break;
            }

            // 이동 후 총 줍기
            getWeapon(pIdx);
        }

        boolean checkPerson(int x, int y) {
            for (int i = 0; i < M; i++) {
                if (pls.get(i).x == x && pls.get(i).y == y) {
                    return true;
                }
            }
            return false;
        }

        int calcPower(int otherIdx) {
            return pls.get(otherIdx).stat + (pls.get(otherIdx).gun == -1 ? 0 : pls.get(otherIdx).gun);
        }
    }

    static void getWeapon(int idx) {
        int x = pls.get(idx).x;
        int y = pls.get(idx).y;
        int gun = pls.get(idx).gun;
        // 해당 칸에 총이 있는지 확인

        if (board2[x][y].isEmpty()) {
            return;
        }

        if (gun != -1) {
            // 총을 가지고 있는 경우
            board2[x][y].add(gun);
            sortGun(x, y);
        } else {
            // 총을 가지고 있지 않은 경우
            sortGun(x, y);
        }

        pls.get(idx).gun = board2[x][y].get(0);
        board2[x][y].remove(0);
    }

    static void sortGun(int x, int y) {
        board2[x][y].sort((o1, o2) -> {
            return o2 - o1;
        });
    }

    public static void main(String[] args) throws IOException {
        input();

        for (int i = 0; i < K; i++) {
            for (int j = 0; j < M; j++) {
                pls.get(j).move();
                pls.get(j).check();
            }
        }

        for (int i = 0; i < M; i++) {
            System.out.print(pls.get(i).point + " ");
        }
    }

    private static void input() throws IOException {
        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        board2 = new List[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                board2[i][j] = new ArrayList<>();
            }
        }

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                board2[i][j].add(Integer.parseInt(st.nextToken()));
            }
        }

        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());
            int s = Integer.parseInt(st.nextToken());

            pls.add(new Person(i, x - 1, y - 1, d, s));
        }
    }
}
