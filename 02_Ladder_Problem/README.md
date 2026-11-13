# Question B: Ladder Problem

> **Problem Category:** 2D Grid BFS / State-Space Search / Shortest Path  
> **Difficulty Level:** Medium  
> **Input / Output:** Standard I/O  

---

## 💡 Real-World Intuition & Analogy

### 🛋️ Carrying a Long Surfboard Down a Hallway ("PIVOT!")

Have you ever tried moving a long surfboard, ladder, or couch through your house?
1. **Walking Straight (Slide):** If the hallway ahead is clear, you just walk forward 1 step. You can slide forward, backward, left, or right—as long as no part of the ladder bangs into a piece of furniture (`'B'`). This costs **1 step**.
2. **Turning a Corner (Rotate $90^\circ$):** 
   - You can't bend a rigid wooden ladder! If you want to turn it from horizontal to vertical, the ends of the ladder swing through the air in a big arc.
   - For a ladder of length $K$, swinging it around requires an entire **$K \times K$ square room** completely empty of furniture and walls. If there is even one chair or wall in that square zone, the ladder hits it and you cannot turn!
   - Every rotation also costs **1 step**.

Your goal: Find the minimum number of steps (slides + rotations) to get the ladder from where it starts (`'l'`) to where it belongs (`'L'`). If obstacles make it impossible to reach the goal, shout **"Impossible"**!

---

## 📋 Problem Specifications & Grid Rules

- Grid size: $M \times N$ (where $0 \le M, N \le 15$).
- Ladder length: $K$ cells long (where $2 \le K \le 6$).
- Cells:
  - `'l'` / `'I'` = Initial ladder position.
  - `'L'` = Destination ladder position.
  - `'B'` = Blocked household item (cannot enter).
  - `'E'` = Empty walkable floor.
- Movement rules:
  1. **Translation:** Move 1 cell in any of 4 orthogonal directions (Up, Down, Left, Right). Orientation stays the same.
  2. **Rotation:** Rotate $90^\circ$ (horizontal $\leftrightarrow$ vertical) only if all cells in the $K \times K$ square formed by current and rotated positions are empty and inside the grid.

---

## 💡 The Optimal Algorithmic Approach

### 1. State Space Representation
A ladder configuration is uniquely determined by 3 values:
$$\text{State} = (r, c, \text{ori})$$
- $(r, c)$ = Coordinate of the top-left cell of the ladder.
- $\text{ori}$ = Orientation ($0$ for Horizontal, $1$ for Vertical).
- Because $M, N \le 15$ and $\text{ori} \in \{0, 1\}$, the total number of possible states in the entire universe is at most:
  $$15 \times 15 \times 2 = 450 \text{ states!}$$

### 2. State Transitions
From any state $(r, c, \text{ori})$:
- **Slide moves:** Try shifting $(r \pm 1, c)$ and $(r, c \pm 1)$. If all $K$ cells remain inside bounds and are not `'B'`, enqueue the new state.
- **Rotation moves:**
  - If Horizontal ($\text{ori} = 0$): Can rotate downwards ($r$) or upwards ($r - K + 1$) using column $c$ or $c + K - 1$ as the pivot axis.
  - If Vertical ($\text{ori} = 1$): Can rotate rightwards ($c$) or leftwards ($c - K + 1$) using row $r$ or $r + K - 1$ as the pivot axis.
  - Check that the corresponding $K \times K$ bounding square is free of `'B'` and inside the grid boundaries.

### 3. Graph Search (BFS)
Because every move (slide or rotate) costs exactly 1 unit of time/step, standard **Breadth-First Search (BFS)** using a `deque` guarantees finding the shortest path in optimal time.

---

## 🔍 Sample Walkthrough (Example 1)

### Input:
```text
5 5
llEEE
EBEEB
EBEBB
EBEEB
EEELL
```

- Initial ladder: Horizontal at $(0, 0)$ to $(0, 1)$, length $K = 2$.
- Target ladder: Horizontal at $(4, 3)$ to $(4, 4)$, length $K = 2$.
- Step 1: Slide right to $(0, 1)$.
- Step 2: Slide right to $(0, 2)$.
- Step 3: Rotate $90^\circ$ into vertical orientation at $(0, 2) \dots (1, 2)$. ($2 \times 2$ block is clear).
- Step 4: Slide down to $(1, 2)$.
- Step 5: Slide down to $(2, 2)$.
- Step 6: Slide down to $(3, 2)$.
- Step 7: Rotate back to horizontal at $(4, 2) \dots (4, 3)$.
- Step 8: Slide right to $(4, 3) \dots (4, 4)$. Reached target!
- Total steps: `8`.

---

## 💻 Python Solution (`ladder_problem.py`)

```python
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
```

---

## ⏱️ Complexity Analysis

| Metric | Complexity | Explanation |
|---|---|---|
| **Time Complexity** | $\mathcal{O}(M \times N \times K^2)$ | Maximum number of states $V \le 2 \times 15 \times 15 = 450$. Each state has at most 4 translations and 4 rotations. Each collision test takes $\mathcal{O}(K^2)$ where $K \le 6$. Total operations $< 1.5 \times 10^4$. Runs in $< 0.01$ seconds. |
| **Space Complexity** | $\mathcal{O}(M \times N)$ | BFS queue and visited state dictionary store at most 450 entries. |

