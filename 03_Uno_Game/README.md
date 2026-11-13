# Question C: Uno Game

> **Problem Category:** Disjoint Set Union (DSU) + 0/1 Knapsack DP + Bitmask Independent Set  
> **Difficulty Level:** Medium-Hard  
> **Input / Output:** Standard I/O  

---

## 🧒 The "Lazy Man & Kid" Explanation (Everyday Real-World Analogy)

### 🎮 The Birthday Party Guest List with Drama

Imagine you are planning the ultimate Uno game party at an arcade.
- Each friend has an entry ticket price (their **skill points**). 
- Your mom gives you a strict wallet budget of $\$W$ (**skill limit**).
- Your goal: **Invite as many kids as possible** without exceeding your budget!

Now comes the playground drama:
1. **Best Friends (Package Deal):** If you invite Aryan, you MUST also invite his best friend Kabir. They are inseparable. If you can't afford both, you can't invite either!
2. **Rivals (The Feud):** Rohan and Neha had a fight. If both are in the room, they will throw Uno cards at each other. So you can invite Rohan, or Neha, or neither—but **NEVER both**!
3. **The Drama Bomb (Internal Feud):** What if Kabir and Aryan are best friends, but they also secretly hate each other (they are also listed as rivals)? Since they insist on coming together, but can't be in the same room, **they disqualify themselves completely**! Neither can come.

### How do we solve this?
- For normal kids with **no rivalries**, it's like a supermarket shopping spree: pick the lightest items to fit the most items in your cart (**Knapsack DP**).
- For kids involved in rivalries, there are only a tiny handful of them ($N_1 + N_2 \le 10$). We can quickly test every peaceful combination on our fingers (**Bitmask**)!

---

## 📋 Problem Specifications & Constraints

- $N \le 25$ (Number of players).
- Player name length $\le 10$ characters.
- Skill level of each player $\le 100$.
- Max limit on total team skill: $W \le 1000$.
- **Crucial Constraint:** $1 \le N_1 + N_2 \le 10$ (Total number of friend pairs + rival pairs $\le 10$).

Because $N_1 + N_2 \le 10$, the number of rival-involved groups is at most 10. That means $2^{10} = 1024$ subsets—computable in milliseconds!

---

## 💡 The Optimal Algorithmic Approach

### 1. Grouping Friends using Disjoint Set Union (DSU)
- Each player starts as their own component.
- For every friend pair $(u, v)$, execute `union(u, v)`.
- For each resulting connected component, sum up:
  - $\text{Total Weight} = \sum \text{skills}$
  - $\text{Total Size} = \text{number of members}$

### 2. Disqualify Self-Conflicting Groups
- For each rival pair $(u, v)$:
  - Find their group roots: $r_u = \text{find}(u)$ and $r_v = \text{find}(v)$.
  - If $r_u == r_v$, then two rival members belong to the *same* mandatory friend group. Mark this group as `banned = True`.

### 3. Divide Groups into Two Pools
1. **Peaceful Pool (No Rivalry):**
   Groups that have no rivalries with anyone. Run standard 0/1 Knapsack Dynamic Programming:
   $$dp[w] = \max(\text{kids chosen with total skill } \le w)$$
2. **Conflict Pool (Rivalry Involved):**
   Only a small number $K \le 10$ of groups have rivalries. Assign each an index $0, 1, \dots, K-1$.
   Build an adjacency conflict bitmask for each group: `adj[i]` has bit $j$ set if group $i$ and group $j$ are rivals.

### 4. Bitmask Enumeration Over Conflict Pool
Iterate through all masks from $0$ to $2^K - 1$:
- Check if the mask represents an **Independent Set** (no two selected groups are rivals).
- If valid, let the total skill of these rival groups be $w_{\text{mask}}$ and their total headcount be $s_{\text{mask}}$.
- If $w_{\text{mask}} \le \text{limit}$, combine them with the optimal peaceful pool:
  $$\text{Total Headcount} = s_{\text{mask}} + dp[\text{limit} - w_{\text{mask}}]$$
- The maximum over all valid masks is the answer!

---

## 🔍 Sample Walkthrough (Example 1)

### Input:
```text
10
Ram Raj Vishnu Teja Alekhya Keerti Ganesh Seetha Rakesh Latha
1 2 3 4 5 6 7 8 9 1
2
Ram Latha
Alekhya Keerti
3
Ram Teja
Raj Rakesh
Seetha Raj
10
```

- Budget limit: $10$.
- Friends: `(Ram, Latha)` (skills $1 + 1 = 2$, size $2$), `(Alekhya, Keerti)` (skills $5 + 6 = 11 > 10$, too expensive).
- Peaceful players: `Vishnu` (skill 3, size 1), `Ganesh` (skill 7, size 1).
- Best team selected: `(Ram, Latha)` + `Raj` + `Vishnu`.
  - Combined skill: $1 + 1 + 2 + 3 = 7 \le 10$.
  - Total players: **4**.

---

## 💻 Python Solution (`uno_game.py`)

```python
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
```

---

## ⏱️ Complexity Analysis

| Metric | Complexity | Explanation |
|---|---|---|
| **Time Complexity** | $\mathcal{O}(N + 2^K + N \times \text{limit})$ | DSU operations take almost $\mathcal{O}(N)$. Knapsack DP runs in $N \times \text{limit} \le 25 \times 1000 = 2.5 \times 10^4$ operations. Bitmask evaluation takes $2^K$ where $K \le 10 \implies 1024$ steps. Overall time $< 0.02$ seconds. |
| **Space Complexity** | $\mathcal{O}(N + \text{limit} + 2^K)$ | Storage for DSU arrays, DP array (size 1001), and bitmask arrays (size 1024). |
