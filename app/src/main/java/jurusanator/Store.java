package jurusanator;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;
/**
 * Everything saved on the phone (SharedPreferences): settings, quiz progress, past results,
 * the coach profile and the daily news cache. Reads never throw: bad data = defaults.
 */
final class Store {
    private static final String FILE = "jurusanator";
    private final SharedPreferences prefs;
    /** A quiz that was started but not finished. */
    static final class Progress {
        final int limit, strategy;
        final int[] answers;
        final long[] ms;
        final long seed;
        Progress(int limit, int strategy, int[] answers, long[] ms, long seed) {
            this.limit = limit; this.strategy = strategy; this.answers = answers; this.ms = ms; this.seed = seed;
        }
    }
    Store(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(FILE,Context.MODE_PRIVATE);
    }
    // ----------------------------------------------------------------settings
    boolean seenWelcome() { return prefs.getBoolean("seenWelcome", false); }
    void markWelcomeSeen() { prefs.edit().putBoolean("seenWelcome",true).apply(); }
    boolean langId(boolean fallback) { return prefs.getBoolean("langId", fallback); }
    void setLangId(boolean id) { prefs.edit().putBoolean("langId", id).apply(); }
    boolean sound() { return prefs.getBoolean("sound", true); }
    void setSound(boolean on) { prefs.edit().putBoolean("sound", on).apply(); }
    boolean dark() { return prefs.getBoolean("dark", false); }
    void setDark(boolean on) { prefs.edit().putBoolean("dark", on).apply(); }
    // ----------------------------------------------------------quiz in progress
    Progress progress() {
        try {
            String a = prefs.getString("pAnswers", "");
            if (a == null || a.isEmpty()) return null;
            int[] answers = History.parseAnswers(a);
            if (answers.length == 0) return null;
            String[] parts = prefs.getString("pMs", "").split(",");
            long[] ms = new long[answers.length];
            for (int i = 0; i < ms.length && i < parts.length; i++) {
                try {
                    ms[i] = Long.parseLong(parts[i].trim());
                } catch (NumberFormatException ignored) {
                    ms[i] = 0;
                }
            }
            return new Progress(prefs.getInt("pLimit", MajorRecommender.DEFAULT_QUESTIONS),
                    prefs.getInt("pStrategy", 0), answers, ms, prefs.getLong("pSeed", 0L));
        } catch (RuntimeException e) {
            return null;
        }
    }
    void setProgress(int limit, int strategy, int[] answers, List<Long> ms, long seed) {
        StringBuilder m = new StringBuilder();
        for (int i = 0; i < ms.size(); i++) {
            if (i > 0) m.append(',');
            m.append(ms.get(i));
        }
        prefs.edit().putInt("pLimit", limit).putInt("pStrategy", strategy)
                .putString("pAnswers", History.encodeAnswers(answers))
                .putString("pMs", m.toString()).putLong("pSeed", seed).apply();
    }
    void clearProgress() {
        prefs.edit().remove("pAnswers").remove("pMs").remove("pSeed").apply();
    }
    // ------------------------------------------------------------------history
    List<History.Entry> history() {
        return History.decode(prefs.getString("history", ""));
    }
    void addHistory(History.Entry entry) {
        List<History.Entry> list = history();
        list.add(0, entry);
        while (list.size() > History.MAX_ENTRIES) list.remove(list.size() - 1);
        prefs.edit().putString("history", History.encode(list)).apply();
    }
    void clearHistory() {
        prefs.edit().remove("history").apply();
    }
    // ------------------------------------------------------------------profile
    Profile profile() {
        return Profile.decode(prefs.getString("profile", ""));
    }
    void saveProfile(Profile profile) {
        prefs.edit().putString("profile", profile.encode()).apply();
    }
    // --------------------------------------------------------------------- news
    /** Cached news for a university: "yyyymmdd" on the first line, then items. */
    String newsCache(int univId) { return prefs.getString("news" + univId, ""); }
    void setNewsCache(int univId, String text) { prefs.edit().putString("news" + univId, text).apply(); }
}


