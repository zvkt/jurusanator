package jurusanator;
import java.util.ArrayList;
import java.util.List;
/**
 * Past quiz results. Only the 12 answers are stored: the results are re-computed
 * from them, so nothing else has to be kept in sync. Plain Java, easy to test.
 */
final class History {
    private History() {}
    static final int MAX_ENTRIES = 8;
    static final class Entry {
        final long time;
        final int[] answers;
        /** Major index the user chose in the tie-break duel, or -1. */
        final int duel;
        /** Question-order seed of that quiz (0 = classic order). */
        final long seed;
        Entry(long time, int[] answers) {
            this(time, answers, -1, 0L);
        }
        Entry(long time, int[] answers, int duel) {
            this(time, answers, duel, 0L);
        }
        Entry(long time, int[] answers, int duel, long seed) {
            this.time = time;
            this.answers = answers;
            this.duel = duel;
            this.seed = seed;
        }
    }
    /** One entry per line: "millis:1,0,4,2,...[@duel][#seed]". */
    static String encode(List<Entry> entries) {
        StringBuilder out = new StringBuilder();
        for (Entry entry : entries) {
            if (out.length() > 0) out.append('\n');
            out.append(entry.time).append(':');
            for (int i = 0; i < entry.answers.length; i++) {
                if (i > 0) out.append(',');
                out.append(entry.answers[i]);
            }
            if (entry.duel >= 0) out.append('@').append(entry.duel);
            if (entry.seed != 0L) out.append('#').append(entry.seed);
        }
        return out.toString();
    }
    /** Never throws: anything malformed is skipped. */
    static List<Entry> decode(String text) {
        List<Entry> entries = new ArrayList<>();
        if (text == null || text.isEmpty()) return entries;
        for (String line : text.split("\n")) {
            try {
                int colon = line.indexOf(':');
                if (colon <= 0) continue;
                long time = Long.parseLong(line.substring(0, colon).trim());
                String rest = line.substring(colon + 1);
                long seed = 0L;
                int hash = rest.indexOf('#');
                if (hash >= 0) {
                    seed = Long.parseLong(rest.substring(hash + 1).trim());
                    rest = rest.substring(0, hash);
                }
                int duel = -1;
                int at = rest.indexOf('@');
                if (at >= 0) {
                    duel = Integer.parseInt(rest.substring(at + 1).trim());
                    rest = rest.substring(0, at);
                }
                int[] answers = parseAnswers(rest);
                if (answers.length > 0) entries.add(new Entry(time, answers, duel, seed));
            } catch (RuntimeException ignored) {
                // skip a broken line, keep the rest
            }
            if (entries.size() >= MAX_ENTRIES) break;
        }
        return entries;
    }
    /** "1,0,4" -> {1,0,4}. Values outside 0..4 are dropped. */
    static int[] parseAnswers(String text) {
        if (text == null || text.trim().isEmpty()) return new int[0];
        String[] parts = text.split(",");
        int[] tmp = new int[parts.length];
        int count = 0;
        for (String part : parts) {
            try {
                int value = Integer.parseInt(part.trim());
                if (value >= 0 && value <= 4) tmp[count++] = value;
            } catch (NumberFormatException ignored) {
                // skip
            }
        }
        int[] result = new int[count];
        System.arraycopy(tmp, 0, result, 0, count);
        return result;
    }
    static String encodeAnswers(int[] answers) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < answers.length; i++) {
            if (i > 0) out.append(',');
            out.append(answers[i]);
        }
        return out.toString();
    }
}


