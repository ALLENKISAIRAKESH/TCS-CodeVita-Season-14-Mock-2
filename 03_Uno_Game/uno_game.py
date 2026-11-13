import sys

raw = sys.stdin.read().split()
if not raw:
    sys.exit(0)
it = iter(raw)

n = int(next(it))
names = [next(it) for _ in range(n)]
mp = {names[i]: i for i in range(n)}
skills = [int(next(it)) for _ in range(n)]

f = int(next(it))
friends = []
for _ in range(f):
    friends.append((mp[next(it)], mp[next(it)]))

r = int(next(it))
rivals = []
for _ in range(r):
    rivals.append((mp[next(it)], mp[next(it)]))

limit = int(next(it))

parent = list(range(n))

def find(x):
    while parent[x] != x:
        parent[x] = parent[parent[x]]
        x = parent[x]
    return x

for a, b in friends:
    ra, rb = find(a), find(b)
    if ra != rb:
        parent[ra] = rb

wt = [0] * n
sz = [0] * n
for i in range(n):
    root = find(i)
    wt[root] += skills[i]
    sz[root] += 1

banned = [False] * n
touched = [False] * n
for a, b in rivals:
    ra, rb = find(a), find(b)
    if ra == rb:
        banned[ra] = True
    else:
        touched[ra] = True
        touched[rb] = True

active = []
pos_map = {}
dp = [0] * (limit + 1)

for i in range(n):
    if find(i) != i or banned[i]:
        continue
    if touched[i]:
        pos_map[i] = len(active)
        active.append(i)
    else:
        w, s = wt[i], sz[i]
        for j in range(limit, w - 1, -1):
            if dp[j - w] + s > dp[j]:
                dp[j] = dp[j - w] + s

m = len(active)
adj = [0] * m
for a, b in rivals:
    ra, rb = find(a), find(b)
    if ra == rb or banned[ra] or banned[rb]:
        continue
    ia = pos_map[ra]
    ib = pos_map[rb]
    adj[ia] |= (1 << ib)
    adj[ib] |= (1 << ia)

tot = 1 << m
sum_w = [0] * tot
sum_s = [0] * tot
valid = [False] * tot
valid[0] = True

ans = dp[limit]

for mask in range(1, tot):
    bit = (mask & -mask).bit_length() - 1
    prev = mask ^ (1 << bit)

    if valid[prev] and not (adj[bit] & prev):
        valid[mask] = True
        grp = active[bit]
        w = sum_w[prev] + wt[grp]
        s = sum_s[prev] + sz[grp]
        sum_w[mask] = w
        sum_s[mask] = s

        if w <= limit:
            score = s + dp[limit - w]
            if score > ans:
                ans = score

print(ans, end='')
