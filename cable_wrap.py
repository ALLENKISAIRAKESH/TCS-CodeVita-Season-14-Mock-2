import sys

raw = sys.stdin.read().split()
if not raw:
    sys.exit(0)

n, m = int(raw[0]), int(raw[1])
stream = []
for tok in raw[2:]:
    stream.extend(tok)
    if len(stream) >= n * m:
        break

g = [stream[i * m : (i + 1) * m] for i in range(n)]

axis = -1
pos = -1

for r in range(n):
    has_r = False
    valid = True
    for c in range(m):
        ch = g[r][c]
        if ch == '.':
            valid = False
            break
        elif ch == 'R':
            has_r = True
        elif ch == 'C':
            if r == 0 or r == n - 1 or g[r - 1][c] != 'C' or g[r + 1][c] != 'C':
                valid = False
                break
    if valid and has_r:
        axis = 0
        pos = r
        break

if axis == -1:
    for c in range(m):
        has_r = False
        valid = True
        for r in range(n):
            ch = g[r][c]
            if ch == '.':
                valid = False
                break
            elif ch == 'R':
                has_r = True
            elif ch == 'C':
                if c == 0 or c == m - 1 or g[r][c - 1] != 'C' or g[r][c + 1] != 'C':
                    valid = False
                    break
        if valid and has_r:
            axis = 1
            pos = c
            break

if axis == -1:
    print(0, end='')
    sys.exit(0)

def ok(r, c):
    if r < 0 or c < 0 or r >= n or c >= m:
        return False
    if g[r][c] == 'C':
        return True
    if g[r][c] == 'R':
        if axis == 0:
            return r > 0 and r < n - 1 and g[r - 1][c] == 'C' and g[r + 1][c] == 'C'
        return c > 0 and c < m - 1 and g[r][c - 1] == 'C' and g[r][c + 1] == 'C'
    return False

def on_rod(r, c):
    return r == pos if axis == 0 else c == pos

sr, sc = -1, -1
for r in range(n):
    for c in range(m):
        if (r == 0 or r == n - 1 or c == 0 or c == m - 1) and g[r][c] == 'C' and not on_rod(r, c):
            deg = 0
            for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                if ok(r + dr, c + dc):
                    deg += 1
            if deg == 1:
                sr, sc = r, c
                break
    if sr != -1:
        break

if sr == -1:
    print(0, end='')
    sys.exit(0)

vis = [[False] * m for _ in range(n)]
cr, cc = sr, sc
pr, pc = -1, -1
total_turns = 0

while True:
    vis[cr][cc] = True
    nr, nc = -1, -1

    if on_rod(cr, cc):
        step = (cr - pr) if axis == 0 else (cc - pc)
        total_turns += step * (1 if g[cr][cc] == 'C' else -1)
        tr, tc = cr + (cr - pr), cc + (cc - pc)
        if ok(tr, tc):
            nr, nc = tr, tc
    else:
        for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            tr, tc = cr + dr, cc + dc
            if ok(tr, tc) and not vis[tr][tc]:
                nr, nc = tr, tc
                break

    if nr == -1:
        break
    pr, pc = cr, cc
    cr, cc = nr, nc

print(abs(total_turns) // 2, end='')
