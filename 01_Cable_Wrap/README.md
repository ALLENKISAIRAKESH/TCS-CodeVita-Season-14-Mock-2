# Question A: Cable Wrap

> **Problem Category:** Grid Topology / Knot Theory / Winding Number  
> **Difficulty Level:** Medium-Hard  
> **Input / Output:** Standard I/O  

---

## 💡 Real-World Intuition & Analogy

### 🔌 The Tangled Phone Charger & The Table Leg

Imagine you are sitting on the floor charging your phone. Your long charging wire is running across the carpet. 

Suddenly, your little brother slides a long wooden ruler (an iron rod) straight across the floor over your wire.

Now, you pick up the wire, loop it over the top of the ruler, run it around, slip it underneath the ruler, and plug it into the wall.
- **What happens if you grab both ends of the wire and pull hard?**  
  It gets completely snagged and stuck around the ruler! You cannot unwrap it because it forms a complete loop (a coil) around the ruler.

- **Now imagine you have a superpower:**  
  Whenever your wire crosses the ruler, you can snap your fingers and magically switch which one is on top! If the ruler was on top of the wire, snap: the wire is now on top.

- **How many times do you need to snap your fingers to pull the wire completely free without touching the ends?**
  - Every time you loop over and under, you create **1 full turn** around the rod.
  - Changing a single crossing flips a half-turn.
  - Therefore, every 1 magic switch cancels out **2 opposing crossing points**!
  - If your wire has wrapped around the rod with a net winding of $W$, the minimum number of finger-snaps you need is:
    $$\text{Minimum Switches} = \frac{|W|}{2}$$

If the wire just zig-zags back and forth on the *same side* without coiling around (net winding = 0), you need **0 switches**—just pull both ends and it slips right out!

---

## 📋 Problem Rules & Grid Representation

You are given an $N \times M$ grid floor:
- `'R'` = Fixed iron rod (runs edge-to-edge horizontally across an entire row, or vertically down an entire column).
- `'C'` = Cable laid over or under the rod.
- `'.'` = Empty floor tile.

### Intersection Rules:
When the cable crosses the rod:
1. If the **rod is on top of the cable**, the cell is marked `'R'`.
2. If the **cable is on top of the rod**, the cell is marked `'C'`.

The cable starts at an edge border tile and ends at another edge border tile. Cables never run diagonally, and separate cables never run side-by-side (there is always at least 1 empty cell between separate paths).

---

## 💡 The Optimal Algorithmic Approach

### Step 1: Detect the Rod's Position and Axis
- Check every row: if all cells in row $r$ are either `'R'` or a valid cable crossing (`'C'` with cable above and below), the rod is **Horizontal** along row $r$.
- Check every column: if all cells in column $c$ are either `'R'` or a valid cable crossing (`'C'` with cable left and right), the rod is **Vertical** along column $c$.

### Step 2: Find a Starting Endpoint
- Look along the 4 borders of the grid (row $0$, row $N-1$, col $0$, col $M-1$).
- Find a `'C'` cell that is not on the rod and has **exactly 1 accessible cable neighbor**. This is our starting endpoint.

### Step 3: Trace the Cable & Accumulate Signed Winding
Walk along the cable from the start to the end using standard path-following:
- When stepping into the rod cell at `(cr, cc)` from `(pr, pc)`:
  - Determine the crossing direction:
    - If horizontal rod: $\Delta = cr - pr$ ($+1$ if moving downwards, $-1$ if moving upwards).
    - If vertical rod: $\Delta = cc - pc$ ($+1$ if moving rightwards, $-1$ if moving leftwards).
  - Determine the crossing height:
    - If the cell is `'C'`, the cable is on top $\implies +1$.
    - If the cell is `'R'`, the rod is on top $\implies -1$.
  - Add $\Delta \times \text{height}$ to our running winding counter.
  - Continue straight through the rod to the other side: `(cr + (cr - pr), cc + (cc - pc))`.
- When not on the rod, step to any unvisited adjacent valid cable neighbor.

### Step 4: Final Answer
$$\text{Result} = \frac{|\text{Total Winding Sum}|}{2}$$

---

## 🔍 Sample Walkthrough (Example 1)

### Input:
```text
5 5
..R..
CCCCC
..R.C
CCRCC
..R..
```

### Trace:
- The rod is vertical at column index $2$ (`..R..`).
- The cable starts at $(1, 0)$ on the left edge.
- Path travels east across the rod at $(1, 2)$:
  - Movement direction: left $\to$ right ($+1$).
  - Tile at $(1, 2)$ is `'C'` (cable is over rod) $\implies +1$.
  - Contribution $= (+1) \times (+1) = +1$.
- Cable loops down and returns westward across the rod at $(3, 2)$:
  - Tile at $(3, 2)$ is `'R'` (rod is over cable) $\implies -1$.
  - Movement direction: right $\to$ left ($-1$).
  - Contribution $= (-1) \times (-1) = +1$.
- Total Net Winding Sum $= (+1) + (+1) = +2$.
- Minimum switches required $= \frac{|2|}{2} = \mathbf{1}$.

Output: `1`

---

## 💻 Python Solution (`cable_wrap.py`)

```python
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
```

---

## ⏱️ Complexity Analysis

| Metric | Complexity | Explanation |
|---|---|---|
| **Time Complexity** | $\mathcal{O}(N \times M)$ | Checking rows/columns takes $N \times M$. Finding the start takes $N \times M$. Tracing the cable visits each cell at most once. Total operations $< 400$ for $N, M \le 20$. Runs in $< 0.005$ seconds. |
| **Space Complexity** | $\mathcal{O}(N \times M)$ | Storage for the 2D grid matrix and the boolean `vis` matrix. |

