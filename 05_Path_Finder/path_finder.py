import sys
import heapq

raw = sys.stdin.read().split()
if not raw:
    sys.exit(0)
it = iter(raw)

n, m = int(next(it)), int(next(it))
grid = [[int(next(it)) for _ in range(m)] for _ in range(n)]

sr, sc = int(next(it)) - 1, int(next(it)) - 1
tr, tc = int(next(it)) - 1, int(next(it)) - 1

k = int(next(it))
blocked = [[False] * m for _ in range(n)]
for _ in range(k):
    blocked[int(next(it)) - 1][int(next(it)) - 1] = True

def get_nbrs(r, c):
    res = []
    for dr in (-1, 0, 1):
        for dc in (-1, 0, 1):
            if dr == 0 and dc == 0:
                continue
            nr, nc = r + dr, c + dc
            if 0 <= nr < n and 0 <= nc < m and not blocked[nr][nc]:
                res.append((nr, nc))
    return res

pq = [(0, sr, sc)]
dist = [[float('inf')] * m for _ in range(n)]
dist[sr][sc] = 0

while pq:
    d, r, c = heapq.heappop(pq)

    if d > dist[r][c]:
        continue
    if r == tr and c == tc:
        break

    nbrs = get_nbrs(r, c)
    if not nbrs:
        continue

    best = max(grid[nr][nc] for nr, nc in nbrs)
    cur_val = grid[r][c]

    for nr, nc in nbrs:
        val = grid[nr][nc]
        if cur_val >= best:
            cost = cur_val - val + 1
        else:
            others = [grid[xr][xc] for xr, xc in nbrs if not (xr == nr and xc == nc)]
            if not others:
                cost = 0
            else:
                top_other = max(others)
                cost = 0 if val > top_other else (top_other - val + 1)

        if d + cost < dist[nr][nc]:
            dist[nr][nc] = d + cost
            heapq.heappush(pq, (d + cost, nr, nc))

print(dist[tr][tc], end='')
