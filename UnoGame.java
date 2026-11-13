import java.util.*;

public class UnoGame {
    static int[] parent;

    static int find(int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        Map<String, Integer> id = new HashMap<>();
        for (int i = 0; i < n; i++) {
            id.put(in.next(), i);
        }
        int[] skill = new int[n];
        for (int i = 0; i < n; i++) {
            skill[i] = in.nextInt();
        }

        parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }

        int f = in.nextInt();
        for (int i = 0; i < f; i++) {
            int a = id.get(in.next());
            int b = id.get(in.next());
            parent[find(a)] = find(b);
        }

        int r = in.nextInt();
        int[][] rivals = new int[r][2];
        for (int i = 0; i < r; i++) {
            rivals[i][0] = id.get(in.next());
            rivals[i][1] = id.get(in.next());
        }
        int limit = in.nextInt();

        int[] weight = new int[n];
        int[] size = new int[n];
        for (int i = 0; i < n; i++) {
            int g = find(i);
            weight[g] += skill[i];
            size[g]++;
        }

        boolean[] banned = new boolean[n];
        boolean[] touched = new boolean[n];
        for (int[] p : rivals) {
            int a = find(p[0]);
            int b = find(p[1]);
            if (a == b) {
                banned[a] = true;
            } else {
                touched[a] = true;
                touched[b] = true;
            }
        }

        int[] slot = new int[n];
        Arrays.fill(slot, -1);
        List<Integer> involved = new ArrayList<>();
        int[] dp = new int[limit + 1];
        for (int g = 0; g < n; g++) {
            if (find(g) != g || banned[g]) continue;
            if (touched[g]) {
                slot[g] = involved.size();
                involved.add(g);
            } else {
                for (int j = limit; j >= weight[g]; j--) {
                    dp[j] = Math.max(dp[j], dp[j - weight[g]] + size[g]);
                }
            }
        }

        int k = involved.size();
        int[] adj = new int[k];
        for (int[] p : rivals) {
            int a = find(p[0]);
            int b = find(p[1]);
            if (a == b || banned[a] || banned[b]) continue;
            adj[slot[a]] |= 1 << slot[b];
            adj[slot[b]] |= 1 << slot[a];
        }

        int total = 1 << k;
        int[] sumW = new int[total];
        int[] sumC = new int[total];
        boolean[] ok = new boolean[total];
        ok[0] = true;
        int best = dp[limit];

        for (int mask = 1; mask < total; mask++) {
            int low = Integer.numberOfTrailingZeros(mask);
            int rest = mask & (mask - 1);
            if (!ok[rest] || (adj[low] & rest) != 0) continue;
            ok[mask] = true;
            int g = involved.get(low);
            sumW[mask] = sumW[rest] + weight[g];
            sumC[mask] = sumC[rest] + size[g];
            if (sumW[mask] <= limit) {
                best = Math.max(best, sumC[mask] + dp[limit - sumW[mask]]);
            }
        }

        System.out.print(best);
    }
}