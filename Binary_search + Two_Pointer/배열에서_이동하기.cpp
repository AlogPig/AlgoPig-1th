#include <bits/stdc++.h>

using namespace std;
#define First ios::sync_with_stdio(0); cin.tie(0);

int n;
int arr[101][101];
int visited[101][101];
int l,r;
int mx_val = 0;
int min_val = 201;
int result = 201;
int dx[4] = {1,0,-1,0};
int dy[4] = {0,1,0,-1};

bool OOB(int x, int y){ //배열 범위 밖인 것을 체크
    return 0<=x && 0<=y && n>x && n>y;
}
bool InRange(int x, int y, int minimum, int maximum){ //최대 최솟값 사이의 수인지 체크
    return arr[x][y] >= minimum && arr[x][y] <= maximum;
}
//left와 right의 범위의 안일때만 동작하는 bfs
bool bfs(int left, int right){
    if(!InRange(0,0,left,right)) return false;
    memset(visited,0,sizeof(visited));
    queue<pair<int,int>> q;
    q.push({0,0});
    visited[0][0] = 1;
    while(!q.empty()){
        auto cur = q.front(); q.pop();
        int cur_x = cur.first;
        int cur_y = cur.second;
        if(cur_x == n-1 && cur_y == n-1) return true;

        for(int i=0; i<4; i++){
            int nx = cur_x + dx[i];
            int ny = cur_y + dy[i];
            if(!OOB(nx,ny)) continue;
            if(!InRange(nx,ny,left,right)) continue;
            if(visited[nx][ny]) continue;
            visited[nx][ny] = 1;
            q.push({nx,ny});
        }
    }
    return false;
}
int main(){
    cin >> n;
    
    for(int i=0; i<n; i++){
        for(int j=0; j<n; j++){
            cin >> arr[i][j];
            mx_val = max(mx_val, arr[i][j]);
        }
    }

    l = 0;
    r = 0;

    // r이 최댓값에 도달할 때까지 루프 진행
    while(r <= mx_val){
        // 현재 [l, r] 범위로 경로가 있는지 확인
        if(bfs(l, r)){
            // 경로가 있다면, 정답을 갱신하고 더 나은 해를 찾기 위해 l을 증가 (범위 좁히기)
            result = min(result, r - l);
            l++;
        }
        else{
            // 경로가 없다면, 경로를 만들기 위해 r을 증가 (범위 넓히기)
            r++;
        }
    }
    cout << result << "\n";
}
