import java.io.*;
import java.util.*;

public class SolveExpression {
    static List<String> tokens = new ArrayList<>();
    static String[] digitBits = new String[10];
    static int pos = 0;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        List<String> raw = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            raw.add(line.replace("\r", ""));
        }

        List<String> rows = new ArrayList<>();
        boolean spaced = raw.size() >= 17;
        for (int i = 1; i < raw.size() && i < 17 && spaced; i += 2) {
            if (raw.get(i).length() != 0) {
                spaced = false;
            }
        }
        if (spaced) {
            for (int i = 0; i < 17; i += 2) {
                rows.add(raw.get(i));
            }
        } else {
            for (int i = 0; i < 9; i++) {
                rows.add(i < raw.size() ? raw.get(i) : "");
            }
        }

        for (int d = 0; d < 10; d++) {
            digitBits[d] = cell(rows, 0, d * 3);
        }

        Map<String, String> symbol = new HashMap<>();
        for (int d = 0; d < 10; d++) {
            symbol.put(digitBits[d], String.valueOf(d));
        }
        String[] ops = {"|", "&", "!", "(", ")"};
        for (int i = 0; i < 5; i++) {
            symbol.put(cell(rows, 3, i * 3), ops[i]);
        }

        int width = 0;
        for (int i = 6; i < 9; i++) {
            width = Math.max(width, rows.get(i).length());
        }
        int count = (width + 2) / 3;
        for (int i = 0; i < count; i++) {
            String key = cell(rows, 6, i * 3);
            String t = symbol.get(key);
            if (t != null) {
                tokens.add(t);
            }
        }

        String result = andExpr();

        while (result.length() % 9 != 0) {
            result = "0" + result;
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < result.length(); i += 9) {
            String chunk = result.substring(i, i + 9);
            for (int d = 0; d < 10; d++) {
                if (digitBits[d].equals(chunk)) {
                    out.append(d);
                    break;
                }
            }
        }
        System.out.print(out);
    }

    static String cell(List<String> rows, int startRow, int col) {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < 3; r++) {
            String s = rows.get(startRow + r);
            for (int c = 0; c < 3; c++) {
                int idx = col + c;
                boolean on = idx < s.length() && s.charAt(idx) != ' ';
                sb.append(on ? '1' : '0');
            }
        }
        return sb.toString();
    }

    static String peek() {
        return pos < tokens.size() ? tokens.get(pos) : "";
    }

    static String andExpr() {
        String v = orExpr();
        while (peek().equals("&")) {
            pos++;
            v = combine(v, orExpr(), true);
        }
        return v;
    }

    static String orExpr() {
        String v = unary();
        while (peek().equals("|")) {
            pos++;
            v = combine(v, unary(), false);
        }
        return v;
    }

    static String unary() {
        if (peek().equals("!")) {
            pos++;
            String v = unary();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < v.length(); i++) {
                sb.append(v.charAt(i) == '1' ? '0' : '1');
            }
            return sb.toString();
        }
        return primary();
    }

    static String primary() {
        if (peek().equals("(")) {
            pos++;
            String v = andExpr();
            pos++;
            return v;
        }
        StringBuilder sb = new StringBuilder();
        while (pos < tokens.size() && Character.isDigit(tokens.get(pos).charAt(0))) {
            sb.append(digitBits[tokens.get(pos).charAt(0) - '0']);
            pos++;
        }
        return sb.toString();
    }

    static String combine(String a, String b, boolean and) {
        int len = Math.max(a.length(), b.length());
        while (a.length() < len) a = "0" + a;
        while (b.length() < len) b = "0" + b;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            boolean x = a.charAt(i) == '1';
            boolean y = b.charAt(i) == '1';
            sb.append((and ? (x && y) : (x || y)) ? '1' : '0');
        }
        return sb.toString();
    }
}