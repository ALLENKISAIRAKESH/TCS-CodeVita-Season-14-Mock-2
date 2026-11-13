# Question E: Path Finder

> **Problem Category:** Shortest Path / Dijkstra's Algorithm / Min-Heap Priority Queue  
> **Difficulty Level:** Medium  
> **Input / Output:** Standard I/O  

---

## 🧒 The "Lazy Man & Kid" Explanation (Everyday Real-World Analogy)

### 🐸 Lilly the Diva Frog on River Stepping Stones

Imagine a frog named Lilly who wants to cross a pond filled with numbered stepping stones to reach her cozy lily pad.
- Lilly is a bit of a diva: from her current stone, she will **ONLY** hop to a neighboring stone if that stone has the **strictly biggest number** among all her surrounding choices!
- But what if the stone she wants to jump to has a smaller number than the others? Lilly carries a pocket full of **magic glitter dust**. She can sprinkle glitter on the next stone to temporarily raise its number until it becomes strictly bigger than the other candidates!
- Once she jumps onto that stone, the glitter wears off and the stone returns to its original number.

Now, Lilly's glitter rules:
1. **Rule 1 (Current stone is King):** If the stone Lilly is currently standing on has a number greater than or equal to ALL its neighbors, she insists that her next stone must beat her *current stone's* value!  
   $$\text{Glitter Needed} = \text{Current Stone} - \text{Next Stone} + 1$$
2. **Rule 2 (Neighbor competition):** Otherwise, she only needs her next stone to beat all the *other surrounding unblocked stones*:  
   $$\text{Glitter Needed} = \max(0, \text{Biggest Other Neighbor} - \text{Next Stone} + 1)$$

What is the minimum total glitter dust Lilly needs to reach her destination? That's classic **Dijkstra's shortest path**!

---

## 📋 Problem Specifications & Constraints

- Grid dimensions: $2 \le N, M \le 20$.
- Cell values: $1 < \text{value} < 100$.
- $K$ blocked cells ($0 \le K \le 10$): Lilly cannot enter blocked cells, and blocked cells are **never counted** when finding the maximum among neighbors.
- Movements allowed: All 8 directions (horizontal, vertical, diagonal).
- Coordinates in input are **1-based** $(r, c)$.

---

## 💡 The Optimal Algorithmic Approach

### 1. Modeling as a Directed Graph
- Each cell $(r, c)$ in the grid is a graph node.
- From $(r, c)$, Lilly can step into any of the 8 adjacent cells $(nr, nc)$ that are inside bounds and not blocked.

### 2. Edge Weight (Cost) Calculation
For a step from $(r, c)$ to $(nr, nc)$:
1. Find all unblocked neighbors of $(r, c)$.
2. Let $M_{\text{all}} = \max(\text{values of all unblocked neighbors})$.
3. **If $A[r][c] \ge M_{\text{all}}$:**
   $$\text{step\_cost} = A[r][c] - A[nr][nc] + 1$$
4. **Else:**
   Let $M_{\text{other}} = \max(\text{values of all unblocked neighbors except } (nr, nc))$.
   $$\text{step\_cost} = \begin{cases} 0 & \text{if } A[nr][nc] > M_{\text{other}} \\ M_{\text{other}} - A[nr][nc] + 1 & \text{otherwise} \end{cases}$$

### 3. Dijkstra's Algorithm
Because all step costs are non-negative ($\ge 0$), Dijkstra's algorithm with a min-priority queue (`heapq`) finds the minimum cumulative cost path from source $(sr, sc)$ to destination $(tr, tc)$.

---

## 🔍 Sample Walkthrough (Example 1)

### Input:
```text
4 4
1 2 3 4
5 6 7 8
9 10 11 12
13 14 15 16
2 2
4 1
2
3 1
1 4
```

- Grid: $4 \times 4$.
- Start: $(2, 2)$ with value $6$.
- Destination: $(4, 1)$ with value $13$.
- Blocked cells: $(3, 1)$ and $(1, 4)$.

### Path Taken: $(2, 2) \to (3, 2) \to (4, 1)$
1. **At $(2, 2)$ [value 6]:**
   - Unblocked neighbors: $(1, 1)=1, (1, 2)=2, (1, 3)=3, (2, 1)=5, (2, 3)=7, (3, 2)=10, (3, 3)=11$.
   - Max neighbor is $11$ at $(3, 3)$. Current cell ($6$) is NOT $\ge 11$.
   - She wants to move to $(3, 2)$ [value 10].
   - Other neighbors' max is $11$.
   - Cost $= 11 - 10 + 1 = \mathbf{2}$.
2. **At $(3, 2)$ [value 10]:**
   - Neighbors: $(2, 1)=5, (2, 2)=6, (2, 3)=7, (3, 3)=11, (4, 1)=13, (4, 2)=14, (4, 3)=15$.
   - Max neighbor is $15$ at $(4, 3)$. Current cell ($10$) is NOT $\ge 15$.
   - She wants to move to $(4, 1)$ [value 13].
   - Other neighbors' max is $15$.
   - Cost $= 15 - 13 + 1 = \mathbf{3}$.
- Total Cost $= 2 + 3 = \mathbf{5}$.

Output: `5`

---

## 💻 Python Solution (`path_finder.py`)

```python
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
```

---

## ⏱️ Complexity Analysis

| Metric | Complexity | Explanation |
|---|---|---|
| **Time Complexity** | $\mathcal{O}(N \times M \log(NM))$ | Grid size $V \le 20 \times 20 = 400$. Each node has at most 8 directed edges. The min-heap operations take at most $8 \times 400 \log(400) \approx 2.5 \times 10^4$ operations. Runs in $< 0.01$ seconds. |
| **Space Complexity** | $\mathcal{O}(N \times M)$ | 2D matrices for grid values, blocked cells, distance table, and priority queue. |
