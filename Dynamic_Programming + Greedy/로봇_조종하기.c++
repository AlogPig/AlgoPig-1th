#include <bits/stdc++.h>

using namespace std;
#define First ios::sync_with_stdio(0); cin.tie(0);

int arr[1001][1001];
int dp[1001][1001][3]; //x,y,들어온 방향에 따라 가질 수 있는 최대값
int dx[3] = {1,0,0}; //하,우,좌
int dy[4] = {0,1,-1};
int N,M;
bool OOB(int x, int y){
    return x>=0 && y>=0 && N>x && M >y;
}

int dfs(int x, int y, int dir){
    if(x == N-1 && y == M-1){
        return arr[x][y];
    }

    if(dp[x][y][dir] != -1) return dp[x][y][dir];

    dp[x][y][dir] = -1e9; //음수의 값도 가지는게 가능하기에

    for(int i=0; i<3; i++){
        if((dir==2 && i==1) || (dir==1 && i==2)) continue;
        int nx = x + dx[i];
        int ny = y + dy[i];
        if(!OOB(nx,ny)) continue;
        dp[x][y][dir] = max(dp[x][y][dir], dfs(nx,ny,i) + arr[x][y]);
    }
    return dp[x][y][dir];
}

int main(){
    First

    cin >> N >> M;
    for(int i=0; i<N; i++){
        for(int j=0; j<M; j++){
            cin >> arr[i][j];
        }
    }
    memset(dp,-1,sizeof(dp));

    cout << dfs(0,0,0) << "\n";

}

#include <iostream>
#include <algorithm>

using namespace std;
#define First ios::sync_with_stdio(0); cin.tie(0); cout.tie(0);

typedef long long ll;

int arr[1001][1001];
int dp[1001][1001];
int temp[1001][1001][2]; //왼쪽 오른쪽만 필요
int N, M;

int main(){
    First

    cin >> N >> M;

    for(int i=0; i<N; i++){
        for(int j=0; j<M; j++){
            cin >> arr[i][j];
        }
    }
    
    dp[0][0] = arr[0][0];
    for (int j = 1; j < M; j++) { //제일 윗줄은 오른쪽만 가능
        dp[0][j] = dp[0][j-1] + arr[0][j];
    }

    // 두 번째 행부터
    for (int i = 1; i < N; i++) { //두번째 줄부터
        for (int j = 0; j < M; j++) {
            dp[i][j] = dp[i-1][j] + arr[i][j]; //바로 윗칸에서 내려온 경우 계산
        }
        
        temp[i][0][0] = dp[i][0];
        for (int j = 1; j < M; j++) { //제일 왼쪽에서 내려와서 오른쪽으로 왔을때의 값 계산
            temp[i][j][0] = max(temp[i][j-1][0] + arr[i][j], dp[i][j]);
        }
        
        temp[i][M-1][1] = dp[i][M-1];
        for (int j = M-2; j >= 0; j--) { //제일 오른쪽에서 내려와서 왼쪽 왔을때의 값 계산
            temp[i][j][1] = max(temp[i][j+1][1] + arr[i][j], dp[i][j]);
        }
        
        for (int j = 0; j < M; j++) {
            dp[i][j] = max(temp[i][j][0], temp[i][j][1]);
        }
    }

    cout << dp[N-1][M-1] << '\n';

    return 0;
}
