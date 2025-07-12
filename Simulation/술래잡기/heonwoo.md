```c++
#include <bits/stdc++.h>

using namespace std;
#define First ios::sync_with_stdio(0); cin.tie(0);
typedef long long ll;

int n, m, h, k;
vector<int> hider[101][101];
vector<int> next_hiders[100][100];
bool tree[100][100];
int seeker_x, seeker_y, seeker_dir;
int score;

int dx[4] = {-1,0,1,0};
int dy[4] = {0,1,0,-1};

bool OOB(int x, int y){
    return 0<=x && x < n && 0<=y && y<n;
}
void move(int x, int y, int dir){
    //3보다 멀면 이동x
    if(abs(seeker_x - x) + abs(seeker_y-y) >3) {
        next_hiders[x][y].push_back(dir);
        return;
    }
    int nx = x+ dx[dir];
    int ny = y + dy[dir];
    if(!OOB(nx,ny)){ //배열 밖이면 방향 바꿈
        dir = (dir+2)%4;
        nx = x + dx[dir];
        ny = y + dy[dir];
    }
    if(nx == seeker_x && ny==seeker_y){//이동 위치에 술래 있으면 이동x
        next_hiders[x][y].push_back(dir);
    }else{ //술래 없으면 이동
        next_hiders[nx][ny].push_back(dir);
    }
}

void hider_move(){
    for(int i=0; i<n; ++i){
        for(int j=0; j<n; ++j){
            for(auto k : hider[i][j]){
                move(i,j,k);
            }
        }
    }
    for(int i=0; i<n; i++){
        for(int j=0; j<n; j++){
            hider[i][j] = next_hiders[i][j];
            next_hiders[i][j].clear();
        }
    }
}

int cnt = 0;
int mx_cnt = 1;
int flag = 0;
int val = 1; //방향 배열 증가나 감소 표시
void seeker_move(){
    if(seeker_x == n/2 && seeker_y == n/2){
        seeker_dir = 0;
        cnt = 0;
        mx_cnt = 1;
        flag = 0;
        val = 1;
    }
    if(seeker_x == 0 && seeker_y == 0){
        seeker_dir = 2;
        cnt = 1;
        mx_cnt = n;
        flag = 1;
        val = -1;
    }
    cnt++;
    seeker_x += dx[seeker_dir];
    seeker_y += dy[seeker_dir];

    if(cnt == mx_cnt){
        cnt = 0;
        seeker_dir = (seeker_dir+val)%4;
        if(!flag) flag = 1;
        else{
            flag = 0;
            mx_cnt += val;
        }
    }
}

void game(int x){
    hider_move();
    seeker_move();
    for(int i=0; i<3; ++i){
        int nx = seeker_x + i * dx[seeker_dir];
        int ny = seeker_y + i * dy[seeker_dir];
        if(!OOB(nx,ny) || tree[nx][ny]) continue;

        score += x * hider[nx][ny].size();
        hider[nx][ny].clear();
    }
}
int main(){
    First
    cin >> n >> m >> h >> k;

    for(int i=0; i<m; ++i){
        int x,y,d;
        cin >> x >> y >> d;
        hider[x-1][y-1].push_back(d);
    }

    for(int i=0; i<h; ++i){
        int x,y;
        cin >> x >> y;
        tree[x-1][y-1] = true;
    }

    seeker_x = n / 2;
    seeker_y = n / 2;
    seeker_dir = 0;

    score = 0;

    for(int i=1; i<=k; ++i){
        game(i);
    }

    cout << score << "\n";
    return 0;
}
```
