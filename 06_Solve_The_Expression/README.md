# Question F: Solve The Expression

> **Problem Category:** 7-Segment ASCII OCR / Bitwise Logic / Recursive Descent Parser  
> **Difficulty Level:** Hard  
> **Input / Output:** Standard I/O  

---

## 🧒 The "Lazy Man & Kid" Explanation (Everyday Real-World Analogy)

### 📟 The Secret Code on the Broken Digital Clock

Have you ever looked at a microwave, digital clock, or calculator screen? Numbers are shown using little glowing LED sticks (called a **7-segment display**).
- For example, an `8` lights up all the sticks, while a `1` only lights up two sticks on the right side.
- In this puzzle, someone created a secret math language using 3x3 grids of characters (`|`, `_`, ` `) on a screen:
  1. **Page 1 of the manual:** Shows you what digits $0$ through $9$ look like in 3x3 ASCII bars.
  2. **Page 2 of the manual:** Shows you what math symbols look like: **Logical OR** (`|`), **Logical AND** (`&`), **Logical NOT** (`!`), and brackets `(` and `)`.
  3. **The Puzzle Page:** Gives you a long equation drawn in 3x3 LED blocks!
- Your job:
  - Turn each 3x3 block into a 9-bit binary string (`1` for an active stick, `0` for an empty space).
  - Solve the equation! But **WATCH OUT**: this puzzle uses a **custom rulebook for operator precedence**:
    1. **Brackets `( )` first** (Highest Priority)
    2. **Logical NOT `!` second** (Flips all 1s to 0s and 0s to 1s)
    3. **Logical OR `|` third** (Bitwise OR)
    4. **Logical AND `&` fourth** (Lowest Priority — in this puzzle, OR happens before AND!)
  - Convert the final binary answer back into regular decimal digits and print it!

---

## 📋 Problem Specifications & Input Format

Input is divided into 3 tiers (each 3 lines tall):
- **Lines 1–3:** Digits $0$ through $9$ in order, each 3 columns wide (total 30 columns).
- **Lines 4–6:** 5 operators in fixed order: Logical OR (`|`), Logical AND (`&`), Logical NOT (`!`), `(`, `)`. Each 3 columns wide (total 15 columns).
- **Lines 7–9:** The mathematical expression to evaluate.

*(Note: In some test cases, lines may be separated by empty lines, making 17 lines total. Our reader gracefully handles both 9-line and 17-line formats).*

### Bitwise Evaluation Rules:
1. **Multi-digit numbers:** For a number like `64`, its binary string is the 9 bits of `6` concatenated with the 9 bits of `4` (length $18$).
2. **Padding:** Before performing a bitwise operation between two operands of different lengths, pad the shorter binary string on the **left with `'0'`s** until both have equal length.
3. **Precedence:** `( )` $>$ `!` $>$ `|` $>$ `&`.
4. **Output:** Pad the result with leading `'0'`s until its length is a multiple of 9, break it into 9-bit chunks, and translate each chunk back to its matching digit $0..9$.

---

## 💡 The Optimal Algorithmic Approach

### 1. 3x3 Block Bit-Encoding
For any 3x3 cell starting at row $r_0$ and column $c_0$:
$$\text{bit}(r, c) = \begin{cases} \text{'1'} & \text{if character is not a space } (' ') \\ \text{'0'} & \text{otherwise} \end{cases}$$
Concatenating all 9 cells produces a unique 9-bit string. We create a dictionary `mapping` that translates 9-bit strings to tokens (`0`–`9`, `|`, `&`, `!`, `(`, `)`).

### 2. Recursive Descent Parsing (Custom Grammar)
Because operator precedence is strictly defined:
- `parse_primary()`: Handles `(` `parse_and()` `)` or multi-digit numbers.
- `parse_unary()`: Handles `!` by recursively calling `parse_unary()` and inverting every bit (`'0'` $\leftrightarrow$ `'1'`).
- `parse_or()`: Handles left-associative `|` chaining:
  $$\text{val} = \text{combine}(\text{val}, \text{parse\_unary}(), \text{is\_and}=\text{False})$$
- `parse_and()`: Handles left-associative `&` chaining:
  $$\text{val} = \text{combine}(\text{val}, \text{parse\_or}(), \text{is\_and}=\text{True})$$

