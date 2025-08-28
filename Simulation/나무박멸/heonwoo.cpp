#include <iostream>
#include <vector>
#include <map>
#include <algorithm>

using namespace std;

int n, m, k, c;
int arr[21][21];
long long answer = 0;

// 이동 방향 배열
int dx[] = {0, 0, 1, -1};
int dy[] = {1, -1, 0, 0};
int ddx[] = {-1, -1, 1, 1}; // 대각선 방향
int ddy[] = {-1, 1, -1, 1};

// 제초제 위치와 남은 수명만 map으로 관리
map<pair<int, int>, int> killer;

bool OOB(int x, int y) {
    return x >= 0 && x < n && y >= 0 && y < n;
}

// 1. 나무 성장
void grow() {
    // 동시 성장을 위해 임시 배열 사용
    int growth_arr[21][21] = {0};
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            if (arr[i][j] > 0) { // arr 배열만으로 나무 유무 판단
                int cnt = 0;
                for (int dir = 0; dir < 4; dir++) {
                    int nx = i + dx[dir];
                    int ny = j + dy[dir];
                    if (OOB(nx, ny) && arr[nx][ny] > 0) {
                        cnt++;
                    }
                }
                growth_arr[i][j] = cnt;
            }
        }
    }
    // 계산된 성장량을 한 번에 적용
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            if (arr[i][j] > 0) {
                arr[i][j] += growth_arr[i][j];
            }
        }
    }
}

// 2. 나무 번식
void spread() {
    // 동시 번식을 위해 임시 배열 사용
    int spread_arr[21][21] = {0};
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            if (arr[i][j] > 0) {
                vector<pair<int, int>> candidates;
                for (int dir = 0; dir < 4; dir++) {
                    int nx = i + dx[dir];
                    int ny = j + dy[dir];
                    // 제초제가 없고, 다른 나무나 벽도 없는 빈 칸
                    if (OOB(nx, ny) && arr[nx][ny] == 0 && killer.find({nx, ny}) == killer.end()) {
                        candidates.push_back({nx, ny});
                    }
                }
                if (!candidates.empty()) {
                    int val = arr[i][j] / candidates.size();
                    for (auto const& pos : candidates) {
                        spread_arr[pos.first][pos.second] += val;
                    }
                }
            }
        }
    }
    // 계산된 번식 결과를 한 번에 적용
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            arr[i][j] += spread_arr[i][j];
        }
    }
}

// 3. 제초제 살포
void spread_killer() {
    int max_kill = -1;
    pair<int, int> best_pos = {-1, -1};

    // 가장 많이 박멸되는 위치 찾기
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            // 나무가 있는 칸만 후보
            if (arr[i][j] > 0) {
                int current_kill = arr[i][j];
                // 대각선 확산 계산
                for (int dir = 0; dir < 4; dir++) {
                    for (int step = 1; step <= k; step++) {
                        int nx = i + ddx[dir] * step;
                        int ny = j + ddy[dir] * step;
                        // 벽이거나 나무가 없으면 그 방향으로 확산 중단
                        if (!OOB(nx, ny) || arr[nx][ny] <= 0) break;
                        current_kill += arr[nx][ny];
                    }
                }
                // 최댓값 갱신 (박멸 수 > 행 > 열 우선순위)
                if (current_kill > max_kill) {
                    max_kill = current_kill;
                    best_pos = {i, j};
                }
            }
        }
    }
    
    // 제초제 뿌리기 실행
    if (best_pos.first != -1) {
        answer += max_kill;
        int x = best_pos.first;
        int y = best_pos.second;

        // 중앙 위치 나무 제거 및 제초제 살포
        arr[x][y] = 0;
        killer[{x, y}] = c + 1;

        // 대각선 위치 나무 제거 및 제초제 살포
        for (int dir = 0; dir < 4; dir++) {
            for (int step = 1; step <= k; step++) {
                int nx = x + ddx[dir] * step;
                int ny = y + ddy[dir] * step;

                if (!OOB(nx, ny)) break;
                
                // 현재 칸이 벽이면, 제초제를 뿌리지 않고 즉시 중단
                if (arr[nx][ny] == -1) break;

                // 나무가 없어도 제초제는 뿌리고 확산은 중단
                if (arr[nx][ny] == 0) {
                    killer[{nx, ny}] = c + 1;
                    break;
                }
                
                // 나무가 있는 경우, 나무 제거 후 제초제 살포
                arr[nx][ny] = 0;
                killer[{nx, ny}] = c + 1;
            }
        }
    }
}

// 4. 제초제 유효기간 감소
void killer_pass() {
    for (auto it = killer.begin(); it != killer.end(); ) {
        it->second--;
        if (it->second == 0) {
            it = killer.erase(it);
        } else {
            ++it;
        }
    }
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(NULL);

    cin >> n >> m >> k >> c;

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            cin >> arr[i][j];
        }
    }

    while (m--) {
        // 올바른 연간 순서
        killer_pass();
        grow();
        spread();
        spread_killer();
    }

    cout << answer << "\n";

    return 0;
}
