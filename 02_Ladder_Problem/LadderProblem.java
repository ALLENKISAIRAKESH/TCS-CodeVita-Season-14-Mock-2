import java.util.*;

public class LadderProblem {
    static int rows, cols, len;
    static char[][] grid;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        rows = in.nextInt();
        cols = in.nextInt();
        grid = new char[rows][cols];
        int filled = 0;
        while (filled < rows * cols && in.hasNext()) {
            String token = in.next();
            for (int i = 0; i < token.length() && filled < rows * cols; i++) {
                grid[filled / cols][filled % cols] = token.charAt(i);
                filled++;
            }
        }

        int sr = -1, sc = -1, sr2 = -1, sc2 = -1, count = 0;
        int dr = -1, dc = -1, dr2 = -1, dc2 = -1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 'l') {
                    if (count == 0) {
                        sr = i;
                        sc = j;
                    }
                    sr2 = i;
                    sc2 = j;
                    count++;
                } else if (grid[i][j] == 'L') {
                    if (dr == -1) {
                        dr = i;
                        dc = j;
                    }
                    dr2 = i;
                    dc2 = j;
                }
            }
        }

        if (count < 2 || dr == -1) {
            System.out.print("Impossible");
            return;
        }

        len = count;
        int startOri = sr == sr2 ? 0 : 1;
        int endOri = dr == dr2 ? 0 : 1;

        int[][][] dist = new int[2][rows][cols];
        for (int[][] layer : dist) {
            for (int[] row : layer) {
                Arrays.fill(row, -1);
            }
        }

        ArrayDeque<int[]> queue = new ArrayDeque<>();
        dist[startOri][sr][sc] = 0;
        queue.add(new int[]{sr, sc, startOri});

        int[] mr = {1, -1, 0, 0};
        int[] mc = {0, 0, 1, -1};

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0], c = cur[1], o = cur[2];
            int d = dist[o][r][c];

            if (r == dr && c == dc && o == endOri) {
                System.out.print(d);
                return;
            }

            for (int k = 0; k < 4; k++) {
                int nr = r + mr[k];
                int nc = c + mc[k];
                if (fits(nr, nc, o) && dist[o][nr][nc] == -1) {
                    dist[o][nr][nc] = d + 1;
                    queue.add(new int[]{nr, nc, o});
                }
            }

            int no = 1 - o;
            if (o == 0) {
                int[] fixedCols = {c, c + len - 1};
                int[] starts = {r, r - len + 1};
                for (int col : fixedCols) {
                    for (int top : starts) {
                        if (squareFree(top, c) && fits(top, col, no) && dist[no][top][col] == -1) {
                            dist[no][top][col] = d + 1;
                            queue.add(new int[]{top, col, no});
                        }
                    }
                }
            } else {
                int[] fixedRows = {r, r + len - 1};
                int[] starts = {c, c - len + 1};
                for (int row : fixedRows) {
                    for (int left : starts) {
                        if (squareFree(r, left) && fits(row, left, no) && dist[no][row][left] == -1) {
                            dist[no][row][left] = d + 1;
                            queue.add(new int[]{row, left, no});
                        }
                    }
                }
            }
        }

        System.out.print("Impossible");
    }

    static boolean fits(int r, int c, int o) {
        for (int i = 0; i < len; i++) {
            int rr = o == 0 ? r : r + i;
            int cc = o == 0 ? c + i : c;
            if (rr < 0 || cc < 0 || rr >= rows || cc >= cols || grid[rr][cc] == 'B') {
                return false;
            }
        }
        return true;
    }

    static boolean squareFree(int top, int left) {
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                int rr = top + i;
                int cc = left + j;
                if (rr < 0 || cc < 0 || rr >= rows || cc >= cols || grid[rr][cc] == 'B') {
                    return false;
                }
            }
        }
        return true;
    }
}