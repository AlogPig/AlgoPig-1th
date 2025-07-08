#include <bits/stdc++.h>

using namespace std;
#define First ios::sync_with_stdio(0); cin.tie(0);
typedef long long ll;
ll N,C;
vector<ll> v;
vector<ll> sum_a;
vector<ll> sum_b;

void dfs(ll srt_idx, ll end_idx, ll sum, vector<ll>& sum_v){
    if(srt_idx == end_idx){
        sum_v.push_back(sum);
        return;
    }
    dfs(srt_idx+1, end_idx, sum,sum_v);
    if(sum + v[srt_idx] <= C){
        dfs(srt_idx+1,end_idx,sum+v[srt_idx],sum_v);
    }
}

int main(){
    First
    cin >> N >> C;
    v.assign(N,0);

    for(int i=0; i<N; i++){
        cin >> v[i];
    }
    //sort(v.begin(),v.end());
    ll mid = N/2;

    dfs(0,mid,0,sum_a);
    dfs(mid,N,0,sum_b);

    sort(sum_b.begin(),sum_b.end());
    ll cnt = 0;
    for(auto s : sum_a){
        ll remain = C - s; //이 무게보다 들어갈 무게가 같거나 작아야 한다
        //upper bound로 이 수보다 처음으로 큰 수가 나오는 인덱스에서 처음 인덱스를 빼면 같거나 작은 애들 find
        cnt += upper_bound(sum_b.begin(),sum_b.end(),remain) - sum_b.begin();
    }
    cout << cnt << "\n";
}