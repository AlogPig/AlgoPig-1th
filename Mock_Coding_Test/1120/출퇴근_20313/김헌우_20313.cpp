#include <bits/stdc++.h>

using namespace std;
#define First ios::sync_with_stdio(0); cin.tie(0);
typedef long long ll;
#define INF 9e18

// dist[노드][마법사용횟수] = 최단 시간
ll dist[1001][101];

// v[u] = {v, 도로_인덱스}
vector<pair<int, int>> v[1001];

// costs[도로_인덱스][k] = k번 마법 썼을 때의 해당 도로 비용
ll costs[2001][101];

int N, M, A, B, K;

void dijkstra() {
    // {총 비용, 현재 노드, 사용한 마법 횟수}
    priority_queue<tuple<ll, int, int>, vector<tuple<ll, int, int>>, greater<tuple<ll, int, int>>> pq;

    // 초기화
    for (int i = 1; i <= N; i++) {
        for (int j = 0; j <= K; j++) {
            dist[i][j] = INF;
        }
    }

    // 시작점 초기화
    dist[A][0] = 0;
    pq.push({0, A, 0});

    while (!pq.empty()) {
        auto [cur_cost, cur_node, cur_k] = pq.top();
        pq.pop();

        if (dist[cur_node][cur_k] < cur_cost) continue;

        //제자리에서 마법을 사용하는 경우 (수직 이동)
        // 비용 0으로 (cur_node, cur_k) -> (cur_node, cur_k + 1) 상태로 전이
        if (cur_k + 1 <= K) {
            if (dist[cur_node][cur_k + 1] > cur_cost) {
                dist[cur_node][cur_k + 1] = cur_cost;
                pq.push({cur_cost, cur_node, cur_k + 1});
            }
        }

        //다른 도시로 이동하는 경우 (수평 이동)
        // 현재 상태(cur_k)의 비용을 사용하여 이동
        for (auto& edge : v[cur_node]) {
            int next_node = edge.first;
            int road_idx = edge.second;

            ll next_cost = cur_cost + costs[road_idx][cur_k];
            if (dist[next_node][cur_k] > next_cost) {
                dist[next_node][cur_k] = next_cost;
                pq.push({next_cost, next_node, cur_k});
            }
        }
    }
}

int main() {
    First
    cin >> N >> M >> A >> B;

    int srt, ed, cost;
    for (int i = 1; i <= M; i++) {
        cin >> srt >> ed >> cost;
        v[srt].push_back({ed, i});
        v[ed].push_back({srt, i});
        costs[i][0] = cost;
    }

    cin >> K;

    for (int k = 1; k <= K; k++) {
        for (int i = 1; i <= M; i++) {
            cin >> cost;
            costs[i][k] = cost;
        }
    }

    dijkstra();

    ll ans = INF;
    for (int k = 0; k <= K; k++) {
        ans = min(ans, dist[B][k]);
    }

    cout << ans << "\n";

    return 0;
}