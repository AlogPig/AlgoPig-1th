#include <iostream>
#include <vector>
#include <algorithm>

using namespace std;

typedef long long ll;

int N, M;
ll A;
vector<int> v;

// 초기 점수가 skill일 때, M발을 쏘아 A점 이상을 만들 수 있는지 확인
bool check(ll skill) {
    ll current_skill = skill; // 현재 사격 실력 (타겟 선정용)
    ll earned_score = 0;      // 순수하게 획득한 점수 (목표 달성 확인용)

    for(int i = 0; i < M; i++) {
        auto it = upper_bound(v.begin(), v.end(), current_skill);

        if (it == v.begin()) {
            return false;
        }

        int target = *(it - 1);

        current_skill += target; // 실력 증가
        earned_score += target;  // 점수 획득

        if (earned_score >= A) return true; // 획득 점수가 목표 A 이상이면 성공
    }

    return earned_score >= A;
}

int main() {
    ios::sync_with_stdio(0);
    cin.tie(0);

    cin >> N >> M >> A;
    v.resize(N);
    for (int i = 0; i < N; i++) {
        cin >> v[i];
    }
    sort(v.begin(), v.end());


    ll srt = 0, ed = 100001;
    ll ans = ed;

    while (srt <= ed) {
        ll mid = (srt + ed) / 2;

        if (check(mid)) {
            ans = mid;
            ed = mid - 1;
        } else {
            srt = mid + 1;
        }
    }

    cout << ans << "\n";
    return 0;
}