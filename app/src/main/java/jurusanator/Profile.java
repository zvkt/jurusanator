package jurusanator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Everything the coach remembers about the user. Plain Java. Saved as simple
 * "key=value" lines (see encode/decode). decode() never throws: bad data is skipped.
 */
final class Profile {
    static final int ROUTE_SNBP = 0, ROUTE_SNBT = 1, ROUTE_MANDIRI = 2;
    static final int STRAT_STANDARD = 0, STRAT_ENGAGE = 1, STRAT_DEEP = 2;
    static final int LOG_MOCK = 0, LOG_STUDY = 1;
    static final float[] DEFAULT_WEIGHTS = {0.35f, 0.15f, 0.35f, 0.15f}; // interest, univ, ready, commit
    private static final int MAX_LOGS = 300, MAX_PROBES = 12, MAX_CHANGES = 8;
    static final class Log {
        final long time;
        final int type;    // LOG_MOCK or LOG_STUDY
        final int skill;   // 0..6
        final int value;   // mock: score 0..100, study: minutes
        Log(long time, int type, int skill, int value) {
            this.time = time; this.type = type; this.skill = skill; this.value = value;
        }
    }
    static final class Probe {
        final long time;
        final int major;
        final int answer; // 0 yes .. 4 no
        final int ms;
        Probe(long time, int major, int answer, int ms) {
            this.time = time; this.major = major; this.answer = answer; this.ms = ms;
        }
    }
    /** How the last quiz was answered (used to judge seriousness). */
    static final class QuizStats {
        final long time;
        final int avgMs;
        final int unsurePct;
        final int samePct;
        final int twinPct; // -1 = unknown
        QuizStats(long time, int avgMs, int unsurePct, int samePct, int twinPct) {
            this.time = time; this.avgMs = avgMs; this.unsurePct = unsurePct;
            this.samePct = samePct; this.twinPct = twinPct;
        }
    }
    static final class Outcome {
        final long time;
        final boolean got;
        final int route, hours, mocks, adherence, applied, hardest;
        Outcome(long time, boolean got, int route, int hours, int mocks, int adherence,
                int applied, int hardest) {
            this.time = time; this.got = got; this.route = route; this.hours = hours;
            this.mocks = mocks; this.adherence = adherence; this.applied = applied;
            this.hardest = hardest;
        }
    }
    int dreamMajor = -1;
    int dreamUniv = -1;
    int route = ROUTE_SNBT;
    long dreamSince;
    final int[] skill = {50, 50, 50, 50, 50, 50, 50};
    boolean rated;
    int bestTime = 2;               // 0 morning, 1 afternoon, 2 evening
    int strategy = STRAT_STANDARD;
    int flags;                      // Coach.F_* diagnosis bits
    int seriousness = -1;           // 0..100, -1 = unknown
    final float[] weights = DEFAULT_WEIGHTS.clone();
    long lastProbe;
    boolean dailyNews;
    final List<Log> logs = new ArrayList<>();
    final List<Probe> probes = new ArrayList<>();
    QuizStats quiz;
    Outcome outcome;
    final Map<String, Integer> dates = new LinkedHashMap<>();      // event key -> yyyymmdd
    final Set<String> calendar = new LinkedHashSet<>();            // keys already put in the calendar
    final List<String[]> changes = new ArrayList<>();              // {en, id}
    boolean hasDream() {
        return dreamMajor >= 0 && dreamUniv >= 0 && Univ.byId(dreamUniv) != null;
    }
    void addLog(Log log) {
        logs.add(log);
        while (logs.size() > MAX_LOGS) logs.remove(0);
    }
    void addProbe(Probe probe) {
        probes.add(probe);
        while (probes.size() > MAX_PROBES) probes.remove(0);
    }
    void addChange(String en, String id) {
        changes.add(new String[]{clean(en), clean(id)});
        while (changes.size() > MAX_CHANGES) changes.remove(0);
    }
    private static String clean(String s) {
        return s.replace("|", "/").replace("~", "-").replace("\n", " ");
    }
    // ----------------------------------------------------------------save / load
    String encode() {
        StringBuilder out = new StringBuilder();
        put(out, "dm", dreamMajor);
        put(out, "du", dreamUniv);
        put(out, "rt", route);
        put(out, "ds", dreamSince);
        put(out, "rated", rated ? 1 : 0);
        put(out, "sk", ints(skill));
        put(out, "bt", bestTime);
        put(out, "st", strategy);
        put(out, "fl", flags);
        put(out, "sr", seriousness);
        put(out, "fw", weights[0] + "," + weights[1] + "," + weights[2] + "," + weights[3]);
        put(out, "lp", lastProbe);
        put(out, "dn", dailyNews ? 1 : 0);
        StringBuilder sb = new StringBuilder();
        for (Log l : logs) {
            if (sb.length() > 0) sb.append(';');
            sb.append(l.time).append(',').append(l.type).append(',').append(l.skill).append(',').append(l.value);
        }
        put(out, "logs", sb.toString());
        sb.setLength(0);
        for (Probe p : probes) {
            if (sb.length() > 0) sb.append(';');
            sb.append(p.time).append(',').append(p.major).append(',').append(p.answer).append(',').append(p.ms);
        }
        put(out, "probes", sb.toString());
        if (quiz != null) {
            put(out, "quiz", quiz.time + "," + quiz.avgMs + "," + quiz.unsurePct + "," + quiz.samePct + "," + quiz.twinPct);
        }
        if (outcome != null) {
            Outcome o = outcome;
            put(out, "out", o.time + "," + (o.got ? 1 : 0) + "," + o.route + "," + o.hours + ","
                    + o.mocks + "," + o.adherence + "," + o.applied + "," + o.hardest);
        }
        sb.setLength(0);
        for (Map.Entry<String, Integer> e : dates.entrySet()) {
            if (sb.length() > 0) sb.append(';');
            sb.append(e.getKey()).append(':').append(e.getValue());
        }
        put(out, "dates", sb.toString());
        sb.setLength(0);
        for (String key : calendar) {
            if (sb.length() > 0) sb.append(';');
            sb.append(key);
        }
        put(out, "cal", sb.toString());
        sb.setLength(0);
        for (String[] c : changes) {
            if (sb.length() > 0) sb.append('|');
            sb.append(c[0]).append('~').append(c[1]);
        }
        put(out, "chg", sb.toString());
        return out.toString();
    }
    private static void put(StringBuilder out, String key, Object value) {
        out.append(key).append('=').append(value).append('\n');
    }
    private static String ints(int[] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(values[i]);
        }
        return sb.toString();
    }
    /** Never throws. Unknown or broken lines are ignored. */
    static Profile decode(String text) {
        Profile p = new Profile();
        if (text == null) return p;
        for (String line : text.split("\n")) {
            int eq = line.indexOf('=');
            if (eq <= 0) continue;
            String key = line.substring(0, eq);
            String value = line.substring(eq + 1);
            try {
                p.read(key, value);
            } catch (RuntimeException ignored) {
                // skip this line only
            }
        }
        return p;
    }
    private void read(String key, String v) {
        switch (key) {
            case "dm": dreamMajor = clamp(Integer.parseInt(v), -1, MajorData.MAJORS.size() - 1); break;
            case "du": dreamUniv = Univ.byId(Integer.parseInt(v)) == null ? -1 : Integer.parseInt(v); break;
            case "rt": route = clamp(Integer.parseInt(v), 0, 2); break;
            case "ds": dreamSince = Long.parseLong(v); break;
            case "rated": rated = "1".equals(v); break;
            case "sk": {
                String[] parts = v.split(",");
                for (int i = 0; i < skill.length && i < parts.length; i++) {
                    skill[i] = clamp(Integer.parseInt(parts[i].trim()), 0, 100);
                }
                break;
            }
            case "bt": bestTime = clamp(Integer.parseInt(v), 0, 2); break;
            case "st": strategy = clamp(Integer.parseInt(v), 0, 2); break;
            case "fl": flags = Integer.parseInt(v); break;
            case "sr": seriousness = clamp(Integer.parseInt(v), -1, 100); break;
            case "fw": {
                String[] parts = v.split(",");
                if (parts.length == 4) {
                    float[] w = new float[4];
                    float sum = 0f;
                    for (int i = 0; i < 4; i++) {
                        w[i] = Math.max(0.05f, Float.parseFloat(parts[i].trim()));
                        sum += w[i];
                    }
                    for (int i = 0; i < 4; i++) weights[i] = w[i] / sum;
                }
                break;
            }
            case "lp": lastProbe = Long.parseLong(v); break;
            case "dn": dailyNews = "1".equals(v); break;
            case "logs":
            for (String item : split(v)) {
                String[] f = item.split(",");
                if (f.length != 4) continue;
                logs.add(new Log(Long.parseLong(f[0]), clamp(Integer.parseInt(f[1]), 0, 1),
                        clamp(Integer.parseInt(f[2]), 0, Skills.COUNT - 1), Integer.parseInt(f[3])));
            }
            break;
            case "probes":
            for (String item : split(v)) {
                String[] f = item.split(",");
                if (f.length != 4) continue;
                probes.add(new Probe(Long.parseLong(f[0]),
                        clamp(Integer.parseInt(f[1]), 0, MajorData.MAJORS.size() - 1),
                        clamp(Integer.parseInt(f[2]), 0, 4), Integer.parseInt(f[3])));
            }
            break;
            case "quiz": {
                String[] f = v.split(",");
                if (f.length == 5) {
                    quiz = new QuizStats(Long.parseLong(f[0]), Integer.parseInt(f[1]),
                            Integer.parseInt(f[2]), Integer.parseInt(f[3]), Integer.parseInt(f[4]));
                }
                break;
            }
            case "out": {
                String[] f = v.split(",");
                if (f.length == 8) {
                    outcome = new Outcome(Long.parseLong(f[0]), "1".equals(f[1]),
                            clamp(Integer.parseInt(f[2]), 0, 2), Integer.parseInt(f[3]),
                            Integer.parseInt(f[4]), Integer.parseInt(f[5]), Integer.parseInt(f[6]),
                            clamp(Integer.parseInt(f[7]), 0, Skills.COUNT - 1));
                }
                break;
            }
            case "dates":
            for (String item : split(v)) {
                int colon = item.indexOf(':');
                if (colon > 0) dates.put(item.substring(0, colon), Integer.parseInt(item.substring(colon + 1)));
            }
            break;
            case "cal":
            for (String item : split(v)) calendar.add(item);
            break;
            case "chg":
            for (String item : v.split("\\|")) {
                int tilde = item.indexOf('~');
                if (tilde > 0) changes.add(new String[]{item.substring(0, tilde), item.substring(tilde + 1)});
            }
            break;
            default:
            break;
        }
    }
    private static List<String> split(String v) {
        List<String> out = new ArrayList<>();
        if (v == null || v.isEmpty()) return out;
        for (String part : v.split(";")) {
            if (!part.isEmpty()) out.add(part);
        }
        return out;
    }
    static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}


