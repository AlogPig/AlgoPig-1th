#include <bits/stdc++.h>
using namespace std;
int N,T;
int arr[31][31];
int cpy_arr[31][31];
int group_arr[1001][1001]; //group num 기반으로 맞닿은 변 구하는 용도(매번 초기화)
int visited[31][31]; //방문표시, 매번 초기화
int adj[1001][1001]; //a에서 b에 맞닿은 변의 수(매번 초기화)
int result = 0;
int g_num;//배정할 그룹 번호
int dx[4] = {1,0,-1,0};
int dy[4] = {0,1,0,-1};
vector<pair<int,int>> srt_point;

int dx1[2] = {1,0}; //하,우만 확인(맞닿은 면 계산시 사용)
int dy1[2] = {0,1};
bool OOB(int x, int y){
    return x>=0 && y>=0 && N>x && N>y;
}

struct group{
    int group_num; //그룹 번호
    int cnt; //칸의 수
    int real_num; //진짜 저장된 번호
    vector<pair<int,int>> points; //그룹에 속한 좌표 위치들
};
vector<group> groups; //매번 초기화

void bfs(int x, int y){ //bfs돌면서 그룹 만들어서 값들 저장
    queue<pair<int,int>> q;
    vector<pair<int,int>> temp_p;
    visited[x][y] = 1;
    temp_p.push_back({x,y});
    q.push({x,y});
    int num = arr[x][y];
    int cnt = 1;
    while(!q.empty()){
        auto cur = q.front(); q.pop();
        x = cur.first;
        y = cur.second;
        for(int dir=0; dir<4; dir++){
            int nx = x + dx[dir];
            int ny = y + dy[dir];
            if(!OOB(nx,ny)) continue;
            if(visited[nx][ny]) continue;
            if(num != arr[nx][ny]) continue; //같은 수가 아닌 경우
            q.push({nx,ny});
            temp_p.push_back({nx,ny});
            visited[nx][ny] = 1;
            cnt++;
        }
    }
    groups.push_back({g_num,cnt,num,temp_p});
}

//group_num 기준으로 다른 배열에 값 매기기
void calc_boundary(){
    memset(group_arr,0,sizeof(group_arr));
    memset(adj,0,sizeof(adj));

    for(auto i : groups){
        int num = i.group_num; //그룹번호
        vector<pair<int,int>> v = i.points;
        for(auto j : v){
            group_arr[j.first][j.second] = num; //그룹번호를 매핑
        }
    }
    for(int i=0; i<N; i++){
        for(int j=0; j<N; j++){
            int cur_group_num = group_arr[i][j];
            for(int dir=0; dir<2; dir++){
                int nx = i + dx1[dir];
                int ny = j + dy1[dir];
                if(!OOB(nx,ny)) continue;
                int nxt_group_num = group_arr[nx][ny];
                if(cur_group_num != nxt_group_num){
                    adj[cur_group_num][nxt_group_num]++;
                    adj[nxt_group_num][cur_group_num]++;
                }
            }
        }
    }
}
//실수
int calc_art(){
    int artery = 0;
    groups.clear();
    memset(visited,0,sizeof(visited));
    g_num = 1;
    for(int i=0; i<N; i++){
        for(int j=0; j<N; j++){
            if(!visited[i][j]){
                bfs(i,j);
                g_num++;
            }
        }
    }
    calc_boundary();

    for(int i=0; i<=groups.size(); i++){
        for(int j=0; j<=groups.size(); j++){
            if(i==j) continue;
            artery += ((groups[i].cnt+groups[j].cnt) * groups[i].real_num * groups[j].real_num * adj[i+1][j+1]);
        }
    }
    return artery;
}

void rotate_cross() {
    int temp_arr[31][31];
    int mid = N / 2;

    for (int j = 0; j < N; j++) {
        temp_arr[N - 1 - j][mid] = arr[mid][j];
    }

    for (int i = 0; i < N; i++) {
        temp_arr[mid][i] = arr[i][mid];
    }

    for (int i = 0; i < N; i++) {
        arr[i][mid] = temp_arr[i][mid];
        arr[mid][i] = temp_arr[mid][i];
    }
}


void rotate_square() {
    int size = (N - 1) / 2;

    vector<vector<int>> sub_arr(size, vector<int>(size));

    for (auto k : srt_point) {
        int start_x = k.first;
        int start_y = k.second;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sub_arr[i][j] = arr[start_x + i][start_y + j];
            }
        }
        vector<vector<int>> rotated_sub_arr(size, vector<int>(size));
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                rotated_sub_arr[j][size - 1 - i] = sub_arr[i][j];
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                arr[start_x + i][start_y + j] = rotated_sub_arr[i][j];
            }
        }
    }
}

int main() {
    cin >> N;
    T = 4;
    for(int i=0; i<N; i++){
        for(int j=0; j<N; j++){
            cin >> arr[i][j];
        }
    }
    srt_point.push_back({0,0});
    srt_point.push_back({0,((N+1)/2)});
    srt_point.push_back({((N+1)/2),0});
    srt_point.push_back({((N+1)/2),((N+1)/2)});

    while(T--) {
        result += calc_art()/2;
        rotate_cross();
        rotate_square(); //4번 회전
    }
    cout << result << "\n";

    return 0;
}