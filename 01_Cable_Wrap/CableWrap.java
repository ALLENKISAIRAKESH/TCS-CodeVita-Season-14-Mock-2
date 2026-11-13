import java.util.*;

public class CableWrap {
    static int n, m;
    static char[][] g;
    static boolean horizontal;
    static int line;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        n = in.nextInt();
        m = in.nextInt();
        g = new char[n][m];

        int filled = 0;
        while (filled < n * m && in.hasNext()) {
            String token = in.next();
            for (int i = 0; i < token.length() && filled < n * m; i++) {
                g[filled / m][filled % m] = token.charAt(i);
                filled++;
            }
        }

        line = -1;
        for (int r = 0; r < n && line == -1; r++) {
            if (rowIsRod(r)) {
                line = r;
                horizontal = true;
            }
        }
        for (int c = 0; c < m && line == -1; c++) {
            if (colIsRod(c)) {
                line = c;
                horizontal = false;
            }
        }
        if (line == -1) {
            System.out.print(0);
            return;
        }

        int[] dr = {1, -1, 0, 0};
        int[] dc = {0, 0, 1, -1};

        // start from a real cable end: border cell, off the rod line, one neighbour
        int sr = -1, sc = -1;
        for (int r = 0; r < n && sr == -1; r++) {
            for (int c = 0; c < m; c++) {
                boolean border = r == 0 || c == 0 || r == n - 1 || c == m - 1;
                if (!border || g[r][c] != 'C' || onLine(r, c)) continue;
                int cnt = 0;
                for (int k = 0; k < 4; k++) {
                    if (passable(r + dr[k], c + dc[k])) cnt++;
                }
                if (cnt == 1) {
                    sr = r;
                    sc = c;
                    break;
                }
            }
        }
        if (sr == -1) {
            System.out.print(0);
            return;
        }

        boolean[][] seen = new boolean[n][m];
        int pr = -1, pc = -1, cr = sr, cc = sc;
        int sum = 0;

        while (true) {
            seen[cr][cc] = true;
            int nr = -1, nc = -1;

            if (onLine(cr, cc)) {
                int d = horizontal ? cr - pr : cc - pc;
                sum += (g[cr][cc] == 'C' ? 1 : -1) * d;
                int tr = cr + (cr - pr);
                int tc = cc + (cc - pc);
                if (passable(tr, tc)) {
                    nr = tr;
                    nc = tc;
                }
            } else {
                for (int k = 0; k < 4; k++) {
                    int tr = cr + dr[k];
                    int tc = cc + dc[k];
                    if (passable(tr, tc) && !seen[tr][tc]) {
                        nr = tr;
                        nc = tc;
                        break;
                    }
                }
            }

            if (nr == -1) break;
            pr = cr;
            pc = cc;
            cr = nr;
            cc = nc;
        }

        System.out.print(Math.abs(sum) / 2);
    }

    static boolean inside(int r, int c) {
        return r >= 0 && c >= 0 && r < n && c < m;
    }

    static boolean onLine(int r, int c) {
        return horizontal ? r == line : c == line;
    }

    static boolean passable(int r, int c) {
        if (!inside(r, c)) return false;
        if (g[r][c] == 'C') return true;
        if (g[r][c] != 'R') return false;
        if (horizontal) {
            return inside(r - 1, c) && inside(r + 1, c)
                    && g[r - 1][c] == 'C' && g[r + 1][c] == 'C';
        }
        return inside(r, c - 1) && inside(r, c + 1)
                && g[r][c - 1] == 'C' && g[r][c + 1] == 'C';
    }

    static boolean rowIsRod(int r) {
        boolean hasRod = false;
        for (int c = 0; c < m; c++) {
            char ch = g[r][c];
            if (ch == '.') return false;
            if (ch == 'R') {
                hasRod = true;
            } else {
                if (r == 0 || r == n - 1) return false;
                if (g[r - 1][c] != 'C' || g[r + 1][c] != 'C') return false;
            }
        }
        return hasRod;
    }

    static boolean colIsRod(int c) {
        boolean hasRod = false;
        for (int r = 0; r < n; r++) {
            char ch = g[r][c];
            if (ch == '.') return false;
            if (ch == 'R') {
                hasRod = true;
            } else {
                if (c == 0 || c == m - 1) return false;
                if (g[r][c - 1] != 'C' || g[r][c + 1] != 'C') return false;
            }
        }
        return hasRod;
    }
}