import java.util.*;

public class PathFinder {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int m = in.nextInt();
        int[][] a = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                a[i][j] = in.nextInt();
            }
        }
        int sr = in.nextInt() - 1;
        int sc = in.nextInt() - 1;
        int tr = in.nextInt() - 1;
        int tc = in.nextInt() - 1;
        int k = in.nextInt();
        boolean[][] blocked = new boolean[n][m];
        for (int i = 0; i < k; i++) {
            int r = in.nextInt() - 1;
            int c = in.nextInt() - 1;
            blocked[r][c] = true;
        }

        int inf = Integer.MAX_VALUE / 2;
        int[][] dist = new int[n][m];
        for (int[] row : dist) {
            Arrays.fill(row, inf);
        }
        dist[sr][sc] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>((x, y) -> x[0] - y[0]);
        pq.add(new int[]{0, sr, sc});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int d = cur[0], r = cur[1], c = cur[2];
            if (d > dist[r][c]) continue;
            if (r == tr && c == tc) break;

            int best = Integer.MIN_VALUE;
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;
                    int nr = r + dr, nc = c + dc;
                    if (nr < 0 || nc < 0 || nr >= n || nc >= m || blocked[nr][nc]) continue;
                    best = Math.max(best, a[nr][nc]);
                }
            }

            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;
                    int nr = r + dr, nc = c + dc;
                    if (nr < 0 || nc < 0 || nr >= n || nc >= m || blocked[nr][nc]) continue;

                    int cost;
                    if (a[r][c] >= best) {
                        cost = a[r][c] - a[nr][nc] + 1;
                    } else {
                        int others = Integer.MIN_VALUE;
                        for (int er = -1; er <= 1; er++) {
                            for (int ec = -1; ec <= 1; ec++) {
                                if (er == 0 && ec == 0) continue;
                                int xr = r + er, xc = c + ec;
                                if (xr < 0 || xc < 0 || xr >= n || xc >= m || blocked[xr][xc]) continue;
                                if (xr == nr && xc == nc) continue;
                                others = Math.max(others, a[xr][xc]);
                            }
                        }
                        cost = a[nr][nc] > others ? 0 : others - a[nr][nc] + 1;
                    }

                    if (d + cost < dist[nr][nc]) {
                        dist[nr][nc] = d + cost;
                        pq.add(new int[]{dist[nr][nc], nr, nc});
                    }
                }
            }
        }

        System.out.print(dist[tr][tc]);
    }
}