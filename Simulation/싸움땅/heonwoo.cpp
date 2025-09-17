#include <iostream>
#include <vector>
#include <queue>
#include <algorithm>

using namespace std;

struct Player {
    int x, y, dir, stat, gun_power, score;
};

int N, M, K;
priority_queue<int> gun_map[21][21];
int player_map[21][21];
Player players[31];

int dx[] = {-1, 0, 1, 0};
int dy[] = {0, 1, 0, -1};

void pickUpBestGun(int p_id) {
    int x = players[p_id].x;
    int y = players[p_id].y;

    if (gun_map[x][y].empty()) {
        return;
    }

    int best_gun = gun_map[x][y].top();

    if (players[p_id].gun_power > 0) {
        if (best_gun > players[p_id].gun_power) {
            gun_map[x][y].pop();
            gun_map[x][y].push(players[p_id].gun_power);
            players[p_id].gun_power = best_gun;
        }
    } else {
        gun_map[x][y].pop();
        players[p_id].gun_power = best_gun;
    }
}

void loserAction(int loser_id, int winner_id) {
    int x = players[loser_id].x;
    int y = players[loser_id].y;
    int dir = players[loser_id].dir;

    if (players[loser_id].gun_power > 0) {
        gun_map[x][y].push(players[loser_id].gun_power);
        players[loser_id].gun_power = 0;
    }

    for (int i = 0; i < 4; ++i) {
        int ndir = (dir + i) % 4;
        int nx = x + dx[ndir];
        int ny = y + dy[ndir];

        if (nx < 1 || nx > N || ny < 1 || ny > N || player_map[nx][ny] != 0) {
            continue;
        }

        players[loser_id].x = nx;
        players[loser_id].y = ny;
        players[loser_id].dir = ndir;
        player_map[nx][ny] = loser_id;
        break;
    }
    
    pickUpBestGun(loser_id);
}

void winnerAction(int winner_id, int loser_id) {
    int x = players[winner_id].x;
    int y = players[winner_id].y;

    if (players[winner_id].gun_power > 0) {
        gun_map[x][y].push(players[winner_id].gun_power);
        players[winner_id].gun_power = 0;
    }
    
    if (!gun_map[x][y].empty()) {
        players[winner_id].gun_power = gun_map[x][y].top();
        gun_map[x][y].pop();
    }
}

void fight(int p1_id, int p2_id) {
    int p1_power = players[p1_id].stat + players[p1_id].gun_power;
    int p2_power = players[p2_id].stat + players[p2_id].gun_power;

    int winner_id, loser_id;
    
    if (p1_power > p2_power) {
        winner_id = p1_id;
        loser_id = p2_id;
    } else if (p2_power > p1_power) {
        winner_id = p2_id;
        loser_id = p1_id;
    } else {
        if (players[p1_id].stat > players[p2_id].stat) {
            winner_id = p1_id;
            loser_id = p2_id;
        } else {
            winner_id = p2_id;
            loser_id = p1_id;
        }
    }
    
    int points = abs(p1_power - p2_power);
    players[winner_id].score += points;

    int fight_x = players[p1_id].x;
    int fight_y = players[p1_id].y;
    player_map[fight_x][fight_y] = winner_id;

    loserAction(loser_id, winner_id);
    winnerAction(winner_id, loser_id);
}


int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(NULL);

    cin >> N >> M >> K;

    for (int i = 1; i <= N; ++i) {
        for (int j = 1; j <= N; ++j) {
            int power;
            cin >> power;
            if (power > 0) {
                gun_map[i][j].push(power);
            }
        }
    }

    for (int i = 1; i <= M; ++i) {
        int x, y, d, s;
        cin >> x >> y >> d >> s;
        players[i] = {x, y, d, s, 0, 0};
        player_map[x][y] = i;
    }

    for (int k = 0; k < K; ++k) {
        for (int i = 1; i <= M; ++i) {
            int curr_x = players[i].x;
            int curr_y = players[i].y;
            int curr_dir = players[i].dir;
            
            int next_x = curr_x + dx[curr_dir];
            int next_y = curr_y + dy[curr_dir];
            
            if (next_x < 1 || next_x > N || next_y < 1 || next_y > N) {
                curr_dir = (curr_dir + 2) % 4;
                players[i].dir = curr_dir;
                next_x = curr_x + dx[curr_dir];
                next_y = curr_y + dy[curr_dir];
            }
            
            player_map[curr_x][curr_y] = 0;
            players[i].x = next_x;
            players[i].y = next_y;

            int other_player_id = player_map[next_x][next_y];
            if (other_player_id != 0) {
                fight(i, other_player_id);
            } else {
                player_map[next_x][next_y] = i;
                pickUpBestGun(i);
            }
        }
    }

    for (int i = 1; i <= M; ++i) {
        cout << players[i].score << " ";
    }
    cout << "\n";

    return 0;
}