---

## 🔍 Sample Walkthrough (Example 1)

### Input Expression:
Expression translates to: `64 && 91`
- Binary for `64`: binary(`6`) $+$ binary(`4`) (18 bits: `010110111000111001`).
- Binary for `91`: binary(`9`) $+$ binary(`1`) (18 bits: `010111011000001001`).
- Perform bitwise AND:
  $$\text{result} = \text{binary}(64) \ \& \ \text{binary}(91) = \text{010110011000001001}$$
- Break into 9-bit chunks:
  - First 9 bits `010110011` matches digit **5**.
  - Second 9 bits `000001001` matches digit **1**.
- Output: `51`.

---

## 💻 Python Solution (`solve_expression.py`)

```python
import sys

lines = [line.rstrip('\r\n') for line in sys.stdin.readlines()]
if not lines:
    sys.exit(0)

spaced = len(lines) >= 17 and all(
    len(lines[i]) == 0 for i in range(1, min(17, len(lines)), 2)
)

rows = []
if spaced:
    for i in range(0, 17, 2):
        rows.append(lines[i])
else:
    non_empty = [l for l in lines if l.strip() != '']
    if len(non_empty) == 9:
        rows = non_empty
    else:
        for i in range(9):
            rows.append(lines[i] if i < len(lines) else '')

if len(rows) < 9:
    sys.exit(0)

def cell(r0, c0):
    bits = []
    for r in range(3):
        s = rows[r0 + r]
        for c in range(3):
            idx = c0 + c
            bits.append('1' if idx < len(s) and s[idx] != ' ' else '0')
    return ''.join(bits)

digits = [cell(0, d * 3) for d in range(10)]
mapping = {digits[d]: str(d) for d in range(10)}

ops = ['|', '&', '!', '(', ')']
for i in range(5):
    mapping[cell(3, i * 3)] = ops[i]

max_len = max(len(rows[6 + r]) for r in range(3))
total_tokens = (max_len + 2) // 3

tokens = []
for i in range(total_tokens):
    key = cell(6, i * 3)
    if key in mapping:
        tokens.append(mapping[key])

pos = 0

def peek():
    return tokens[pos] if pos < len(tokens) else ''

def combine(a, b, is_and):
    L = max(len(a), len(b))
    a = a.zfill(L)
    b = b.zfill(L)
    res = []
    for x, y in zip(a, b):
        bit = (x == '1' and y == '1') if is_and else (x == '1' or y == '1')
        res.append('1' if bit else '0')
    return ''.join(res)

def parse_primary():
    global pos
    if peek() == '(':
        pos += 1
        v = parse_and()
        if peek() == ')':
            pos += 1
        return v
    bits = []
    while pos < len(tokens) and tokens[pos].isdigit():
        d = int(tokens[pos])
        bits.append(digits[d])
        pos += 1
    return ''.join(bits)

def parse_unary():
    global pos
    if peek() == '!':
        pos += 1
        v = parse_unary()
        return ''.join('0' if b == '1' else '1' for b in v)
    return parse_primary()

def parse_or():
    global pos
    v = parse_unary()
    while peek() == '|':
        pos += 1
        v = combine(v, parse_unary(), False)
    return v

def parse_and():
    global pos
    v = parse_or()
    while peek() == '&':
        pos += 1
        v = combine(v, parse_or(), True)
    return v

res = parse_and()

rem = len(res) % 9
if rem != 0:
    res = res.zfill(len(res) + (9 - rem))

out = []
for i in range(0, len(res), 9):
    chunk = res[i : i + 9]
    for d in range(10):
        if digits[d] == chunk:
            out.append(str(d))
            break

print(''.join(out), end='')
```

---

## ⏱️ Complexity Analysis

| Metric | Complexity | Explanation |
|---|---|---|
| **Time Complexity** | $\mathcal{O}(L \times B)$ | $L \le 32$ tokens, and each operand binary string has length $B \le 32 \times 9 = 288$ bits. Parsing and bitwise operations take at most a few thousand CPU instructions. Runs in $< 0.01$ seconds. |
| **Space Complexity** | $\mathcal{O}(L \times B)$ | Memory required for recursion stack and binary string representations. |
