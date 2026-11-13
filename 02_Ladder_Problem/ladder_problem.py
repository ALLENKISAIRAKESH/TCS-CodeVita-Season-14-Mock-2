import sys
from collections import deque

raw = sys.stdin.read().split()
if not raw:
    sys.exit(0)

n = int(raw[0])
m = int(raw[1])

stream = []
for tok in raw[2:]:
    stream.extend(tok)
    if len(stream) >= n * m:
        break

grid = [stream[i * m : (i + 1) * m] for i in range(n)]

src = []
dst = []
for r in range(n):
    for c in range(m):
        ch = grid[r][c]
        if ch in ('l', 'I', 'i'):
            src.append((r, c))
        elif ch == 'L':
            dst.append((r, c))

k = len(src)
if k < 2 or len(dst) != k:
    print("Impossible", end='')
    sys.exit(0)

src.sort()
dst.sort()

so = 0 if src[0][0] == src[-1][0] else 1
do = 0 if dst[0][0] == dst[-1][0] else 1

start = (src[0][0], src[0][1], so)
target = (dst[0][0], dst[0][1], do)

def fits(r, c, ori):
    for i in range(k):
        rr = r if ori == 0 else r + i
        cc = c + i if ori == 0 else c
        if rr < 0 or cc < 0 or rr >= n or cc >= m or grid[rr][cc] == 'B':
            return False
    return True

def box_clear(r0, c0):
    for dr in range(k):
        for dc in range(k):
            rr = r0 + dr
            cc = c0 + dc
            if rr < 0 or cc < 0 or rr >= n or cc >= m or grid[rr][cc] == 'B':
                return False
    return True

q = deque([start])
dist = {start: 0}
ans = -1

while q:
    r, c, ori = q.popleft()
    d = dist[(r, c, ori)]

    if (r, c, ori) == target:
        ans = d
        break

    for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1)):
        nr, nc = r + dr, c + dc
        if fits(nr, nc, ori):
            nxt = (nr, nc, ori)
            if nxt not in dist:
                dist[nxt] = d + 1
                q.append(nxt)

    no = 1 - ori
    if ori == 0:
        for fc in (c, c + k - 1):
            for tr in (r, r - k + 1):
                if box_clear(tr, c) and fits(tr, fc, no):
                    nxt = (tr, fc, no)
                    if nxt not in dist:
                        dist[nxt] = d + 1
                        q.append(nxt)
    else:
        for fr in (r, r + k - 1):
            for lc in (c, c - k + 1):
                if box_clear(r, lc) and fits(fr, lc, no):
                    nxt = (fr, lc, no)
                    if nxt not in dist:
                        dist[nxt] = d + 1
                        q.append(nxt)

if ans != -1:
    print(ans, end='')
else:
    print("Impossible", end='')
