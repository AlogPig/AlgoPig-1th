#include <iostream>
#include <vector>
#include <queue>
#include <deque>
#include <algorithm>
#include <cmath>

using namespace std;

int n, m, k;
int arr[21][21];
int total_score = 0;

// 각 팀의 멤버 좌표를 머리->꼬리 순서로 저장 (팀 번호는 1부터 시작)
vector<deque<pair<int, int>>> teams;

int dx[] = {0, 1, 0, -1}; // 우, 하, 좌, 상
int dy[] = {1, 0, -1, 0};

// 초기 설정: 각 팀의 경로를 찾아서 teams 벡터에 저장하는 함수
void find_teams() {
    teams.push_back({}); // 0번 인덱스는 더미
    bool visited[21][21] = {false};
    
    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
            if (arr[i][j] == 1 && !visited[i][j]) {
                deque<pair<int, int>> current_team;
                queue<pair<int, int>> q;

                q.push({i, j});
                visited[i][j] = true;
                current_team.push_back({i, j});

                // 머리부터 시작해서 몸통, 꼬리 순으로 팀원을 찾음
                pair<int, int> tracer = {i, j};
                while (true) {
                    bool found_next = false;
                    for (int dir = 0; dir < 4; ++dir) {
                        int nx = tracer.first + dx[dir];
                        int ny = tracer.second + dy[dir];

                        if (nx < 0 || nx >= n || ny < 0 || ny >= n || visited[nx][ny] || arr[nx][ny] == 0) continue;
                        
                        // 머리 바로 뒤는 2여야 하고, 몸통 뒤는 1이 아니어야 함
                        int current_val = arr[tracer.first][tracer.second];
                        int next_val = arr[nx][ny];

                        if (current_val == 1 && next_val != 2) continue;
                        if (current_val == 2 && next_val == 1) continue;


                        if (next_val == 2 || next_val == 3) {
                             visited[nx][ny] = true;
                             current_team.push_back({nx,ny});
                             tracer = {nx,ny};
                             found_next = true;
                             if(next_val == 3) break; // 꼬리를 찾으면 종료
                             break;
                        }
                    }
                    if (!found_next) break; // 더 이상 이어지는 팀원이 없으면 종료
                }
                teams.push_back(current_team);
            }
        }
    }
}

// 1. 모든 팀을 한 칸씩 이동시키는 함수 (수정된 버전)
void move_all_teams() {
    // 먼저 모든 팀의 논리적인 위치 (deque)를 한 칸씩 이동
    for (int i = 1; i <= m; ++i) {
        if (teams[i].empty()) continue;

        pair<int, int> head = teams[i].front();
        
        // 머리가 이동할 다음 위치 찾기
        int next_x = -1, next_y = -1;
        // 머리 주변의 경로(4)를 먼저 찾음
        for (int dir = 0; dir < 4; ++dir) {
            int nx = head.first + dx[dir];
            int ny = head.second + dy[dir];
            if (nx < 0 || nx >= n || ny < 0 || ny >= n) continue;
            // 바로 뒤에 있는 몸통으로 후진하는 경우는 제외
            if (teams[i].size() > 1 && nx == teams[i][1].first && ny == teams[i][1].second) continue;
            
            if (arr[nx][ny] == 4 || arr[nx][ny] == 3) {
                 next_x = nx;
                 next_y = ny;
                 break;
            }
        }
        
        teams[i].push_front({next_x, next_y});
        teams[i].pop_back();
    }

    // 이동이 끝난 후, arr 배열을 deques의 최신 상태를 보고 한번에 갱신
    // 1. arr 배열의 모든 사람(1,2,3)을 일단 경로(4)로 변경
    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
            if (arr[i][j] == 1 || arr[i][j] == 2 || arr[i][j] == 3) {
                arr[i][j] = 4;
            }
        }
    }

    // 2. 최신 deques 정보를 바탕으로 arr 배열에 사람들을 다시 그림
    for (int i = 1; i <= m; ++i) {
        if (teams[i].empty()) continue;
        for (int j = 0; j < teams[i].size(); ++j) {
            int x = teams[i][j].first;
            int y = teams[i][j].second;
            if (j == 0) arr[x][y] = 1; // 머리
            else if (j == teams[i].size() - 1) arr[x][y] = 3; // 꼬리
            else arr[x][y] = 2; // 몸통
        }
    }
}

// 2 & 3. 공을 던져 맞히고 점수 계산 및 방향 전환하는 함수
void throw_ball(int round) {
    int r = (round - 1) % (4 * n);
    int x, y, dir;

    if (r < n) { // 좌 -> 우
        x = r; y = 0; dir = 0;
    } else if (r < 2 * n) { // 하 -> 상
        x = n - 1; y = r - n; dir = 3;
    } else if (r < 3 * n) { // 우 -> 좌
        x = n - 1 - (r - 2 * n); y = n - 1; dir = 2;
    } else { // 상 -> 하
        x = 0; y = n - 1 - (r - 3 * n); dir = 1;
    }

    for (int i = 0; i < n; ++i) {
        if (arr[x][y] >= 1 && arr[x][y] <= 3) {
            // 사람을 맞힌 경우
            int hit_team_idx = -1;
            int hit_person_rank = -1;

            // 어느 팀의 몇 번째 사람인지 찾기
            for (int t_idx = 1; t_idx <= m; ++t_idx) {
                for (int p_idx = 0; p_idx < teams[t_idx].size(); ++p_idx) {
                    if (teams[t_idx][p_idx].first == x && teams[t_idx][p_idx].second == y) {
                        hit_team_idx = t_idx;
                        hit_person_rank = p_idx + 1;
                        break;
                    }
                }
                if (hit_team_idx != -1) break;
            }
            
            if (hit_team_idx != -1) {
                // 점수 추가
                total_score += pow(hit_person_rank, 2);

                // 머리-꼬리 방향 전환
                reverse(teams[hit_team_idx].begin(), teams[hit_team_idx].end());
                
                // 바뀐 머리-꼬리 arr에 반영
                for (int j = 0; j < teams[hit_team_idx].size(); ++j) {
                    int hx = teams[hit_team_idx][j].first;
                    int hy = teams[hit_team_idx][j].second;
                    if (j == 0) arr[hx][hy] = 1;
                    else if (j == teams[hit_team_idx].size() - 1) arr[hx][hy] = 3;
                    else arr[hx][hy] = 2;
                }
            }
            return; // 한 라운드에 한 번만 맞힘
        }
        x += dx[dir];
        y += dy[dir];
    }
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(NULL);

    cin >> n >> m >> k;
    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
            cin >> arr[i][j];
        }
    }

    find_teams();
    
    for (int round = 1; round <= k; ++round) {
        move_all_teams();
        throw_ball(round);
    }
    
    cout << total_score << endl;

    return 0;
}
