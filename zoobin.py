import sys
from collections import deque

raw = sys.stdin.read().split()
if not raw:
    sys.exit(0)
it = iter(raw)

e = int(next(it))
g1 = []
top = 0
for _ in range(e):
    u, v = int(next(it)), int(next(it))
    g1.append((min(u, v), max(u, v)))
    top = max(top, u, v)

g2 = []
for _ in range(e):
    u, v = int(next(it)), int(next(it))
    g2.append((min(u, v), max(u, v)))
    top = max(top, u, v)

n = top + 1

def encode(edges):
    return tuple(sorted((min(u, v), max(u, v)) for u, v in edges))

start = encode(g1)
target = encode(g2)

if start == target:
    print(0, end='')
    sys.exit(0)

def get_cycles(edges):
    adj = [[] for _ in range(n)]
    for u, v in edges:
        adj[u].append(v)
        adj[v].append(u)

    cycs = []
    path = []
    used = [False] * n

    def dfs(root, cur, d):
        for nxt in adj[cur]:
            if nxt == root:
                if d >= 3:
                    cycs.append(list(path))
            elif nxt > root and not used[nxt]:
                used[nxt] = True
                path.append(nxt)
                dfs(root, nxt, d + 1)
                path.pop()
                used[nxt] = False

    for s in range(n):
        used[s] = True
        path.append(s)
        dfs(s, s, 1)
        path.pop()
        used[s] = False

    return cycs

q = deque([start])
dist = {start: 0}
ans = -1

while q:
    cur = q.popleft()
    d = dist[cur]

    for cyc in get_cycles(cur):
        to = list(range(n))
        sz = len(cyc)
        for i in range(sz):
            to[cyc[i]] = cyc[(i + 1) % sz]

        nxt_edges = []
        for u, v in cur:
            nu, nv = to[u], to[v]
            nxt_edges.append((min(nu, nv), max(nu, nv)))
        nxt = encode(nxt_edges)

        if nxt not in dist:
            if nxt == target:
                ans = d + 1
                q.clear()
                break
            dist[nxt] = d + 1
            q.append(nxt)

print(ans, end='')
