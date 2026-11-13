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
