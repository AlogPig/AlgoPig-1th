#include <bits/stdc++.h>

using namespace std;
#define First ios::sync_with_stdio(0); cin.tie(0);
typedef long long ll;

int N;
vector<ll> height;
vector<ll> ans;

int main(){
    First
    cin >> N;
    height.assign(N, 0);
    ans.assign(N, 0);

    ll sum_origin = 0;
    for(int i=0; i<N; i++){
        cin >> height[i];
        sum_origin += height[i];
    }

    ll prev = 0; //최소 이 값보단 더 커야한다
    for(int i=0; i<N; i++){

        ll curr = prev+1;
        if((i + 1) % 2 == 0) {
            if (abs(curr - height[i]) % 2 != 0) {
                curr++;
            }
        } else {
            if (curr == height[i]) {
            } else {
                if (abs(curr - height[i]) % 2 == 0) {
                    curr++;
                }
            }
        }
        ans[i] = curr;
        prev = curr;
    }

    ll sum_current = 0;
    for(int i=0; i<N; i++) sum_current += ans[i];

    ll remain = sum_origin - sum_current;

    if (remain < 0) {
        cout << "NO" << "\n";
        return 0;
    }

    for(int i=N-1; i>=0; i--){
        if (remain <= 0) break;

        ll limit;
        if (i == N-1) limit = 2e18;
        else limit = ans[i+1] - 1;

        ll can_add = min(remain, limit - ans[i]);
        if (can_add <= 0) continue;

        if ((i + 1) % 2 == 0) {
            if (can_add % 2 != 0) can_add--;
        } else {
            if (ans[i] == height[i]) {
                if (can_add % 2 == 0) can_add--;
            } else {
                if (can_add % 2 != 0) can_add--;
            }
        }

        if (can_add > 0) {
            ans[i] += can_add;
            remain -= can_add;
        }
    }

    if (remain == 0 || remain == 1) {
        cout << "YES" << "\n";
        for(int i=0; i<N; i++){
            cout << ans[i] - height[i] << (i == N-1 ? "" : " ");
        }
        cout << "\n";
    } else {
        cout << "NO" << "\n";
    }

    return 0;
}
