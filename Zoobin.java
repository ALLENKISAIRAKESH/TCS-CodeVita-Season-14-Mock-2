import java.util.*;

public class Zoobin {
    static int size;
    static boolean[][] adj;
    static int[] path;
    static boolean[] used;
    static List<int[]> cycles;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int e = in.nextInt();
        int[][] first = new int[e][2];
        int[][] second = new int[e][2];
        int top = 0;
        for (int i = 0; i < e; i++) {
            first[i][0] = in.nextInt();
            first[i][1] = in.nextInt();
            top = Math.max(top, Math.max(first[i][0], first[i][1]));
        }
        for (int i = 0; i < e; i++) {
            second[i][0] = in.nextInt();
            second[i][1] = in.nextInt();
            top = Math.max(top, Math.max(second[i][0], second[i][1]));
        }
        size = top + 1;

        String start = encode(first);
        String goal = encode(second);

        if (start.equals(goal)) {
            System.out.print(0);
            return;
        }

        Map<String, Integer> dist = new HashMap<>();
        ArrayDeque<String> queue = new ArrayDeque<>();
        dist.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            String cur = queue.poll();
            int d = dist.get(cur);

            int[][] edges = decode(cur);
            adj = new boolean[size][size];
            for (int[] ed : edges) {
                adj[ed[0]][ed[1]] = true;
                adj[ed[1]][ed[0]] = true;
            }

            cycles = new ArrayList<>();
            path = new int[size];
            used = new boolean[size];
            for (int s = 0; s < size; s++) {
                path[0] = s;
                used[s] = true;
                walk(s, s, 1);
                used[s] = false;
            }

            for (int[] cyc : cycles) {
                int[] to = new int[size];
                for (int i = 0; i < size; i++) {
                    to[i] = i;
                }
                for (int i = 0; i < cyc.length; i++) {
                    to[cyc[i]] = cyc[(i + 1) % cyc.length];
                }
                int[][] moved = new int[edges.length][2];
                for (int i = 0; i < edges.length; i++) {
                    moved[i][0] = to[edges[i][0]];
                    moved[i][1] = to[edges[i][1]];
                }
                String next = encode(moved);
                if (!dist.containsKey(next)) {
                    if (next.equals(goal)) {
                        System.out.print(d + 1);
                        return;
                    }
                    dist.put(next, d + 1);
                    queue.add(next);
                }
            }
        }

        System.out.print(-1);
    }

    static void walk(int s, int u, int depth) {
        for (int v = 0; v < size; v++) {
            if (!adj[u][v]) continue;
            if (v == s) {
                if (depth >= 3) {
                    cycles.add(Arrays.copyOf(path, depth));
                }
            } else if (v > s && !used[v]) {
                used[v] = true;
                path[depth] = v;
                walk(s, v, depth + 1);
                used[v] = false;
            }
        }
    }

    static String encode(int[][] edges) {
        char[] arr = new char[edges.length];
        for (int i = 0; i < edges.length; i++) {
            int a = Math.min(edges[i][0], edges[i][1]);
            int b = Math.max(edges[i][0], edges[i][1]);
            arr[i] = (char) (a * 128 + b);
        }
        Arrays.sort(arr);
        return new String(arr);
    }

    static int[][] decode(String s) {
        int[][] edges = new int[s.length()][2];
        for (int i = 0; i < s.length(); i++) {
            int code = s.charAt(i);
            edges[i][0] = code / 128;
            edges[i][1] = code % 128;
        }
        return edges;
    }
}